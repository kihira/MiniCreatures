package kihira.minicreatures;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import kihira.minicreatures.common.GuiHandler;
import kihira.minicreatures.common.entity.CommandSpawnEntity;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.EntityTRex;
import kihira.minicreatures.proxy.CommonProxy;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

@Mod(modid = "kihira/minicreatures", name = "Mini Creatures", version = "0.0.1")
public class MiniCreatures {

    @SidedProxy(clientSide = "minicreatures.proxy.ClientProxy", serverSide = "minicreatures.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value = "kihira/minicreatures")
    public static MiniCreatures instance;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.registerRenderers();
        proxy.registerSounds();
        registerEntities();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        LanguageRegistry.instance().addStringLocalization("entity.minicreatures.MiniFox.name", "en_US", "Mini Fox");
        LanguageRegistry.instance().addStringLocalization("entity.minicreatures.MiniTRex.name", "en_US", "Mini TRex");
        LanguageRegistry.instance().addStringLocalization("entity.minicreatures.MiniPlayer.name", "en_US", "Mini Player");
    }

    public void registerEntities() {
        EntityRegistry.registerModEntity(EntityFox.class, "MiniFox", 0, this, 64, 5, true);
        EntityRegistry.addSpawn(EntityFox.class, 6, 4, 2, EnumCreatureType.creature, BiomeGenBase.plains, BiomeGenBase.forest, BiomeGenBase.forestHills);
        EntityRegistry.registerModEntity(EntityTRex.class, "MiniTRex", 1, this, 64, 5, true);
        EntityRegistry.addSpawn(EntityTRex.class, 2, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle, BiomeGenBase.jungleHills);
        EntityRegistry.registerModEntity(EntityMiniPlayer.class, "MiniPlayer", 2, this, 64, 5, true);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandSpawnEntity());
    }
}
