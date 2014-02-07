package minicreatures.client.render;

import minicreatures.client.model.ModelTRex;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderTRex extends RenderLiving {

    private final ResourceLocation trexTexture = new ResourceLocation("minifox", "textures/entity/trex.png");

    public RenderTRex() {
        super(new ModelTRex(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return trexTexture;
    }
}
