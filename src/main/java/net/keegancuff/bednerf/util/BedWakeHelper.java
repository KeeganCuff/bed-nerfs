package net.keegancuff.bednerf.util;

import net.keegancuff.bednerf.BedNerfMod;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;

import java.util.Optional;


public class BedWakeHelper {
    public static boolean tryWakePlayer(World world, PlayerEntity player){
        if (!player.isSleeping() || world.getDifficulty() == Difficulty.PEACEFUL /*|| player.isCreative()*/ || world.isClient()){
            return false;
        }
        ServerWorld serverWorld = (ServerWorld) world;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        BlockPos playerPos = serverPlayer.getBlockPos();
        int x = playerPos.getX(), y = playerPos.getY(), z = playerPos.getZ();
        int testX, testY, testZ;
        BedNerfMod.LOGGER.info("Checking if the player will wake up. " + x + " / " + y + " / " + z);
        for (int i = 0; i<200; i++){ // 200 tries for finding successful path
            testX = x + MathHelper.nextInt(serverWorld.getRandom(), -32, 32);
            testY = y + MathHelper.nextInt(serverWorld.getRandom(), -5, 5);
            testZ = z + MathHelper.nextInt(serverWorld.getRandom(), -32, 32);
            BlockPos testPos = new BlockPos(testX, testY, testZ);

            EntityType<? extends HostileEntity> type = getRand(serverWorld.getRandom());
            SpawnRestriction.Location location = SpawnRestriction.getLocation(type);
            if (!SpawnHelper.canSpawn(location, serverWorld, testPos, type) || world.getLightLevel(LightType.BLOCK, testPos) != 0) {
                continue;
            } // found a valid spawn of a monster within 32 blocks of sleeping player
            BedNerfMod.LOGGER.info("Found valid spawn location for " + type.getName().getString() + ". " + testX + " / " + testY + " / " + testZ);
            // now we create a mob to test if it can find a path from that spawnpoint
            HostileEntity hostileMob = type.create(serverWorld);
            hostileMob.setPosition(testPos.toCenterPos());
            hostileMob.setOnGround(true);
            hostileMob.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Player bed sleep failure", 2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            Path testPath = hostileMob.getNavigation().findPathTo(playerPos, 1);
            if (testPath == null){
                continue;
            } // path exists
            BedNerfMod.LOGGER.info(testPath.reachesTarget() ? "path complete: length = " + testPath.getLength() : "path incomplete");
            //only down to spawn on the last 6 blocks of the path
            if (type == EntityType.SKELETON){
                for (int l = testPath.reachesTarget() ? 5 : 1; l<=10 && l <=testPath.getLength(); l++){
                    Vec3d pathPos = testPath.getNodePosition(hostileMob, testPath.getLength() - l);
                    if (SpawnHelper.canSpawn(location, serverWorld, testPos, type)){ // if the place in the path is a valid spawn
                        hostileMob.setPosition(pathPos);
                        if (testPath.reachesTarget() || hostileMob.canSee(player)){ // and the path is complete or the mob has los on the player
                            summonMob(type, hostileMob, serverWorld, pathPos, serverPlayer);
                            return true;
                        }
                    }
                    if (l == 10){
                        BedNerfMod.LOGGER.info("Spawn attempt failed");
                        return false;
                    }
                }
            }
            for (int l = testPath.reachesTarget() ? 2 : 1; l<=6 && l <=testPath.getLength(); l++){
                Vec3d pathPos = testPath.getNodePosition(hostileMob, testPath.getLength() - l);
                if (SpawnHelper.canSpawn(location, serverWorld, testPos, type)){ // if the place in the path is a valid spawn
                    hostileMob.setPosition(pathPos);
                    if (testPath.reachesTarget() || hostileMob.canSee(player)){ // and the path is complete or the mob has los on the player
                        summonMob(type, hostileMob, serverWorld, pathPos, serverPlayer);
                        return true;
                    }
                }
                if (l == 6){
                    BedNerfMod.LOGGER.info("Spawn attempt failed");
                }
            }
        }


        return false;
    }

    public static EntityType<? extends HostileEntity> getRand(Random random){
        int rand = MathHelper.nextInt(random, 0, 3);
        return switch (rand) {
            case 0 -> EntityType.ZOMBIE;
            case 1 -> EntityType.SKELETON;
            case 2 -> EntityType.CREEPER;
            default -> EntityType.SPIDER;
        };
    }

    private static void summonMob(EntityType<? extends HostileEntity> type, HostileEntity entity, ServerWorld world, Vec3d pos, PlayerEntity player){
        entity.setPosition(pos);
        entity.setTarget(player);
        entity.initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.EVENT, null, null);
        world.spawnEntity(entity);
        player.wakeUp();
        if (type == EntityType.SKELETON){
            ((SkeletonEntity) entity).attack(player, 1f);
        }
        BedNerfMod.LOGGER.info("Summoned " + entity.getName().getString() + " and woke player.");
    }
}
