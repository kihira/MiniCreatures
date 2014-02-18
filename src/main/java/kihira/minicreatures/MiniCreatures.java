package kihira.minicreatures;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import kihira.minicreatures.common.GuiHandler;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.EntityTRex;
import kihira.minicreatures.proxy.CommonProxy;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

@Mod(modid = "minicreatures", useMetadata = true)
public class MiniCreatures {

    @SidedProxy(clientSide = "kihira.minicreatures.proxy.ClientProxy", serverSide = "kihira.minicreatures.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value = "minicreatures")
    public static MiniCreatures instance;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.registerRenderers();
        proxy.registerSounds();
        registerEntities();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    public void registerEntities() {
        EntityRegistry.registerModEntity(EntityFox.class, "MiniFox", 0, this, 64, 5, true);
        EntityRegistry.addSpawn(EntityFox.class, 6, 4, 2, EnumCreatureType.creature, BiomeGenBase.plains, BiomeGenBase.forest, BiomeGenBase.forestHills);
        EntityRegistry.registerModEntity(EntityTRex.class, "MiniTRex", 1, this, 64, 5, true);
        EntityRegistry.addSpawn(EntityTRex.class, 2, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle, BiomeGenBase.jungleHills);
        EntityRegistry.registerModEntity(EntityMiniPlayer.class, "MiniPlayer", 2, this, 64, 5, true);
    }
}
