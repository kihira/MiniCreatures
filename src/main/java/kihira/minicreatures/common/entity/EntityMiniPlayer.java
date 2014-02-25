package kihira.minicreatures.common.entity;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.CustomizerRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class EntityMiniPlayer extends EntityTameable implements IMiniCreature {

    private final InventoryBasic inventory = new InventoryBasic(this.getCommandSenderName(), false, 18);
    private ArrayList<String> parts = new ArrayList<String>();
    //True if aiming with bow. Not currently in use.
    public boolean isAiming = false;

    public EntityMiniPlayer(World par1World) {
        super(par1World);
        this.setSize(0.5F, 1F);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(5, new EntityAILookIdle(this));
        this.setTamed(false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);

        NBTTagList nbttaglist = new NBTTagList();
        for (String part : this.parts) {
            if (part != null) nbttaglist.appendTag(new NBTTagString(part));
        }
        par1NBTTagCompound.setTag("Parts", nbttaglist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);

        NBTTagList tagList = par1NBTTagCompound.getTagList("Parts", 0);
        this.parts = new ArrayList<String>(tagList.tagCount());
        for (int i = 0; i < tagList.tagCount(); i++) {
            this.parts.add(tagList.getStringTagAt(i));
        }
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (!this.worldObj.isRemote) {
            if (!this.isTamed()) {
                this.setTamed(true);
                this.setOwner(player.getCommandSenderName());
            }
            else if (this.isTamed()) {
                if (itemstack != null) {
                    if (itemstack.getItem() instanceof ItemArmor) {
                        int i = EntityLiving.getArmorPosition(itemstack) - 1;
                        if (this.getEquipmentInSlot(i + 1) == null) {
                            this.setCurrentItemOrArmor(i + 1, itemstack.copy());
                            if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                        else {
                            EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.getEquipmentInSlot(i + 1));
                            this.worldObj.spawnEntityInWorld(entityItem);
                            this.setCurrentItemOrArmor(i + 1, null);
                        }
                        return true;
                    }
                    if (this.getCarrying() == null) {
                        ItemStack newItemStack = itemstack.copy();
                        newItemStack.stackSize = 1;
                        this.setCarrying(newItemStack);
                        player.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                    else if (this.getCarrying() != null && itemstack.getItem() == Items.stick) {
                        EntityItem entityItem = new EntityItem(player.worldObj, this.posX, this.posY, this.posZ, this.getCarrying().copy());
                        player.worldObj.spawnEntityInWorld(entityItem);
                        this.setCarrying(null);
                        for (int i = 0; i < inventory.getSizeInventory(); i++) {
                            ItemStack itemStackToDrop = inventory.getStackInSlot(i);
                            if (itemStackToDrop != null) {
                                entityItem = new EntityItem(player.worldObj, this.posX, this.posY, this.posZ, itemStackToDrop);
                                player.worldObj.spawnEntityInWorld(entityItem);
                            }
                            inventory.setInventorySlotContents(i, null);
                        }
                    }
                }
                if (!player.isSneaking() && this.getCarrying() != null) {
                    if (Block.getBlockFromItem(this.getCarrying().getItem()) == Blocks.chest) player.openGui(MiniCreatures.instance, 0, player.worldObj, this.getEntityId(), 0, 0);
                    else if (Block.getBlockFromItem(this.getCarrying().getItem()) == Blocks.anvil) player.openGui(MiniCreatures.instance, 1, player.worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
                }
            /*
            else if (player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isBreedingItem(itemstack)) {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.setPathToEntity(null);
                this.setTarget(null);
                this.setAttackTarget(null);
            }
            */
            }
        }
        return super.interact(player);
    }

    public void setCarrying(ItemStack itemStack) {
        this.setCurrentItemOrArmor(0, itemStack);
    }

    public ItemStack getCarrying() {
        return this.getHeldItem();
    }

    @Override
    public void setCustomNameTag(String par1Str) {
        this.inventory.func_110133_a(par1Str);
        super.setCustomNameTag(par1Str);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    @Override
    public IInventory getInventory() {
        return this.inventory;
    }

    @Override
    public EnumSet<CustomizerRegistry.EnumPartCategory> getPartCatergoies() {
        return EnumSet.allOf(CustomizerRegistry.EnumPartCategory.class);
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
