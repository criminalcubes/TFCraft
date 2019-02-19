package com.bioxx.tfc.Commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.api.TFCOptions;

public class DiscordCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "tfcdiscord";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "<time>";
    }

    @Override
    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0)
        {
            long currentTime = TFC_Time.getTotalTicks();
            int year = 1000 + TFC_Time.getYear();

            if (par2ArrayOfStr[0].equals("time"))
            {
                par1ICommandSender.addChatMessage(new ChatComponentText(String.format(
                        "```На сервере сейчас %d день %d месяца %d года, %d часов.```",
                        TFC_Time.getDayOfMonth(),
                        TFC_Time.getMonth(),
                        year,
                        TFC_Time.getHour()
                )));
                return;
            }
        }
        else{
            (new net.minecraft.command.CommandTime()).processCommand(par1ICommandSender,par2ArrayOfStr);
            return;
        }
        throw new WrongUsageException("<time>", new Object[0]);
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"time"}) : null;
    }

    /**
     * Set the time in the server object.
     */
    protected void setTime(ICommandSender par1ICommandSender, int par2)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            MinecraftServer.getServer().worldServers[j].setWorldTime(par2);
        }
    }

    /**
     * Adds (or removes) time in the server object.
     */
    protected void addTime(ICommandSender par1ICommandSender, int par2)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            WorldServer worldserver = MinecraftServer.getServer().worldServers[j];
            worldserver.setWorldTime(worldserver.getWorldTime() + par2);
        }
    }
}
