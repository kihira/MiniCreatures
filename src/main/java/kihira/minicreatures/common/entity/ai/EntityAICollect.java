/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EntityAICollect<T extends EntityTameable & IMiniCreature> extends EntityAIBase implements IRole {

    private final T entity;
    private final float collectRadius;
    private final float searchRadius;
    private boolean remove = false;

    private static final Predicate<EntityItem> CAN_BE_PICKED = input -> input!= null && !input.isDead && input.cannotPickup();

    public EntityAICollect(T entity, float collectRadius, float searchRadius) {
        this.entity = entity;
        this.collectRadius = collectRadius;
        this.searchRadius = searchRadius;
        //setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        return !remove && !entity.isDead && entity.getOwner() != null && entity.getInventory() != null;
    }

    @Override
    public void startExecuting() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateTask() {
        List<EntityItem> entityItems = getEntityItemsInRadius(collectRadius, 1);
        for (EntityItem entityItem : entityItems) {
            ItemStack itemStack = entityItem.getItem();
            //Collect
            if (addItemStackToInventory(itemStack)) {
                entity.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 0.2F, ((entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entity.onItemPickup(entityItem, itemStack.getCount());
            }
        }
        if (searchRadius > 0 && entity.getNavigator().noPath() && entity.world.getTotalWorldTime() % 20 == 0) {
            entityItems = getEntityItemsInRadius(searchRadius, 1);
            EntityItem nearestItem = null;
            for (EntityItem entityItem : entityItems) {
                //We want the closest item to the mini creature
                if (nearestItem == null || (nearestItem.getDistanceSq(entity) > entityItem.getDistanceSq(entity) &&
                        entityItem.getDistanceSq(entity.getOwner()) < (searchRadius * 2F))) {
                    nearestItem = entityItem;
                }
            }
            if (nearestItem != null) {
                entity.getNavigator().tryMoveToEntityLiving(nearestItem, 1.2F);
            }
        }
    }

    @Override
    public void resetTask() {
        remove = false;
    }

    @Override
    public void markForRemoval() {
        remove = true;
    }

    private List<EntityItem> getEntityItemsInRadius(float radius, float yRadius) {
        List<EntityItem> items = entity.world.getEntitiesWithinAABB(EntityItem.class, entity.getEntityBoundingBox().expand(radius, yRadius, radius), CAN_BE_PICKED::test);
        List<EntityItem> validItems = new ArrayList<>();
        if (items.size() > 0) {
            for (Entity entity : items) {
                EntityItem entityItem = (EntityItem) entity;
                int[] oreIDs = OreDictionary.getOreIDs(entityItem.getItem());
                if (oreIDs.length > 0) {
                    for (int oreID : oreIDs) {
                        String oreName = OreDictionary.getOreName(oreID);
                        if (oreName.startsWith("gem") || oreName.startsWith("ore")) {
                            validItems.add(entityItem);
                        }
                    }
                }
            }
        }
        return validItems;
    }

    private boolean addItemStackToInventory(ItemStack itemStackToAdd) {
        if (!itemStackToAdd.isEmpty()) {
            IInventory inventory = entity.getInventory();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (itemStack.isEmpty() && inventory.isItemValidForSlot(i, itemStackToAdd)) {
                    inventory.setInventorySlotContents(i, itemStackToAdd.copy());
                    itemStackToAdd.setCount(0);
                    return true;
                }
                else if (!itemStack.isEmpty()) {
                    int stackSize = itemStackToAdd.getCount();

                    //Check if itemstacks can merge
                    if (itemStack.isItemEqual(itemStackToAdd) && itemStack.getCount() < itemStack.getMaxStackSize() && itemStack.getCount() < inventory.getInventoryStackLimit() && ItemStack.areItemStackTagsEqual(itemStack, itemStackToAdd)) {
                        int k = stackSize;

                        if (i > itemStack.getMaxStackSize() - itemStack.getCount()) {
                            k = itemStack.getMaxStackSize() - itemStack.getCount();
                        }

                        if (k > inventory.getInventoryStackLimit() - itemStack.getCount()) {
                            k = inventory.getInventoryStackLimit() - itemStack.getCount();
                        }

                        if (k > 0) {
                            stackSize -= k;
                            itemStack.grow(k);
                        }
                        itemStackToAdd.setCount(stackSize);

                        return true;
                    }
                }
            }
        }
        return false;
    }
}
