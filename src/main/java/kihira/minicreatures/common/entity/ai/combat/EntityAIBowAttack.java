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
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private int giveUpTime = 60;

    public EntityAIBowAttack(@NotNull IRangedAttackMob entity, double speedAmplifier, int delay, float maxDistance)
    {
        this.entityRanged = entity;
        this.entity = (EntityLiving) entity;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.entity.getAttackTarget() != null && this.hasBow();
    }

    private boolean hasBow() {
        return this.entity.getHeldItemMainhand() != null && this.entity.getHeldItemMainhand().getItem() instanceof ItemBow;
    }

    public boolean continueExecuting() {
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.hasBow();
    }

    public void startExecuting() {
        super.startExecuting();
    }

    public void resetTask() {
        super.startExecuting();
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
    }

    public void updateTask() {
        EntityLivingBase target = this.entity.getAttackTarget();

        if (target != null) {
            double dist = this.entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
            boolean canSee = this.entity.getEntitySenses().canSee(target);
            boolean flag1 = this.seeTime > 0;

            if (canSee != flag1) {
                this.seeTime = 0;
            }

            if (canSee) {
                ++this.seeTime;
            }
            else {
                --this.seeTime;
            }

            if (dist <= (double)this.maxAttackDistance && this.seeTime >= 20) {
                this.entity.getNavigator().clearPathEntity();
                ++this.strafingTime;
            }
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
                if (!canSee && this.seeTime < -giveUpTime) {
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
            else if (--this.attackTime <= 0 && this.seeTime >= -giveUpTime)
            {
                this.entity.setActiveHand(EnumHand.MAIN_HAND);
            }
        }
    }
}
