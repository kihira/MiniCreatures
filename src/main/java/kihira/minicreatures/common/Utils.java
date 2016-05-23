package kihira.minicreatures.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Utils {

    public static void decreaseCurrentStack(EntityPlayer player, ItemStack stack) {
        decreaseStack(player, stack, player.inventory.currentItem);
    }

    public static void decreaseStack(EntityPlayer player, ItemStack stack, int slot) {
        if (!player.capabilities.isCreativeMode && --stack.stackSize <= 0) player.inventory.setInventorySlotContents(slot, null);
    }
}
