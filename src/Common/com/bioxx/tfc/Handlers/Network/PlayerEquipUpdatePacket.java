package com.bioxx.tfc.Handlers.Network;

import com.bioxx.tfc.Core.Player.InventoryPlayerTFC;
import com.bioxx.tfc.Items.ItemBlocks.ItemBarrels;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCItems;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PlayerEquipUpdatePacket extends AbstractPacket {
    private int playerId;

    private ItemStack stack = null;


    public PlayerEquipUpdatePacket() {
    }

    public PlayerEquipUpdatePacket(ItemStack stack, int playerId) {
        this.playerId = playerId;
        this.stack = stack;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {


        PacketBuffer pb = new PacketBuffer(buffer);
        try {
            pb.writeItemStackToBuffer(this.stack);
            pb.writeInt(playerId);
        } catch (Exception IOException) {

        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {

        PacketBuffer pb = new PacketBuffer(buffer);


        try {
            this.stack = pb.readItemStackFromBuffer();
            this.playerId = pb.readInt();
        } catch (Exception IOException) {

        }

    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        if (Minecraft.getMinecraft().thePlayer.getEntityId() == playerId) {
            return;
        }

        Entity playerToUpd = player.worldObj.getEntityByID(playerId);
        if (playerToUpd == null || !(playerToUpd instanceof EntityPlayer)) {
            return;
        }
        ((InventoryPlayerTFC) ((EntityPlayer) playerToUpd).inventory).extraEquipInventory[0] = stack;


    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        // Nothing to do
    }
}
