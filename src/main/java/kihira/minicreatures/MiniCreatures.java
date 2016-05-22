/*
 * Copyright (C) 2014  Kihira
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package kihira.minicreatures;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import kihira.foxlib.common.gson.GsonHelper;
import kihira.minicreatures.client.TailsCompatHandler;
import kihira.minicreatures.common.CommandSpawnEntity;
import kihira.minicreatures.common.entity.*;
import kihira.minicreatures.common.handler.EventHandler;
import kihira.minicreatures.common.handler.GuiHandler;
import kihira.minicreatures.common.item.ItemCustomizer;
import kihira.minicreatures.common.personality.Mood;
import kihira.minicreatures.common.personality.MoodTest;
import kihira.minicreatures.common.personality.MoodVariable;
import kihira.minicreatures.common.personality.Personality;
import kihira.minicreatures.proxy.CommonProxy;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(modid = "minicreatures", name = "Mini Creatures", version = "${version}", useMetadata = true)
public class MiniCreatures {

    @SidedProxy(clientSide = "kihira.minicreatures.proxy.ClientProxy", serverSide = "kihira.minicreatures.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance()
    public static MiniCreatures instance;
    public static final Logger logger = LogManager.getLogger("MiniCreatues");

    public static final ItemCustomizer itemCustomizer = new ItemCustomizer();
    //public static final ItemMindControlHelmet itemMindControlHelmet = new ItemMindControlHelmet();

    public static boolean enableMiniFoxes;
    public static boolean enableMiniTRex;
    public static boolean enableMiniPlayers;
    public static boolean enableMiniShark;
    public static boolean enableMiniRedPandas;
    public static boolean enableCustomizer;
    public static int randomNameChance;

    private static boolean hasGottenNames = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        loadConfig(e.getSuggestedConfigurationFile());
        proxy.registerRenderers();
        registerEntities();
        proxy.registerItems();
        proxy.registerCustomizerParts();
        proxy.registerMessages();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        if (e.getSide().isClient() && Loader.isModLoaded("Tails")) {
            MinecraftForge.EVENT_BUS.register(new TailsCompatHandler());
        }

        loadPersonalityTypes(e.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandSpawnEntity());

        if (!hasGottenNames) {
            Runnable getNamesRunnable = new Runnable() {
                private String namesLoc = "https://raw.github.com/kihira/MiniCreatures/master/src/main/resources/assets/minicreatures/text/names.json";

                @Override
                public void run() {
                    try {
                        Gson gson = GsonHelper.createGson();
                        Reader reader = new InputStreamReader(new URL(this.namesLoc).openStream());
                        //Reader reader = new InputStreamReader(MiniCreatures.class.getResourceAsStream("/assets/minicreatures/text/names.json"));
                        String[] names = gson.fromJson(reader, String[].class);
                        reader.close();

                        if (names != null && names.length > 0) {
                            EventHandler.names = names;
                        }

                    } catch (Exception e1) {
                        logger.debug(e1);
                    }
                }
            };
            getNamesRunnable.run();
            hasGottenNames = true;
        }
    }

    private void loadConfig(File configFile) {
        Configuration configuration = new Configuration(configFile);
        configuration.load();

        enableMiniFoxes = configuration.getBoolean("Enable Mini Fox", Configuration.CATEGORY_GENERAL, true, "");
        enableMiniTRex = configuration.getBoolean("Enable Mini T-Rex", Configuration.CATEGORY_GENERAL, true, "");
        enableMiniPlayers = configuration.getBoolean("Enable Mini Players", Configuration.CATEGORY_GENERAL, false, "This feature is still in development and may cause issues");
        enableMiniShark = configuration.getBoolean("Enable Mini Sharks", Configuration.CATEGORY_GENERAL, true, "");
        enableMiniRedPandas = configuration.getBoolean("Enable Mini Red Pandas", Configuration.CATEGORY_GENERAL, true, "");
        enableCustomizer = configuration.getBoolean("Enable Customizer", Configuration.CATEGORY_GENERAL, false, "This feature is still in development and may cause issues");
        randomNameChance = configuration.getInt("Random Name Chance", Configuration.CATEGORY_GENERAL, 200, 0, 500, "Chance that a Mini Creature will randomly spawn with a name. Higher = lower chance. Set to 0 to disable");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadPersonalityTypes(File configDir) {
        File personalityTypesFile = new File(configDir, File.separator + "MiniCreatures" + File.separator + "PersonalityTypes.json");

        try {
            Gson gson = GsonHelper.createGson(Mood.class);
            if (!personalityTypesFile.exists()) {
                //Create files/directories
                new File(configDir, File.separator + "MiniCreatures").mkdirs();
                personalityTypesFile.createNewFile();

                JsonWriter jsonWriter = new JsonWriter(new FileWriter(personalityTypesFile));

                //TODO just copy a pre-genned file like marker beacons
                //Create defaults
                //Create default personalities
                jsonWriter.beginArray();
                List<String> list = new ArrayList<String>();
                list.add(EntityMiniPlayer.class.getName());
                gson.toJson(gson.toJsonTree(new Mood("psychotic", list, new HashMap<String, MoodVariable>() {{
                    put("happiness", new MoodVariable(35, 50));
                    put("hostility", new MoodVariable(40, 50));
                }})), jsonWriter);
                gson.toJson(gson.toJsonTree(new Mood("coldblooded", list, new HashMap<String, MoodVariable>() {{
                    put("happiness", new MoodVariable(-50, 0));
                    put("hostility", new MoodVariable(40, 50));
                }})), jsonWriter);
                gson.toJson(gson.toJsonTree(new Mood("happy", list, new HashMap<String, MoodVariable>() {{
                    put("happiness", new MoodVariable(10, 50));
                    put("hostility", new MoodVariable(-50, 10));
                }})), jsonWriter);
                gson.toJson(gson.toJsonTree(new Mood("depressed", list, new HashMap<String, MoodVariable>() {{
                    put("happiness", new MoodVariable(-50, 0));
                    put("hostility", new MoodVariable(-10, 10));
                }})), jsonWriter);
                gson.toJson(gson.toJsonTree(new MoodTest("test")), jsonWriter);
                jsonWriter.endArray();
                jsonWriter.close();
            }

            //Load personality types
            JsonReader reader = new JsonReader(new FileReader(personalityTypesFile));
            reader.beginArray();
            while (reader.hasNext()) {
                Mood mood = gson.fromJson(reader, Mood.class);
                Personality.moodList.add(mood);
                logger.debug("Loaded mood %s", mood.toString());
            }
            reader.endArray();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void registerEntities() {
        if (enableMiniFoxes) {
            EntityRegistry.registerModEntity(EntityFox.class, "MiniFox", 0, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityFox.class, 6, 2, 4, EnumCreatureType.CREATURE, Biomes.PLAINS, Biomes.FOREST, Biomes.FOREST_HILLS);
        }
        if (enableMiniTRex) {
            EntityRegistry.registerModEntity(EntityMiniTRex.class, "MiniTRex", 1, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityMiniTRex.class, 2, 1, 2, EnumCreatureType.CREATURE, Biomes.JUNGLE, Biomes.JUNGLE_HILLS);
        }
        if (enableMiniPlayers) EntityRegistry.registerModEntity(EntityMiniPlayer.class, "MiniPlayer", 2, this, 64, 1, true);
        if (enableMiniShark) {
            EntityRegistry.registerModEntity(EntityMiniShark.class, "MiniShark", 3, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityMiniTRex.class, 10, 2, 4, EnumCreatureType.WATER_CREATURE, Biomes.OCEAN, Biomes.DEEP_OCEAN);
        }
        if (enableMiniRedPandas) {
            EntityRegistry.registerModEntity(EntityRedPanda.class, "MiniRedPanda", 4, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityRedPanda.class, 6, 2, 4, EnumCreatureType.CREATURE, Biomes.FOREST, Biomes.FOREST_HILLS, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS);
        }
    }
}
