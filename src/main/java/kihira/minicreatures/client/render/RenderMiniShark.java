package kihira.minicreatures.client.render;

import kihira.minicreatures.client.model.ModelMiniShark;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderMiniShark extends RendererLivingEntity {

    private static final ResourceLocation sharkTexture = new ResourceLocation("minicreatures", "textures/entity/minishark.png");

    public RenderMiniShark() {
        super(new ModelMiniShark(), 0.4F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return sharkTexture;
    }
}
