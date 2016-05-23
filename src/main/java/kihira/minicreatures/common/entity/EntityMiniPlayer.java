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
import com.mojang.realmsclient.gui.ChatFormatting;
import kihira.foxlib.common.gson.GsonHelper;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.Utils;
import kihira.minicreatures.common.customizer.EnumPartCategory;
import kihira.minicreatures.common.entity.ai.EntityAIHeal;
import kihira.minicreatures.common.entity.ai.EntityAIProspect;
import kihira.minicreatures.common.entity.ai.EnumRole;
import kihira.minicreatures.common.entity.ai.combat.EntityAIBowAttack;
import kihira.minicreatures.common.entity.ai.combat.EntityAIUsePotion;
import kihira.minicreatures.common.entity.ai.idle.EntityAIIdleBlockChat;
import kihira.minicreatures.common.entity.ai.idle.EntityAIIdleEntityChat;
import kihira.minicreatures.common.personality.IPersonality;
import kihira.minicreatures.common.personality.Mood;
import kihira.minicreatures.common.personality.MoodVariable;
import kihira.minicreatures.common.personality.Personality;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

public class EntityMiniPlayer extends EntityTameable implements IMiniCreature, ICustomisable, IRangedAttackMob, IPersonality {

    private static final AttributeModifier itemUseSlowdown = (new AttributeModifier(UUID.fromString("D59B2377-0D44-455D-BE81-AA60D7485D52"), "Item Use Penalty", -0.25D, 0)).setSaved(false);
    private static final DataParameter<String> PARTS = EntityDataManager.createKey(EntityMiniPlayer.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> MIND_CONTROLLED = EntityDataManager.createKey(EntityMiniPlayer.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> CHAT = EntityDataManager.createKey(EntityMiniPlayer.class, DataSerializers.STRING);
    private static final DataParameter<Integer> ROLE = EntityDataManager.createKey(EntityMiniPlayer.class, DataSerializers.VARINT);

    private final InventoryBasic inventory = new InventoryBasic(this.getName(), false, 18);
    private final EntityAIBowAttack aiArrowAttack = new EntityAIBowAttack(this, 1D, 20, 50);
    private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 1D, true);
    private Personality personality = new Personality();

    //Maintain an array list client side for previewing
    @SideOnly(Side.CLIENT)
    private ArrayList<String> previewParts = new ArrayList<>();

    @SideOnly(Side.CLIENT)
    public String statMessage = "";
    @SideOnly(Side.CLIENT)
    public int statMessageTime = 60;

    public EntityMiniPlayer(World par1World) {
        super(par1World);
        this.setSize(0.3F, 1.1F);
        this.setTamed(false);
        this.setCombatAI();
    }

    public boolean isChild() {
        return false;
    }

    protected void entityInit() {
        super.entityInit();

        this.getDataManager().register(PARTS, "");
        this.getDataManager().register(MIND_CONTROLLED, false);
        this.getDataManager().register(CHAT, "");
        this.getDataManager().register(ROLE, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40);
    }

    @Override
    protected void initEntityAI() {
        setRole(EnumRole.NONE);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        //Save personality
        tagCompound.setString("Personality", GsonHelper.toJson(this.personality));

        //Save parts list
        NBTTagList nbttaglist = new NBTTagList();
        for (String part : this.getDataManager().get(PARTS).split(",")) {
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
        this.getDataManager().set(PARTS, s);

        //Role
        setRole(EnumRole.values()[tagCompound.getInteger("Role")]);

        this.setCombatAI();
        this.inventory.setCustomName(this.getName());
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack) {
        if (!this.worldObj.isRemote && hand == EnumHand.MAIN_HAND) { // todo hand check is fix to prevent double activation
            //MiniCreatures.proxy.simpleNetworkWrapper.sendTo(new ProspectBlocksMessage(this.getEntityId(), 5, 10), (EntityPlayerMP) this.getOwner());
            //Allow taming in creative mode without items
            if (!this.isTamed() && player.capabilities.isCreativeMode) {
                this.setOwnerId(player.getPersistentID());
                this.setTamed(true);
            }
            //If tamed
            if (this.isTamed()) {
                //If player has item
                if (stack != null) {
                    //If armour, equip it
                    if (stack.getItem() instanceof ItemArmor && !player.isSneaking()) {
                        EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(stack);
                        if (this.getItemStackFromSlot(slot) == null) {
                            this.setItemStackToSlot(slot, stack.copy());
                            Utils.decreaseCurrentStack(player, stack);
                        }
                        else {
                            EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.getItemStackFromSlot(slot));
                            this.worldObj.spawnEntityInWorld(entityItem);
                            this.setItemStackToSlot(slot, null);
                        }
                        return true;
                    }
                    //If lead and mounted, unmount
                    else if (stack.getItem() == Items.LEAD && this.isRiding() && !player.isSneaking()) this.dismountRidingEntity();
                    //If entity is holding something and player is holding a stick, drop current item
                    else if (stack.getItem() == Items.STICK && this.getHeldItem(EnumHand.MAIN_HAND) != null) {
                        EntityItem entityItem = new EntityItem(player.worldObj, this.posX, this.posY, this.posZ, this.getHeldItem(EnumHand.MAIN_HAND).copy());
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
                    else if (stack.getItem() == Items.DIAMOND) {
                        personality.changeMoodVariableLevel(this, "happiness", 5);
                    }
                    else if (this.getHeldItem(EnumHand.MAIN_HAND) == null) {
                        ItemStack newItemStack = stack.copy();
                        newItemStack.stackSize = 1;
                        this.setCarrying(newItemStack);
                        playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                        Utils.decreaseCurrentStack(player, stack);
                    }
                }
                //If entity is holding something and player isn't, open GUI
                else if (this.getHeldItem(EnumHand.MAIN_HAND) != null && !player.isSneaking()) {
                    if (Block.getBlockFromItem(this.getHeldItem(EnumHand.MAIN_HAND).getItem()) == Blocks.CHEST) player.openGui(MiniCreatures.instance, 0, player.worldObj, this.getEntityId(), 0, 0);
                    else if (Block.getBlockFromItem(this.getHeldItem(EnumHand.MAIN_HAND).getItem()) == Blocks.ANVIL) player.openGui(MiniCreatures.instance, 1, player.worldObj, (int) this.posX, (int) this.posY, (int) this.posZ);
                }
                else if (player.isEntityEqual(this.getOwner()) && !this.worldObj.isRemote) {
                    if (this.isRiding()) {
                        EntityTameable ridingEntity = (EntityTameable) this.getRidingEntity();
                        ridingEntity.getAISit().setSitting(!ridingEntity.isSitting());
                        ridingEntity.setJumping(false);
                        this.getNavigator().clearPathEntity();
                        this.setRevengeTarget(null);
                        this.setAttackTarget(null);
                    }
                    else {
                        this.getAISit().setSitting(!this.isSitting());
                        this.isJumping = false;
                        this.getNavigator().clearPathEntity();
                        this.setRevengeTarget(null);
                        this.setAttackTarget(null);
                    }
                }
            }
        }
        //setCarrying(new ItemStack(Items.potionitem, 1, 16457));
        return super.processInteract(player, hand, stack);
    }

    @Override
    protected void updateAITasks() {
        //Gotta do this so it doesn't get stuck shooting arrows
        if (this.getAttackTarget() != null && this.getAttackTarget().isDead) this.setAttackTarget(null);
        super.updateAITasks();
        this.personality.onUpdate(this);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.updateArmSwingProgress();
        if (this.worldObj.isRemote && this.statMessageTime < 60) {
            this.statMessageTime++;
        }
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if (EntityTameable.TAMED.equals(key)) {
            if (this.isSitting()) this.setSize(0.3f, 0.85f);
            else setSize(0.3f, 1.1f);
            setScale(1f);
        }
    }

    @Override
    public void setAttackTarget(EntityLivingBase attackTarget) {
        if (this.isRiding() && this.getRidingEntity() instanceof EntityFox) {
            EntityFox entityFox = (EntityFox) this.getRidingEntity();
            entityFox.setAttackTarget(attackTarget);
        }
        super.setAttackTarget(attackTarget);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float attackDamage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        float knockback = 0;

        if (target instanceof EntityLivingBase) {
            attackDamage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)target).getCreatureAttribute());
            knockback += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean damaged = target.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);

