package com.bioxx.tfc.Core.Player;

import com.bioxx.tfc.Entities.AI.EntityAIPanicTFC;
import cpw.mods.fml.common.eventhandler.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;

import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;

import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.Config.TFC_ConfigFiles;
import com.bioxx.tfc.Handlers.Network.AbstractPacket;
import com.bioxx.tfc.Handlers.Network.ConfigSyncPacket;
import com.bioxx.tfc.Handlers.Network.InitClientWorldPacket;
import com.bioxx.tfc.Handlers.Network.PlayerUpdatePacket;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PlayerTracker
{
	private Random random = new Random();

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		//		TerraFirmaCraft.log.info("-----------------------------PLAYER LOGGIN EVENT-------------------");
		//		TerraFirmaCraft.log.info("------"+event.player.getDisplayName()+" : "+ event.player.getUniqueID().toString()+"--------");

		PlayerManagerTFC.getInstance().players.add(new PlayerInfo(
				event.player.getCommandSenderName(),
				event.player.getUniqueID()));
		TFC_ConfigFiles.reloadAll();
		AbstractPacket pkt = new InitClientWorldPacket(event.player);
		TerraFirmaCraft.PACKET_PIPELINE.sendTo(pkt, (EntityPlayerMP) event.player);
		TerraFirmaCraft.PACKET_PIPELINE.sendTo(new ConfigSyncPacket(), (EntityPlayerMP) event.player);

		//		TerraFirmaCraft.log.info("-----------------------------Sending TestPacket");
		//AbstractPacket pkt2 = new TestPacket("Sent to Player: "+event.player.getDisplayName());
		//TerraFirmaCraft.packetPipeline.sendTo(pkt2, (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onClientConnect(ClientConnectedToServerEvent event)
	{

		//		TerraFirmaCraft.log.info("-----"+FMLClientHandler.instance().getClientPlayerEntity().getDisplayName()+" : "+
		//				FMLClientHandler.instance().getClientPlayerEntity().getUniqueID().toString()+"-------");
		//
		TerraFirmaCraft.proxy.onClientLogin();
	}

	@SubscribeEvent
	public void onClientDisconnect(ServerDisconnectionFromClientEvent event)
	{
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		float foodLevel = event.player.worldObj.rand.nextFloat() * 12 + 12;
		FoodStatsTFC foodstats = TFC_Core.getPlayerFoodStats(event.player);
		foodstats.setFoodLevel(foodLevel);
		TFC_Core.setPlayerFoodStats(event.player, foodstats);
		event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000);
		event.player.setHealth(1000f * (0.25f + (event.player.worldObj.rand.nextFloat() * 0.25f)));

		PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(event.player);
		if( pi.tempSkills != null)
			TFC_Core.setSkillStats(event.player, pi.tempSkills);

		// Load the item in the back slot if keepInventory was set to true.
		if (pi.tempEquipment != null && event.player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
		{
			InventoryPlayerTFC invPlayer = (InventoryPlayerTFC) event.player.inventory;
			invPlayer.extraEquipInventory = pi.tempEquipment.clone();
			pi.tempEquipment = null;
		}

		// Send a request to the server for the skills data.
		AbstractPacket pkt = new PlayerUpdatePacket(event.player, 3);
		TerraFirmaCraft.PACKET_PIPELINE.sendTo(pkt, (EntityPlayerMP) event.player);

		// Restore exp
		double percent = 0.3;
		EntityPlayer player = event.player;
		if (pi.restoreExp != 0) {
			TFC_Core.sendInfoMessage(player, new ChatComponentTranslation("exp.restore", pi.restoreExp, pi.restoreExp - (int)(pi.restoreExp * percent)));
			//player.experienceTotal = pi.restoreExp - (int)(pi.restoreExp * percent);
			player.addExperience(pi.restoreExp - (int)(pi.restoreExp * percent));
			pi.restoreExp = 0;
		}

		// Restore workbench
		if (pi.hasWorkbench) {
			player.getEntityData().setBoolean("craftingTable", true);
			PlayerInventory.upgradePlayerCrafting(player);
		}

		// Send coordinates
		if (pi.deathX != 0 && pi.deathY != 0 && pi.deathZ != 0) {
			int messageNumber = random.nextInt(3) + 1;
			String messageCode = "respawn.message" + messageNumber;
			TFC_Core.sendInfoMessage(player, new ChatComponentTranslation(messageCode, pi.deathX, (int) pi.deathY, (int) pi.deathZ));
			pi.deathX = 0;
			pi.deathY = 0;
			pi.deathZ = 0;
		}
	}

	@SubscribeEvent
	public void notifyPickup(ItemPickupEvent event)
	{
		/*ItemStack quiver = null;
		ItemStack ammo = item.getEntityItem();
		for(int i = 0; i < 9; i++) 
		{
			if(player.inventory.getStackInSlot(i) != null && player.inventory.getStackInSlot(i).getItem() instanceof ItemQuiver)
			{
				quiver = player.inventory.getStackInSlot(i);
				break;
			}
		}

		if(quiver != null && (ammo.getItem() instanceof ItemArrow || ammo.getItem() instanceof ItemJavelin))
		{
			ItemStack is = ((ItemQuiver)quiver.getItem()).addItem(quiver, ammo);
			item.setEntityItemStack(is);
		}*/
	}

	// Register the Player Toss Event Handler, workaround for a crash fix
	@SubscribeEvent
	public void onPlayerTossEvent(ItemTossEvent event)
	{
		if(event.entityItem == null)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event)  {

		// ???
		try
		{
			event.player.inventoryContainer.addCraftingToCrafters((ICrafting)event.player);
		}
		catch (IllegalArgumentException exception)
		{
			//LogHelper.error(ReferenceTAPI.MOD_NAME, "Inventory has already be resync'd");
		}

		AbstractPacket pkt = new InitClientWorldPacket(event.player);
		TerraFirmaCraft.PACKET_PIPELINE.sendTo(pkt, (EntityPlayerMP) event.player);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player.isPlayerSleeping()) {
			if (event.player.posY < 0) {
				event.player.setPositionAndUpdate(event.player.posX, event.player.posY + 256, event.player.posZ);
			}
		}
	}
}

