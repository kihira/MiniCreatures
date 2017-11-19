package kihira.minicreatures.common;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityHelper {

    public static float[] getPitchYawToEntity(Entity sourceEntity, Entity targetEntity) {
        return getPitchYawToEntity(sourceEntity.posX, sourceEntity.posY, sourceEntity.posZ, targetEntity);
    }

    public static float[] getPitchYawToEntity(double sourceX, double sourceY, double sourceZ, Entity targetEntity) {
        return getPitchYawToPosition(sourceX, sourceY, sourceZ, targetEntity.posX - targetEntity.width,
                targetEntity.posY - targetEntity.height + targetEntity.getEyeHeight() + ((targetEntity.world.isRemote) ? 0.1F : 0.22F),
                targetEntity.posZ - targetEntity.width);
    }

    public static float[] getPitchYawToPosition(double sourceX, double sourceY, double sourceZ, BlockPos target) {
        return getPitchYawToPosition(sourceX, sourceY, sourceZ, target.getX(), target.getY(), target.getZ());
    }

    public static float[] getPitchYawToPosition(double sourceX, double sourceY, double sourceZ, double targetX, double targetY, double targetZ) {
        double xDiff = targetX - sourceX;
        double yDiff = targetY - sourceY;
        double zDiff = targetZ - sourceZ;
        double d3 = MathHelper.sqrt((xDiff * xDiff) + (zDiff * zDiff));
        double pitch = (-(Math.atan2(yDiff, d3) * 180d / Math.PI));
        double yaw = (Math.atan2(zDiff, xDiff) * 180d / Math.PI) - 90d;

        return new float[]{(float) pitch, (float) yaw};
    }

    public static float updateRotation(float currRot, float intendedRot, float maxInc) {
        float f3 = MathHelper.wrapDegrees(intendedRot - currRot);
        if (f3 > maxInc) f3 = maxInc;
        if (f3 < -maxInc) f3 = -maxInc;
        return currRot + f3;
    }
}
