package kihira.minicreatures.proxy;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.client.model.parts.PartModelFairy;
import kihira.minicreatures.client.model.parts.PartModelHorns;
import kihira.minicreatures.client.model.parts.PartModelTail;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.entity.EntityFox;
import kihira.minicreatures.common.entity.EntityMiniPlayer;
import kihira.minicreatures.common.entity.EntityMiniShark;
import kihira.minicreatures.common.entity.EntityTRex;
import kihira.minicreatures.common.network.PacketHandler;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

public class CommonProxy {

    public final PacketHandler packetHandler = new PacketHandler();

    public void registerRenderers() { }

    public void registerEntities() {
        if (MiniCreatures.enableMiniFoxes) {
            EntityRegistry.registerModEntity(EntityFox.class, "MiniFox", 0, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityFox.class, 6, 2, 4, EnumCreatureType.creature, BiomeGenBase.plains, BiomeGenBase.forest, BiomeGenBase.forestHills);
        }
        if (MiniCreatures.enableMiniTRex) {
            EntityRegistry.registerModEntity(EntityTRex.class, "MiniTRex", 1, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityTRex.class, 2, 1, 2, EnumCreatureType.creature, BiomeGenBase.jungle, BiomeGenBase.jungleHills);
        }
        if (MiniCreatures.enableMiniPlayers) EntityRegistry.registerModEntity(EntityMiniPlayer.class, "MiniPlayer", 2, this, 64, 1, true);
        if (MiniCreatures.enableMiniShark) {
            EntityRegistry.registerModEntity(EntityMiniShark.class, "MiniShark", 3, this, 64, 1, true);
            EntityRegistry.addSpawn(EntityTRex.class, 10, 2, 4, EnumCreatureType.waterCreature, BiomeGenBase.ocean, BiomeGenBase.deepOcean);
        }
    }

    //TODO only register on server join after config has synced with server to allow server disabling
    public void registerCustomizerParts() {
        CustomizerRegistry.registerPart("fairy", new PartModelFairy());
        CustomizerRegistry.registerPart("horns", new PartModelHorns());
        CustomizerRegistry.registerPart("tail", new PartModelTail());
    }

    public void registerItems() {
        if (MiniCreatures.enableCustomizer) GameRegistry.registerItem(MiniCreatures.itemCustomizer, "customizer");
        //GameRegistry.registerItem(itemMindControlHelmet, "mindControlHelmet");
    }

}
