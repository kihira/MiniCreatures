package kihira.minicreatures.common.entity;

import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.customizer.EnumPartCategory;
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
    //True if aiming with bow. Not currently in use.
    public boolean isAiming = false;

    //Maintain an array list client side for previewing
    @SideOnly(Side.CLIENT)
    private ArrayList<String> previewParts;

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

    public boolean isChild() {
        return false;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, ""); //Parts list
        this.dataWatcher.addObject(19, 0); //Has Mind Control device
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

        //Save parts list
        NBTTagList nbttaglist = new NBTTagList();
        for (String part : this.dataWatcher.getWatchableObjectString(18).split(",")) {
            if (part != null) nbttaglist.appendTag(new NBTTagString(part));
        }
        par1NBTTagCompound.setTag("Parts", nbttaglist);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        MiniCreatures.logger.info("Reading NBT!");

        //Load parts list
        NBTTagList tagList = par1NBTTagCompound.getTagList("Parts", 8);
        String s = "";
        for (int i = 0; i < tagList.tagCount(); i++) {
            s += tagList.getStringTagAt(i) + ",";
        }
        this.dataWatcher.updateObject(18, s);
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
                    else if (this.getCarrying() == null) {
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
                else if (!player.isSneaking() && this.getCarrying() != null) {
                    if (Block.getBlockFromItem(this.getCarrying().getItem()) == Blocks.chest) player.openGui(MiniCreatures.instance, 0, player.worldObj, this.getEntityId(), 0, 0);
                    else if (Block.getBlockFromItem(this.getCarrying().getItem()) == Blocks.anvil) player.openGui(MiniCreatures.instance, 1, player.worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
                }
                else if (player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote) {
                    this.aiSit.setSitting(!this.isSitting());
                    this.setSitting(!this.isSitting());
                    this.isJumping = false;
                    this.setPathToEntity(null);
                    this.setTarget(null);
                    this.setAttackTarget(null);
                }
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

    public void setMindControlled(boolean mindControlled) {
        this.dataWatcher.updateObject(19, mindControlled ? 1 : 0);
    }

    public boolean isMindControlled() {
        return this.dataWatcher.getWatchableObjectInt(19) == 1;
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
    public EnumSet<EnumPartCategory> getPartCatergoies() {
        return EnumSet.allOf(EnumPartCategory.class);
    }

    @Override
    public ArrayList<String> getCurrentParts(boolean isPreview) {
        if (isPreview) return this.previewParts;
        else {
            ArrayList<String> parts = new ArrayList<String>();
            for (String part : this.dataWatcher.getWatchableObjectString(18).split(",")) {
                if (!Strings.isNullOrEmpty(part)) parts.add(part);
            }
            return parts;
        }
    }

    @Override
    public void setParts(ArrayList<String> parts, boolean isPreview) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            if (isPreview) this.previewParts = parts;
            else this.previewParts = new ArrayList<String>();
        }
        String s = "";
        for (String part : parts) {
            s += part + ",";
        }
        this.dataWatcher.updateObject(18, s);
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }
}
