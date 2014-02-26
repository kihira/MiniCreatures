package kihira.minicreatures.client.model.parts;

import kihira.minicreatures.common.customizer.EnumPartCategory;
import kihira.minicreatures.common.customizer.ICustomizerPartClient;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class PartModelFairy extends ModelBase implements ICustomizerPartClient {

    ModelRenderer fairyWingLeft;
    ModelRenderer fairy;
    ModelRenderer fairyWingRight;

    public PartModelFairy() {
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
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public boolean isPartValidForEntity(Entity entity, EnumPartCategory partCategory) {
        return (entity instanceof EntityMiniPlayer) && ((partCategory == EnumPartCategory.ALL) || (partCategory == EnumPartCategory.BODY));
    }

    @Override
    public void render(Entity entity, ModelBase modelMiniPlayer, float par2, float par3, float par4, float par5, float par6, float par7) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ClientProxy.specialTextures);

        GL11.glPushMatrix();
        GL11.glScalef(1.0F / 2F, 1.0F / 2F, 1.0F / 2F);
        GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
        fairy.render(par7);
        GL11.glPopMatrix();
    }
}
