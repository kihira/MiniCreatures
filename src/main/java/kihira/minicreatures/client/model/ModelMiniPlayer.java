package kihira.minicreatures.client.model;

import kihira.minicreatures.common.CustomizerRegistry;
import kihira.minicreatures.common.ICustomizerPart;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelMiniPlayer extends ModelBiped {

    ModelRenderer tailBase;
    ModelRenderer tailPart1;
    ModelRenderer tailPart2;
    ModelRenderer tailTip;
    ModelRenderer hornLeftBase;
    ModelRenderer hornLeftPart1;
    ModelRenderer hornLeftPart2;
    ModelRenderer hornLeftPart3;
    ModelRenderer hornLeftPart4;
    ModelRenderer hornLeftPart5;
    ModelRenderer hornRightBase;
    ModelRenderer hornRightPart1;
    ModelRenderer hornRightPart2;
    ModelRenderer hornRightPart3;
    ModelRenderer hornRightPart4;
    ModelRenderer hornRightPart5;
    ModelRenderer fairyWingLeft;
    ModelRenderer fairy;
    ModelRenderer fairyWingRight;


    public ModelMiniPlayer() {
        super();
        tailBase = new ModelRenderer(this, 0, 0);
        tailBase.addBox(-1.5F, 8.8F, 3.25F, 3, 3, 2);
        tailBase.setRotationPoint(0F, 0F, 0F);
        tailBase.rotateAngleX = -0.1570796F;
        tailPart1 = new ModelRenderer(this, 0, 5);
        tailPart1.addBox(-1F, 6.75F, 8F, 2, 2, 3);
        tailPart1.setRotationPoint(0F, 0F, 0F);
        tailPart1.rotateAngleX = -0.5235988F;
        tailPart2 = new ModelRenderer(this, 0, 10);
        tailPart2.addBox(-1F, -0.7F, 12.9F, 2, 2, 4);
        tailPart2.setRotationPoint(0F, 0F, 0F);
        tailPart2.rotateAngleX = -1.134464F;
        tailTip = new ModelRenderer(this, 0, 16);
        tailTip.addBox(-0.5F, 14.8F, -7.7F, 1, 4, 1);
        tailTip.setRotationPoint(0F, 0F, 0F);
        tailTip.rotateAngleX = 0.8726646F;

        hornLeftBase = new ModelRenderer(this, 12, 0);
        hornLeftBase.addBox(3F, -9F, -2F, 2, 2, 2);
        hornLeftBase.setRotationPoint(0F, 0F, 0F);
        hornLeftPart1 = new ModelRenderer(this, 12, 4);
        hornLeftPart1.addBox(4F, -10F, -1F, 2, 2, 4);
        hornLeftBase.addChild(hornLeftPart1);
        hornLeftPart2 = new ModelRenderer(this, 12, 10);
        hornLeftPart2.addBox(5F, -9F, 1F, 2, 2, 3);
        hornLeftBase.addChild(hornLeftPart2);
        hornLeftPart3 = new ModelRenderer(this, 12, 15);
        hornLeftPart3.addBox(6F, -8F, 2F, 2, 3, 2);
        hornLeftBase.addChild(hornLeftPart3);
        hornLeftPart4 = new ModelRenderer(this, 12, 20);
        hornLeftPart4.addBox(7F, -6F, 1F, 2, 2, 2);
        hornLeftBase.addChild(hornLeftPart4);
        hornLeftPart5 = new ModelRenderer(this, 12, 24);
        hornLeftPart5.addBox(6F, -5F, -2F, 2, 2, 4);
        hornLeftBase.addChild(hornLeftPart5);

        hornRightBase = new ModelRenderer(this, 12, 0);
        hornRightBase.addBox(-5F, -9F, -2F, 2, 2, 2);
        hornRightBase.setRotationPoint(0F, 0F, 0F);
        hornRightPart1 = new ModelRenderer(this, 12, 4);
        hornRightPart1.addBox(-6F, -10F, -1F, 2, 2, 4);
        hornRightBase.addChild(hornRightPart1);
        hornRightPart2 = new ModelRenderer(this, 12, 10);
        hornRightPart2.addBox(-7F, -9F, 1F, 2, 2, 3);
        hornRightBase.addChild(hornRightPart2);
        hornRightPart3 = new ModelRenderer(this, 12, 15);
        hornRightPart3.addBox(-8F, -8F, 2F, 2, 3, 2);
        hornRightBase.addChild(hornRightPart3);
        hornRightPart4 = new ModelRenderer(this, 12, 20);
        hornRightPart4.addBox(-9F, -6F, 1F, 2, 2, 2);
        hornRightBase.addChild(hornRightPart4);
        hornRightPart5 = new ModelRenderer(this, 12, 24);
        hornRightPart5.addBox(-8F, -5F, -2F, 2, 2, 4);
        hornRightBase.addChild(hornRightPart5);

        fairy = new ModelRenderer(this, 24, 8);
        fairy.addBox(-13.1F, -9F, 5F, 4, 4, 4);
        fairy.setRotationPoint(0F, 0F, 0F);
        setRotation(fairy, -0.1047198F, 0F, 0F);
        fairyWingLeft = new ModelRenderer(this, 24, -8);
        fairyWingLeft.addBox(-12F, -9F, 6F, 0, 8, 8);
        setRotation(fairyWingLeft, -0.0349066F, 0.1745329F, 0.0872665F);
        fairy.addChild(fairyWingLeft);
        fairyWingRight = new ModelRenderer(this, 24, -8);
        fairyWingRight.addBox(-10F, -9.8F, 9F, 0, 8, 8);
        setRotation(fairyWingRight, 0.0349066F, -0.1745329F, -0.0872665F);
        fairy.addChild(fairyWingRight);

        this.bipedBody.rotateAngleX = 0.0F;
        this.bipedRightLeg.rotationPointZ = 0.1F;
        this.bipedLeftLeg.rotationPointZ = 0.1F;
        this.bipedRightLeg.rotationPointY = 12.0F;
        this.bipedLeftLeg.rotationPointY = 12.0F;
        this.bipedHead.rotationPointY = 0.0F;
        this.bipedHeadwear.rotationPointY = 0.0F;
    }

    @SuppressWarnings("unchecked")
    public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
        EntityMiniPlayer miniPlayer = (EntityMiniPlayer)par1Entity;
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

        for (ICustomizerPart part:CustomizerRegistry.getPartList().values()) {
            part.render(par1Entity, this, par2, par3, par4, par5, par6, par7);
        }

        GL11.glPopMatrix();
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
        this.bipedHead.rotateAngleY = par4 / (180F / (float)Math.PI);
        this.bipedHead.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.hornLeftBase.rotateAngleY = this.bipedHead.rotateAngleY;
        this.hornLeftBase.rotateAngleX = this.bipedHead.rotateAngleX;
        this.hornRightBase.rotateAngleY = this.bipedHead.rotateAngleY;
        this.hornRightBase.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedRightArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 2.0F * par2 * 0.5F;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
        this.bipedRightLeg.rotateAngleY = 0.0F;
        this.bipedLeftLeg.rotateAngleY = 0.0F;

        if (this.isRiding) {
            this.bipedRightArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedLeftArm.rotateAngleX += -((float)Math.PI / 5F);
            this.bipedRightLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            this.bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2F / 5F);
            this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
            this.bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10F);
        }

        this.bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        this.bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;

        if (this.aimedBow || this.heldItemLeft != 0 || this.heldItemRight != 0 || this.onGround > -9990F) super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);

        if (this.heldItemRight != 0 && (((EntityMiniPlayer)par7Entity).getCarrying().getItem() instanceof ItemBlock)) {
            this.bipedLeftArm.rotateAngleX = -0.8F;
            this.bipedLeftArm.rotateAngleZ = -0.05F;
            this.bipedRightArm.rotateAngleX = -0.8F;
            this.bipedRightArm.rotateAngleZ = 0.05F;
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void renderHorns(float par1) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ClientProxy.specialTextures);
        GL11.glPushMatrix();
        GL11.glScalef(1.5F / 2F, 1.5F / 2F, 1.5F / 2F);
        GL11.glTranslatef(0.0F, 16.0F * par1, 0.0F);
        this.hornLeftBase.render(par1);
        this.hornRightBase.render(par1);
        GL11.glPopMatrix();
    }

    public void renderTail(float par1) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ClientProxy.specialTextures);
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / 2F, 1.0F / 2F, 1.0F / 2F);
        GL11.glTranslatef(0.0F, 24.0F * par1, 0.0F);
        this.tailBase.render(par1);
        this.tailPart1.render(par1);
        this.tailPart2.render(par1);
        this.tailTip.render(par1);
        GL11.glPopMatrix();
    }
}
