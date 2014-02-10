package minicreatures.common.entity;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

public class EntityMiniPlayer extends EntityTameable {

    public EntityMiniPlayer(World par1World) {
        super(par1World);
        this.setSize(0.5f, 1.7f);
        //ID of block carried
        this.dataWatcher.addObject(18, 0);
    }

    public int getCarrying() {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    @Override
    public boolean isChild() {
        return true;
    }
}
