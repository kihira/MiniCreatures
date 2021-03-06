package kihira.minicreatures.common.entity;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.ai.EnumRole;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class EntityMiniCreature extends EntityTameable implements IMiniCreature {

    private static final DataParameter<Boolean> HAS_CHEST = EntityDataManager.createKey(EntityMiniCreature.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> COLLAR_COLOUR = EntityDataManager.createKey(EntityMiniCreature.class, DataSerializers.BYTE);
    public static final DataParameter<Boolean> IS_HAPPY = EntityDataManager.createKey(EntityMiniCreature.class, DataSerializers.BOOLEAN);

    private final IInventory inventory = new InventoryBasic(this.getName(), false, 18);
    private final Item tamingItem;

    EntityMiniCreature(World worldIn, Item tamingItem) {
        super(worldIn);
        this.tamingItem = tamingItem;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(HAS_CHEST, false); //Has chest
        this.getDataManager().register(COLLAR_COLOUR, (byte) 0); //Collar colour
        this.getDataManager().register(IS_HAPPY, false); //Is happy. TODO temp until proper AI implementation
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3d);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);

        if (this.hasChest()) {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
                if (this.inventory.getStackInSlot(i) != null) {
                    NBTTagCompound stacktag = new NBTTagCompound();
                    stacktag.setByte("Slot", (byte)i);
                    this.inventory.getStackInSlot(i).writeToNBT(stacktag);
                    nbttaglist.appendTag(stacktag);
                }
            }
            tag.setTag("Items", nbttaglist);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);

        if (this.hasChest()) {
            NBTTagList nbttaglist = tag.getTagList("Items", 0);
            for (int i = 0; i < nbttaglist.tagCount(); i++) {
                NBTTagCompound stacktag = nbttaglist.getCompoundTagAt(i);
                int j = stacktag.getByte("Slot");
                if (j >= 0 && j < this.inventory.getSizeInventory()) this.inventory.setInventorySlotContents(j, new ItemStack(stacktag));
            }
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (this.isTamed()) {
            // Heal
            if (stack.getItem() instanceof ItemFood) {
                ItemFood itemfood = (ItemFood)stack.getItem();
                if (itemfood.isWolfsFavoriteMeat() && this.getHealth() < this.getMaxHealth()) {
                    this.heal((float) itemfood.getHealAmount(stack));
                    stack.shrink(1);
                    return true;
                }
            }
            // Add chest
            else if (Block.getBlockFromItem(stack.getItem()) == Blocks.CHEST && !this.hasChest()) {
                this.setHasChest(true);
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                stack.shrink(1);
                return true;
            }
            // Dye collar
            else if (stack.getItem() == Items.DYE) {
                EnumDyeColor i = EnumDyeColor.byDyeDamage(stack.getMetadata());
                if (i != this.getCollarColour()) {
                    this.setCollarColour(i);
                    stack.shrink(1);
                    return true;
                }
            }
            // Open chest
            if (!player.isSneaking() && this.hasChest()) {
                //Send Entity ID as x coord. Inspired by OpenBlocks
                player.openGui(MiniCreatures.instance, 0, player.world, this.getEntityId(), 0, 0);
            }
            else if (player.isEntityEqual(this.getOwner()) && !this.world.isRemote && !this.isBreedingItem(stack)) {
                this.getAISit().setSitting(!this.isSitting());
                this.isJumping = false;
                this.getNavigator().clearPath();
                this.setAttackTarget(null);
                this.setRevengeTarget(null);
            }
        }
        else if (stack.getItem() == tamingItem) {
            stack.shrink(1);
            if (!this.world.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.getNavigator().clearPath();
                    this.setAttackTarget(null);
                    this.getAISit().setSitting(true);
                    this.setHealth(20.0F);
                    this.setOwnerId(player.getPersistentID());
                    this.playTameEffect(true);
                    this.world.setEntityState(this, (byte)7);
                }
                else {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        //Drop chest contents on death
        if (!this.world.isRemote && hasChest()) {
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = inventory.getStackInSlot(i);
                if (itemstack != null) this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    @Override
    public boolean canMateWith(EntityAnimal target) {
        if (target == this || !this.isTamed() || !(target.getClass() == this.getClass())) return false;
        else {
            EntityMiniCreature entityMiniCreature = (EntityMiniCreature) target;
            return entityMiniCreature.isTamed() && (!entityMiniCreature.isSitting() && this.isInLove() && entityMiniCreature.isInLove());
        }
    }
    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean damage = target.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
        if (damage) this.applyEnchantments(this, target);

        return damage;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    public boolean hasChest() {
        return this.getDataManager().get(HAS_CHEST);
    }

    public void setHasChest(boolean hasChest) {
        this.getDataManager().set(HAS_CHEST, hasChest);
    }

    public EnumDyeColor getCollarColour() {
        return EnumDyeColor.byMetadata(this.getDataManager().get(COLLAR_COLOUR) & 15);
    }

    public void setCollarColour(EnumDyeColor colour) {
        this.dataManager.set(COLLAR_COLOUR, (byte) (this.dataManager.get(COLLAR_COLOUR) & 240 | colour.getMetadata() & 15));
    }

    @Override
    public IInventory getInventory() {
        return this.inventory;
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }

    @Override
    public void applyAI(EnumRole role) {}
}
