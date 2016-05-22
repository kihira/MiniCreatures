package kihira.minicreatures.client.render.layers;

import kihira.minicreatures.client.render.RenderMiniCreature;
import kihira.minicreatures.common.entity.EntityMiniCreature;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCollar<T extends RenderMiniCreature> implements LayerRenderer<EntityMiniCreature> {

    private final T render;
    private final ResourceLocation collarTexture;

    public LayerCollar(T render, ResourceLocation collarTexture) {
        this.render = render;
        this.collarTexture = collarTexture;
    }

    @Override
    public void doRenderLayer(EntityMiniCreature entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity.isTamed() && !entity.isInvisible()) {
            EnumDyeColor enumdyecolor = EnumDyeColor.byMetadata(entity.getCollarColour().getMetadata());
            float[] col = EntitySheep.getDyeRgb(enumdyecolor);

            render.bindTexture(collarTexture);
            GlStateManager.color(col[0], col[1], col[2]);
            render.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
