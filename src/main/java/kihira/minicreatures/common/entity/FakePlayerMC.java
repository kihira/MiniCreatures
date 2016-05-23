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

import javax.annotation.Nullable;
import java.util.Collection;

public class FakePlayerMC extends FakePlayer {

    private final EntityMiniPlayer miniPlayer;

    public FakePlayerMC(WorldServer world, GameProfile name, EntityMiniPlayer miniPlayer) {
        super(world, name);
        this.miniPlayer = miniPlayer;
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return miniPlayer.getActivePotionEffects();
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

    @Nullable
    @Override
    public PotionEffect removeActivePotionEffect(@Nullable Potion potioneffectin) {
        return miniPlayer.removeActivePotionEffect(potioneffectin);
    }

    @Override
    public void removePotionEffect(Potion potion) {
        miniPlayer.removePotionEffect(potion);
    }

    @Override
    public void curePotionEffects(ItemStack itemStack) {
        miniPlayer.curePotionEffects(itemStack);
    }
}
