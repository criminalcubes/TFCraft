package com.bioxx.tfc.Handlers;

import com.bioxx.tfc.Containers.ContainerTFC;
import com.bioxx.tfc.TileEntities.NetworkTileEntity;
import com.bioxx.tfc.api.TFC_ItemHeat;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import joptsimple.internal.Strings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.*;

public class ServerTickHandler
{
	private long wSeed = Long.MIN_VALUE;
	public int ticks;

	private long lastInvTickTime = 0;
	private long minContainersTickPeriod = 1000;
	private int itemHeatingCount = 99;
	private boolean processingContainers = false;
	private List<Boolean> processingContainersStatuses = Collections.synchronizedList(new ArrayList<Boolean>());

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

	private List<String> teBlacklist = Arrays.asList(
			"sladki.tfc.ab.TileEntities.TEPotteryKiln",
			"cubex2.mods.multipagechest.TileEntityMultiPageChest",
			"com.bioxx.tfc.TileEntities.TEChest",
			"sladki.tfc.TileEntities.TECellarShelf",
			"com.bioxx.tfc.TileEntities.TEFruitTreeWood",
			"sladki.tfc.TileEntities.TEIceBunker",
			"com.bioxx.tfc.TileEntities.TESluice",
			"com.bioxx.tfc.TileEntities.TENestBox",
			"com.bioxx.tfc.TileEntities.TELogPile"
	);

	private List<String> teWhitelist = Arrays.asList(
			"udary.tfcudarymod.tileentities.devices.TileEntityAlloyCalculator"
	);

	@SubscribeEvent
	public void onServerTick(WorldTickEvent event) {
		if (event.side == Side.SERVER) {
			updateProcessingChunks();
			long now = MinecraftServer.getSystemTimeMillis();
			if (!processingContainers && now - lastInvTickTime >= minContainersTickPeriod) {
				processingContainers = true;
				lastInvTickTime = now;

				int worldCounter = 0;
				for (final WorldServer ws : MinecraftServer.getServer().worldServers) {
					processingContainersStatuses.add(false);
					final int innerWorldCounter = worldCounter;

					Runnable filterTask = new Runnable() {
						@Override
						public void run() {
							try {
								int eCounter = 0;
								int teCounter = 0;
								Map<String, Integer> containersMap = new HashMap<String, Integer>();

								List<Chunk> chunks = new ArrayList<Chunk>(ws.theChunkProviderServer.loadedChunks);

								Iterator iterator = chunks.iterator();
								while (iterator.hasNext()) {
									Chunk chunk = (Chunk) iterator.next();

									if (chunk.chunkTileEntityMap != null && chunk.chunkTileEntityMap.values() != null) {
										List<TileEntity> tileEntityList = new ArrayList<TileEntity>(chunk.chunkTileEntityMap.values());
										for (Object obj : tileEntityList) {
											TileEntity te = (TileEntity) obj;

											if (teBlacklist.contains(te.getClass().getName())) {
												continue;
											}

											if (te instanceof IInventory && (teWhitelist.contains(te.getClass().getName()) || !(te instanceof NetworkTileEntity))) {
												IInventory iinv = (IInventory) te;
												TFC_Core.handleItemTicking(iinv, te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, TFCOptions.foodDecayRate);
												for (int i = 0; i < iinv.getSizeInventory(); i++) {
													ItemStack is = iinv.getStackInSlot(i);
													if (is != null) {
														for (int j = 0; j < itemHeatingCount; j++) {
															TFC_ItemHeat.handleItemHeat(is);
														}
													}
												}

												teCounter++;

												String key = te.getClass().getName();
												if (containersMap.get(key) == null) {
													containersMap.put(key, 1);
												} else {
													containersMap.put(key, containersMap.get(key) + 1);
												}
											}
										}
									}

									if (chunk.entityLists != null) {
										List<List> entityListsList = Arrays.asList(chunk.entityLists);
										for (List l : entityListsList) {
											List<Entity> entityList = new ArrayList<Entity>(l);
											for (Entity e : entityList) {
												if (e instanceof IInventory) {
													IInventory iinv = (IInventory) e;
													TFC_Core.handleItemTicking(iinv, e.worldObj, (int)e.posX, (int)e.posY, (int)e.posZ, TFCOptions.foodDecayRate);
													for (int i = 0; i < iinv.getSizeInventory(); i++) {
														ItemStack is = iinv.getStackInSlot(i);
														if (is != null) {
															for (int j = 0; j < itemHeatingCount; j++) {
																TFC_ItemHeat.handleItemHeat(is);
															}
														}
													}

													eCounter++;

													String key = e.getClass().getName();
													if (containersMap.get(key) == null) {
														containersMap.put(key, 1);
													} else {
														containersMap.put(key, containersMap.get(key) + 1);
													}
												}
											}
										}
									}
								}

								/*
								System.out.println("[TerraFirmaCraft][DIM" + ws.provider.dimensionId + "] HandleItemTicking: tileEntities - " + teCounter + ", entities - " + eCounter);
								for (Map.Entry entry : containersMap.entrySet()) {
									System.out.println("[TerraFirmaCraft][DIM" + ws.provider.dimensionId + "] " + entry.getValue() + " - " + entry.getKey());
								}
								*/
							} catch (Exception e) {
								System.out.println("[TerraFirmaCraft] (ERROR) Error on " + Thread.currentThread().getName() + ": " + ExceptionUtils.getRootCauseMessage(e));
								e.printStackTrace();
							}

							processingContainersStatuses.set(innerWorldCounter, true);
							Thread.currentThread().interrupt();
						}
					};

					Thread thread = new Thread(filterTask, "containersTickThread-DIM" + ws.provider.dimensionId);
					thread.start();

					worldCounter++;
				}
			}
		}
	}

	private void updateProcessingChunks() {
		int stat = 0;
		for (Boolean status : processingContainersStatuses) {
			if (status) {
				stat++;
			}
		}
		if (stat == processingContainersStatuses.size()) {
			processingContainers = false;
			processingContainersStatuses.clear();
		}
	}
}
