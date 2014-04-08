package kihira.minicreatures.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelTRex extends ModelBase {

    ModelRenderer leftLegLower;
    ModelRenderer neck;
    ModelRenderer tailTip;
    ModelRenderer leftLegUpper;
    ModelRenderer rightLegUpper;
    ModelRenderer leftLegFoot;
    ModelRenderer leftArm;
    ModelRenderer tailBase;
    ModelRenderer tailMid;
    ModelRenderer body;
    ModelRenderer rightArm;
    ModelRenderer rightLegLower;
    ModelRenderer rightLegFoot;
    ModelRenderer head;

    public ModelTRex() {
        textureWidth = 128;
        textureHeight = 64;

        leftLegUpper = new ModelRenderer(this, 6, 5);
        leftLegUpper.addBox(-1F, -1.5F, -5F, 2, 3, 5);
        leftLegUpper.setRotationPoint(2.5F, 15.4F, 1F);
        setRotation(leftLegUpper, 0.9599311F, 0F, 0F);

        leftLegLower = new ModelRenderer(this, 0, 5);
        leftLegLower.addBox(-0.5F, 0.8F, -5.2F, 1, 6, 2);
        leftLegLower.setRotationPoint(2.5F, 15.4F, 1F);
        setRotation(leftLegLower, 0.5759587F, 0F, 0F);

        leftLegFoot = new ModelRenderer(this, 0, 0);
        leftLegFoot.addBox(-1.5F, 7.6F, -3F, 3, 1, 4);
        leftLegFoot.setRotationPoint(2.5F, 15.4F, 1F);

        rightLegUpper = new ModelRenderer(this, 6, 5);
        rightLegUpper.addBox(-1F, -1.5F, -5F, 2, 3, 5);
        rightLegUpper.setRotationPoint(-2.5F, 15.4F, 1F);
        setRotation(rightLegUpper, 0.9599311F, 0F, 0F);

        rightLegLower = new ModelRenderer(this, 0, 5);
        rightLegLower.addBox(-0.5F, 0.8F, -5.2F, 1, 6, 2);
        rightLegLower.setRotationPoint(-2.5F, 15.4F, 1F);
        setRotation(rightLegLower, 0.5759587F, 0F, 0F);

        rightLegFoot = new ModelRenderer(this, 0, 0);
        rightLegFoot.addBox(-1.5F, 7.6F, -3F, 3, 1, 4);
        rightLegFoot.setRotationPoint(-2.5F, 15.4F, 1F);

        neck = new ModelRenderer(this, 20, 0);
        neck.addBox(-1.5F, 3F, -2F, 3, 3, 5);
        neck.setRotationPoint(0F, 8F, 0F);
        setRotation(neck, -0.8486954F, 0F, 0F);
        tailTip = new ModelRenderer(this, 0, 40);
        tailTip.addBox(-1F, 10F, 3.546667F, 2, 2, 4);
        tailTip.setRotationPoint(0F, 8F, 0F);
        setRotation(tailTip, 0.6108652F, 0F, 0F);

        leftArm = new ModelRenderer(this, 20, 8);
        leftArm.addBox(1F, 5F, -9F, 1, 1, 4);
        leftArm.setRotationPoint(0F, 8F, 0F);
        setRotation(leftArm, 0.4537856F, 0F, 0F);
        tailBase = new ModelRenderer(this, 0, 25);
        tailBase.addBox(-2F, 6F, 3F, 4, 4, 4);
        tailBase.setRotationPoint(0F, 8F, 0F);
        setRotation(tailBase, -0.0872665F, 0F, 0F);
        tailMid = new ModelRenderer(this, 0, 33);
        tailMid.addBox(-1.5F, 8F, 3F, 3, 3, 4);
        tailMid.setRotationPoint(0F, 8F, 0F);
        setRotation(tailMid, 0.296706F, 0F, 0F);
        body = new ModelRenderer(this, 0, 13);
        body.addBox(-2.5F, 3F, 1F, 5, 5, 7);
        body.setRotationPoint(0F, 8F, 0F);
        setRotation(body, -0.6283185F, 0F, 0F);
        rightArm = new ModelRenderer(this, 20, 8);
        rightArm.addBox(-2F, 5F, -9F, 1, 1, 4);
        rightArm.setRotationPoint(0F, 8F, 0F);
        setRotation(rightArm, 0.4537856F, 0F, 0F);
        head = new ModelRenderer(this, 36, 0);
        head.addBox(-2.5F, -3.5F, -7F, 5, 5, 7);
        head.setRotationPoint(0F, 10F, -3F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        leftLegLower.render(f5);
        neck.render(f5);
        tailTip.render(f5);
        leftLegUpper.render(f5);
        rightLegUpper.render(f5);
        leftLegFoot.render(f5);
        leftArm.render(f5);
        tailBase.render(f5);
        tailMid.render(f5);
        body.render(f5);
        rightArm.render(f5);
        rightLegLower.render(f5);
        rightLegFoot.render(f5);
        head.render(f5);
    }

    /*
    public void setLivingAnimations(EntityLivingBase entityLivingBase, float par2, float par3, float par4) {
        this.leftLegUpper.rotateAngleX = MathHelper.cos(par2 * 0.6F) * 1.4F * par3 + 0.9599311F;
        this.rightLegUpper.rotateAngleX = MathHelper.cos(par2 * 0.6F + (float) Math.PI) * 1.4F * par3  + 0.9599311F;

        //TODO Make this work
        float x = (float)((5 * Math.sin(this.leftLegUpper.rotateAngleX + Math.toRadians(90))) + 15.4F);
        float y = (float)((5 * Math.cos(this.leftLegUpper.rotateAngleX + Math.toRadians(90))) + 1F);
        //System.out.println(Math.toDegrees(this.leftLegUpper.rotateAngleX));
        //this.leftLegLower.setRotationPoint(x, y, (float)-2.3);
        this.leftLegLower.setRotationPoint((float)2.5, x, y);
        this.leftLegLower.rotateAngleX = 0F;

        //this.leftLegLower.rotateAngleX = -MathHelper.cos(par2 * 0.6F + (float)Math.PI) * 1.4F * par3 + 0.5759587F;
        //this.rightLegLower.rotateAngleX = -MathHelper.cos(par2 * 0.6F) * 1.4F * par3 + 0.5759587F;
    }
    */

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
        this.head.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float)Math.PI);

        this.leftLegUpper.rotateAngleX = (MathHelper.cos(par1 * 0.6F) * 1F * par2) / 2 + 0.9599311F;
        this.leftLegLower.rotateAngleX = (MathHelper.cos(par1 * 0.6F) * 1F * par2) / 2 + 0.5759587F;
        this.leftLegFoot.rotateAngleX = (MathHelper.cos(par1 * 0.6F) * 1F * par2) / 2;
        this.rightLegUpper.rotateAngleX = (MathHelper.cos(par1 * 0.6F + (float) Math.PI) * 1.4F * par2) / 2 + 0.9599311F;
        this.rightLegLower.rotateAngleX = (MathHelper.cos(par1 * 0.6F + (float) Math.PI) * 1.4F * par2) / 2 + 0.5759587F;
        this.rightLegFoot.rotateAngleX = (MathHelper.cos(par1 * 0.6F + (float) Math.PI) * 1.4F * par2) / 2;
    }
}
