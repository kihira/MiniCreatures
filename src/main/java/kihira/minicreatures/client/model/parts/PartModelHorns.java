package kihira.minicreatures.client.model.parts;

import kihira.minicreatures.client.model.ModelMiniPlayer;
import kihira.minicreatures.common.CustomizerRegistry;
import kihira.minicreatures.common.ICustomizerPart;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class PartModelHorns extends ModelBase implements ICustomizerPart<ModelMiniPlayer> {

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

    public PartModelHorns() {
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
    }

    @Override
    public boolean isPartValidForEntity(Entity entity, CustomizerRegistry.EnumPartCategory enumPartCategory) {
        return (entity instanceof EntityMiniPlayer && enumPartCategory == CustomizerRegistry.EnumPartCategory.HEAD);
    }

    @Override
    public void render(Entity entity, ModelMiniPlayer modelBase, float par2, float par3, float par4, float par5, float par6, float par7) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ClientProxy.specialTextures);

        this.hornLeftBase.rotateAngleY = modelBase.bipedHead.rotateAngleY;
        this.hornLeftBase.rotateAngleX = modelBase.bipedHead.rotateAngleX;
        this.hornRightBase.rotateAngleY = modelBase.bipedHead.rotateAngleY;
        this.hornRightBase.rotateAngleX = modelBase.bipedHead.rotateAngleX;

        GL11.glPushMatrix();
        GL11.glScalef(1.5F / 2F, 1.5F / 2F, 1.5F / 2F);
        GL11.glTranslatef(0.0F, 16.0F * par7, 0.0F);
        this.hornLeftBase.render(par7);
        this.hornRightBase.render(par7);
        GL11.glPopMatrix();
    }
}
