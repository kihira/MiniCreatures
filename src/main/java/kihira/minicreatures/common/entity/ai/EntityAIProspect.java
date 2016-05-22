/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity.ai;

import kihira.foxlib.common.EntityHelper;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.OreFinder;
import kihira.minicreatures.common.network.ProspectBlocksMessage;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIProspect extends EntityAIBase implements IRole {

    private final EntityTameable entity;
    private final int speed;
    private final int radius;
    private final int yRadius;
    private OreFinder oreFinder;
    private int cooldown;
    //This is set true when we need to prospect. Yeah not great but it works
    public boolean prospect;
    public BlockPos target;

    /**
     * Creates an EntityAI task for prospecting ores. This only finds and lists the ores, does nothing with them. Speed
     * controls at the rate it looks through the defined area (radius). Avoid setting too high to reduce lag.
     * @param entity The entity
     * @param speed The search speed
     * @param radius The search area radius
     */
    public EntityAIProspect(EntityTameable entity, int speed, int radius, int yRadius) {
        this.entity = entity;
        this.speed = speed;
        this.radius = radius;
        this.yRadius = yRadius;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        return !entity.isDead && prospect;
    }

    @Override
    public boolean continueExecuting() {
        if (oreFinder != null && !oreFinder.hasNext()) {
            //Send packet on finish
            MiniCreatures.proxy.simpleNetworkWrapper.sendTo(new ProspectBlocksMessage(oreFinder.orePositions.toArray(new int[][]{})), (EntityPlayerMP) entity.getOwner());
            return false;
        }
        else {
            return shouldExecute();
        }
    }

    @Override
    public void updateTask() {
        if (oreFinder == null) {
            //Head to target block first
            if (entity.getNavigator().noPath()) {
                entity.getNavigator().setPath(entity.getNavigator().getPathToXYZ(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5), 1D);
            }
            else if (entity.getDistanceSq(target) < 4D) {
                oreFinder = new OreFinder(entity.worldObj, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), radius, yRadius);
            }
        }
        else if (oreFinder.hasNext()) {
            oreFinder.next(speed);
            if (entity.ticksExisted % 6 == 0) entity.swingArm(EnumHand.MAIN_HAND);
        }

        //Face at target block
        entity.getLookHelper().setLookPosition(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 10F, entity.getVerticalFaceSpeed());
        float[] pitchYaw = EntityHelper.getPitchYawToPosition(entity.posX, entity.posY, entity.posZ, target);
        entity.rotationPitch = EntityHelper.updateRotation(entity.rotationPitch, pitchYaw[0], 5);
        entity.rotationYaw = EntityHelper.updateRotation(entity.rotationYaw, pitchYaw[1], 5);
    }

    @Override
    public void resetTask() {
        oreFinder = null;
        target = null;
        cooldown = 0;
        prospect = false;
    }

    @Override
    public void markForRemoval() {
        resetTask();
    }
}
