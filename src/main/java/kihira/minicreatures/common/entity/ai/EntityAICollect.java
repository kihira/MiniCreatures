/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class EntityAICollect<T extends EntityTameable & IMiniCreature> extends EntityAIBase implements IRole {

    private final T entity;
    private final float collectRadius;
    private final float searchRadius;
    private boolean remove = false;

    private static final IEntitySelector itemSelector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return !entity.isDead && ((EntityItem) entity).delayBeforeCanPickup <= 0;
        }
    };

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
            ItemStack itemStack = entityItem.getEntityItem();
            int stackSize = itemStack.stackSize;
            //Collect
            if (addItemStackToInventory(itemStack)) {
                entity.worldObj.playSoundAtEntity(entity, "random.pop", 0.2F, ((entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entity.onItemPickup(entityItem, stackSize);

                if (itemStack.stackSize <= 0) {
                    entityItem.setDead();
                }
            }
        }
        if (searchRadius > 0 && entity.getNavigator().noPath() && entity.worldObj.getTotalWorldTime() % 20 == 0) {
            entityItems = getEntityItemsInRadius(searchRadius, 1);
            EntityItem nearestItem = null;
            for (EntityItem entityItem : entityItems) {
                //We want the closest item to the mini creature
                if (nearestItem == null || (nearestItem.getDistanceSqToEntity(entity) > entityItem.getDistanceSqToEntity(entity) &&
                        entityItem.getDistanceSqToEntity(entity.getOwner()) < (searchRadius * 2F))) {
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

    @SuppressWarnings("unchecked")
    private List<EntityItem> getEntityItemsInRadius(float radius, float yRadius) {
        List<Entity> entities = entity.worldObj.selectEntitiesWithinAABB(EntityItem.class, entity.boundingBox.expand(radius, yRadius, radius), itemSelector);
        List<EntityItem> entityItems = new ArrayList<EntityItem>();
        if (entities != null && entities.size() > 0) {
            for (Entity entity : entities) {
                EntityItem entityItem = (EntityItem) entity;
                int[] oreIDs = OreDictionary.getOreIDs(entityItem.getEntityItem());
                if (oreIDs.length > 0) {
                    for (int oreID : oreIDs) {
                        String oreName = OreDictionary.getOreName(oreID);
                        if (oreName.startsWith("gem") || oreName.startsWith("ore")) {
                            entityItems.add(entityItem);
                        }
                    }
                }
            }
        }
        return entityItems;
    }

    public boolean addItemStackToInventory(ItemStack itemStackToAdd) {
        if (itemStackToAdd != null && itemStackToAdd.stackSize != 0 && itemStackToAdd.getItem() != null) {
            IInventory inventory = entity.getInventory();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack itemStack = inventory.getStackInSlot(i);
                if (itemStack == null && inventory.isItemValidForSlot(i, itemStackToAdd)) {
                    inventory.setInventorySlotContents(i, ItemStack.copyItemStack(itemStackToAdd));
                    itemStackToAdd.stackSize = 0;
                    return true;
                }
                else if (itemStack != null) {
                    int stackSize = itemStackToAdd.stackSize;

                    //Check if itemstacks can merge
                    if (itemStack.isItemEqual(itemStackToAdd) && itemStack.stackSize < itemStack.getMaxStackSize() && itemStack.stackSize < inventory.getInventoryStackLimit() && ItemStack.areItemStackTagsEqual(itemStack, itemStackToAdd)) {
                        int k = stackSize;

                        if (i > itemStack.getMaxStackSize() - itemStack.stackSize) {
                            k = itemStack.getMaxStackSize() - itemStack.stackSize;
                        }

                        if (k > inventory.getInventoryStackLimit() - itemStack.stackSize) {
                            k = inventory.getInventoryStackLimit() - itemStack.stackSize;
                        }

                        if (k > 0) {
                            stackSize -= k;
                            itemStack.stackSize += k;
                        }
                        itemStackToAdd.stackSize = stackSize;

                        return true;
                    }
                }
            }
        }
        return false;
    }
}
