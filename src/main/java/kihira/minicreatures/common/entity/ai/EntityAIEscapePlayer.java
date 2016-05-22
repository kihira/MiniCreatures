package kihira.minicreatures.common.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIEscapePlayer extends EntityAIBase {

    private final EntityCreature theEntity;
    private final float triggerDistance;
    private final double speed;
    private EntityPlayer closestPlayer;
    private Path thePath;

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
        this.closestPlayer = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, 9D);
        World world = this.theEntity.worldObj;
        //Search a 6x6 area for a tree
        int searchRadius = 3;
        //We floor the position to prevent issues
        int entityX = MathHelper.floor_double(this.theEntity.posX);
        int entityY = MathHelper.floor_double(this.theEntity.posY);
        int entityZ = MathHelper.floor_double(this.theEntity.posZ);

        if (this.closestPlayer == null) return false;
        //If we're already on leaves and 3 blocks above the player, we can assume this is a tree and is a safe place
        BlockPos pos = new BlockPos(entityX, entityY - 1, entityZ);
        IBlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock().isLeaves(blockState, world, pos) && entityY - this.closestPlayer.posY >= 3F) return false;

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    //If we find a valid target, lets go there
                    // todo replace with isWood on Block
                    if (world.getBlockState(new BlockPos(entityX + x, entityY + y, entityZ + z)).getBlock() == Blocks.LOG) {
                        //TODO check if valid tree
                        Path path = this.theEntity.getNavigator().getPathToXYZ(entityX + x + 1, entityY + y, entityZ + z);
                        if (path != null) {
                            this.shouldClimb = true;
                            this.thePath = path;
                            return true;
                        }
                    }
                }
            }
        }

        //Just find a random path away instead
        Vec3d vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, new Vec3d(this.closestPlayer.posX, this.closestPlayer.posY, this.closestPlayer.posZ));
        if (vec3 == null) return false;
        else if (this.closestPlayer.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.closestPlayer.getDistanceSqToEntity(this.theEntity)) {
            return false;
        }
        else {
            Path path = this.theEntity.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
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

                BlockPos leavesPos = new BlockPos(entityX, entityY - 1, entityZ);
                IBlockState leavesState = world.getBlockState(leavesPos);
                BlockPos logPos = new BlockPos(entityX - 1, entityY, entityZ);
                if (!(world.isAirBlock(theEntity.getPosition()) && leavesState.getBlock().isLeaves(leavesState, world, leavesPos))
                        && world.getBlockState(logPos).getBlock().isWood(world, logPos)) {
                    this.isClimbing = true;
                    this.theEntity.noClip = true;
                    this.theEntity.motionY = 0F;
                    this.theEntity.moveEntity(0F, 0.3F, 0F);
                }
                else {
                    this.isClimbing = false;
                    this.theEntity.noClip = false;
                    this.theEntity.getNavigator().clearPathEntity();
                }
            }
        }
    }
}
