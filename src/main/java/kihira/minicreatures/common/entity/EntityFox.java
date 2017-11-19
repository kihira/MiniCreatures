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

package kihira.minicreatures.common.entity;

import kihira.minicreatures.common.entity.ai.EntityAIHappy;
import kihira.minicreatures.common.entity.ai.EntityAIHeal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityFox extends EntityMiniCreature implements IMiniCreature {

    private static final DataParameter<String> PARTS_LIST = EntityDataManager.createKey(EntityMiniCreature.class, DataSerializers.STRING);

    public EntityFox(World par1World) {
        super(par1World, Items.BONE);
        this.setSize(0.5F, 0.5F);
        this.setTamed(false);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(PARTS_LIST, ""); //Parts list
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2d);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit = new EntityAISit(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(4, new EntityAIHeal(this, 200, 1));
        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(5, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(8, new EntityAIHappy(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(4, new EntityAITargetNonTamed<>(this, EntityChicken.class, false, null));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        //Happy
        if (this.world.isRemote && this.getDataManager().get(IS_HAPPY) && this.rand.nextInt(5) == 0) {
            double x = this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width;
            double y = this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height);
            double z = this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double)this.width;
            this.world.spawnParticle(EnumParticleTypes.HEART, x, y, z, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);

        //Save parts list
        NBTTagList nbttaglist = new NBTTagList();
        for (String part : this.getDataManager().get(PARTS_LIST).split(",")) {
            if (part != null) nbttaglist.appendTag(new NBTTagString(part));
        }
        tag.setTag("Parts", nbttaglist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);

        //Load parts list
        NBTTagList tagList = tag.getTagList("Parts", 8);
        String s = "";
        for (int i = 0; i < tagList.tagCount(); i++) {
            s += tagList.getStringTagAt(i) + ",";
        }
        this.getDataManager().set(PARTS_LIST, s);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() instanceof ItemFood);
    }

    @Override
    public double getMountedYOffset() {
        return isSitting() ? -0.3d : -0.2d ;
    }

    // todo mini player rotations are being very very weird. This method probably isn't the place to solve it, must be caused by something
    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float sin = MathHelper.sin(this.renderYawOffset * 0.017453292F);
            float cos = MathHelper.cos(this.renderYawOffset * 0.017453292F);
            float offset = 0.1F;
            passenger.setPosition(this.posX + (double)(offset * sin), this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ - (double)(offset * cos));

            if (passenger instanceof EntityLivingBase) {
                ((EntityLivingBase)passenger).renderYawOffset = this.renderYawOffset;
            }
        }
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 4;
    }

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WOLF_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    @Override
    protected float getSoundPitch() {
        return this.rand.nextFloat() - this.rand.nextFloat() * 0.3F + 1.5F;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected Item getDropItem() {
        return Items.LEATHER;
    }

    @Override
    protected void dropFewItems(boolean hitByPlayerRecently, int lootingLevel) {
        int j = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingLevel);

        for (int k = 0; k < j; ++k) {
            this.dropItem(Items.LEATHER, 1);
        }
    }

    @Override
    public EntityFox createChild(EntityAgeable entityageable) {
        EntityFox entityFox = new EntityFox(this.world);
        UUID uuid = this.getOwnerId(); //Get owner UUID

        if (uuid != null) {
            entityFox.setOwnerId(uuid);
            entityFox.setTamed(true);
        }
        return entityFox;
    }
}
