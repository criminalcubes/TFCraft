package com.bioxx.tfc.Commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.WorldGen.TFCBiome;
import com.bioxx.tfc.WorldGen.Generators.WorldGenFissure;
import com.bioxx.tfc.WorldGen.Generators.Trees.WorldGenCustomFruitTree;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCOptions;
import com.bioxx.tfc.api.Util.CmdUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import static com.google.common.base.Strings.isNullOrEmpty;

public class GenCommand extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "gen";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if(!TFCOptions.enableDebugMode)
		{
			TFC_Core.sendInfoMessage(player, new ChatComponentText("Debug Mode Required"));
			return;
		}
                
                if (params.length == 0 || "help".equalsIgnoreCase(params[0])) 
                {
                    TFC_Core.sendInfoMessage(player, new ChatComponentText(GEN_USAGE));
                    return;
                } 
                
                if (params.length == 1)
		{
			if (params[0].equalsIgnoreCase("fruittree"))
			{
				TFC_Core.sendInfoMessage(player, new ChatComponentText("Generating Fruit Tree"));
                           	float temp = TFC_Climate.getBioTemperatureHeight(sender.getEntityWorld(), (int)player.posX, (int)player.posY, (int)player.posZ);
                                float rain = TFC_Climate.getRainfall(sender.getEntityWorld(), (int)player.posX, (int)player.posY, (int)player.posZ);
                                if(!(temp > 10 && temp < 25 && rain >= 500)) {
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Generation Failed. Need: 25>temp>10 rain >=500"));
                                        return;
                                }

				WorldGenerator fruitGen = new WorldGenCustomFruitTree(false, TFCBlocks.fruitTreeLeaves, 0/*meta is type of fruit tree 0-8*/);
				if (!fruitGen.generate(sender.getEntityWorld(), sender.getEntityWorld().rand, (int) player.posX, (int) player.posY, (int) player.posZ))
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Generation Failed"));
			} 
		}
		else if (params.length == 2)
		{
			if(params[0].equals("fissure"))
			{
				WorldGenFissure gen = null;
				if(params[1].equals("water"))
				{
					gen = new WorldGenFissure(TFCBlocks.freshWater);
					gen.checkStability = false;
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Generating Water"));
				}
				else if(params[1].equals("hotwater"))
				{
					gen = new WorldGenFissure(TFCBlocks.hotWater);
					gen.checkStability = false;
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Generating Hot Springs"));
				}
				else
				{
					gen = new WorldGenFissure(null);
					gen.checkStability = false;
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Generating Fissure"));
				}
				gen.generate(sender.getEntityWorld(), sender.getEntityWorld().rand, (int)player.posX, (int)player.posY - 1, (int)player.posZ);
			}
                        else if ("clear-tree".equalsIgnoreCase(params[0])) {
                            replaceTrees(player, Integer.parseInt(params[1]));
                            return;
                        } 
                        else if ("stop-fire".equalsIgnoreCase(params[0])) {
                            stopFire(player, Integer.parseInt(params[1]));
                            return;
                        }
			else if (params[0].equalsIgnoreCase("tree"))
			{
                                if ("list".equalsIgnoreCase(params[1])) {
                                    TFC_Core.sendInfoMessage(player, new ChatComponentText(getTreesList()));
                                    return;
                                } 
				int i = getTree(params[1]);
				if (i != -1)
				{
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Generating Small " + params[1] + " Tree"));
					WorldGenerator treeGen = TFCBiome.getTreeGen(i, false);
					if (!treeGen.generate(sender.getEntityWorld(), sender.getEntityWorld().rand, (int) player.posX, (int) player.posY, (int) player.posZ))
						TFC_Core.sendInfoMessage(player, new ChatComponentText("Generation Failed"));
				}
				else
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Invalid Tree"));
			} 
		}
		else if (params.length == 3 && params[0].equalsIgnoreCase("tree") && params[2].equalsIgnoreCase("big"))
		{
			int i = getTree(params[1]);

			if (i != -1)
			{
				TFC_Core.sendInfoMessage(player, new ChatComponentText("Generating Big " + params[1] + " Tree"));
				WorldGenerator treeGen = TFCBiome.getTreeGen(i, true);
				if (!treeGen.generate(sender.getEntityWorld(), sender.getEntityWorld().rand, (int) player.posX, (int) player.posY, (int) player.posZ))
					TFC_Core.sendInfoMessage(player, new ChatComponentText("Generation Failed"));
			}
			else
				TFC_Core.sendInfoMessage(player, new ChatComponentText("Invalid Tree"));
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return GEN_USAGE;
	}
        private static final String GEN_USAGE = "fruittree | fissure <water,hotwater,x> | tree name <big> | tree list | clear-tree chunk-radius | stop-fire chunk-radius";
        private static final String[] TREES = {"oak", "aspen", "birch", "chestnut", "douglasfir", "hickory", "maple", "ash", "pine", "sequoia", "spruce", "sycamore", "whitecedar", "whiteelm", "willow", "kapok", "acacia"};
	public int getTree(String tree)
	{
            if (isNullOrEmpty(tree)) return -1;
            
            for (int i = 0; i < TREES.length; i++) {
                if (TREES[i].equalsIgnoreCase(tree)) return i;
            }
            return -1;
	}
        
        public String getTreesList() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < TREES.length; i++) {
                if (i> 0) sb.append(' ');
                sb.append(TREES[i]);
            }
            return sb.toString();
        }
        private void replaceTrees(EntityPlayerMP player, int radius) 
        {
                TFC_Core.sendInfoMessage(player, new ChatComponentText("Clearing Area from Trees Within a Radius of " + radius)); 
                World world = player.worldObj;
                if (radius > 8) 
                    radius = 0;
                long pack = CmdUtils.getRangeForPlayer(player);
                int lowY = CmdUtils.getA(pack), highY = CmdUtils.getB(pack);
                
                for (int i = -radius; i <= radius; i++)
                {
                    for (int k = -radius; k <= radius; k++)
                    {
                        Chunk chunk = world.getChunkFromBlockCoords((int) player.posX + i * 16, (int) player.posZ + k * 16);
                        for (int x = 0; x < 16; x++)
                        {
                            for (int z = 0; z < 16; z++)
                            {
                                for (int y = highY; y > lowY; y-- )
                                {
                                    Block block = chunk.getBlock(x, y, z);
                                    if (block == Blocks.air) continue;
                                    if (block == TFCBlocks.logNatural || block == TFCBlocks.logNatural2 || block == TFCBlocks.leaves || block== TFCBlocks.leaves2)
                                             world.setBlock(x + (chunk.xPosition * 16), y, z + (chunk.zPosition * 16), Blocks.air, 0, 2);
                                 }
                            }
                        }
                    }
                }
                TFC_Core.sendInfoMessage(player, new ChatComponentText("Clearing Area Complete"));
        }
        private void stopFire(EntityPlayerMP player, int radius) 
        {
                TFC_Core.sendInfoMessage(player, new ChatComponentText("Clearing Area from Fire Within a Radius of " + radius));
                World world = player.worldObj;
                if (radius > 8) 
                    radius = 0;
                long pack = CmdUtils.getRangeForPlayer(player);
                int lowY = CmdUtils.getA(pack), highY = CmdUtils.getB(pack);
                
                for (int i = -radius; i <= radius; i++)
                {
                    for (int k = -radius; k <= radius; k++)
                    {
                        Chunk chunk = world.getChunkFromBlockCoords((int) player.posX + i * 16, (int) player.posZ + k * 16);
                        for (int x = 0; x < 16; x++)
                        {
                            for (int z = 0; z < 16; z++)
                            {
                                for (int y = highY; y > lowY; y-- )
                                {
                                    Block block = chunk.getBlock(x, y, z);
                                    if (block == Blocks.fire)
                                        world.setBlock(x + (chunk.xPosition * 16), y, z + (chunk.zPosition * 16), Blocks.air, 0, 2);
                                }
                            }
                        }
                    }
                }
                TFC_Core.sendInfoMessage(player, new ChatComponentText("Clearing Area Complete"));
        }
}
