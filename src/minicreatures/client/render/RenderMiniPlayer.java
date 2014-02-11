package minicreatures.client.render;

import minicreatures.client.model.ModelMiniPlayer;
import minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sun.net.www.content.text.plain;

public class RenderMiniPlayer extends RenderBiped {

    public RenderMiniPlayer() {
        super(new ModelMiniPlayer(), 0.5F);
    }

    protected void renderCarrying(EntityMiniPlayer miniPlayer, float par2) {
        super.renderEquippedItems(miniPlayer, par2);
        if (miniPlayer.getCarrying() > 0) {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glPushMatrix();
            float f1 = 0.3F;
            GL11.glTranslatef(0.0F, 0.8875F, -0.3F);
            //f1 *= 1.0F;
            GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-f1, -f1, f1);
            int i = miniPlayer.getBrightnessForRender(par2);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(TextureMap.locationBlocksTexture);
            this.renderBlocks.renderBlockAsItem(Block.blocksList[miniPlayer.getCarrying()], miniPlayer.getCarrying(), 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
        this.renderCarrying((EntityMiniPlayer)par1EntityLivingBase, par2);
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
        ((ModelMiniPlayer)this.mainModel).isCarrying = miniPlayer.getCarrying() > 0;
        super.doRenderLiving((EntityLiving) par1Entity, par2, par4, par6, par8, par9);
    }
}
