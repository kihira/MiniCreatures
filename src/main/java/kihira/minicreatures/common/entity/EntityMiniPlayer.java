package kihira.minicreatures.common.entity;

import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.customizer.EnumPartCategory;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class EntityMiniPlayer extends EntityTameable implements IMiniCreature, IRangedAttackMob {

    private final InventoryBasic inventory = new InventoryBasic(this.getCommandSenderName(), false, 18);
    //True if aiming with bow. Not currently in use.
    public boolean isAiming = false;

    private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F);
    private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, 1.2D, true);

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
        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.setTamed(false);

        if (par1World != null && !par1World.isRemote) this.setCombatAI();
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
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40D);
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
        this.setCombatAI();
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
                    if (this.isRiding()) {
                        EntityTameable ridingEntity = (EntityTameable) this.ridingEntity;
                        ridingEntity.func_70907_r().setSitting(!ridingEntity.isSitting());
                        ridingEntity.setSitting(!ridingEntity.isSitting());
                        ridingEntity.setJumping(false);
                        ridingEntity.setPathToEntity(null);
                        ridingEntity.setTarget(null);
                        ridingEntity.setAttackTarget(null);
                    }
                    else {
                        this.aiSit.setSitting(!this.isSitting());
                        this.setSitting(!this.isSitting());
                        this.isJumping = false;
                        this.setPathToEntity(null);
                        this.setTarget(null);
                        this.setAttackTarget(null);
                    }
                }
            }
        }
        return super.interact(player);
    }

    @Override
    public void onLivingUpdate() {
        //Gotta do this so it doesn't get stuck shooting arrows
        if (this.getAttackTarget() != null && this.getAttackTarget().isDead) this.setAttackTarget(null);
        super.onLivingUpdate();
    }

    @Override
    public void setAttackTarget(EntityLivingBase attackTarget) {
        if (this.isRiding() && this.ridingEntity instanceof EntityFox) {
            EntityFox entityFox = (EntityFox) this.ridingEntity;
            entityFox.setPathToEntity(entityFox.getNavigator().getPathToEntityLiving(attackTarget));
        }
        super.setAttackTarget(attackTarget);
    }

    /*
    protected void attackEntity(Entity par1Entity, float par2) {
        if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            this.attackEntityAsMob(par1Entity);
        }
    }
    */

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        float attackDamage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int knockback = 0;

        if (par1Entity instanceof EntityLivingBase) {
            attackDamage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) par1Entity);
            knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)par1Entity);
        }

        boolean flag = par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);

        if (flag) {
            if (knockback > 0) {
                par1Entity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float)knockback * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);
            if (j > 0) par1Entity.setFire(j * 4);

            if (par1Entity instanceof EntityLivingBase) EnchantmentHelper.func_151384_a((EntityLivingBase)par1Entity, this);

            EnchantmentHelper.func_151385_b(this, par1Entity);
        }

        return flag;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2) {
        EntityArrow entityarrow = new EntityArrow(this.worldObj, this, var1, 1.6F, 2);
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        entityarrow.setDamage((double)(var2 * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.difficultySetting.getDifficultyId() * 0.11F));

        if (power > 0) entityarrow.setDamage(entityarrow.getDamage() + (double)power * 0.5D + 0.5D);
        if (punch > 0) entityarrow.setKnockbackStrength(punch);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0) entityarrow.setFire(100);

        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entityarrow);
    }

    public void setCombatAI() {
        this.tasks.removeTask(this.aiAttackOnCollide);
        this.tasks.removeTask(this.aiArrowAttack);
        ItemStack itemstack = this.getHeldItem();

        if (itemstack != null && itemstack.getItem() == Items.bow) this.tasks.addTask(3, this.aiArrowAttack);
        else this.tasks.addTask(3, this.aiAttackOnCollide);
    }

    public void setCarrying(ItemStack itemStack) {
        this.setCurrentItemOrArmor(0, itemStack);
        this.setCombatAI();
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
