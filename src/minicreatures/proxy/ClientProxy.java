package minicreatures.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import minicreatures.client.render.RenderFox;
import minicreatures.client.render.RenderMiniPlayer;
import minicreatures.client.render.RenderTRex;
import minicreatures.common.entity.EntityFox;
import minicreatures.common.entity.EntityMiniPlayer;
import minicreatures.common.entity.EntityTRex;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFox.class, new RenderFox());
        RenderingRegistry.registerEntityRenderingHandler(EntityTRex.class, new RenderTRex());
        RenderingRegistry.registerEntityRenderingHandler(EntityMiniPlayer.class, new RenderMiniPlayer());
    }

    @Override
    public void registerSounds() {

    }
}
