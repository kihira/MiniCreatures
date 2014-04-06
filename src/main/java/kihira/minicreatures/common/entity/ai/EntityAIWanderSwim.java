package kihira.minicreatures.common.entity.ai;

import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;

public class EntityAIWanderSwim extends EntityAIBase {

    private EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private double speed;

    public EntityAIWanderSwim(EntityCreature par1EntityCreature, double par2) {
        this.entity = par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
        if (this.entity.getRNG().nextInt(60) != 0) {
            return false;
        }
        else {
            Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

            if (vec3 == null || !(this.entity.worldObj.getBlock((int) vec3.xCoord, (int) vec3.yCoord, (int) vec3.zCoord) instanceof BlockLiquid)) {
                System.out.println("Not water =(");
                return false;
            }
            else {
                this.xPosition = vec3.xCoord;
                this.yPosition = vec3.yCoord;
                this.zPosition = vec3.zCoord;
                System.out.println(vec3);
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entity.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entity.getNavigator().setPath(getEntityPathToXYZ(), this.speed);
    }

    //Copying this from World so I can make sure it pathfinds in water properly
    public PathEntity getEntityPathToXYZ() {
        int l = MathHelper.floor_double(this.entity.posX);
        int i1 = MathHelper.floor_double(this.entity.posY);
        int j1 = MathHelper.floor_double(this.entity.posZ);
        int k1 = (int)(this.entity.getNavigator().getPathSearchRange() + 8.0F);
        int l1 = l - k1;
        int i2 = i1 - k1;
        int j2 = j1 - k1;
        int k2 = l + k1;
        int l2 = i1 + k1;
        int i3 = j1 + k1;
        ChunkCache chunkcache = new ChunkCache(this.entity.worldObj, l1, i2, j2, k2, l2, i3, 0);
        return (new PathFinder(chunkcache, false, false, false, false)).createEntityPathTo(this.entity, (int) this.xPosition, (int) this.yPosition, (int) this.zPosition, this.entity.getNavigator().getPathSearchRange());
    }
}
