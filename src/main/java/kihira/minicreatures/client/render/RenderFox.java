package kihira.minicreatures.client.render;

import kihira.minicreatures.client.model.ModelFox;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFox extends RenderLiving {

    private final ResourceLocation foxTexture = new ResourceLocation("kihira/minicreatures", "textures/entity/fox.png");

    public RenderFox() {
        super(new ModelFox(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return foxTexture;
    }
}