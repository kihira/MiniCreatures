/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class EntityAIUsePotion extends EntityAIBase implements IRole {

    private final IMiniCreature miniCreature;
    private final float healthThreshold;
    private final int damageThreshold;
    private final int cooldown;
    private int ticksToNextPotion;
    private boolean remove = false;

    public EntityAIUsePotion(IMiniCreature miniCreature, float healthThreshold, int damageThreshold, int cooldown) {
        this.miniCreature = miniCreature;
        this.healthThreshold = healthThreshold;
        this.damageThreshold = damageThreshold;
        this.cooldown = cooldown;
    }

    @Override
    public boolean shouldExecute() {
        return !remove && !miniCreature.getEntity().isDead;
    }

    @Override
    public void updateTask() {
        ticksToNextPotion = ticksToNextPotion <= 0 ? 0 : ticksToNextPotion - 1;

        if (ticksToNextPotion == 0) {
            EntityLiving entity = miniCreature.getEntity();
            IInventory inventory = miniCreature.getInventory();
            //Fire resistance
            if (entity.isBurning() && !entity.isPotionActive(Potion.fireResistance)) {
                applyPotionEffectIfFound(inventory, entity, 12);
            }
            //Heal/regen
            if (entity.getHealth() / entity.getMaxHealth() <= healthThreshold) {
                //If regen is active, just apply healing. Otherwise search for regen too
                if (entity.isPotionActive(Potion.regeneration)) {
                    applyPotionEffectIfFound(inventory, entity, 6);
                }
                else {
                    applyPotionEffectIfFound(inventory, entity, 6, 10);
                }
            }
            //Water breathing
            if (entity.getAir() < 50 && !entity.isPotionActive(Potion.waterBreathing)) {
                entity.setAir(300);
                applyPotionEffectIfFound(inventory, entity, 13);
            }
            //Only apply resistance if attack attacked within the last second and has lost more then one heart of damage . Should prevent accidental casting.
            if ((entity.getLastAttackerTime() < 20) && (entity.getMaxHealth() - entity.getHealth() >= damageThreshold) && !entity.isPotionActive(Potion.resistance)) {
                applyPotionEffectIfFound(inventory, entity, 11);
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
                        entity.addPotionEffect(new PotionEffect(effect));
                        entity.playSound("random.drink", 0.5F, entity.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                        inventory.setInventorySlotContents(i, null);
                        ticksToNextPotion = cooldown;
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) break;
        }
    }
}
