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
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import kihira.foxlib.common.gson.GsonHelper;
import kihira.minicreatures.common.CommandSpawnEntity;
import kihira.minicreatures.common.EventHandler;
import kihira.minicreatures.common.GuiHandler;
import kihira.minicreatures.common.entity.*;
import kihira.minicreatures.common.item.ItemCustomizer;
import kihira.minicreatures.common.personality.Mood;
import kihira.minicreatures.common.personality.MoodTest;
import kihira.minicreatures.common.personality.MoodVariable;
import kihira.minicreatures.common.personality.Personality;
import kihira.minicreatures.proxy.CommonProxy;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(modid = "minicreatures", name = "Mini Creatures", version = "${version}", useMetadata = true, dependencies = "required-after:foxlib@[0.1.0,)")
public class MiniCreatures {

    @SidedProxy(clientSide = "kihira.minicreatures.proxy.ClientProxy", serverSide = "kihira.minicreatures.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(value = "minicreatures")
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

        loadPersonalityTypes(e.getModConfigurationDirectory());
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
        property = configuration.get(Configuration.CATEGORY_GENERAL, "Enable Mini Sharks", true);
        enableMiniShark = property.getBoolean(true);
        property = configuration.get(Configuration.CATEGORY_GENERAL, "Enable Mini Red Pandas", true);
        enableMiniRedPandas = property.getBoolean(true);
        property = configuration.get(Configuration.CATEGORY_GENERAL, "Enable Customizer", true);
        property.comment = "This feature is still in development and may cause issues";
        enableCustomizer = property.getBoolean(true);


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
            EntityRegistry.addSpawn(EntityFox.class, 6, 2, 4, EnumCreatureType.creature, BiomeGenBase.plains, BiomeGenBase.forest, BiomeGenBase.forestHills);
        }
        if (enableMiniTRex) {
            EntityRegistry.registerModEntity(EntityTRex.class, "MiniTRex", 1, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityTRex.class, 2, 1, 2, EnumCreatureType.creature, BiomeGenBase.jungle, BiomeGenBase.jungleHills);
        }
        if (enableMiniPlayers) EntityRegistry.registerModEntity(EntityMiniPlayer.class, "MiniPlayer", 2, this, 64, 1, true);
        if (enableMiniShark) {
            EntityRegistry.registerModEntity(EntityMiniShark.class, "MiniShark", 3, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityTRex.class, 10, 2, 4, EnumCreatureType.waterCreature, BiomeGenBase.ocean, BiomeGenBase.deepOcean);
        }
        if (enableMiniRedPandas) {
            EntityRegistry.registerModEntity(EntityRedPanda.class, "MiniRedPanda", 4, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityRedPanda.class, 6, 2, 4, EnumCreatureType.creature, BiomeGenBase.forest, BiomeGenBase.forestHills, BiomeGenBase.birchForest, BiomeGenBase.birchForestHills);
        }
    }
}
