package kihira.minicreatures.client.render.layers;

import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerBackpack implements LayerRenderer<EntityMiniPlayer> {
    private ItemStack backpack;

    public LayerBackpack() {
        backpack = new ItemStack(MiniCreatures.itemBackpack);
    }

    @Override
    public void doRenderLayer(EntityMiniPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entitylivingbaseIn.hasBackpack()) return;

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-180f, 1f, 0f, 0f);
        GlStateManager.translate(0f, -0.875f, 0f);
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        Minecraft.getMinecraft().getItemRenderer().renderItem(entitylivingbaseIn, backpack, ItemCameraTransforms.TransformType.NONE);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
