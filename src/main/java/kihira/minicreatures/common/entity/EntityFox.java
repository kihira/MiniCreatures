package kihira.minicreatures.common.entity;

import com.google.common.base.Strings;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.customizer.EnumPartCategory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class EntityFox extends EntityTameable implements IMiniCreature {

    private final IInventory inventory = new InventoryBasic(this.getCommandSenderName(), false, 18);

    public EntityFox(World par1World) {
        super(par1World);
        this.setSize(0.4f, 0.4f);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(5, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAITargetNonTamed(this, EntityChicken.class, 200, false));
        this.setTamed(false);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, 0);
        this.dataWatcher.addObject(19, (byte) BlockColored.func_150032_b(1));
        this.dataWatcher.addObject(20, "");
    }

    public boolean hasChest() {
        return this.dataWatcher.getWatchableObjectInt(18) == 1;
    }

    public void setHasChest(boolean hasChest) {
        this.dataWatcher.updateObject(18, hasChest ? 1 : 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (this.isTamed()) {
            if (itemstack != null) {
                if (itemstack.getItem() instanceof ItemFood) {
                    ItemFood itemfood = (ItemFood)itemstack.getItem();
                    if (itemfood.isWolfsFavoriteMeat() && this.getMaxHealth() < 20.0F) {
                        this.heal((float)itemfood.func_150905_g(itemstack));
                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        return true;
                    }
                }
                else if (Block.getBlockFromItem(itemstack.getItem()) == Blocks.chest && !this.hasChest()) {
                    this.setHasChest(true);
                    this.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    return true;
                }
                else if (itemstack.getItem() == Items.dye) {
                    int i = BlockColored.func_150032_b(itemstack.getItemDamage());
                    if (i != this.getCollarColor()) {
                        this.setCollarColor(i);
                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        return true;
                    }
                }
                else if (itemstack.getItem() == Items.brick) {
                    double d0 = 7.0D;
                    List list = this.worldObj.getEntitiesWithinAABB(EntityMiniPlayer.class, AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));

                    if (list != null) {
                        for (Object aList : list) {
                            EntityLiving entityliving = (EntityLiving) aList;
                            if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player) {
                                entityliving.setLeashedToEntity(null, true);
                                entityliving.mountEntity(this);
                                break;
                            }
                        }
                    }
                }
                else if (itemstack.getItem() == Items.brick) return false;
            }
            if (!player.isSneaking() && this.hasChest()) {
                //Send Entity ID as x coord. Inspired by OpenBlocks
                player.openGui(MiniCreatures.instance, 0, player.worldObj, this.getEntityId(), 0, 0);
            }
            else if (player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isBreedingItem(itemstack)) {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity(null);
                this.setTarget(null);
                this.setAttackTarget(null);
            }
        }
        else if (itemstack != null && itemstack.getItem() == Items.bone) {
            if (!player.capabilities.isCreativeMode) --itemstack.stackSize;
            if (itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            if (!this.worldObj.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.setPathToEntity(null);
                    this.setAttackTarget(null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.setOwner(player.getCommandSenderName());
                    this.playTameEffect(true);
                    this.worldObj.setEntityState(this, (byte)7);
                }
                else {
                    this.playTameEffect(false);
                    this.worldObj.setEntityState(this, (byte)6);
                }
            }
            return true;
        }
        return super.interact(player);
    }

    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);

        if (!this.worldObj.isRemote && hasChest()) {
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = inventory.getStackInSlot(i);
                if (itemstack != null) this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);

        tag.setBoolean("HasChest", this.hasChest());
        tag.setByte("CollarColor", (byte) this.getCollarColor());
        if (this.hasChest()) {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                if (inventory.getStackInSlot(i) != null) {
                    NBTTagCompound stacktag = new NBTTagCompound();
                    stacktag.setByte("Slot", (byte)i);
                    inventory.getStackInSlot(i).writeToNBT(stacktag);
                    nbttaglist.appendTag(stacktag);
                }
            }
            tag.setTag("Items", nbttaglist);
        }
        //Save parts list
        NBTTagList nbttaglist = new NBTTagList();
        for (String part : this.dataWatcher.getWatchableObjectString(20).split(",")) {
            if (part != null) nbttaglist.appendTag(new NBTTagString(part));
        }
        tag.setTag("Parts", nbttaglist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setHasChest(tag.getBoolean("HasChest"));

        if (this.hasChest()) {
            NBTTagList nbttaglist = tag.getTagList("Items", 0);
            for (int i = 0; i < nbttaglist.tagCount(); i++) {
                NBTTagCompound stacktag = nbttaglist.getCompoundTagAt(i);
                int j = stacktag.getByte("Slot");
                if (j >= 0 && j < inventory.getSizeInventory()) inventory.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(stacktag));
            }
        }
        if (tag.hasKey("CollarColor", 99)) this.setCollarColor(tag.getByte("CollarColor"));
        //Load parts list
        NBTTagList tagList = tag.getTagList("Parts", 8);
        String s = "";
        for (int i = 0; i < tagList.tagCount(); i++) {
            s += tagList.getStringTagAt(i) + ",";
        }
        this.dataWatcher.updateObject(20, s);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack != null && (itemStack.getItem() instanceof ItemFood);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 8;
    }

    @Override
    protected void func_145780_a(int par1, int par2, int par3, Block par4) {
        this.playSound("mob.wolf.step", 0.15F, 1.0F);
    }

    @Override
    protected String getLivingSound() {
        return "mob.wolf.bark";
    }

    @Override
    protected String getHurtSound() {
        return "mob.wolf.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.wolf.death";
    }

    @Override
    protected float getSoundPitch() {
        return this.rand.nextFloat() - this.rand.nextFloat() * 0.3F + 1.5F;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected Item getDropItem() {
        return Items.leather;
    }

    @Override
    protected void dropFewItems(boolean hitByPlayerRecently, int lootingLevel) {
        int j = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingLevel);

        for (int k = 0; k < j; ++k) {
            this.dropItem(Items.leather, 1);
        }
    }

    public int getCollarColor() {
        return this.dataWatcher.getWatchableObjectByte(19) & 15;
    }

    public void setCollarColor(int par1) {
        this.dataWatcher.updateObject(19, (byte) (par1 & 15));
    }

    @Override
    public EntityFox createChild(EntityAgeable entityageable) {
        EntityFox entityFox = new EntityFox(this.worldObj);
        String s = this.getOwnerName();

        if (s != null && s.trim().length() > 0) {
            entityFox.setOwner(s);
            entityFox.setTamed(true);
        }
        return entityFox;
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        int i = this.isTamed() ? 4 : 2;
        return par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)i);
    }

    @Override
    public boolean canMateWith(EntityAnimal par1EntityAnimal) {
        if (par1EntityAnimal == this || !this.isTamed() || !(par1EntityAnimal instanceof EntityFox)) return false;
        else {
            EntityFox entityFox = (EntityFox)par1EntityAnimal;
            return entityFox.isTamed() && (!entityFox.isSitting() && this.isInLove() && entityFox.isInLove());
        }
    }

    @Override
    public IInventory getInventory() {
        return this.inventory;
    }

    @Override
    public EnumSet<EnumPartCategory> getPartCatergoies() {
        return EnumSet.of(EnumPartCategory.ALL, EnumPartCategory.HEAD, EnumPartCategory.BODY);
    }

    @Override
    public ArrayList<String> getCurrentParts(boolean isPreview) {
        ArrayList<String> parts = new ArrayList<String>();
        for (String part : this.dataWatcher.getWatchableObjectString(20).split(",")) {
            if (!Strings.isNullOrEmpty(part)) parts.add(part);
        }
        return parts;
    }

    @Override
    public void setParts(ArrayList<String> parts, boolean isPreview) {
        String s = "";
        for (String part : parts) {
            s += part + ",";
        }
        System.out.println(s);
        this.dataWatcher.updateObject(20, s);
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }
}
