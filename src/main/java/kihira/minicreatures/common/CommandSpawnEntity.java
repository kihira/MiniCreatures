package kihira.minicreatures.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import java.util.List;

public class CommandSpawnEntity extends CommandBase {

    @Override
    public String getCommandName() {
        return "spawnentity";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/spawnentity <entityname> [amount]";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        if (var2.length > 0) {
            String entityName = var2[0];
            Entity entity = EntityList.createEntityByName(entityName, var1.getEntityWorld());
            entity.setPosition(var1.getPlayerCoordinates().posX, var1.getPlayerCoordinates().posY, var1.getPlayerCoordinates().posZ);
            if (var2.length == 2) {
                for (int i = 0; i < Integer.parseInt(var2[1]); i++) {
                    var1.getEntityWorld().spawnEntityInWorld(entity);
                }
            }
            else var1.getEntityWorld().spawnEntityInWorld(entity);
        }
    }

    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        String[] entityNameList = (String[]) EntityList.stringToClassMapping.keySet().toArray(new String[EntityList.stringToClassMapping.size()]);
        return par2ArrayOfStr.length > 0 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, entityNameList) : null;
    }

    public int compareTo(Object par1Obj) {
        return this.compareTo((ICommand)par1Obj);
    }
}