        if (damaged) {
            if (knockback > 0) {
                ((EntityLivingBase)target).knockBack(this, knockback * 0.5F, MathHelper.sin(this.rotationYaw * 0.017453292F), -MathHelper.cos(this.rotationYaw * 0.017453292F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int fire = EnchantmentHelper.getFireAspectModifier(this);
            if (fire > 0) target.setFire(fire * 4);

            if (target instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)target;
                ItemStack itemMainhand = this.getHeldItemMainhand();
                ItemStack itemActive = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

                if (itemMainhand != null && itemActive != null && itemMainhand.getItem() instanceof ItemAxe && itemActive.getItem() == Items.SHIELD) {
                    float shieldChance = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

                    if (this.rand.nextFloat() < shieldChance) {
                        entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                        this.worldObj.setEntityState(entityplayer, (byte)30);
                    }
                }
            }

            this.personality.changeMoodVariableLevel(this, "happiness", -5);
            this.personality.changeMoodVariableLevel(this, "hostility", 5);

            this.applyEnchantments(this, target);
        }

        return damaged;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float velocity) {
        EntityArrow entityarrow = new EntityTippedArrow(this.worldObj, this);
        double xDist = target.posX - this.posX;
        double yDist = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
        double zDist = target.posZ - this.posZ;
        double d3 = (double)MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
        entityarrow.setThrowableHeading(xDist, yDist + d3 * 0.20000000298023224D, zDist, 1.6F, (float)(14 - this.worldObj.getDifficulty().getDifficultyId() * 4));

        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, this.getHeldItem(EnumHand.MAIN_HAND));
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, this.getHeldItem(EnumHand.MAIN_HAND));

