package kihira.minicreatures.common.entity.ai.combat;

import com.sun.istack.internal.NotNull;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;

public class EntityAIBowAttack extends EntityAIBase {

    private final IRangedAttackMob entityRanged;
    private final EntityLiving entity;
    private final double moveSpeedAmp;
    private int attackCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int trackTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private int giveUpTime = 60;

    public EntityAIBowAttack(@NotNull IRangedAttackMob entity, double speedAmplifier, int delay, float maxDistance) {
        this.entityRanged = entity;
        this.entity = (EntityLiving) entity;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        return this.entity.getAttackTarget() != null && this.hasBow();
    }

    private boolean hasBow() {
        return this.entity.getHeldItemMainhand().getItem() instanceof ItemBow;
    }

    @Override
    public void resetTask() {
        this.trackTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
        this.entity.getMoveHelper().strafe(0,0);
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = this.entity.getAttackTarget();

        if (target != null) {
            double dist = this.entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
            boolean canSee = this.entity.getEntitySenses().canSee(target);
            boolean tracking = this.trackTime > 0;

            if (canSee != tracking) {
                this.trackTime = 0;
            }

            if (canSee) ++this.trackTime;
            else --this.trackTime;

            // Can see target and within range
            if (dist <= (double)this.maxAttackDistance && this.trackTime >= 10) {
                this.entity.getNavigator().clearPath();
                ++this.strafingTime;
            }
            // Try to move to target if unseen or out of range
            else {
                this.entity.getNavigator().tryMoveToEntityLiving(target, this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.entity.getRNG().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.entity.getRNG().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (dist > (double)(this.maxAttackDistance * 0.75F)) {
                    this.strafingBackwards = false;
                }
                else if (dist < (double)(this.maxAttackDistance * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.faceEntity(target, 30.0F, 30.0F);
            }
            else {
                this.entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            }

            if (this.entity.isHandActive()) {
                // lost track of target
                if (!canSee && this.trackTime < -giveUpTime) {
                    this.entity.resetActiveHand();
                }
                else if (canSee) {
                    int itemUseCount = this.entity.getItemInUseMaxCount();

                    // todo change attack speed depending on how close entity is?
                    if (itemUseCount >= 20) {
                        this.entity.resetActiveHand();
                        this.entityRanged.attackEntityWithRangedAttack(target, ItemBow.getArrowVelocity(itemUseCount));
                        this.attackTime = this.attackCooldown;
                    }
                }
            }
            else if (--this.attackTime <= 0 && this.trackTime >= -giveUpTime)
            {
                this.entity.setActiveHand(EnumHand.MAIN_HAND);
            }
        }
    }
}
