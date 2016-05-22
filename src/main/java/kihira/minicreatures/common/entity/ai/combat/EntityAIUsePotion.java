/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai.combat;

import kihira.foxlib.common.EntityHelper;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.ai.IRole;
import kihira.minicreatures.common.network.ItemUseMessage;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class EntityAIUsePotion extends EntityAIBase implements IRole {

    private final EntityMiniPlayer miniPlayer;
    private final float healthThreshold;
    private final int damageThreshold;
    private final int cooldown;
    private int ticksToNextPotion;
    private boolean remove = false;

    public EntityAIUsePotion(EntityMiniPlayer miniPlayer, float healthThreshold, int damageThreshold, int cooldown) {
        this.miniPlayer = miniPlayer;
        this.healthThreshold = healthThreshold;
        this.damageThreshold = damageThreshold;
        this.cooldown = cooldown;
    }

    @Override
    public boolean shouldExecute() {
        return !remove && !miniPlayer.getEntity().isDead && miniPlayer.getOwner() != null;
    }

    @Override
    public void updateTask() {
        ticksToNextPotion = ticksToNextPotion <= 0 ? 0 : ticksToNextPotion - 1;

        if (ticksToNextPotion == 0) {
            // todo support off hand
            ItemStack heldItemStack = miniPlayer.getHeldItem(EnumHand.MAIN_HAND);
            if (heldItemStack != null && heldItemStack.getItem() instanceof ItemPotion) {
                //Throw splashes
                if (heldItemStack.getItem() == Items.SPLASH_POTION) {
                    miniPlayer.playSound(SoundEvents.ENTITY_SPLASH_POTION_THROW, 0.5F, 0.4F / (miniPlayer.getRNG().nextFloat() * 0.4F + 0.8F));

                    if (!miniPlayer.worldObj.isRemote) {
                        float prevPitch = miniPlayer.rotationPitch;
                        float prevYaw = miniPlayer.rotationYaw;
                        //If owner is nearby, try to hit us both with potion
                        if (miniPlayer.getDistanceSqToEntity(miniPlayer.getOwner()) < 16F) {
                            float[] pitchYaw = EntityHelper.getPitchYawToEntity(miniPlayer, miniPlayer.getOwner());
                            miniPlayer.rotationPitch = pitchYaw[0];
                            miniPlayer.rotationYaw = pitchYaw[1];
                        }
                        else {
                            //Throw on self
                            miniPlayer.rotationPitch = 90F;
                        }

                        miniPlayer.worldObj.spawnEntityInWorld(new EntityPotion(miniPlayer.worldObj, miniPlayer, heldItemStack));

                        miniPlayer.rotationPitch = prevPitch;
                        miniPlayer.rotationYaw = prevYaw;
                    }

                    if (heldItemStack.stackSize-- <= 0) {
                        miniPlayer.setCarrying(null);
                    }
                    else miniPlayer.setCarrying(heldItemStack);
                }
                else {
                    miniPlayer.setItemInUse(heldItemStack, heldItemStack.getMaxItemUseDuration());
                    MiniCreatures.proxy.simpleNetworkWrapper.sendToAllAround(new ItemUseMessage(miniPlayer.getEntityId(), heldItemStack.getMaxItemUseDuration()),
                            new NetworkRegistry.TargetPoint(miniPlayer.dimension, miniPlayer.posX, miniPlayer.posY, miniPlayer.posZ, 64));
                }
                ticksToNextPotion = heldItemStack.getMaxItemUseDuration() + cooldown;
                return;
            }

            IInventory inventory = miniPlayer.getInventory();
            //Fire resistance
            if (miniPlayer.isBurning() && !miniPlayer.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                applyPotionEffectIfFound(inventory, miniPlayer, MobEffects.FIRE_RESISTANCE);
            }
            //Heal/regen
            if (miniPlayer.getHealth() / miniPlayer.getMaxHealth() <= healthThreshold) {
                //If regen is active, just apply healing. Otherwise search for regen too
                if (miniPlayer.isPotionActive(MobEffects.REGENERATION)) {
                    applyPotionEffectIfFound(inventory, miniPlayer, MobEffects.INSTANT_HEALTH);
                }
                else {
                    applyPotionEffectIfFound(inventory, miniPlayer, MobEffects.INSTANT_HEALTH, MobEffects.REGENERATION);
                }
            }
            //Water breathing
            if (miniPlayer.getAir() < 50 && !miniPlayer.isPotionActive(MobEffects.WATER_BREATHING)) {
                miniPlayer.setAir(300);
                applyPotionEffectIfFound(inventory, miniPlayer, MobEffects.WATER_BREATHING);
            }
            //Only apply resistance if attack attacked within the last second and has lost more then one heart of damage . Should prevent accidental casting.
            if ((miniPlayer.getLastAttackerTime() < 20) && (miniPlayer.getMaxHealth() - miniPlayer.getHealth() >= damageThreshold) && !miniPlayer.isPotionActive(MobEffects.RESISTANCE)) {
                applyPotionEffectIfFound(inventory, miniPlayer, MobEffects.RESISTANCE);
            }
            //TODO sprint potion for fleeing?
        }
    }

    @Override
    public void resetTask() {
        ticksToNextPotion = 0;
        remove = false;
    }

    @Override
    public void markForRemoval() {
        remove = true;
    }

    @SuppressWarnings("unchecked")
    private void applyPotionEffectIfFound(IInventory inventory, EntityLiving entity, Potion ... potions) {
        boolean flag = false;
        Arrays.sort(potions);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemPotion) {
                List<PotionEffect> potionEffects = PotionUtils.getEffectsFromStack(itemStack);
                for (PotionEffect effect : potionEffects) {
                    if (ArrayUtils.contains(potions, effect.getPotion())) {
                        //Apply all effects
                        for (PotionEffect effect1 : potionEffects) {
                            entity.addPotionEffect(new PotionEffect(effect1));
                        }

                        ticksToNextPotion = itemStack.getMaxItemUseDuration() + cooldown;
                        // todo support off hand
                        inventory.setInventorySlotContents(i, miniPlayer.getHeldItem(EnumHand.MAIN_HAND)); //Put away current item
                        miniPlayer.setHeldItem(EnumHand.MAIN_HAND, itemStack);
                        miniPlayer.setItemInUse(itemStack, itemStack.getMaxItemUseDuration());
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) break;
        }
    }
}
