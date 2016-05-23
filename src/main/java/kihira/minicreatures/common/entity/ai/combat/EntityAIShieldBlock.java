package kihira.minicreatures.common.entity.ai.combat;

import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

// todo support for ranged attacks?
public class EntityAIShieldBlock extends EntityAIBase {
    private final EntityMiniPlayer entity;
    private final float dist;
    private EnumHand shieldHand;

    public EntityAIShieldBlock(EntityMiniPlayer entity, float dist) {
        this.entity = entity;
        this.dist = dist;
    }

    @Override
    public boolean shouldExecute() {
        shieldHand = getShield();
        return shieldHand != null && entity.getAttackTarget() != null && entity.getDistanceToEntity(entity.getAttackTarget()) <= dist && shieldHand != entity.getActiveHand() && !entity.isSwingInProgress;
    }

    @Override
    public boolean continueExecuting() {
        return shieldHand != null && entity.getAttackTarget() != null && entity.getDistanceToEntity(entity.getAttackTarget()) <= dist;
    }

    @Override
    public void startExecuting() {
        entity.setActiveHand(shieldHand);
    }

    @Override
    public void resetTask() {
        entity.resetActiveHand();
    }

    @Nullable
    private EnumHand getShield() {
        if (entity.getHeldItemMainhand() != null && entity.getHeldItemMainhand().getItem() == Items.SHIELD) return EnumHand.MAIN_HAND;
        else if (entity.getHeldItemOffhand() != null && entity.getHeldItemOffhand().getItem() == Items.SHIELD) return EnumHand.OFF_HAND;
        return null;
    }
}
