package com.bioxx.tfc.Render;

import com.bioxx.tfc.Render.Models.ModelBarrel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Specials.Pre;
import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.api.Interfaces.IEquipable;

public class RenderLargeItem {

	//private ModelQuiver quiver = new ModelQuiver();

	//private static final ResourceLocation tempTexture = new ResourceLocation(Reference.ModID, "textures/models/armor/leatherquiver_1.png");
	//private ModelBarrel barrel = new ModelBarrel(0);
	public RenderLargeItem(){

	}
	public void render(Entity entity, ItemStack item, Pre e) {
		this.doRender(entity,e.renderer, item);
	}

	public void render(EntityPlayer  entity, ItemStack item, Pre e){this.doRender(entity, e.renderer, item);
	}



	public void doRender(Entity entity, RenderPlayer renderModel, ItemStack item){
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
