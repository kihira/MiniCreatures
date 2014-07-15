/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.minicreatures.common.entity;

import com.google.common.base.Strings;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.ai.EntityAIEscapePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityRedPanda extends EntityTameable implements IMiniCreature {

    private final IInventory inventory = new InventoryBasic(this.getCommandSenderName(), false, 18);
    private EntityAIEscapePlayer aiEscapePlayer;

    public EntityRedPanda(World par1World) {
        super(par1World);
        this.setSize(0.35f, 0.5f);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);

        this.aiEscapePlayer = new EntityAIEscapePlayer(this, 8F, 1.33D);

        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(4, this.aiEscapePlayer);
        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(5, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityChicken.class, 750, false));
        this.setTamed(false);
        this.renderDistanceWeight = 4D;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, 0); //Has chest
        this.dataWatcher.addObject(19, (byte) BlockColored.func_150032_b(1)); //Collar colour
    }

    public boolean hasChest() {
        return this.dataWatcher.getWatchableObjectInt(18) == 1;
    }

    public void setHasChest(boolean hasChest) {
        this.dataWatcher.updateObject(18, hasChest ? 1 : 0);
    }

    public boolean isOnLadder() {
        if (this.aiEscapePlayer.isClimbing) {
            this.isCollidedHorizontally = true;
        }
        return this.aiEscapePlayer.isClimbing;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (this.isTamed()) {
            if (itemstack != null) {
                if (itemstack.getItem() instanceof ItemFood) {
                    ItemFood itemfood = (ItemFood)itemstack.getItem();
                    if (itemfood.isWolfsFavoriteMeat() && this.getHealth() < this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue()) {
                        this.heal((float) itemfood.func_150905_g(itemstack));
                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        return true;
                    }
                }
                else if (Block.getBlockFromItem(itemstack.getItem()) == Blocks.chest && !this.hasChest()) {
                    this.setHasChest(true);
                    this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    return true;
                }
                else if (itemstack.getItem() == Items.dye) {
                    int i = BlockColored.func_150032_b(itemstack.getItemDamage());
                    if (i != this.getCollarColor()) {
                        this.setCollarColor(i);
                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        return true;
                    }
                }
            }
            if (!player.isSneaking() && this.hasChest()) {
                //Send Entity ID as x coord. Inspired by OpenBlocks
                player.openGui(MiniCreatures.instance, 0, player.worldObj, this.getEntityId(), 0, 0);
            }
            else if (player.isEntityEqual(this.getOwner()) && !this.worldObj.isRemote && !this.isBreedingItem(itemstack)) {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity(null);
                this.setTarget(null);
                this.setAttackTarget(null);
            }
        }
        else if (itemstack != null && itemstack.getItem() == Items.reeds) {
            if (!player.capabilities.isCreativeMode) --itemstack.stackSize;
            if (itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            if (!this.worldObj.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.setPathToEntity(null);
                    this.setAttackTarget(null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.func_152115_b(player.getUniqueID().toString());
                    this.playTameEffect(true);
                    this.worldObj.setEntityState(this, (byte)7);
                }
                else {
                    this.playTameEffect(false);
                    this.worldObj.setEntityState(this, (byte)6);
                }
            }
            return true;
        }
        return super.interact(player);
    }

    @Override
    public void moveEntity(double p_70091_1_, double p_70091_3_, double p_70091_5_) {
        if (this.noClip) {
            //System.out.println(p_70091_3_);
            this.boundingBox.offset(p_70091_1_, p_70091_3_, p_70091_5_);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double) this.yOffset - (double) this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else super.moveEntity(p_70091_1_, p_70091_3_, p_70091_5_);
    }

    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);

        //Drop chest contents on death
        if (!this.worldObj.isRemote && hasChest()) {
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = inventory.getStackInSlot(i);
                if (itemstack != null) this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);

        tag.setBoolean("HasChest", this.hasChest());
        tag.setByte("CollarColor", (byte) this.getCollarColor());
        if (this.hasChest()) {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
                if (this.inventory.getStackInSlot(i) != null) {
                    NBTTagCompound stacktag = new NBTTagCompound();
                    stacktag.setByte("Slot", (byte)i);
                    this.inventory.getStackInSlot(i).writeToNBT(stacktag);
                    nbttaglist.appendTag(stacktag);
                }
            }
            tag.setTag("Items", nbttaglist);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setHasChest(tag.getBoolean("HasChest"));

        if (this.hasChest()) {
            NBTTagList nbttaglist = tag.getTagList("Items", 0);
            for (int i = 0; i < nbttaglist.tagCount(); i++) {
                NBTTagCompound stacktag = nbttaglist.getCompoundTagAt(i);
                int j = stacktag.getByte("Slot");
                if (j >= 0 && j < this.inventory.getSizeInventory()) this.inventory.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(stacktag));
            }
        }
        if (tag.hasKey("CollarColor", 99)) this.setCollarColor(tag.getByte("CollarColor"));
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() == Items.reeds);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 8;
    }

    @Override
    protected void func_145780_a(int par1, int par2, int par3, Block par4) {
        this.playSound("mob.wolf.step", 0.15F, 1.0F);
    }

    @Override
    protected String getLivingSound() {
        return "mob.wolf.bark";
    }

    @Override
    protected String getHurtSound() {
        return "mob.wolf.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.wolf.death";
    }

    @Override
    protected float getSoundPitch() {
        return this.rand.nextFloat() - this.rand.nextFloat() * 0.3F + 1.5F;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    public int getCollarColor() {
        return this.dataWatcher.getWatchableObjectByte(19) & 15;
    }

    public void setCollarColor(int par1) {
        this.dataWatcher.updateObject(19, (byte) (par1 & 15));
    }

    @Override
    public EntityRedPanda createChild(EntityAgeable entityageable) {
        EntityRedPanda entityRedPanda = new EntityRedPanda(this.worldObj);
        String s = this.func_152113_b(); //Get owner UUID

        if (!Strings.isNullOrEmpty(s)) {
            entityRedPanda.func_152115_b(s); //Set owner UUID
            entityRedPanda.setTamed(true);
        }
        return entityRedPanda;
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        int i = this.isTamed() ? 3 : 2;
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) i);
    }

    @Override
    public boolean canMateWith(EntityAnimal entityAnimal) {
        if (entityAnimal.equals(this) || !this.isTamed() || !(entityAnimal instanceof EntityRedPanda)) {
            return false;
        }
        else {
            EntityRedPanda entityRedPanda = (EntityRedPanda) entityAnimal;
            return entityRedPanda.isTamed() && !entityRedPanda.isSitting() && this.isInLove() && entityRedPanda.isInLove();
        }
    }

    @Override
    public IInventory getInventory() {
        return this.inventory;
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }
}
