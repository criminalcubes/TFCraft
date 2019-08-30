package com.bioxx.tfc.Handlers;

import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.TileEntities.NetworkTileEntity;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.api.TFCOptions;
import net.minecraft.world.WorldServer;

import java.util.*;

public class ServerTickHandler
{
	private long wSeed = Long.MIN_VALUE;
	public int ticks;

	private static long  lastInvTickTime = 0;

	@SubscribeEvent
	public void onServerWorldTick(WorldTickEvent event)
	{
		World world = event.world;
		if(event.phase == Phase.START)
		{
			if(world.provider.dimensionId == 0 && world.getWorldInfo().getSeed() != wSeed)
			{
				TFC_Core.setupWorld(world);
				wSeed = world.getWorldInfo().getSeed();
			}
			TFC_Time.updateTime(world);

			/*if(ServerOverrides.isServerEmpty())
				return;*/
			if(MinecraftServer.getServer().getCurrentPlayerCount() == 0 && TFCOptions.simSpeedNoPlayers > 0)
			{
				ticks++;
				long t = world.getWorldInfo().getWorldTotalTime();
				long w = world.getWorldInfo().getWorldTime();
				if(ticks < TFCOptions.simSpeedNoPlayers)
				{
					world.getWorldInfo().incrementTotalWorldTime(t-1L);
					world.getWorldInfo().setWorldTime(w-1L);
				}
				else
				{
					ticks = 0;
				}
			}
		}
		/*else if(event.phase == Phase.END)
		{
		
		}*/
	}

	@SubscribeEvent
	public void onServerTick(WorldTickEvent event) {
		if (event.side == Side.SERVER) {
			long now = MinecraftServer.getSystemTimeMillis();
			if (now - lastInvTickTime >= 30000) {
				lastInvTickTime = now;

				int eCounter = 0;
				int teCounter = 0;
				for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
					List<TileEntity> tileEntityList = new ArrayList<TileEntity>(worldServer.loadedTileEntityList);

					for (Iterator<TileEntity> teIterator = tileEntityList.iterator(); teIterator.hasNext();) {
						TileEntity te = teIterator.next();
						if (te instanceof IInventory && !(te instanceof NetworkTileEntity)) {
							IInventory iinv = (IInventory) te;
							TFC_Core.handleItemTicking(iinv, te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, TFCOptions.foodDecayRateInOtherContainers);
							teCounter++;
						}
					}

					List<Entity> entityList = new ArrayList<Entity>(worldServer.loadedEntityList);

					for (Iterator<Entity> eIterator = entityList.iterator(); eIterator.hasNext();) {
						Entity e = eIterator.next();
						if (e instanceof IInventory) {
							IInventory iinv = (IInventory) e;
							TFC_Core.handleItemTicking(iinv, e.worldObj, (int)e.posX, (int)e.posY, (int)e.posZ, TFCOptions.foodDecayRateInOtherContainers);
                            eCounter++;
						}
					}
				}

				System.out.println("[TerraFirmaCraft] HandleItemTicking: tileEntities - " + teCounter + ", entities - " + eCounter);
			}
		}
	}
}
