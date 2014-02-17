package minicreatures.common.entity;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

public class EntityTRex extends EntityTameable {

    public EntityTRex(World par1World) {
        super(par1World);
        setSize(0.5F, 0.5F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }
}
