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

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMiniShark extends EntityWaterMob {

    private double waypointX;
    private double waypointY;
    private double waypointZ;
    private EntityPlayer targetedEntity;
    private int attackTick;

    public EntityMiniShark(World par1World) {
        super(par1World);
        this.setSize(0.8F, 0.4F);
        //this.setSize(0.95F, 0.95F);
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
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), worldObj.difficultySetting.getDifficultyId() * 2);
    }


    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.attackTick > 0) this.attackTick--;

            if (this.isInWater()) {
                double xDist = this.waypointX - this.posX;
                double yDist = this.waypointY - this.posY;
                double zDist = this.waypointZ - this.posZ;
                double d3 = xDist * xDist + yDist * yDist + zDist * zDist;
                d3 = Math.sqrt(d3);

                this.motionX += xDist / d3 * (this.targetedEntity != null ? 0.02D : 0.01D);
                this.motionY += yDist / d3 * (this.targetedEntity != null ? 0.02D : 0.01D);
                this.motionZ += zDist / d3 * (this.targetedEntity != null ? 0.02D : 0.01D);
            }
            else {
                this.motionX *= 0.9D;
                this.motionY -= 0.08D;
                this.motionY *= 0.9800000190734863D;
                this.motionZ *= 0.9D;
            }

            if (this.targetedEntity != null) {
                this.faceEntity(this.targetedEntity, 5F, 5F);

                double distToTarget = this.getDistanceSq(this.targetedEntity.posX, this.targetedEntity.boundingBox.minY, this.targetedEntity.posZ);
                double d10 = (double)(this.width * 5F * this.width * 5F + this.targetedEntity.width);
                if (distToTarget <= d10 && this.attackTick <= 0) {
                    this.attackTick = 20;
                    this.attackEntityAsMob(this.targetedEntity);
                }
            }
            else {
                this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI;
            }
        }
    }

    @Override
    protected void updateEntityActionState() {
        moveStrafing = 0.0F;
        moveForward = 0.0F;
        entityAge++;

        double xDist = waypointX - posX;
        double yDist = waypointY - posY;
        double zDist = waypointZ - posZ;
        double d3 = xDist * xDist + yDist * yDist + zDist * zDist;

        if (this.isInWater()) {
            if (this.targetedEntity == null && ((d3 < 4D || d3 > 3600D) || !isCourseTraversable(d3))) {
                for (int i = 0; i < 3; i++) {
                    double targetX = posX + MathHelper.getRandomDoubleInRange(rand, -8F, 8F);
                    double targetY = posY + MathHelper.getRandomDoubleInRange(rand, -1F, 1F);
                    double targetZ = posZ + MathHelper.getRandomDoubleInRange(rand, -8F, 8F);

                    if ((worldObj.getBlock((int) targetX, (int) targetY, (int) targetZ).getMaterial() == Material.water) && isCourseTraversable(MathHelper.sqrt_double(d3))) {
                        waypointX = targetX;
                        waypointY = targetY;
                        waypointZ = targetZ;
                        break;
                    }
                    else {
                        resetWaypoints();
                    }
                }
            }
        }

        if (targetedEntity != null && (targetedEntity.isDead || !targetedEntity.isInWater() || this.targetedEntity.getDistanceSqToEntity(this) < 4096D || !canEntityBeSeen(this.targetedEntity))) {
            targetedEntity = null;
        }
        if (targetedEntity == null) targetedEntity = worldObj.getClosestVulnerablePlayerToEntity(this, 64D);
        if (targetedEntity != null) {
            this.waypointX = this.targetedEntity.posX;
            this.waypointY = this.targetedEntity.posY;
            this.waypointZ = this.targetedEntity.posZ;
        }

        despawnEntity();
    }

    private void resetWaypoints() {
        this.waypointX = this.posX;
        this.waypointY = this.posY;
        this.waypointZ = this.posZ;
    }

    private boolean isCourseTraversable(double dist) {
        double x = (this.waypointX - this.posX) / dist;
        double y = (this.waypointY - this.posY) / dist;
        double z = (this.waypointZ - this.posZ) / dist;
        AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(x, y, z);

        for (int i = 1; (double)i < dist; ++i) {
            if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
        if (isInWater()) {
            this.motionX *= 0.95F;
            this.motionY *= 0.95F;
            this.motionZ *= 0.95F;
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double xDiff = this.posX - this.prevPosX;
        double zDiff = this.posZ - this.prevPosZ;
        float limbSwing = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff) * 4.0F;

        if (limbSwing > 1.0F) {
            limbSwing = 1.0F;
        }

        this.limbSwingAmount += (limbSwing - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    @Override
    public boolean isInWater() {
        return this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY + 0.25), MathHelper.floor_double(this.posZ)).getMaterial() == Material.water;
    }
}
