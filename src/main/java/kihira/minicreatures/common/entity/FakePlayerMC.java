/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Collection;

public class FakePlayerMC extends FakePlayer {

    private final EntityMiniPlayer miniPlayer;

    public FakePlayerMC(WorldServer world, GameProfile name, EntityMiniPlayer miniPlayer) {
        super(world, name);
        this.miniPlayer = miniPlayer;
    }

    @Override
    public Collection getActivePotionEffects() {
        return miniPlayer.getActivePotionEffects();
    }

    @Override
    public boolean isPotionActive(int potionID) {
        return miniPlayer.isPotionActive(potionID);
    }

    @Override
    public boolean isPotionActive(Potion potion) {
        return miniPlayer.isPotionActive(potion);
    }

    @Override
    public PotionEffect getActivePotionEffect(Potion potion) {
        return miniPlayer.getActivePotionEffect(potion);
    }

    @Override
    public void addPotionEffect(PotionEffect potionEffect) {
        miniPlayer.addPotionEffect(potionEffect);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return miniPlayer.isPotionApplicable(potionEffect);
    }

    @Override
    public void removePotionEffectClient(int potionID) {
        miniPlayer.removePotionEffectClient(potionID);
    }

    @Override
    public void removePotionEffect(int potionID) {
        miniPlayer.removePotionEffect(potionID);
    }

    @Override
    public void curePotionEffects(ItemStack itemStack) {
        miniPlayer.curePotionEffects(itemStack);
    }
}
