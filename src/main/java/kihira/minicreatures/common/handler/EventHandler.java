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
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class EventHandler {

    public static String[] names;

    private long lastTrigger = 0;

    //TODO Overhaul this? Make it so it can be calculated server side
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void leftClick(MouseEvent interactEvent) {
        //Only trigger if it has been more then 5 seconds
        if ((System.currentTimeMillis() - this.lastTrigger > 5000) && Minecraft.getMinecraft().gameSettings.keyBindAttack.isPressed()) {
            //Only if it's a sword
            if (Minecraft.getMinecraft().thePlayer.getHeldItem(EnumHand.MAIN_HAND) != null && Minecraft.getMinecraft().thePlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
                RayTraceResult target = getMouseOver(20);
                if (target != null && target.typeOfHit == RayTraceResult.Type.ENTITY) {
                    this.lastTrigger = System.currentTimeMillis();
                    MiniCreatures.proxy.simpleNetworkWrapper.sendToServer(new SetAttackTargetMessage(target.entityHit.getEntityId()));
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityEvent.EntityConstructing event) {
        if (event.getEntity().worldObj != null) {
            Random random = event.getEntity().worldObj.rand;
            if (names != null && MiniCreatures.randomNameChance != 0 && event.getEntity() instanceof IMiniCreature && random.nextInt(MiniCreatures.randomNameChance) == 0) {
                EntityLiving entityLiving = ((IMiniCreature) event.getEntity()).getEntity();
                entityLiving.setCustomNameTag(names[random.nextInt(names.length - 1)]);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //Used to start making nearby mini players prospect
        if (!(event.getEntityPlayer() instanceof FakePlayer) && event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND) != null
                && event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemPickaxe &&
                event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.STONE && event.getEntityPlayer().isSneaking()) {
            int radius = 5;
            EntityPlayer player = event.getEntityPlayer();
            List<EntityMiniPlayer> list = event.getWorld().getEntitiesWithinAABB(EntityMiniPlayer.class, new AxisAlignedBB(
                    player.posX - radius, player.posY - radius, player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius));

            if (list.size() > 0) {
                for (EntityMiniPlayer miniPlayer : list) {
                    if (miniPlayer.getOwner() == player && miniPlayer.getRole() == EnumRole.MINER) {
                        EntityAIProspect prospectAI = getEntityAITask(miniPlayer, EntityAIProspect.class);
                        if (prospectAI != null) {
                            prospectAI.prospect = true;
                            prospectAI.target = event.getPos();
                            event.setCanceled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        //We do this render here as if the player right clicks with a lead, entity.interact never fires
        if (event.getEntityPlayer().getActiveItemStack() != null && event.getEntityPlayer().getActiveItemStack().getItem() == Items.LEAD) {
            //Mount the fox
            if (event.getTarget() instanceof EntityFox) {
                EntityFox entityFox = (EntityFox) event.getTarget();
                if (!entityFox.isBeingRidden() && !entityFox.isRiding()) {
                    double d0 = 7.0D;
                    List list = entityFox.worldObj.getEntitiesWithinAABB(EntityMiniPlayer.class, new AxisAlignedBB(entityFox.posX - d0, entityFox.posY - d0, entityFox.posZ - d0, entityFox.posX + d0, entityFox.posY + d0, entityFox.posZ + d0));

                    for (Object aList : list) {
                        EntityLiving entityliving = (EntityLiving) aList;
                        if (entityliving instanceof EntityMiniPlayer && entityliving.getLeashed() && entityliving.getLeashedToEntity() == event.getEntityPlayer()) {
                            entityliving.setLeashedToEntity(null, true);
                            entityliving.startRiding(entityFox);
                            event.setCanceled(true);
                            break;
                        }
                    }
                }
            }
            //Unmount from fox
            else if (event.getTarget() instanceof EntityMiniPlayer) {
                EntityMiniPlayer entityMiniPlayer = (EntityMiniPlayer) event.getTarget();
                if (entityMiniPlayer.isRiding()) {
                    entityMiniPlayer.dismountRidingEntity();
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
        if (event.getTarget() instanceof EntityZombie) {
            EntityZombie entityZombie = (EntityZombie) event.getTarget();
            EntityPlayer player = event.getEntityPlayer();
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            //Is target viable
            if (entityZombie.isChild() && !entityZombie.isConverting() && entityZombie.isPotionActive(MobEffects.WEAKNESS)
                    && heldItem != null && heldItem.getItem() instanceof ItemPotion) {
                List<PotionEffect> potionEffects = PotionUtils.getEffectsFromStack(heldItem);
                // todo optimise. do we need to loop here?
                for (PotionEffect effect : potionEffects) {
                    if (effect.getPotion() == MobEffects.INSTANT_HEALTH) {
                        event.setCanceled(true);
                        event.getEntityPlayer().setHeldItem(EnumHand.MAIN_HAND, null);

                        //Create the mini player and copy some data
                        EntityMiniPlayer miniPlayer = new EntityMiniPlayer(player.worldObj);
                        miniPlayer.copyLocationAndAnglesFrom(entityZombie);
                        miniPlayer.setCustomNameTag(entityZombie.getCustomNameTag());
                        miniPlayer.setOwnerId(player.getUniqueID());
                        miniPlayer.setTamed(true);

                        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                            miniPlayer.setItemStackToSlot(slot, entityZombie.getItemStackFromSlot(slot));
                        }

                        //Spawn them in
                        player.worldObj.removeEntity(entityZombie);
                        player.worldObj.spawnEntityInWorld(miniPlayer);

                        FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(event.getEntityPlayer().worldObj.provider.getDimension())
                                .spawnParticle(EnumParticleTypes.HEART, miniPlayer.posX + (miniPlayer.width / 2), miniPlayer.posY + miniPlayer.height, miniPlayer.posZ + (miniPlayer.width / 2) - miniPlayer.width, 10, 0.5, 1, 0.5, 0);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T, E> T getEntityAITask(EntityLiving entityLiving, Class<? super E> taskClass) {
        Set<EntityAITasks.EntityAITaskEntry> tasks = entityLiving.tasks.taskEntries;
        for (EntityAITasks.EntityAITaskEntry taskEntry : tasks) {
            if (taskEntry.action.getClass() == taskClass) {
                return (T) taskEntry.action;
            }
        }
        return null;
    }

    private RayTraceResult getMouseOver(double distance) {
        RayTraceResult objectMouseOver = null;
        if (Minecraft.getMinecraft().theWorld != null) {
            Entity pointedEntity = null;
            Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
            objectMouseOver = renderViewEntity.rayTrace(distance, 1);
            Vec3d posVec = renderViewEntity.getPositionVector();

            if (objectMouseOver != null) distance = objectMouseOver.hitVec.distanceTo(posVec);

            Vec3d lookVec = renderViewEntity.getLook(1);
            Vec3d targetVec = posVec.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
            Vec3d resultVec = null;
            List list = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(renderViewEntity,
                    renderViewEntity.getEntityBoundingBox().addCoord(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance).expand(1, 1, 1));
            double d2 = distance;

            for (Object aList : list) {
                Entity entity = (Entity) aList;

                if (entity.canBeCollidedWith()) {
                    float f2 = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double) f2, (double) f2, (double) f2);
                    RayTraceResult rayResult = axisalignedbb.calculateIntercept(posVec, targetVec);

                    if (axisalignedbb.isVecInside(posVec)) {
                        if (0.0D < d2 || d2 == 0.0D) {
                            pointedEntity = entity;
                            resultVec = rayResult == null ? posVec : rayResult.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (rayResult != null) {
                        double d3 = posVec.distanceTo(rayResult.hitVec);

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity == renderViewEntity.getRidingEntity() && !entity.canRiderInteract()) {
                                if (d2 == 0.0D) {
                                    pointedEntity = entity;
                                    resultVec = rayResult.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                resultVec = rayResult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
            }

            if (pointedEntity != null && (d2 < distance || objectMouseOver == null)) {
                objectMouseOver = new RayTraceResult(pointedEntity, resultVec);
            }
        }
        return objectMouseOver;
    }
}
