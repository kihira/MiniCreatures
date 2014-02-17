package kihira.minicreatures.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import kihira.minicreatures.client.render.RenderFox;
import kihira.minicreatures.client.render.RenderMiniPlayer;
import kihira.minicreatures.client.render.RenderTRex;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.EntityTRex;

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
