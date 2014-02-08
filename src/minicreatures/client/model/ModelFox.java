package minicreatures.client.model;

import minicreatures.common.entity.EntityFox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelFox extends ModelBase {

    ModelRenderer LBLeg;
    ModelRenderer head;
    ModelRenderer tailMid;
    ModelRenderer RBLeg;
    ModelRenderer RFLeg;
    ModelRenderer LFLeg;
    ModelRenderer Body;
    ModelRenderer tailTip;
    ModelRenderer tailBase;
    ModelRenderer chest;

    public ModelFox() {
        textureWidth = 64;
        textureHeight = 64;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-3F, -3F, -4F, 6, 6, 4);
        head.setRotationPoint(0F, 17F, -2F);
        head.setTextureOffset(20, 0).addBox(-1.5F, 0F, -7F, 3, 3, 3); //Muzzle
        head.setTextureOffset(30, 6).addBox(0.5F, -5F, -3F, 2, 2, 1); //Left Ear
        head.setTextureOffset(36, 6).addBox(1.5F, -6F, -3F, 1, 1, 1); //Left Ear Tip
        head.setTextureOffset(20, 6).addBox(-2.5F, -5F, -3F, 2, 2, 1); //Right Ear
        head.setTextureOffset(26, 6).addBox(-2.5F, -6F, -3F, 1, 1, 1); //Right Ear Tip
        LBLeg = new ModelRenderer(this, 42, 5);
        LBLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        LBLeg.setRotationPoint(1.5F, 21F, 2.5F);
        RBLeg = new ModelRenderer(this, 42, 5);
        RBLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        RBLeg.setRotationPoint(-1.5F, 21F, 2.5F);
        RFLeg = new ModelRenderer(this, 42, 5);
        RFLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        RFLeg.setRotationPoint(-1.5F, 21F, -1.5F);
        LFLeg = new ModelRenderer(this, 42, 5);
        LFLeg.addBox(-1F, 0F, -1F, 2, 3, 2);
        LFLeg.setRotationPoint(1.5F, 21F, -1.5F);
        Body = new ModelRenderer(this, 32, 10);
        Body.addBox(-2F, -2F, -2F, 4, 4, 5);
        Body.setRotationPoint(0F, 19F, 0F);
        tailTip = new ModelRenderer(this, 24, 10);
        tailTip.addBox(-1F, -1F, 0F, 2, 2, 2);
        tailTip.setRotationPoint(0F, 15.5F, 7F);
        setRotation(tailTip, 1.029744F, 0F, 0F);
        tailMid = new ModelRenderer(this, 0, 10);
        tailMid.addBox(-1.5F, -1.5F, 0F, 3, 3, 4);
        tailMid.setRotationPoint(0F, 17.3F, 4F);
        setRotation(tailMid, 0.5934119F, 0F, 0F);
        tailBase = new ModelRenderer(this, 14, 10);
        tailBase.addBox(-1F, -1F, 0F, 2, 2, 3);
        tailBase.setRotationPoint(0F, 18F, 2F);
        setRotation(tailBase, 0.3316126F, 0F, 0F);
        chest = new ModelRenderer(this, 0, 17);
        chest.addBox(-3F, -1.5F, 0.5F, 6, 6, 8);
        chest.setRotationPoint(0F, 19F, -2F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        LBLeg.render(f5);
        head.renderWithRotation(f5);
        tailMid.render(f5);
        RBLeg.render(f5);
        RFLeg.render(f5);
        LFLeg.render(f5);
        Body.render(f5);
        tailTip.render(f5);
        tailBase.render(f5);
        GL11.glPushMatrix();
        GL11.glScalef(1f, 0.5f, 0.5f);
        GL11.glTranslatef(0.0f, 18F * f5,  f5 - 0.15f);
        chest.render(f5);
        GL11.glPopMatrix();
    }

    public void setLivingAnimations(EntityLivingBase entityLivingBase, float par2, float par3, float par4) {
        EntityFox entityFox = (EntityFox)entityLivingBase;

        this.LFLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F) * 1.4F * par3;
        this.RFLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F + (float)Math.PI) * 1.4F * par3;
        this.LBLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F + (float)Math.PI) * 1.4F * par3;
        this.RBLeg.rotateAngleX = MathHelper.cos(par2 * 1.5F) * 1.4F * par3;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
        this.head.rotateAngleX = par5 / (180F / (float)Math.PI);
        this.head.rotateAngleY = par4 / (180F / (float)Math.PI);
    }
}