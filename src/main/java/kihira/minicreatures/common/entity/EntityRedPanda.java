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

import kihira.minicreatures.common.entity.ai.EntityAIEscapePlayer;
import kihira.minicreatures.common.entity.ai.EntityAIHappy;
import kihira.minicreatures.common.entity.ai.EntityAIHeal;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityRedPanda extends EntityMiniCreature implements IMiniCreature {

    private EntityAIEscapePlayer aiEscapePlayer;

    public EntityRedPanda(World par1World) {
        super(par1World, Items.REEDS);
        this.setSize(0.5F, 0.5F);
        this.setTamed(false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1d);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit = new EntityAISit(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(4, this.aiEscapePlayer = new EntityAIEscapePlayer(this, 8F, 1.33D));
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
    public boolean isOnLadder() {
        if (this.aiEscapePlayer.isClimbing) {
            this.isCollidedHorizontally = true;
        }
        return this.aiEscapePlayer.isClimbing;
    }

    @Override
    public void moveEntity(double x, double y, double z) {
        if (this.noClip) {
            this.getEntityBoundingBox().offset(x, y, z);
            this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
            this.posY = this.getEntityBoundingBox().minY + this.getYOffset() - (double) this.height;
            this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
        }
        else super.moveEntity(x, y, z);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() == Items.REEDS);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 8;
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

    @Override
    protected SoundEvent getHurtSound() {
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
    public EntityRedPanda createChild(EntityAgeable entityageable) {
        EntityRedPanda entityRedPanda = new EntityRedPanda(this.worldObj);
        UUID s = this.getOwnerId();

        if (s != null) {
            entityRedPanda.setOwnerId(s);
            entityRedPanda.setTamed(true);
        }
        return entityRedPanda;
    }
}
