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

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityMiniShark extends EntityWaterMob {

    private double waypointX;
    private double waypointY;
    private double waypointZ;
    private EntityPlayer targetedEntity;
    private int attackTick;

    public EntityMiniShark(World par1World) {
        super(par1World);
        //this.setSize(0.3F, 0.3F);
        this.setSize(0.95F, 0.95F);
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
                d3 = (double) MathHelper.sqrt_double(d3);

                //If we can move there, do so. Otherwise reset
                if (this.isCourseTraversable(d3)) {
                    this.motionX += xDist / d3 * (this.targetedEntity != null ? 0.02D : 0.01D);
                    this.motionY += yDist / d3 * (this.targetedEntity != null ? 0.02D : 0.01D);
                    this.motionZ += zDist / d3 * (this.targetedEntity != null ? 0.02D : 0.01D);
                }
                else {
                    resetWaypoints();
                }
            }
            else {
                this.motionX *= 0.9D;
                this.motionY -= 0.08D;
                this.motionY *= 0.9800000190734863D;
                this.motionZ *= 0.9D;
            }

            if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < 4096D) {
                this.faceEntity(this.targetedEntity, 5F, 5F);

                if (this.canEntityBeSeen(this.targetedEntity) && this.targetedEntity.isInWater()) {
                    this.waypointX = this.targetedEntity.posX;
                    this.waypointY = this.targetedEntity.posY;
                    this.waypointZ = this.targetedEntity.posZ;

                    double distToTarget = this.getDistanceSq(this.targetedEntity.posX, this.targetedEntity.boundingBox.minY, this.targetedEntity.posZ);
                    double d10 = (double)(this.width * 5F * this.width * 5F + this.targetedEntity.width);
                    if (distToTarget <= d10 && this.attackTick <= 0) {
                        this.attackTick = 20;
                        this.attackEntityAsMob(this.targetedEntity);
                    }
                }
            }
            else {
                this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI;
            }
        }
    }

    @Override
    protected void updateEntityActionState() {
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;

        double xDist = this.waypointX - this.posX;
        double yDist = this.waypointY - this.posY;
        double zDist = this.waypointZ - this.posZ;
        double d3 = xDist * xDist + yDist * yDist + zDist * zDist;

        if (this.isInWater()) {
            if ((d3 < 3D || d3 > 3600D) && this.rand.nextInt() > 30) {
                for (int i = 0; i < 5; i++) {
                    this.waypointX = this.posX + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16F);
                    this.waypointY = this.posY + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 2F);
                    this.waypointZ = this.posZ + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16F);

                    //If waypoint invalid, reset
                    if (!(this.worldObj.getBlock((int) this.waypointX, (int) this.waypointY, (int) this.waypointZ) instanceof BlockLiquid) && isCourseTraversable(MathHelper.sqrt_double(d3))) {
                        resetWaypoints();
                    }
                    else {
                        boolean flag = false;
                        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                            Block block = worldObj.getBlock((int) Math.floor(posX + dir.offsetX), (int) Math.floor(posY + dir.offsetY), (int) Math.floor(posZ + dir.offsetZ));
                            if (!(block.getMaterial() == Material.air || block.getMaterial() == Material.water)) {
                                resetWaypoints();
                                flag = true;
                            }
                        }
                        if (!flag) break;
                    }
                }
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.isDead) this.targetedEntity = null;
        if (this.targetedEntity == null) this.targetedEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 64D);
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
            //axisalignedbb.offset(x, y, z);
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
        return this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() == Material.water;
        //return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
        //return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.6000000238418579D, 0.0D), Material.water, this);
    }
}