        double damage = (double)(velocity * 3.0F) + this.rand.nextGaussian() * 0.25D;
        entityarrow.setDamage(damage);

        if (power > 0) entityarrow.setDamage(entityarrow.getDamage() + (double)power * 0.5D + 0.5D);
        if (punch > 0) entityarrow.setKnockbackStrength(punch);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, this.getHeldItem(EnumHand.MAIN_HAND)) > 0) entityarrow.setFire(100);

        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entityarrow);
    }

    public void setCombatAI() {
        this.tasks.removeTask(this.aiAttackOnCollide);
        this.tasks.removeTask(this.aiArrowAttack);
        ItemStack itemstack = this.getHeldItem(EnumHand.MAIN_HAND);

        if (itemstack != null && itemstack.getItem() == Items.BOW) this.tasks.addTask(3, this.aiArrowAttack);
        else this.tasks.addTask(3, this.aiAttackOnCollide);
    }

    public void setCarrying(ItemStack itemStack) {
        this.setHeldItem(EnumHand.MAIN_HAND, itemStack);
        this.setCombatAI();
    }

    public void setMindControlled(boolean mindControlled) {
        this.getDataManager().set(MIND_CONTROLLED, mindControlled);
    }

    public boolean isMindControlled() {
        return this.getDataManager().get(MIND_CONTROLLED);
    }

    public void setRole(EnumRole role) {
        EnumRole.resetAI(this);
        role.applyAI(this);
        this.getDataManager().set(ROLE, role.ordinal());
    }

    public EnumRole getRole() {
        int id = this.getDataManager().get(ROLE);
        if (id <= EnumRole.values().length) return EnumRole.values()[id];
        else return EnumRole.NONE;
    }

    @Override
    public String getChat() {
        return this.getDataManager().get(CHAT);
    }

    @Override
    public void setChat(String string) {
        this.getDataManager().set(CHAT, Strings.nullToEmpty(string));
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.inventory.setCustomName(name);
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
            ArrayList<String> parts = new ArrayList<>();
            for (String part : this.getDataManager().get(PARTS).split(",")) {
                if (!Strings.isNullOrEmpty(part)) parts.add(part);
            }
            return parts;
        }
    }

    @Override
    public void setParts(ArrayList<String> parts, boolean isPreview) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            if (isPreview) this.previewParts = parts;
            else this.previewParts = new ArrayList<>();
        }
        String s = "";
        for (String part : parts) {
            s += part + ",";
        }
        this.getDataManager().set(PARTS, s);
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }

    @Override
    public void applyAI(EnumRole role) {
        //General tasks
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, aiSit = new EntityAISit(this));
        tasks.addTask(4, new EntityAIHeal(this, 150, 1));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8F));
        tasks.addTask(6, new EntityAILookIdle(this));
        tasks.addTask(7, new EntityAIIdleBlockChat(this, 6));
        tasks.addTask(7, new EntityAIIdleEntityChat(this, 12));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));

        switch (role) {
            case COMBAT: {
                tasks.addTask(3, new EntityAIUsePotion(this, 0.5F, 2, 100));
                tasks.addTask(4, new EntityAIFollowOwner(this, 1.2D, 4F, 4F));
                break;
            }
            case MINER: {
                tasks.addTask(4, new EntityAIFollowOwner(this, 1.1D, 4F, 4F));
                tasks.addTask(6, new EntityAIProspect(this, 25, 6, 3));
                break;
            }
            default: {
                tasks.addTask(4, new EntityAIFollowOwner(this, 1.1D, 10F, 4F));
                tasks.addTask(5, new EntityAIWander(this, 1D));
            }
        }
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
                    stat += ChatFormatting.DARK_GREEN + I18n.format("mood.variable." + entry.getKey() + ".name") + "+;";
                }
                //If less, show that's decreased
                else if (newValue < oldValue) {
                    stat += ChatFormatting.DARK_RED + I18n.format("mood.variable." + entry.getKey() + ".name") + "-;";
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
