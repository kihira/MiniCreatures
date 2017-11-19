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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class EventHandler {

    public static String[] names;

    private long lastTrigger = 0;

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        if (MiniCreatures.enableCustomizer) event.getRegistry().register(MiniCreatures.itemCustomizer);
        if (MiniCreatures.enableMindControl) event.getRegistry().register(MiniCreatures.itemMindControlHelmet);
    }

    //TODO Overhaul this? Make it so it can be calculated server side
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void leftClick(MouseEvent interactEvent) {
        //Only trigger if it has been more then 5 seconds
        if ((System.currentTimeMillis() - this.lastTrigger > 5000) && Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown()) {
            //Only if it's a sword
            if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemSword) {
                Minecraft.getMinecraft().entityRenderer.getMouseOver(1);
                if (Minecraft.getMinecraft().pointedEntity != null) {
                    this.lastTrigger = System.currentTimeMillis();
                    MiniCreatures.proxy.simpleNetworkWrapper.sendToServer(new SetAttackTargetMessage(Minecraft.getMinecraft().pointedEntity.getEntityId()));
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityEvent.EntityConstructing event) {
        if (event.getEntity().world != null) {
            Random random = event.getEntity().world.rand;
            if (names != null && MiniCreatures.randomNameChance != 0 && event.getEntity() instanceof IMiniCreature && random.nextInt(MiniCreatures.randomNameChance) == 0) {
                EntityLiving entityLiving = ((IMiniCreature) event.getEntity()).getEntity();
                entityLiving.setCustomNameTag(names[random.nextInt(names.length - 1)]);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //Used to start making nearby mini players prospect
        if (!(event.getEntityPlayer() instanceof FakePlayer)
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
    public void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        //We do this render here as if the player right clicks with a lead, entity.interact never fires
        if (event.getEntityPlayer().getHeldItemMainhand().getItem() == Items.LEAD) {
            //Mount the fox
            if (event.getTarget() instanceof EntityFox) {
                EntityFox entityFox = (EntityFox) event.getTarget();
                if (!entityFox.isBeingRidden() && !entityFox.isRiding() && entityFox.getOwner() == event.getEntityPlayer()) {
                    double dist = 7.0D;
                    List list = entityFox.world.getEntitiesWithinAABB(EntityMiniPlayer.class, entityFox.getEntityBoundingBox().grow(dist));

                    for (Object aList : list) {
                        EntityLiving entityliving = (EntityLiving) aList;
                        if (entityliving instanceof EntityMiniPlayer && entityliving.getLeashed() && entityliving.getLeashHolder() == event.getEntityPlayer()) {
                            entityliving.clearLeashed(true, true);
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
    public void onAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityZombieVillager) {
            EntityZombieVillager entityZombie = (EntityZombieVillager) event.getTarget();
            EntityPlayer player = event.getEntityPlayer();
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            //Is target viable
            if (entityZombie.isChild() && !entityZombie.isConverting() && entityZombie.isPotionActive(MobEffects.WEAKNESS)
                    && heldItem.getItem() instanceof ItemPotion) {
                List<PotionEffect> potionEffects = PotionUtils.getEffectsFromStack(heldItem);
                // todo optimise. do we need to loop here?
                for (PotionEffect effect : potionEffects) {
                    if (effect.getPotion() == MobEffects.INSTANT_HEALTH) {
                        event.setCanceled(true);
                        heldItem.shrink(1);

                        //Create the mini player and copy some data
                        EntityMiniPlayer miniPlayer = new EntityMiniPlayer(player.world);
                        miniPlayer.copyLocationAndAnglesFrom(entityZombie);
                        miniPlayer.setCustomNameTag(entityZombie.getCustomNameTag());
                        miniPlayer.setOwnerId(player.getUniqueID());
                        miniPlayer.setTamed(true);

                        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                            miniPlayer.setItemStackToSlot(slot, entityZombie.getItemStackFromSlot(slot));
                        }

                        //Spawn them in
                        player.world.removeEntity(entityZombie);
                        player.world.spawnEntity(miniPlayer);

                        FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(event.getEntityPlayer().world.provider.getDimension())
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
}
