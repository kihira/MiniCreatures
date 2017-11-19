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

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMiniShark extends EntityWaterMob {

    private double waypointX;
    private double waypointY;
    private double waypointZ;
    private EntityPlayer targetedEntity;
    private int attackTick;
    private int healCooldown;

    public EntityMiniShark(World par1World) {
        super(par1World);
        setSize(0.5F, 0.3F);
        stepHeight = 0F;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.75D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean attack = entity.attackEntityFrom(DamageSource.causeMobDamage(this), world.getDifficulty().getDifficultyId() * 2);
        if (attack && healCooldown <= 0) {
            heal(world.getDifficulty().getDifficultyId());
            healCooldown = 60;
        }
        return attack;
    }


    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    public void onLivingUpdate() {
        super.onLivingUpdate();
            if (attackTick > 0) attackTick--;
            if (healCooldown > 0) healCooldown--;

            if (this.isInWater()) {
                double xDist = this.waypointX - this.posX;
                double yDist = this.waypointY - this.posY;
                double zDist = this.waypointZ - this.posZ;
                double d3 = xDist * xDist + yDist * yDist + zDist * zDist;
                //Make sure d is greater then 0 otherwise we'll end up dividing by 0
                if (d3 > 0) {
                    d3 = Math.sqrt(d3);

                    if (!world.isRemote) {
                        double speed = (this.targetedEntity != null ? 0.02D : 0.01D) * getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
                        this.motionX += xDist / d3 * speed;
                        this.motionY += yDist / d3 * speed * 1.5f;
                        this.motionZ += zDist / d3 * speed;
                    }
                }
            }
            else {
                if (!world.isRemote) {
                    this.motionX = 0D;
                    this.motionY -= 0.08D;
                    this.motionY *= 0.9800000190734863D;
                    this.motionZ = 0D;
                }
            }

            if (this.targetedEntity != null) {
                this.faceEntity(this.targetedEntity, 5F, 5F);

                double distToTarget = this.getDistanceSq(this.targetedEntity.posX, this.targetedEntity.getEntityBoundingBox().minY, this.targetedEntity.posZ);
                double d10 = (double)(this.width + this.targetedEntity.width);
                if (distToTarget <= d10 && this.attackTick <= 0) {
                    this.attackTick = 20;
                    this.attackEntityAsMob(this.targetedEntity);
                }
            }
            else {
                this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI;
            }
    }

    // todo port to 1.9.4
/*    @Override
    protected void updateEntityActionState() {
        moveStrafing = 0.0F;
        moveForward = 0.0F;
        entityAge++;

        double xDist = waypointX - posX;
        double yDist = waypointY - posY;
        double zDist = waypointZ - posZ;
        double d3 = xDist * xDist + yDist * yDist + zDist * zDist;

        if (this.isInWater()) {
            if (this.targetedEntity == null && ((d3 < 1D || d3 > 3600D) || !isCourseTraversable(d3))) {
                for (int i = 0; i < 3; i++) {
                    double targetX = Math.floor(posX + MathHelper.getRandomDoubleInRange(rand, -8F, 8F));
                    double targetY = Math.floor(posY + MathHelper.getRandomDoubleInRange(rand, -1F, 2F));
                    double targetZ = Math.floor(posZ + MathHelper.getRandomDoubleInRange(rand, -8F, 8F));

                    if ((world.getBlock((int) targetX, (int) targetY, (int) targetZ).getMaterial() == Material.water) && isCourseTraversable(MathHelper.sqrt_double(d3))) {
                        waypointX = targetX;
                        waypointY = targetY + (world.getBlock((int) targetX, (int) targetY + 1, (int) targetZ) instanceof BlockAir ? height : 0);
                        waypointZ = targetZ;
                        break;
                    }
                    else {
                        resetWaypoints();
                    }
                }
            }
        }

        if (targetedEntity == null) targetedEntity = world.getClosestPlayerToEntity(this, 64D);
        checkTargetValid();
        if (targetedEntity != null) {
            this.waypointX = this.targetedEntity.posX;
            this.waypointY = this.targetedEntity.posY;
            this.waypointZ = this.targetedEntity.posZ;
        }

        despawnEntity();
    }*/

    private void checkTargetValid() {
        if (targetedEntity != null && (targetedEntity.isDead || !targetedEntity.isInWater() || this.targetedEntity.getDistanceSq(this) > 3600D || !canEntityBeSeen(this.targetedEntity))) {
            targetedEntity = null;
        }
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
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().offset(x, y, z);

        for (int i = 1; (double)i < dist; ++i) {
            if (!this.world.getCollisionBoxes(this, axisalignedbb).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
        if (waypointY > posY && waypointY - posY > 1) motionY += 0.05f;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public boolean isInWater() {
        // todo port to 1.9.4
        // this.inWater = this.world.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY + 0.25), MathHelper.floor_double(this.posZ)).getMaterial() == Material.WATER;
        //this.inWater = this.world.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY + 0.25), MathHelper.floor_double(this.posZ)).getMaterial() == Material.water;
        return this.inWater;
    }

    @Override
    public void applyEntityCollision(Entity entity){
        super.applyEntityCollision(entity);
        if (targetedEntity != null && entity.equals(targetedEntity) && attackTick <= 0){
            targetedEntity.attackEntityAsMob(targetedEntity);
            attackTick = 20;
        }
    }

    @Override
    protected void despawnEntity() {
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) setDead();
        else super.despawnEntity();
    }

    private static class AIMove  extends EntityAIBase {

        @Override
        public boolean shouldExecute() {
            return false;
        }
    }
}
