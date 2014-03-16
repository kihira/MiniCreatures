package kihira.minicreatures;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import kihira.minicreatures.client.model.parts.PartModelFairy;
import kihira.minicreatures.client.model.parts.PartModelHorns;
import kihira.minicreatures.client.model.parts.PartModelTail;
import kihira.minicreatures.common.CommandSpawnEntity;
import kihira.minicreatures.common.GuiHandler;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.EntityTRex;
import kihira.minicreatures.common.item.ItemCustomizer;
import kihira.minicreatures.common.network.PacketHandler;
import kihira.minicreatures.proxy.CommonProxy;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = "minicreatures", useMetadata = true, guiFactory = "kihira.minicreatures.client.gui.ConfigGuiFactory")
public class MiniCreatures {

    @SidedProxy(clientSide = "kihira.minicreatures.proxy.ClientProxy", serverSide = "kihira.minicreatures.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value = "minicreatures")
    public static MiniCreatures instance;
    public static final Logger logger = LogManager.getLogger("MiniCreatues");
    public static final PacketHandler packetHandler = new PacketHandler();

    public static final ItemCustomizer itemCustomizer = new ItemCustomizer();
    //public static final ItemMindControlHelmet itemMindControlHelmet = new ItemMindControlHelmet();

    public static boolean enableMiniFoxes;
    public static boolean enableMiniTRex;
    public static boolean enableMiniPlayers;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        loadConfig(e.getSuggestedConfigurationFile());
        proxy.registerRenderers();
        proxy.registerSounds();
        registerEntities();
        registerItems();
        registerCustomizerParts();
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

    //TODO only register on server join after config has synced with server to allow server disabling
    private void registerCustomizerParts() {
        CustomizerRegistry.registerPart("fairy", new PartModelFairy());
        CustomizerRegistry.registerPart("horns", new PartModelHorns());
        CustomizerRegistry.registerPart("tail", new PartModelTail());
    }

    private void registerItems() {
        GameRegistry.registerItem(itemCustomizer, "customizer");
       // GameRegistry.registerItem(itemMindControlHelmet, "mindControlHelmet");
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
