package kihira.minicreatures.common.entity.ai;

import kihira.minicreatures.common.entity.EntityMiniCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class EntityAIHappy extends EntityAIBase {

    private final EntityTameable theEntity;
    private int timesRunAround = 0;

    public EntityAIHappy(EntityTameable theFox) {
        this.theEntity = theFox;
        this.setMutexBits(1);
    }
    @Override
    public boolean shouldExecute() {
        if (this.theEntity.isTamed() && !this.theEntity.isSitting() && theEntity.getRNG().nextInt(250) == 0) {
            EntityPlayer player = (EntityPlayer) this.theEntity.getOwner();
            return player != null && this.theEntity.getDistanceSq(player) < 32 && !this.theEntity.hasPath();
        }
        return false;
    }

    @Override
    public void startExecuting() {
        //Set a path next to the entity
        this.findAndSetPath();
        this.theEntity.getDataManager().set(EntityMiniCreature.IS_HAPPY, true);
    }

    @Override
    public void updateTask() {
        this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getOwner(), 10.0F, this.theEntity.getVerticalFaceSpeed());

        if (!this.theEntity.hasPath()) {
            if (this.theEntity.getDistanceSq(this.theEntity.getOwner()) < 2 && this.theEntity.getRNG().nextInt(15) == 0) this.theEntity.getJumpHelper().setJumping();
            this.timesRunAround++;
            this.findAndSetPath();
        }
    }

    @Override
    public void resetTask() {
        this.theEntity.getDataManager().set(EntityMiniCreature.IS_HAPPY, false);
        this.timesRunAround = 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.timesRunAround < 10;
    }

    private void findAndSetPath() {
        Random random = new Random();
        EntityPlayer player = (EntityPlayer) this.theEntity.getOwner();

        if (player != null) {
            this.theEntity.getNavigator().setPath(this.theEntity.getNavigator().getPathToXYZ(player.posX +
                   MathHelper.getInt(random, -1, 1), player.getEntityBoundingBox().minY, player.posZ + MathHelper.getInt(random, -1, 1)), 1.2D);
        }
    }
}
