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

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMiniShark extends EntityWaterMob {

    private double waypointX;
    private double waypointY;
    private double waypointZ;
    private EntityPlayer targetedEntity;
    private int attackTick;

    public EntityMiniShark(World par1World) {
        super(par1World);
        this.setSize(0.95F, 0.95F);
        this.getNavigator().setAvoidsWater(false);
        this.getNavigator().setCanSwim(true);
        this.renderDistanceWeight = 4D;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }

    @Override
    public boolean isAIEnabled() {
        return false;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected void updateEntityActionState() {
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            this.setDead();
        }

        if (this.attackTick > 0) this.attackTick--;

        double d0 = this.waypointX - this.posX;
        double d1 = this.waypointY - this.posY;
        double d2 = this.waypointZ - this.posZ;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;

        if ((d3 < 1D || d3 > 3600D) && this.rand.nextInt() > 30) {
            this.waypointX = this.posX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16F);
            this.waypointY = this.posY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 2F);
            this.waypointZ = this.posZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16F);

            if (!(this.worldObj.getBlock((int) this.waypointX, (int) this.waypointY, (int) this.waypointZ) instanceof BlockLiquid)) {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        d3 = (double) MathHelper.sqrt_double(d3);

        if (this.isCourseTraversable(d3)) {
            this.motionX += d0 / d3 * 0.02D;
            this.motionY += d1 / d3 * 0.02D;
            this.motionZ += d2 / d3 * 0.02D;
        }
        else {
            this.waypointX = this.posX;
            this.waypointY = this.posY;
            this.waypointZ = this.posZ;
        }

        if (this.targetedEntity != null && this.targetedEntity.isDead) this.targetedEntity = null;
        if (this.targetedEntity == null) this.targetedEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 64D);
        if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < 4096D) {
            double d5 = this.targetedEntity.posX - this.posX;
            double d7 = this.targetedEntity.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(d5, d7)) * 180F / (float)Math.PI;

            if (this.canEntityBeSeen(this.targetedEntity) && this.targetedEntity.isInWater()) {
                this.waypointX = this.targetedEntity.posX;
                this.waypointY = this.targetedEntity.posY;
                this.waypointZ = this.targetedEntity.posZ;

                double d9 = this.getDistanceSq(this.targetedEntity.posX, this.targetedEntity.boundingBox.minY, this.targetedEntity.posZ);
                double d10 = (double)(this.width * 2.0F * this.width * 2.0F + this.targetedEntity.width);
                if (d9 <= d10 && this.attackTick <= 20) {
                    this.attackTick = 20;
                    this.attackEntityAsMob(this.targetedEntity);
                }
            }
        }
        else {
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180F / (float)Math.PI;
        }
    }

    private boolean isCourseTraversable(double par7) {
        double d4 = (this.waypointX - this.posX) / par7;
        double d5 = (this.waypointY - this.posY) / par7;
        double d6 = (this.waypointZ - this.posZ) / par7;
        AxisAlignedBB axisalignedbb = this.boundingBox.copy();

        for (int i = 1; (double)i < par7; ++i) {
            axisalignedbb.offset(d4, d5, d6);
            if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty()) return false;
        }

        return true;
    }

    @Override
    public void moveEntityWithHeading(float par1, float par2) {
        if (this.isInWater()) {
            float f2 = 0.91F;
            float f3 = 0.16277136F / (f2 * f2 * f2);
            this.moveFlying(par1, par2, this.onGround ? 0.1F * f3 : 0.02F);
            f2 = 0.91F;

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)f2;
            this.motionY *= (double)f2;
            this.motionZ *= (double)f2;
        }
        else if (this.handleLavaMovement()) {
            this.moveFlying(par1, par2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        }
        else {
            //this.moveFlying(par1, par2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY = -0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
        }
    }

    @Override
    public boolean isInWater() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this);
    }
}
