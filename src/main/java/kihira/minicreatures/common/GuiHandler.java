package kihira.minicreatures.common;

import cpw.mods.fml.common.network.IGuiHandler;
import kihira.minicreatures.common.entity.ICreatureInventory;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        //Get Entity ID as x coord. Inspired by OpenBlocks
        switch (ID) {
            case (0): return new ContainerChest(player.inventory, ((ICreatureInventory)world.getEntityByID(x)).getInventory());
            case (1): return new ContainerCarriedAnvil(player.inventory, world, x, y, z, player);
            default: return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case (0): return new GuiChest(player.inventory, ((ICreatureInventory)world.getEntityByID(x)).getInventory());
            case (1): return new GuiRepair(player.inventory, world, x, y, z);
            default: return null;
        }
    }
}
