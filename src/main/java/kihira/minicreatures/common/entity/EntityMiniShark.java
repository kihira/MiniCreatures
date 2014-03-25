package kihira.minicreatures.common.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.world.World;

public class EntityMiniShark extends EntityWaterMob {

    public EntityMiniShark(World par1World) {
        super(par1World);
        this.setSize(0.95F, 0.95F);
        this.getNavigator().setAvoidsWater(false);
        this.getNavigator().setCanSwim(true);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public boolean isInWater() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.6000000238418579D, 0.0D), Material.water, this);
    }
}
