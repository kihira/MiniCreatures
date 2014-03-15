package kihira.minicreatures.client.render;

import kihira.minicreatures.client.model.ModelMiniPlayer;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

public class RenderMiniPlayer extends RenderBiped {

    public RenderMiniPlayer() {
        super(new ModelMiniPlayer(), 0.3F);
    }

    //Copied from RenderPlayer.renderSpecials
    private void renderCarrying(EntityMiniPlayer entityMiniPlayer, float par2) {
        ItemStack itemstack = entityMiniPlayer.getHeldItem();
        if (itemstack != null) {
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));
            if (entityMiniPlayer.isSitting()) GL11.glTranslatef(0F, 0.3F, 0F);
            if (itemstack.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))) {
                GL11.glTranslatef(0.0F, 0.8875F, -0.3F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-0.3F, -0.3F, -0.3F);
                this.renderManager.itemRenderer.renderItem(entityMiniPlayer, itemstack, 0);
            }
            else {
                GL11.glTranslatef(0F, 0.725F, -0.025F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                super.renderEquippedItems(entityMiniPlayer, par2);
            }
        }
    }

    @Override
    protected void func_82421_b() {
        //We need to set these values so armour renders properly. Not fucking idea why
        this.field_82423_g = new ModelMiniPlayer(1.0F);
        this.field_82425_h = new ModelMiniPlayer(0.5F);
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
        this.renderCarrying((EntityMiniPlayer) par1EntityLivingBase, par2);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        EntityMiniPlayer entityMiniPlayer = (EntityMiniPlayer)entity;
        if (entityMiniPlayer.hasCustomNameTag()) {
            resourcelocation = AbstractClientPlayer.getLocationSkull(entityMiniPlayer.getCustomNameTag());
            AbstractClientPlayer.getDownloadImageSkin(resourcelocation, entityMiniPlayer.getCustomNameTag());
        }
        return resourcelocation;
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer)par1Entity;
        ((ModelMiniPlayer)this.mainModel).bipedHeadwear.showModel = miniPlayer.getEquipmentInSlot(4) == null;
        super.doRender((EntityLiving) par1Entity, par2, par4, par6, par8, par9);
    }
}
