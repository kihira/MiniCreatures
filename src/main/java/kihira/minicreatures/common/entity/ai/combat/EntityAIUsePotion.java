/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai.combat;

import cpw.mods.fml.common.network.NetworkRegistry;
import kihira.foxlib.common.EntityHelper;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.ai.IRole;
import kihira.minicreatures.common.network.ItemUseMessage;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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
            if (miniPlayer.getHeldItem() != null && miniPlayer.getHeldItem().getItem() instanceof ItemPotion) {
                ItemStack heldItemStack = miniPlayer.getHeldItem();
                //Throw splashes
                if (ItemPotion.isSplash(heldItemStack.getItemDamage())) {
                    miniPlayer.worldObj.playSoundAtEntity(miniPlayer, "random.bow", 0.5F, 0.4F / (miniPlayer.getRNG().nextFloat() * 0.4F + 0.8F));

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
            if (miniPlayer.isBurning() && !miniPlayer.isPotionActive(Potion.fireResistance)) {
                applyPotionEffectIfFound(inventory, miniPlayer, 12);
            }
            //Heal/regen
            if (miniPlayer.getHealth() / miniPlayer.getMaxHealth() <= healthThreshold) {
                //If regen is active, just apply healing. Otherwise search for regen too
                if (miniPlayer.isPotionActive(Potion.regeneration)) {
                    applyPotionEffectIfFound(inventory, miniPlayer, 6);
                }
                else {
                    applyPotionEffectIfFound(inventory, miniPlayer, 6, 10);
                }
            }
            //Water breathing
            if (miniPlayer.getAir() < 50 && !miniPlayer.isPotionActive(Potion.waterBreathing)) {
                miniPlayer.setAir(300);
                applyPotionEffectIfFound(inventory, miniPlayer, 13);
            }
            //Only apply resistance if attack attacked within the last second and has lost more then one heart of damage . Should prevent accidental casting.
            if ((miniPlayer.getLastAttackerTime() < 20) && (miniPlayer.getMaxHealth() - miniPlayer.getHealth() >= damageThreshold) && !miniPlayer.isPotionActive(Potion.resistance)) {
                applyPotionEffectIfFound(inventory, miniPlayer, 11);
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
    private void applyPotionEffectIfFound(IInventory inventory, EntityLiving entity, int ... potionIDs) {
        boolean flag = false;
        Arrays.sort(potionIDs);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemPotion) {
                List<PotionEffect> potionEffects = ((ItemPotion) itemStack.getItem()).getEffects(itemStack);
                for (PotionEffect effect : potionEffects) {
                    if (ArrayUtils.contains(potionIDs, effect.getPotionID())) {
                        //Apply all effects
                        for (PotionEffect effect1 : potionEffects) {
                            entity.addPotionEffect(new PotionEffect(effect1));
                        }

                        ticksToNextPotion = itemStack.getMaxItemUseDuration() + cooldown;
                        inventory.setInventorySlotContents(i, miniPlayer.getHeldItem()); //Put away current item
                        miniPlayer.setCurrentItemOrArmor(0, itemStack);
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
