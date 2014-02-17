package kihira.minicreatures.common.entity;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class CommandSpawnEntity extends CommandBase {

    @Override
    public String getCommandName() {
        return "spawnentity";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/spawnentity <entityname> [count]";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if (astring != null && astring.length > 0) {
            EntityPlayer player = icommandsender.getEntityWorld().getPlayerEntityByName(icommandsender.getCommandSenderName());
            if (player != null) {
                Entity entity = null;
                if (astring[0].equals("fox")) entity = new EntityFox(player.worldObj);
                else if (astring[0].equals("trex")) entity = new EntityTRex(player.worldObj);
                else if (astring[0].equals("player")) entity = new EntityMiniPlayer(player.worldObj);
                if (entity != null) {
                    entity.setPosition(player.posX, player.posY, player.posZ);
                    player.worldObj.spawnEntityInWorld(entity);
                }
            }
        }
        else throw new WrongUsageException("commands.spawnentity.usage", astring);
    }
}
