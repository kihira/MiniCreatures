package kihira.minicreatures.client.model;

import kihira.minicreatures.client.gui.GuiCustomizer;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.customizer.ICustomizerPart;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import org.lwjgl.opengl.GL11;

public class ModelMiniPlayer extends ModelBiped {

    public ModelMiniPlayer() {
        this(0.0F);
    }

    public ModelMiniPlayer(float par1) {
        super(par1, 0.0F, 64, 32);

        this.bipedBody.rotateAngleX = 0.0F;
        this.bipedRightLeg.rotationPointZ = 0.1F;
        this.bipedLeftLeg.rotationPointZ = 0.1F;
        this.bipedRightLeg.rotationPointY = 12.0F;
        this.bipedLeftLeg.rotationPointY = 12.0F;
        this.bipedHead.rotationPointY = 0.0F;
        this.bipedHeadwear.rotationPointY = 0.0F;
    }

    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        IMiniCreature miniPlayer = (IMiniCreature)par1Entity;
        //GL11.glPushMatrix();
        this.setRotationAngles(par2, par3, par4, par5, par6, par7, par1Entity);
        float f6 = 2.0F;
        GL11.glPushMatrix();
        GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
        GL11.glTranslatef(0.0F, 16.0F * par7, 0.0F);
        this.bipedHead.render(par7);
        this.bipedHeadwear.render(par7);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
        GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
        this.bipedBody.render(par7);
        this.bipedRightArm.render(par7);
        this.bipedLeftArm.render(par7);
        this.bipedRightLeg.render(par7);
        this.bipedLeftLeg.render(par7);
        GL11.glPopMatrix();

        for (String partName : miniPlayer.getCurrentParts(Minecraft.getMinecraft().currentScreen instanceof GuiCustomizer)) {
            ICustomizerPart part = CustomizerRegistry.getPart(partName);
            if (part != null) part.render(par1Entity, this, par2, par3, par4, par5, par6, par7);
        }
        //GL11.glPopMatrix();
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) par7Entity;
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, miniPlayer);

        if (this.isRiding) {
            GL11.glTranslatef(0F, 0F, 0.1F);
            if ((miniPlayer.ridingEntity instanceof EntityFox) && (((EntityFox) miniPlayer.ridingEntity).isSitting())) GL11.glTranslatef(0F, 0.2F, 0F);
        }

        if (this.heldItemRight != 0 && (miniPlayer.getCarrying().getItem() instanceof ItemBlock)) {
            this.bipedLeftArm.rotateAngleX = -0.8F;
            this.bipedLeftArm.rotateAngleZ = -0.05F;
            this.bipedRightArm.rotateAngleX = -0.8F;
            this.bipedRightArm.rotateAngleZ = 0.05F;
        }

        if (miniPlayer.isSitting()) {
            GL11.glTranslatef(0F, 0.3F, 0F);
            this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -((float)Math.PI * 2.5F / 5F);
            this.bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2.5F / 5F);
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
        }
    }

    public void setLivingAnimations(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer) par1EntityLivingBase;
        this.aimedBow = !miniPlayer.isSitting() && (miniPlayer.getCarrying() != null) && (miniPlayer.getCarrying().getItem() == Items.bow);
        super.setLivingAnimations(par1EntityLivingBase, par2, par3, par4);
    }
}
