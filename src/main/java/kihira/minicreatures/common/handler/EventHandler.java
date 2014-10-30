/*
 * Copyright (C) 2014  Kihira
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package kihira.minicreatures.common.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.IMiniCreature;
import kihira.minicreatures.common.entity.ai.EntityAIProspect;
import kihira.minicreatures.common.entity.ai.EnumRole;
import kihira.minicreatures.common.network.SetAttackTargetMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Random;

public class EventHandler {

    public static String[] names;

    private long lastTrigger = 0;

    //TODO Overhaul this? Make it so it can be calculated server side
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void leftClick(MouseEvent interactEvent) {
        //Only trigger if it has been more then 5 seconds
        if ((System.currentTimeMillis() - this.lastTrigger > 5000) && Minecraft.getMinecraft().gameSettings.keyBindAttack.getIsKeyPressed()) {
            //Only if it's a sword
            if (Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                MovingObjectPosition target = getMouseOver(20);
                if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    this.lastTrigger = System.currentTimeMillis();
                    MiniCreatures.proxy.simpleNetworkWrapper.sendToServer(new SetAttackTargetMessage(target.entityHit.getEntityId()));
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityEvent.EntityConstructing event) {
        if (event.entity.worldObj != null) {
            Random random = event.entity.worldObj.rand;
            if (names != null && MiniCreatures.randomNameChance != 0 && event.entity instanceof IMiniCreature && random.nextInt(MiniCreatures.randomNameChance) == 0) {
                EntityLiving entityLiving = ((IMiniCreature) event.entity).getEntity();
                entityLiving.setCustomNameTag(names[random.nextInt(names.length - 1)]);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onPlayerIteract(PlayerInteractEvent event) {
        //Used to start making nearby mini players prospect
        if (!(event.entityPlayer instanceof FakePlayer) && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK &&
                event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() instanceof ItemPickaxe &&
                event.world.getBlock(event.x, event.y, event.z) == Blocks.stone && event.entityPlayer.isSneaking()) {
            int radius = 5;
            EntityPlayer player = event.entityPlayer;
            List<EntityMiniPlayer> list = event.world.getEntitiesWithinAABB(EntityMiniPlayer.class, AxisAlignedBB.getBoundingBox(
                    player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius));

            if (list != null && list.size() > 0) {
                for (EntityMiniPlayer miniPlayer : list) {
                    if (miniPlayer.getOwner() == player && miniPlayer.getRole() == EnumRole.MINER) {
                        EntityAIProspect prospectAI = getEntityAITask(miniPlayer, EntityAIProspect.class);
                        if (prospectAI != null) {
                            prospectAI.prospect = true;
                            prospectAI.target = new int[]{event.x, event.y, event.z};
                            event.setCanceled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteract(EntityInteractEvent event) {
        //We do this render here as if the player right clicks with a lead, entity.interact never fires
        if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Items.lead) {
            //Mount the fox
            if (event.target instanceof EntityFox) {
                EntityFox entityFox = (EntityFox) event.target;
                if (entityFox.riddenByEntity == null && entityFox.ridingEntity == null) {
                    double d0 = 7.0D;
                    List list = entityFox.worldObj.getEntitiesWithinAABB(EntityMiniPlayer.class, AxisAlignedBB.getBoundingBox(entityFox.posX - d0, entityFox.posY - d0, entityFox.posZ - d0, entityFox.posX + d0, entityFox.posY + d0, entityFox.posZ + d0));

                    if (list != null) {
                        for (Object aList : list) {
                            EntityLiving entityliving = (EntityLiving) aList;
                            if (entityliving instanceof EntityMiniPlayer && entityliving.getLeashed() && entityliving.getLeashedToEntity() == event.entityPlayer) {
                                entityliving.setLeashedToEntity(null, true);
                                entityliving.mountEntity(entityFox);
                                event.setCanceled(true);
                                break;
                            }
                        }
                    }
                }
            }
            //Unmount from fox
            else if (event.target instanceof EntityMiniPlayer) {
                EntityMiniPlayer entityMiniPlayer = (EntityMiniPlayer) event.target;
                if (entityMiniPlayer.ridingEntity != null) {
                    entityMiniPlayer.mountEntity(null);
                    event.setCanceled(true);
                }
            }
        }
    }

    /**
     * This is used to check if when the player hits a baby zombie with a health potion so we can then spawn a Mini Player
     * @param event the currentEvent
     */
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onAttack(AttackEntityEvent event) {
        if (event.target instanceof EntityZombie) {
            EntityZombie entityZombie = (EntityZombie) event.target;
            //Is target viable
            if (entityZombie.isChild() && !entityZombie.isConverting() && entityZombie.isPotionActive(Potion.weakness) && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion) {
                List<PotionEffect> potionEffects = PotionHelper.getPotionEffects(event.entityPlayer.getCurrentEquippedItem().getItemDamage(), false);
                for (PotionEffect effect : potionEffects) {
                    if (effect.getPotionID() == Potion.heal.getId()) {
                        event.setCanceled(true);
                        event.entityPlayer.setCurrentItemOrArmor(0, null);

                        //Create the mini player and copy some data
                        EntityMiniPlayer miniPlayer = new EntityMiniPlayer(event.entityPlayer.worldObj);
                        miniPlayer.copyLocationAndAnglesFrom(entityZombie);
                        miniPlayer.setCustomNameTag(entityZombie.getCustomNameTag());
                        miniPlayer.func_152115_b(event.entityPlayer.getUniqueID().toString());
                        miniPlayer.setTamed(true);
                        for (int i = 0; i < 5; i++) {
                            miniPlayer.setCurrentItemOrArmor(i, entityZombie.getEquipmentInSlot(i));
                        }

                        //Spawn them in
                        event.entityPlayer.worldObj.removeEntity(entityZombie);
                        event.entityPlayer.worldObj.spawnEntityInWorld(miniPlayer);

                        //Spawns a particle on the server (ie tells all nearby players about this particle)
                        //name, x, y, z, particleCount, areaAlongXAxisToDisperse, areaAlongYAxisToDisperse, areaAlongZAxisToDisperse, ?
                        //Final 4 params seem to define an area based around x, y, z where the particles will randomly spawn
                        MinecraftServer.getServer().worldServerForDimension(event.entityPlayer.worldObj.provider.dimensionId).func_147487_a("heart", miniPlayer.posX + (miniPlayer.width / 2), miniPlayer.posY + miniPlayer.height, miniPlayer.posZ + (miniPlayer.width / 2) - miniPlayer.width, 10, 0.5, 1, 0.5, 0);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T, E> T getEntityAITask(EntityLiving entityLiving, Class<? super E> taskClass) {
        List<EntityAITasks.EntityAITaskEntry> tasks = entityLiving.tasks.taskEntries;
        for (EntityAITasks.EntityAITaskEntry taskEntry : tasks) {
            if (taskEntry.action.getClass() == taskClass) {
                return (T) taskEntry.action;
            }
        }
        return null;
    }

    private MovingObjectPosition getMouseOver(double distance) {
        MovingObjectPosition objectMouseOver = null;
        if ((Minecraft.getMinecraft().renderViewEntity != null) && (Minecraft.getMinecraft().theWorld != null)) {
            Entity pointedEntity = null;
            objectMouseOver = Minecraft.getMinecraft().renderViewEntity.rayTrace(distance, 1);
            Vec3 vec3 = Minecraft.getMinecraft().renderViewEntity.getPosition(1);

            if (objectMouseOver != null) distance = objectMouseOver.hitVec.distanceTo(vec3);

            Vec3 vec31 = Minecraft.getMinecraft().renderViewEntity.getLook(1);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
            Vec3 vec33 = null;
            List list = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(Minecraft.getMinecraft().renderViewEntity, Minecraft.getMinecraft().renderViewEntity.boundingBox.addCoord(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance).expand(1, 1, 1));
            double d2 = distance;

            for (Object aList : list) {
                Entity entity = (Entity) aList;

                if (entity.canBeCollidedWith()) {
                    float f2 = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double) f2, (double) f2, (double) f2);
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                    if (axisalignedbb.isVecInside(vec3)) {
                        if (0.0D < d2 || d2 == 0.0D) {
                            pointedEntity = entity;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (movingobjectposition != null) {
                        double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity == Minecraft.getMinecraft().renderViewEntity.ridingEntity && !entity.canRiderInteract()) {
                                if (d2 == 0.0D) {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
            }

            if (pointedEntity != null && (d2 < distance || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
        }
        return objectMouseOver;
    }
}
