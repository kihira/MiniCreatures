package kihira.minicreatures.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.world.World;

public class ContainerCarriedAnvil extends ContainerRepair {

    public ContainerCarriedAnvil(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
        super(par1InventoryPlayer, par2World, par3, par4, par5, par6EntityPlayer);
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return true;
    }
}
