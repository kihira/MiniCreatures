/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class EntityAICollect extends EntityAIBase implements IRole {

    private final EntityMiniPlayer miniPlayer;
    private final float collectRadius;
    private final float searchRadius;
    private boolean remove = false;

    private static final IEntitySelector itemSelector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return !entity.isDead && ((EntityItem) entity).delayBeforeCanPickup <= 0;
        }
    };

    public EntityAICollect(EntityMiniPlayer miniPlayer, float collectRadius, float searchRadius) {
        this.miniPlayer = miniPlayer;
        this.collectRadius = collectRadius;
        this.searchRadius = searchRadius;
    }

    @Override
    public boolean shouldExecute() {
        return !remove && !miniPlayer.isDead;
    }

    @Override
    public void startExecuting() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateTask() {
        List<Entity> entities = miniPlayer.worldObj.selectEntitiesWithinAABB(EntityItem.class, miniPlayer.boundingBox.expand(collectRadius, 1, collectRadius), itemSelector);
        if (entities != null && entities.size() > 0) {
            for (Entity entity : entities) {
                EntityItem entityItem = (EntityItem) entity;
                int[] oreIDs = OreDictionary.getOreIDs(entityItem.getEntityItem());
                if (oreIDs.length > 0) {
                    for (int oreID : oreIDs) {
                        String oreName = OreDictionary.getOreName(oreID);
                        if (oreName.startsWith("gem") || oreName.startsWith("ore")) {
                            ItemStack itemStack = entityItem.getEntityItem();
                            int stackSize = itemStack.stackSize;
                            //Collect
                            if (addItemStackToInventory(itemStack)) {
                                miniPlayer.worldObj.playSoundAtEntity(miniPlayer, "random.pop", 0.2F, ((miniPlayer.getRNG().nextFloat() - miniPlayer.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                                miniPlayer.onItemPickup(entityItem, stackSize);

                                if (itemStack.stackSize <= 0) {
                                    entityItem.setDead();
                                }
                            }
                        }
                    }
                }
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

    public boolean addItemStackToInventory(ItemStack itemStackToAdd) {
        if (itemStackToAdd != null && itemStackToAdd.stackSize != 0 && itemStackToAdd.getItem() != null) {
            IInventory inventory = miniPlayer.getInventory();
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
