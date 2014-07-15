package kihira.minicreatures.common.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIEscapePlayer extends EntityAIBase {

    private final EntityCreature theEntity;
    private final float triggerDistance;
    private final double speed;
    private EntityPlayer closestPlayer;
    private PathEntity thePath;

    private boolean shouldClimb;
    public boolean isClimbing;

    public EntityAIEscapePlayer(EntityCreature theEntity, float triggerDistance, double speed) {
        this.theEntity = theEntity;
        this.triggerDistance = triggerDistance;
        this.speed = speed;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        if (this.theEntity instanceof EntityTameable && ((EntityTameable)this.theEntity).isTamed()) {
            return false;
        }
        this.closestPlayer = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, 18D);

        if (this.closestPlayer == null) return false;

        //Search a 6x6 area for a tree
        int searchRadius = 3;
        //We floor the position to prevent issues
        int entityX = MathHelper.floor_double(this.theEntity.posX);
        int entityY = MathHelper.floor_double(this.theEntity.posY);
        int entityZ = MathHelper.floor_double(this.theEntity.posZ);
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    Block block = this.theEntity.worldObj.getBlock(entityX + x, entityY + y, entityZ + z);
                    //If we find a valid target, lets go there
                    if (block == Blocks.log) {
                        //TODO check if valid tree
                        PathEntity path = this.theEntity.getNavigator().getPathToXYZ(entityX + x + 1, entityY + y, entityZ + z);
                        if (path != null) {
                            this.shouldClimb = true;
                            this.thePath = path;
                            return true;
                        }
                    }
                }
            }
        }

        //Just find a random path away
        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, Vec3.createVectorHelper(this.closestPlayer.posX, this.closestPlayer.posY, this.closestPlayer.posZ));
        if (vec3 == null) return false;
        else {
            PathEntity path = this.theEntity.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            if (path != null) {
                this.thePath = path;
                return true;
            }
            else return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        if (this.shouldClimb) {
            return !(this.theEntity.getNavigator().noPath() && !this.isClimbing);
        }
        else return !this.theEntity.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().setPath(this.thePath, this.speed);
    }

    @Override
    public void resetTask() {
        this.closestPlayer = null;
        this.thePath = null;
        this.shouldClimb = false;
        this.isClimbing = false;
    }

    @Override
    public void updateTask() {
        if (this.theEntity.getNavigator().noPath()) {
            if (this.shouldClimb) {
                World world = this.theEntity.worldObj;
                //We floor the position to prevent issues
                int entityX = MathHelper.floor_double(this.theEntity.posX);
                int entityY = MathHelper.floor_double(this.theEntity.posY);
                int entityZ = MathHelper.floor_double(this.theEntity.posZ);

                if (!(world.isAirBlock(entityX, entityY, entityZ) && world.getBlock(entityX, entityY - 1, entityZ) == Blocks.leaves)) {
                    this.isClimbing = true;
                    this.theEntity.noClip = true;
                    this.theEntity.motionY = 0F;
                    this.theEntity.moveEntity(0F, 0.3F, 0F);
                }
                else {
                    this.isClimbing = false;
                    this.theEntity.noClip = false;
                }
            }
        }
    }
}
