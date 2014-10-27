/*
 * Copyright (C) 2014  Kihira
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package kihira.minicreatures.common.entity;

import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.foxlib.common.gson.GsonHelper;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.customizer.EnumPartCategory;
import kihira.minicreatures.common.entity.ai.EntityAIHeal;
import kihira.minicreatures.common.entity.ai.EntityAIIdleBlockChat;
import kihira.minicreatures.common.entity.ai.EntityAIIdleEntityChat;
import kihira.minicreatures.common.entity.ai.EnumRole;
import kihira.minicreatures.common.personality.IPersonality;
import kihira.minicreatures.common.personality.Mood;
import kihira.minicreatures.common.personality.MoodVariable;
import kihira.minicreatures.common.personality.Personality;
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
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;

public class EntityMiniPlayer extends EntityTameable implements IMiniCreature, ICustomisable, IRangedAttackMob, IPersonality {

    private final InventoryBasic inventory = new InventoryBasic(this.getCommandSenderName(), false, 18);
    private final EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 20, 50, 15F); //Set par3 and par4 to the same to have a consant firing rate. par5 seems to effect damage output. Higher = more damage falloff
    private final EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, 1.0D, true);
    private Personality personality = new Personality();

    //Maintain an array list client side for previewing
    @SideOnly(Side.CLIENT)
    private ArrayList<String> previewParts = new ArrayList<String>();

    @SideOnly(Side.CLIENT)
    public String statMessage = "";
    @SideOnly(Side.CLIENT)
    public int statMessageTime = 60;

    private int itemUseCount = -1;

    public EntityMiniPlayer(World par1World) {
        super(par1World);
        this.setSize(0.4F, 1F);
        this.getNavigator().setAvoidsWater(true);
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.tasks.addTask(4, new EntityAIHeal(this, 150, 1, true));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.tasks.addTask(7, new EntityAIIdleBlockChat(this, 6));
        this.tasks.addTask(7, new EntityAIIdleEntityChat(this, 12));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.setTamed(false);
        this.renderDistanceWeight = 4D;

        if (par1World != null && !par1World.isRemote) this.setCombatAI();
    }

    public boolean isChild() {
        return false;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(18, ""); //Parts list
        this.dataWatcher.addObject(19, 0); //Has Mind Control device
        this.dataWatcher.addObject(20, ""); //Chat
        this.dataWatcher.addObject(21, 0); //Role ID
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30000001192092896D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        //Save personality
        tagCompound.setString("Personality", GsonHelper.toJson(this.personality));

        //Save parts list
        NBTTagList nbttaglist = new NBTTagList();
        for (String part : this.dataWatcher.getWatchableObjectString(18).split(",")) {
            if (part != null) nbttaglist.appendTag(new NBTTagString(part));
        }
        tagCompound.setTag("Parts", nbttaglist);

        //Role
        tagCompound.setInteger("Role", getRole().ordinal());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        //Load Personality
        Personality personality = GsonHelper.createGson(Mood.class).fromJson(tagCompound.getString("Personality"), Personality.class);
        if (personality != null) {
            this.personality = personality;
            System.out.println(this.personality);
        }

        //Load parts list
        NBTTagList tagList = tagCompound.getTagList("Parts", 8);
        String s = "";
        for (int i = 0; i < tagList.tagCount(); i++) {
            s += tagList.getStringTagAt(i) + ",";
        }
        this.dataWatcher.updateObject(18, s);

        //Role
        setRole(EnumRole.values()[tagCompound.getInteger("Role")]);

        this.setCombatAI();
        this.inventory.func_110133_a(this.getCommandSenderName());
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (!this.worldObj.isRemote) {
            //Allow taming in creative mode without items
            if (!this.isTamed() && player.capabilities.isCreativeMode) {
                this.func_152115_b(player.getUniqueID().toString()); //Set owner UUID
                this.setTamed(true);
            }
            //If tamed
            if (this.isTamed()) {
                //If player has item
                if (itemstack != null) {
                    //If armour, equip it
                    if (itemstack.getItem() instanceof ItemArmor && !player.isSneaking()) {
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
                    //If lead and mounted, unmount
                    else if (itemstack.getItem() == Items.lead && this.isRiding() && !player.isSneaking()) this.mountEntity(null);
                    //If entity is holding something and player is holding a stick, drop current item
                    else if (itemstack.getItem() == Items.stick && this.getHeldItem() != null) {
                        EntityItem entityItem = new EntityItem(player.worldObj, this.posX, this.posY, this.posZ, this.getHeldItem().copy());
                        player.worldObj.spawnEntityInWorld(entityItem);
                        this.setCarrying(null);
                        //Drop chest contents
                        for (int i = 0; i < inventory.getSizeInventory(); i++) {
                            ItemStack itemStackToDrop = inventory.getStackInSlot(i);
                            if (itemStackToDrop != null) {
                                entityItem = new EntityItem(player.worldObj, this.posX, this.posY, this.posZ, itemStackToDrop);
                                player.worldObj.spawnEntityInWorld(entityItem);
                            }
                            inventory.setInventorySlotContents(i, null);
                        }
                    }
                    else if (itemstack.getItem() == Items.diamond) {
                        personality.changeMoodVariableLevel(this, "happiness", 5);
                    }
                    else if (this.getHeldItem() == null) {
                        ItemStack newItemStack = itemstack.copy();
                        newItemStack.stackSize = 1;
                        this.setCarrying(newItemStack);
                        player.playSound("mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                        if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                }
                //If entity is holding something and player isn't, open GUI
                if (this.getHeldItem() != null && !player.isSneaking()) {
                    if (Block.getBlockFromItem(this.getHeldItem().getItem()) == Blocks.chest) player.openGui(MiniCreatures.instance, 0, player.worldObj, this.getEntityId(), 0, 0);
                    else if (Block.getBlockFromItem(this.getHeldItem().getItem()) == Blocks.anvil) player.openGui(MiniCreatures.instance, 1, player.worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
                }
                else if (player.isEntityEqual(this.getOwner()) && !this.worldObj.isRemote) {
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
    protected void updateAITick() {
        //Gotta do this so it doesn't get stuck shooting arrows
        if (this.getAttackTarget() != null && this.getAttackTarget().isDead) this.setAttackTarget(null);
        super.updateAITick();
        this.personality.onUpdate(this);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isRemote && this.statMessageTime < 60) {
            this.statMessageTime++;
        }
        //Use item
        if (itemUseCount > -1) {
            if (itemUseCount % 4 == 0) {
                if (getHeldItem().getItemUseAction() == EnumAction.drink) {
                    this.playSound("random.drink", 0.3F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                }
            }
            if (itemUseCount-- == 0) {
                onItemUseFinish();
            }
        }
    }

    @Override
    public void setAttackTarget(EntityLivingBase attackTarget) {
        if (this.isRiding() && this.ridingEntity instanceof EntityFox) {
            EntityFox entityFox = (EntityFox) this.ridingEntity;
            entityFox.setAttackTarget(attackTarget);
        }
        super.setAttackTarget(attackTarget);
    }

    public void attackEntity(Entity par1Entity, float par2) {
        if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            this.swingItem();
            this.attackEntityAsMob(par1Entity);
            this.personality.changeMoodVariableLevel(this, "happiness", -5);
            this.personality.changeMoodVariableLevel(this, "hostility", 5);
        }
    }

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
        EntityArrow entityarrow = new EntityArrow(this.worldObj, this, var1, Math.max(1F, getDistanceToEntity(var1) / 10F), 1);
        entityarrow.posX = this.posX;
        entityarrow.posY = this.posY + this.getEyeHeight();
        entityarrow.posZ = this.posZ;

        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        double damage = (double)(var2 * 3.0F) + this.rand.nextGaussian() * 0.25D;
        entityarrow.setDamage(damage);

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

    public void setMindControlled(boolean mindControlled) {
        this.dataWatcher.updateObject(19, mindControlled ? 1 : 0);
    }

    public boolean isMindControlled() {
        return this.dataWatcher.getWatchableObjectInt(19) == 1;
    }

    public void setRole(EnumRole role) {
        EnumRole.resetAI(this);
        role.applyAI(this);
        this.dataWatcher.updateObject(21, role.ordinal());
    }

    public EnumRole getRole() {
        int id = this.dataWatcher.getWatchableObjectInt(21);
        if (id <= EnumRole.values().length) return EnumRole.values()[id];
        else return EnumRole.NONE;
    }

    public void setHeldItemInUse() {
        if (getHeldItem() != null) {
            itemUseCount = getHeldItem().getMaxItemUseDuration();
        }
    }

    private void onItemUseFinish() {
        if (getHeldItem() != null) {
            //TODO Not ideal but all methods require a player so instead we'll just null the current item. Need to fix it to work with stackable items
            //Maybe a fake player so we can pass them?
            setCurrentItemOrArmor(0, null);
            itemUseCount = -1;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getItemUseCount() {
        return itemUseCount;
    }

    @SideOnly(Side.CLIENT)
    public int getItemInUseDuration() {
        return itemUseCount > -1 ? getHeldItem().getMaxItemUseDuration() - itemUseCount : 0;
    }

    @Override
    public String getChat() {
        return this.dataWatcher.getWatchableObjectString(20);
    }

    @Override
    public void setChat(String string) {
        this.dataWatcher.updateObject(20, Strings.nullToEmpty(string));
    }

    @Override
    public void setCustomNameTag(String par1Str) {
        super.setCustomNameTag(par1Str);
        this.inventory.func_110133_a(par1Str);
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

    @Override
    public Personality getPersonality() {
        return this.personality;
    }

    @Override
    public void setPersonality(Personality newPersonality) {
        //Check if there is an existing one
        if (this.getPersonality() != null) {
            Personality oldPersonality = this.getPersonality();
            String stat = "";
            //Loop through the new personalities
            for (Map.Entry<String, MoodVariable> entry : newPersonality.moodVariables.entrySet()) {
                int newValue = entry.getValue().getCurrentValue();
                int oldValue = oldPersonality.getMoodVariableValue(entry.getKey());
                //If greater, show that's increased
                if (newValue > oldValue) {
                    stat += EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("mood.variable." + entry.getKey() + ".name") + "+;";
                }
                //If less, show that's decreased
                else if (newValue < oldValue) {
                    stat += EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("mood.variable." + entry.getKey() + ".name") + "-;";
                }
            }
            //If any stats have changed, show them
            if (!stat.isEmpty()) {
                this.statMessage = stat;
                this.statMessageTime = 0;
            }
        }

        this.personality = newPersonality;
    }

    @Override
    public EntityLiving theEntity() {
        return this;
    }
}
