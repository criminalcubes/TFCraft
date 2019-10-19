package com.bioxx.tfc.Render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.api.Interfaces.IEquipable;

public class RenderLargeItem {

	//private ModelQuiver quiver = new ModelQuiver();
	//private static final ResourceLocation tempTexture = new ResourceLocation(Reference.ModID, "textures/models/armor/leatherquiver_1.png");

	public RenderLargeItem(){

	}

//	public void render(EntityLivingBase entity, ItemStack item) {
//		this.doRender(entity, item);
//	}

	public void render(EntityPlayer entity, RenderPlayer renderModel, ItemStack item) {
		this.doRender(entity, renderModel, item);
	}

	public void render(Entity entity, ItemStack item) {
		this.doRender(entity, item);
	}

//	public void doRender(EntityLivingBase entity, ItemStack item){
//		float entityTranslateY = entity instanceof EntityPlayer ? 0F : -1.5F;
//		GL11.glPushMatrix();
//		//Minecraft.getMinecraft().renderEngine.bindTexture(tempTexture);
//		if (!entity.isSneaking()){ GL11.glTranslatef(0F,  0.2F + entityTranslateY + 0.0F/*0.65F*/, 0.5F);
//		}
//		else{ GL11.glTranslatef(0F, 0.2F + entityTranslateY - 0.1F/*0.55F*/, 0.6F);
//		GL11.glRotatef(20F, 1F, 0F, 0F);}
//		GL11.glScalef(0.8F, 0.8F, 0.8F);
//		GL11.glRotatef(180, 0F, 0F, 1F);
//		Block block ;
//		if(item != null){
//			if(item.getItem() instanceof IEquipable){
//				((IEquipable)(item.getItem())).onEquippedRender();
//			}
//			else if(Block.getBlockFromItem(item.getItem()) instanceof IEquipable){
//				((IEquipable)(Block.getBlockFromItem(item.getItem()))).onEquippedRender();
//			}
//			block = Block.getBlockFromItem(item.getItem());
//			TFC_Core.bindTexture(TextureMap.locationBlocksTexture);
//			RenderBlocks.getInstance().renderBlockAsItem(block, item.getItemDamage(), 1F);
//		}
//		GL11.glPopMatrix();
//	}

	public void doRender(Entity entity, ItemStack item){
		float entityTranslateY = entity instanceof EntityPlayer ? 0F : -1.5F;
		GL11.glPushMatrix();
		//Minecraft.getMinecraft().renderEngine.bindTexture(tempTexture);
		if (!entity.isSneaking())
		{
			GL11.glTranslatef(0F,  0.2F + entityTranslateY + 0.0F/*0.65F*/, 0.5F);
		}
		else
		{
			GL11.glTranslatef(0F, 0.2F + entityTranslateY - 0.1F/*0.55F*/, 0.6F);
			GL11.glRotatef(20F, 1F, 0F, 0F);
		}
		GL11.glScalef(0.8F, 0.8F, 0.8F);
		GL11.glRotatef(180, 0F, 0F, 1F);
		Block block ;
		if(item != null){
			if(item.getItem() instanceof IEquipable){
				((IEquipable)(item.getItem())).onEquippedRender();
			}
			else if(Block.getBlockFromItem(item.getItem()) instanceof IEquipable){
				((IEquipable)(Block.getBlockFromItem(item.getItem()))).onEquippedRender();
			}
			block = Block.getBlockFromItem(item.getItem());
			TFC_Core.bindTexture(TextureMap.locationBlocksTexture);
			RenderBlocks.getInstance().renderBlockAsItem(block, item.getItemDamage(), 1F);
		}
		GL11.glPopMatrix();
	}

	public void doRender(EntityPlayer entity, RenderPlayer renderModel, ItemStack item){
		GL11.glPushMatrix();
		renderModel.modelBipedMain.bipedBody.postRender(0.0625F);
		GL11.glTranslatef(0.0f, 0.6f, 0.5f);
		GL11.glRotated(0,0f,0f,1f);
		GL11.glScalef(0.8F, 0.8F, 0.8F);

		Block block ;
		if (item != null) {
			if(item.getItem() instanceof IEquipable){
				((IEquipable)(item.getItem())).onEquippedRender();
			}
			else if(Block.getBlockFromItem(item.getItem()) instanceof IEquipable){
				((IEquipable)(Block.getBlockFromItem(item.getItem()))).onEquippedRender();
			}
			block = Block.getBlockFromItem(item.getItem());
			TFC_Core.bindTexture(TextureMap.locationBlocksTexture);
			RenderBlocks.getInstance().renderBlockAsItem(block, item.getItemDamage(), 1F);
		}

		GL11.glPopMatrix();
	}
}
