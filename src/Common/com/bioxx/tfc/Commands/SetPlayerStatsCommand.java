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
import com.bioxx.tfc.api.Util.CmdUtils;

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
        
    	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
            String msg = "player | player health food water | player health | player food water | player full|min";//--set-max-food-state";
            int pCount = params.length;
            //no params - show usages
            if (pCount == 0) {
                sender.addChatMessage(new ChatComponentText(msg));
                return;
            } 
            //first params is a player name
            int i = 0;
            EntityPlayerMP player = getPlayer(sender, params[i++]);//throw new PlayerNotFoundException("Unknown Player"); "That player cannot be found"
            if (player == null) throw new PlayerNotFoundException("Not Found");
            
            //No more params - show state for the player
            if (pCount == i ) {
                msg = getPlayerStateMsg(player);
                sender.addChatMessage(new ChatComponentText(msg));
                return;
            }

            //set all player stats (+FoodStats Nutr) to max values  '/sps player full'
            boolean full = "full".equalsIgnoreCase(params[i]);
            //set all player stats to max food nutr to min
            boolean min = !full && "min".equalsIgnoreCase(params[i]);
            if (full || min) {
                FoodStatsTFC fs = TFC_Core.getPlayerFoodStats(player);                
                fs.waterLevel = (fs.getMaxWater(player));
                fs.setFoodLevel(fs.getMaxStomach(player));
                fs.nutrProtein = full ? 1f : 0f;
                fs.nutrDairy = full ? 1f : 0f;
                fs.nutrFruit = full ? 1f : 0f;
                fs.nutrGrain = full ? 1f : 0f;
                fs.nutrVeg = full ? 1f : 0f;
                TFC_Core.setPlayerFoodStats(player, fs);
                player.setHealth(player.getMaxHealth());
                //no more params
                i = pCount;
            }
            // sps player health food water
            else {
                double health = 0;
                int last = pCount - i;
                // health | health food water 
                if (last == 3 || last == 1) 
                    health = CmdUtils.parseDouble(params, i++, 0, 100);

                if (health > 0 && !player.isDead)
                    player.setHealth( (int)((health/100d) * player.getMaxHealth()));

                // if there were parameters use if for food water
                if (i < pCount) {
                    FoodStatsTFC fs = TFC_Core.getPlayerFoodStats(player);
                    double foodlvl = CmdUtils.parseDouble(params, i++, 0, 100);
                    double waterlvl = CmdUtils.parseDouble(params, i++, 0, 100);

                    fs.setFoodLevel((int)(foodlvl/100d) * fs.getMaxStomach(player));
                    fs.waterLevel = ((int)((waterlvl/100d) * fs.getMaxWater(player)));

                    TFC_Core.setPlayerFoodStats(player, fs);
                }
            }
            msg = getPlayerStateMsg(player);
            sender.addChatMessage(new ChatComponentText(msg));
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "";
	}
        
        public String getPlayerStateMsg(EntityPlayerMP player) {
            String msg;
            if (player == null) { 
                msg = "Illegal player";
            } else {
                FoodStatsTFC fs = TFC_Core.getPlayerFoodStats(player);
                msg = String.format("Player: %s Health: %s Food: %s Water: %s  f:%s v:%s g:%s p:%s d:%s",
                        player.getGameProfile().getName(), player.getHealth(), fs.getFoodLevel(), fs.waterLevel,
                         (int)(fs.nutrFruit*100), (int)(fs.nutrVeg*100), (int)(fs.nutrGrain*100), (int)(fs.nutrProtein*100), (int)(fs.nutrDairy*100));
            }
            return msg;
        }
}
