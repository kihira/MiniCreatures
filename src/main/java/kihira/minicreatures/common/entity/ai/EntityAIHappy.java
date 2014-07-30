package kihira.minicreatures.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

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
        if (this.theEntity.isTamed() && theEntity.getRNG().nextInt(200) == 0 && !this.theEntity.isSitting()) {
            EntityPlayer player = (EntityPlayer) this.theEntity.getOwner();
            if (player != null && this.theEntity.getDistanceSqToEntity(player) < 32 && !this.theEntity.hasPath()) return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        //Set a path next to the entity
        this.findAndSetPath();
        this.theEntity.getDataWatcher().updateObject(21, 1);
    }

    @Override
    public void updateTask() {
        Random random = this.theEntity.getRNG();
        this.theEntity.getLookHelper().setLookPositionWithEntity(this.theEntity.getOwner(), 10.0F, this.theEntity.getVerticalFaceSpeed());

        if (!this.theEntity.hasPath()) {
            this.timesRunAround++;
            this.findAndSetPath();
        }
    }

    @Override
    public void resetTask() {
        this.theEntity.getDataWatcher().updateObject(21, 0);
        this.timesRunAround = 0;
    }

    @Override
    public boolean continueExecuting() {
        return this.timesRunAround < 10;
    }

    private void findAndSetPath() {
        Random random = new Random();
        EntityPlayer player = (EntityPlayer) this.theEntity.getOwner();

        if (player != null) {
            this.theEntity.getNavigator().setPath(this.theEntity.getNavigator().getPathToXYZ(player.posX +
                   MathHelper.getRandomIntegerInRange(random, -1, 1), player.boundingBox.minY, player.posZ + MathHelper.getRandomIntegerInRange(random, -1, 1)), 1.2D);
        }
    }
}
