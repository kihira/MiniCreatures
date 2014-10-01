/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

public class EntityAIHeal extends EntityAIBase {

    private final IMiniCreature miniCreature;
    private final int regenCooldown;
    private final int regenAmount;
    private final boolean useInventory;
    private int ticksToNextRegen;
    private int ticksToNextPotion;
    private int stackSlot;

    public EntityAIHeal(IMiniCreature miniCreature, int regenCooldown, int regenAmount, boolean useInventory) {
        this.miniCreature = miniCreature;
        this.regenCooldown = regenCooldown;
        this.regenAmount = regenAmount;
        this.useInventory = useInventory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateTask() {
        ticksToNextRegen = ticksToNextRegen <= 0 ? 0 : ticksToNextRegen - 1;
        ticksToNextPotion = ticksToNextPotion <= 0 ? 0 : ticksToNextPotion - 1;

        if (ticksToNextRegen == 0) {
            //Natural regen
            miniCreature.getEntity().heal(regenAmount);
            ticksToNextRegen = regenCooldown;
        }
        //Potion healing. Only heal on less then 50% health with this
        if (useInventory && ticksToNextPotion == 0 && miniCreature.getEntity().getHealth() / miniCreature.getEntity().getMaxHealth() <= 0.5F) {
            ItemStack itemStack = getHealingItem();
            if (itemStack != null) {
                List<PotionEffect> effects = ((ItemPotion) itemStack.getItem()).getEffects(itemStack);
                for (PotionEffect effect : effects) {
                    miniCreature.getEntity().addPotionEffect(new PotionEffect(effect));
                }
                miniCreature.getEntity().playSound("random.drink", 0.5F, miniCreature.getEntity().getRNG().nextFloat() * 0.1F + 0.9F);
                miniCreature.getInventory().setInventorySlotContents(stackSlot, null);
                ticksToNextPotion = 200; // 10 seconds
            }
        }
    }

    @Override
    public boolean shouldExecute() {
        return miniCreature.getEntity().getHealth() < miniCreature.getEntity().getMaxHealth();
    }

    @Override
    public void resetTask() {
        ticksToNextRegen = 0;
        stackSlot = -1;
    }

    @SuppressWarnings("unchecked")
    private ItemStack getHealingItem() {
        ItemStack itemStack = null;
        if (miniCreature.getInventory() != null) {
            IInventory inventory = miniCreature.getInventory();
            boolean flag = false;

            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemPotion) {
                    List<PotionEffect> effects = ((ItemPotion) stack.getItem()).getEffects(stack);
                    for (PotionEffect effect : effects) {
                        if (effect.getPotionID() == Potion.heal.getId()) {
                            itemStack = stack;
                            stackSlot = i;
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) break;
            }
        }
        return itemStack;
    }
}
