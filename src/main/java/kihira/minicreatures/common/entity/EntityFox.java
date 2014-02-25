package kihira.minicreatures.common.entity;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.customizer.EnumPartCategory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
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
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class EntityFox extends EntityTameable implements IMiniCreature {

    private final IInventory inventory = new InventoryBasic(this.getCommandSenderName(), false, 18);
    private ArrayList<String> parts = new ArrayList<String>();

    public EntityFox(World par1World) {
        super(par1World);
        this.setSize(0.5f, 0.25f);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(4, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAITargetNonTamed(this, EntityChicken.class, 200, false));
        this.setTamed(false);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, 0);
        this.dataWatcher.addObject(19, (byte) BlockColored.func_150032_b(1));
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
            }
            /*
            else if (this.riddenByEntity == null && !this.isRiding() && !worldObj.isRemote) {
                EntityMiniPlayer miniPlayer = new EntityMiniPlayer(player.worldObj);
                miniPlayer.setCustomNameTag(player.getCommandSenderName());
                miniPlayer.setPosition(posX, posY, posZ);
                player.worldObj.spawnEntityInWorld(miniPlayer);
                miniPlayer.mountEntity(this);
            }
            */
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
    public boolean canMateWith(EntityAnimal par1EntityAnimal) {
        if (par1EntityAnimal == this || !this.isTamed() || !(par1EntityAnimal instanceof EntityFox)) return false;
        else {
            EntityFox entityFox = (EntityFox)par1EntityAnimal;
            return entityFox.isTamed() && (!entityFox.isSitting() && this.isInLove() && entityFox.isInLove());
        }
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public EnumSet<EnumPartCategory> getPartCatergoies() {
        return EnumSet.allOf(EnumPartCategory.class);
    }

    @Override
    public ArrayList<String> getCurrentParts() {
        return this.parts;
    }

    @Override
    public void setParts(ArrayList<String> parts) {
        this.parts = parts;
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }
}
