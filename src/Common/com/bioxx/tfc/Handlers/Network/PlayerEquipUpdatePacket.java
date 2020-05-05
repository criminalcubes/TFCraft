package com.bioxx.tfc.Handlers.Network;

import com.bioxx.tfc.Core.Player.InventoryPlayerTFC;
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
    private int itemId;
    private int itemDamage;


    public PlayerEquipUpdatePacket() {
    }

    public PlayerEquipUpdatePacket(int playerId, int itemId, int itemDamage) {
        this.playerId = playerId;
        this.itemId = itemId;
        this.itemDamage = itemDamage;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        PacketBuffer pb = new PacketBuffer(buffer);
        try {
            pb.writeInt(this.playerId);
            pb.writeInt(this.itemId);
            pb.writeInt(this.itemDamage);
        } catch (Exception e) {
            System.out.println("[TerraFirmaCraft] (ERROR) Error on encode PlayerEquipUpdatePacket: " + e.getMessage());
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        PacketBuffer pb = new PacketBuffer(buffer);
        try {
            this.playerId = pb.readInt();
            this.itemId = pb.readInt();
            this.itemDamage = pb.readInt();
        } catch (Exception e) {
            System.out.println("[TerraFirmaCraft] (ERROR) Error on decode PlayerEquipUpdatePacket: " + e.getMessage());
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

        ItemStack is = new ItemStack(Item.getItemById(this.itemId), 1,this.itemDamage);
        ((InventoryPlayerTFC) ((EntityPlayer) playerToUpd).inventory).extraEquipInventory[0] = is;
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        // Nothing to do
    }
}
