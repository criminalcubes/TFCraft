package com.bioxx.tfc.Commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.Player.FoodStatsTFC;
import net.minecraft.util.ChatComponentText;

public class SetPlayerStatsCommand extends CommandBase{

	@Override
	public String getCommandName() {
		return "sps";
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{
		return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
	}
        
        public EntityPlayerMP findPlayerByName(ICommandSender sender, String name) {
            EntityPlayerMP player = null;
            try{
                player = getPlayer(sender, name);
            }catch(PlayerNotFoundException e){
                throw new PlayerNotFoundException("Unknown Player");
            }
            return player;
        }
        
        public String getPlayerStateMsg(EntityPlayerMP player) {
            String msg;
            if (player == null) { 
                msg = "Illegal player";
            } else {
                FoodStatsTFC fs = TFC_Core.getPlayerFoodStats(player);
                msg = String.format("Player: %s Health: %s Food: %s Water: %s",
                        player.getGameProfile().getName(), player.getHealth(), fs.getFoodLevel(), fs.waterLevel);
            }
            return msg;
        }
        
        public boolean isValidPlayerName(String param) {
            if (param == null || param.isEmpty())
                return false;
            int digits = 0;
            if (Character.isAlphabetic(param.charAt(0))) 
                return true;
            for (int i = 0; i < param.length(); i++) {
                char c = param.charAt(i);
                if (Character.isDigit(c))
                    digits++;
            }
            //Consider that a valid name must contain at least 2 non-numeric characters
            int notDigitChars = param.length() - digits; 
            return notDigitChars > 2;//-0.5
        }
        
        public double parseDouble(String[] params, int index, int maxLimit) {
            double value = 0;
            if ( params == null || index < 0 || index > params.length) 
                return 0;
            try {
                value = Double.parseDouble(params[index]);
            } catch(NumberFormatException e) {
                throw new PlayerNotFoundException("Invalid");
            }
            if (value < 0 || value > maxLimit) {
                throw new PlayerNotFoundException("OutOfBounds 0-"+maxLimit);
            }
            return value;
        }
        
    	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
            String msg = "player health food water | player | player health | player food water | health | food wather";
            int pCount = params.length;
            //no params show usages
            if (pCount == 0) {
                sender.addChatMessage(new ChatComponentText(msg));
                return;
            } 
            // if first params is a line similar to the player's name
            int i = 0;
            EntityPlayerMP player = pCount > 0 && isValidPlayerName(params[i])
                    ? findPlayerByName(sender, params[i++]) 
                    : getCommandSenderAsPlayer(sender);
            
            //No more parmas - show state for the player
            if (pCount == i ) {
                msg = getPlayerStateMsg(player);
                sender.addChatMessage(new ChatComponentText(msg));
                return;
            }

            if (player == null) {
                throw new PlayerNotFoundException("Invalid");
            }
            
            double health = 0;
            int last = pCount - i;
            // health | health food water 
            if (last == 3 || last == 1) 
                health = parseDouble(params, i++, 100);
            
            if (health > 0 && !player.isDead)
                player.setHealth( (int)((health/100d) * player.getMaxHealth()));
            
            // if there were parameters use if for food water
            if (i < pCount) {
                FoodStatsTFC fs = TFC_Core.getPlayerFoodStats(player);
                double foodlvl = parseDouble(params, i++, 100);
                double waterlvl = parseDouble(params, i++, 100);

                fs.setFoodLevel((int)foodlvl);
                fs.waterLevel = ((int)((waterlvl/100d) * fs.getMaxWater(player)));

                TFC_Core.setPlayerFoodStats(player, fs);
            }
            msg = getPlayerStateMsg(player);
            sender.addChatMessage(new ChatComponentText(msg));
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "";
	}

}
