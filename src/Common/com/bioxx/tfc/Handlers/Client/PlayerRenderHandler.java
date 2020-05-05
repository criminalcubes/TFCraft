package com.bioxx.tfc.Handlers.Client;

import com.bioxx.tfc.Core.Player.InventoryPlayerTFC;
import com.bioxx.tfc.Items.ItemBlocks.ItemBarrels;
import com.bioxx.tfc.Items.ItemQuiver;
import com.bioxx.tfc.Render.RenderLargeItem;
import com.bioxx.tfc.Render.RenderQuiver;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent.Specials.Pre;

public class PlayerRenderHandler {

	public static final RenderQuiver RENDER_QUIVER = new RenderQuiver();
	public static final RenderLargeItem RENDER_LARGE = new RenderLargeItem();

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerRenderTick(/*RenderPlayerEvent.Specials.*/Pre e) {
		EntityLivingBase el = e.entityLiving;

		if(el instanceof EntityPlayer){
			if(((EntityPlayer)el).inventory instanceof InventoryPlayerTFC){
				//ItemStack equipables[] = TFC_Core.getBack((EntityPlayer)el);
				ItemStack[] equipables = ((InventoryPlayerTFC)((EntityPlayer)el).inventory).extraEquipInventory;

				for (ItemStack i : equipables) {
				    if (i != null && i.getItem() != null) {
				        if (i.getItem() instanceof ItemQuiver) {
                            RENDER_QUIVER.render(e.entityLiving, i, e.renderer);

                        } else if (i.getItem() instanceof ItemBarrels) {
                            RENDER_LARGE.render(e.entityLiving, i, e);


                        } else {
                            // do nothing (shields renders in another mod)
                        }
                    }
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		/*if (e.side.isClient()) {
		
		}*/
	}
}
