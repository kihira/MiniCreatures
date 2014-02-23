package kihira.minicreatures;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import kihira.minicreatures.common.CommandSpawnEntity;
import kihira.minicreatures.common.GuiHandler;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.EntityTRex;
import kihira.minicreatures.proxy.CommonProxy;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

@Mod(modid = "minicreatures", useMetadata = true, guiFactory = "kihira.minicreatures.client.gui.ConfigGuiFactory")
public class MiniCreatures {

    @SidedProxy(clientSide = "kihira.minicreatures.proxy.ClientProxy", serverSide = "kihira.minicreatures.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value = "minicreatures")
    public static MiniCreatures instance;

    public static boolean enableMiniFoxes;
    public static boolean enableMiniTRex;
    public static boolean enableMiniPlayers;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        loadConfig(e.getSuggestedConfigurationFile());
        proxy.registerRenderers();
        proxy.registerSounds();
        registerEntities();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandSpawnEntity());
    }

    private void loadConfig(File configFile) {
        Configuration configuration = new Configuration(configFile);
        Property property;
        configuration.load();

        property = configuration.get(Configuration.CATEGORY_GENERAL, "Enable Mini Fox", true);
        enableMiniFoxes = property.getBoolean(true);
        property = configuration.get(Configuration.CATEGORY_GENERAL, "Enable Mini T-Rex", true);
        enableMiniTRex = property.getBoolean(true);
        property = configuration.get(Configuration.CATEGORY_GENERAL, "Enable Mini Players", true);
        enableMiniPlayers = property.getBoolean(true);

        if (configuration.hasChanged()) configuration.save();
    }

    private void registerEntities() {
        if (enableMiniFoxes) {
            EntityRegistry.registerModEntity(EntityFox.class, "MiniFox", 0, this, 64, 5, true);
            EntityRegistry.addSpawn(EntityFox.class, 6, 4, 2, EnumCreatureType.creature, BiomeGenBase.plains, BiomeGenBase.forest, BiomeGenBase.forestHills);
        }
        if (enableMiniTRex) {
            EntityRegistry.registerModEntity(EntityTRex.class, "MiniTRex", 1, this, 64, 5, true);
            EntityRegistry.addSpawn(EntityTRex.class, 2, 1, 1, EnumCreatureType.creature, BiomeGenBase.jungle, BiomeGenBase.jungleHills);
        }
        if (enableMiniPlayers) EntityRegistry.registerModEntity(EntityMiniPlayer.class, "MiniPlayer", 2, this, 64, 5, true);
    }
}
