package com.bioxx.tfc.Render;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.TFCBlocks;
import com.bioxx.tfc.Blocks.Devices.BlockSluice;
import com.bioxx.tfc.Blocks.Flora.BlockFruitLeaves;
import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.Food.FloraIndex;
import com.bioxx.tfc.Food.FloraManager;
import com.bioxx.tfc.Render.Blocks.RenderFlora;
import com.bioxx.tfc.TileEntities.TEPartial;
import com.bioxx.tfc.TileEntities.TEWaterPlant;
import com.bioxx.tfc.TileEntities.TileEntityFruitTreeWood;
import com.bioxx.tfc.WorldGen.DataLayer;
import com.bioxx.tfc.WorldGen.TFCWorldChunkManager;
import com.bioxx.tfc.api.TFCOptions;

public class TFC_CoreRender
{
	public static boolean renderBlockSlab(Block block, int x, int y, int z, RenderBlocks renderblocks)
	{
		TEPartial te = (TEPartial) renderblocks.blockAccess.getTileEntity(x, y, z);
		int md = renderblocks.blockAccess.getBlockMetadata(x, y, z);

		boolean breaking = false;
		/*if(renderblocks.overrideBlockTexture >= 240)
        {
        	breaking = true;
        }*/

		if(te.TypeID <= 0)
			return false;

		int type = te.TypeID;
		int meta = te.MetaID;
		Block b = Block.getBlockById(type);
		IIcon tex = b.getIcon(0, meta);

		//if(!breaking)
		//	ForgeHooksClient.bindTexture(Block.blocksList[type].getTextureFile(), ModLoader.getMinecraftInstance().renderEngine.getTexture(Block.blocksList[type].getTextureFile()));

		long extraX = (te.extraData) & 0xf;
		long extraY = (te.extraData >> 4) & 0xf;
		long extraZ = (te.extraData >> 8) & 0xf;
		long extraX2 = (te.extraData >> 12) & 0xf;
		long extraY2 = (te.extraData >> 16) & 0xf;
		long extraZ2 = (te.extraData >> 20) & 0xf;

		float div = 1f / 8;

		renderblocks.setRenderBounds(0.0F+ (div * extraX), 0.0F+ (div * extraY), 0.0F+ (div * extraZ), 1.0F-(div * extraX2), 1-(div * extraY2), 1.0F-(div * extraZ2));

		//This is the old ore code that I experimented with
		IIcon over = renderblocks.overrideBlockTexture;
		if(!breaking && (b == TFCBlocks.Ore || b == TFCBlocks.Ore2 || b == TFCBlocks.Ore3))
		{
			//TFCBiome biome = (TFCBiome) renderblocks.blockAccess.getBiomeGenForCoords(par2, par4);
			renderblocks.overrideBlockTexture = getRockTexture(Minecraft.getMinecraft().theWorld, x, y, z);
			renderblocks.renderStandardBlock(block, x, y, z);
			renderblocks.overrideBlockTexture = over;
		}

		if(!breaking)
			renderblocks.overrideBlockTexture = tex;

		renderblocks.renderStandardBlock(block, x, y, z);
		renderblocks.overrideBlockTexture = over;

		return true;
	}

	public static boolean renderBlockStairs(Block block, int x, int y, int z, RenderBlocks renderblocks)
	{
		boolean breaking = false;
		/*if(renderblocks.overrideBlockTexture >= 240)
        {
        	breaking = true;
        }*/

		int var5 = renderblocks.blockAccess.getBlockMetadata(x, y, z);
		int var6 = var5 & 3;
		float var7 = 0.0F;
		float var8 = 0.5F;
		float var9 = 0.5F;
		float var10 = 1.0F;

		if ((var5 & 4) != 0)
		{
			var7 = 0.5F;
			var8 = 1.0F;
			var9 = 0.0F;
			var10 = 0.5F;
		}

		TEPartial te = (TEPartial) renderblocks.blockAccess.getTileEntity(x, y, z);
		if(te.TypeID <= 0)
			return false;

		int type = te.TypeID;
		int meta = te.MetaID;
		IIcon tex = Block.getBlockById(type).getIcon(0, meta);
		if(!breaking)
		{
			//ForgeHooksClient.bindTexture(Block.blocksList[type].getTextureFile(), ModLoader.getMinecraftInstance().renderEngine.getTexture(Block.blocksList[type].getTextureFile()));
			renderblocks.overrideBlockTexture = tex;
		}
		renderblocks.renderAllFaces = true;
		renderblocks.setRenderBounds(0.0F, var7, 0.0F, 1.0F, var8, 1.0F);
		renderblocks.renderStandardBlock(block, x, y, z);

		if (var6 == 0)
		{
			renderblocks.setRenderBounds(0.5F, var9, 0.0F, 1.0F, var10, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		else if (var6 == 1)
		{
			renderblocks.setRenderBounds(0.0F, var9, 0.0F, 0.5F, var10, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		else if (var6 == 2)
		{
			renderblocks.setRenderBounds(0.0F, var9, 0.5F, 1.0F, var10, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		else if (var6 == 3)
		{
			renderblocks.setRenderBounds(0.0F, var9, 0.0F, 1.0F, var10, 0.5F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		renderblocks.clearOverrideBlockTexture();
		renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderblocks.renderAllFaces = false;
		return true;
	}

	public static boolean RenderSulfur(Block block, int x, int y, int z, RenderBlocks renderblocks)
	{
		IBlockAccess world = renderblocks.blockAccess;
		if(world.getBlock(x, y, z+1).isSideSolid(world, x, y, z, ForgeDirection.NORTH))
		{
			renderblocks.setRenderBounds(0.0F, 0.0F, 0.99F, 1.0F, 1.0F, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		if(world.getBlock(x, y, z-1).isSideSolid(world, x, y, z, ForgeDirection.SOUTH))
		{
			renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.01F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		if(world.getBlock(x+1, y, z).isSideSolid(world, x, y, z, ForgeDirection.EAST))
		{
			renderblocks.setRenderBounds(0.99F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		if(world.getBlock(x-1, y, z).isSideSolid(world, x, y, z, ForgeDirection.WEST))
		{
			renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 0.01F, 1.0F, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		if(world.getBlock(x, y+1, z).isSideSolid(world, x, y, z, ForgeDirection.DOWN))
		{
			renderblocks.setRenderBounds(0.0F, 0.99F, 0.0F, 1.0F, 1.0F, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}
		if(world.getBlock(x, y-1, z).isSideSolid(world, x, y, z, ForgeDirection.UP))
		{
			renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.01F, 1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		return true;
	}

	public static boolean RenderSnow(Block block, int i, int j, int k, RenderBlocks renderblocks)
	{
		int meta = renderblocks.blockAccess.getBlockMetadata(i, j, k);
		float drift = 0.04F + (meta * 0.06F);
		renderblocks.setRenderBounds(0.0F, 0.0F, 0F, 1.0F, drift, 1.0F);
		renderblocks.renderStandardBlock(block, i, j, k);
		return true;
	}

	public static boolean RenderWoodTrunk(Block block, int i, int j, int k, RenderBlocks renderblocks)
	{
		IBlockAccess blockAccess = renderblocks.blockAccess;

		if(true/*blockAccess.getBlockMaterial(i, j+1, k) == Material.leaves || blockAccess.getBlockMaterial(i, j-1, k) == Material.leaves || 
                blockAccess.getBlock(i, j+1, k) == mod_TFC_Core.fruitTreeWood || blockAccess.getBlock(i, j-1, k) == mod_TFC_Core.fruitTreeWood*/)
		{
			if(blockAccess.getTileEntity(i, j, k) != null && (blockAccess.getBlock(i, j-1, k) == TFCBlocks.fruitTreeWood || blockAccess.getBlock(i, j-1, k).isOpaqueCube()))
			{
				renderblocks.setRenderBounds(0.3F, 0.0F, 0.3F, 0.7F, 1.0F, 0.7F);
				renderblocks.renderStandardBlock(block, i, j, k);
			}
			if(blockAccess.getBlock(i-1, j, k).getMaterial() == Material.leaves || blockAccess.getBlock(i-1, j, k) == TFCBlocks.fruitTreeWood)
			{
				renderblocks.setRenderBounds(0.0F, 0.4F, 0.4F, 0.5F, 0.6F, 0.6F);
				renderblocks.renderStandardBlock(block, i, j, k);
			}
			if(blockAccess.getBlock(i+1, j, k).getMaterial() == Material.leaves || blockAccess.getBlock(i+1, j, k) == TFCBlocks.fruitTreeWood)
			{
				renderblocks.setRenderBounds(0.5F, 0.4F, 0.4F, 1.0F, 0.6F, 0.6F);
				renderblocks.renderStandardBlock(block, i, j, k);
			}
			if(blockAccess.getBlock(i, j, k-1).getMaterial() == Material.leaves || blockAccess.getBlock(i, j, k-1) == TFCBlocks.fruitTreeWood)
			{
				renderblocks.setRenderBounds(0.4F, 0.4F, 0.0F, 0.6F, 0.6F, 0.5F);
				renderblocks.renderStandardBlock(block, i, j, k);
			}
			if(blockAccess.getBlock(i, j, k+1).getMaterial() == Material.leaves || blockAccess.getBlock(i, j, k+1) == TFCBlocks.fruitTreeWood)
			{
				renderblocks.setRenderBounds(0.4F, 0.4F, 0.5F, 0.6F, 0.6F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);
			}
		}

		if(!((TileEntityFruitTreeWood)blockAccess.getTileEntity(i, j, k)).isTrunk && blockAccess.getBlock(i, j-1, k) != TFCBlocks.fruitTreeWood && !blockAccess.getBlock(i, j-1, k).isOpaqueCube())
		{
			renderblocks.setRenderBounds(0.0F, 0.4F, 0.4F, 0.5F, 0.6F, 0.6F);
			renderblocks.renderStandardBlock(block, i, j, k);

			renderblocks.setRenderBounds(0.5F, 0.4F, 0.4F, 1.0F, 0.6F, 0.6F);
			renderblocks.renderStandardBlock(block, i, j, k);

			renderblocks.setRenderBounds(0.4F, 0.4F, 0.0F, 0.6F, 0.6F, 0.5F);
			renderblocks.renderStandardBlock(block, i, j, k);

			renderblocks.setRenderBounds(0.4F, 0.4F, 0.5F, 0.6F, 0.6F, 1.0F);
			renderblocks.renderStandardBlock(block, i, j, k);
		}

		//renderblocks.func_83020_a(0.0F, 0.0F, 0.0F, 1F, 1F, 1F);
		return true;
	}

	public static Random renderRandom = new Random();

	public static boolean RenderLooseRock(Block block, int i, int j, int k, RenderBlocks renderblocks)
	{
		boolean breaking = false;
		/*if(renderblocks.overrideBlockTexture >= 240)
        {
        	breaking = true;
        }*/

		int meta = renderblocks.blockAccess.getBlockMetadata(i, j, k);
		World w = Minecraft.getMinecraft().theWorld;
		TFCWorldChunkManager wcm = ((TFCWorldChunkManager)w.getWorldChunkManager());
		renderblocks.renderAllFaces = true;

		DataLayer rockLayer1 = TFC_Climate.getManager(w).getRockLayerAt(i, k, 0);
		if(rockLayer1 != null && rockLayer1.block != null && !breaking)
			renderblocks.overrideBlockTexture = rockLayer1.block.getIcon(0, rockLayer1.data2);

		int seed = i * k + j;
		renderRandom.setSeed(seed);

		float xOffset = (renderRandom.nextInt(5) - 2) * 0.05f;
		float zOffset = (renderRandom.nextInt(5) - 2) * 0.05f;

		float xOffset2 = (renderRandom.nextInt(5) - 2) * 0.05f;
		float yOffset2 = (renderRandom.nextInt(5) - 2) * 0.05f;
		float zOffset2 = (renderRandom.nextInt(5) - 2) * 0.05f;

		renderblocks.setRenderBounds(0.35F + xOffset, 0.00F, 0.35F + zOffset, 0.65F + xOffset2, 0.15F + yOffset2, 0.65F + zOffset2);
		renderblocks.renderStandardBlock(block, i, j, k);
		//renderblocks.func_83020_a(0.20F, 0.00F, 0.2F, 0.8F, 0.25F, 0.8F);
		renderblocks.clearOverrideBlockTexture();

		return true;
	}

	public static boolean RenderOre(Block block, int xCoord, int yCoord, int zCoord,float par5, float par6, float par7, RenderBlocks renderblocks, IBlockAccess iblockaccess)
	{
		/*boolean breaking = false;
        if(renderblocks.overrideBlockTexture >= 240)
        {
        	breaking = true;
        }

        if(!breaking)
        {
        	//render the background rock
            renderblocks.overrideBlockTexture = getRockTexture(ModLoader.getMinecraftInstance().theWorld, xCoord, yCoord, zCoord);
            renderblocks.renderStandardBlock(block, xCoord, yCoord, zCoord);
            renderblocks.clearOverrideBlockTexture();

            //render the ore overlay
            renderblocks.renderStandardBlock(block, xCoord, yCoord, zCoord);
        }

        //renderblocks.renderStandardBlock(block, xCoord, yCoord, zCoord);
		 */
		return true;
	}

	public static IIcon getRockTexture(World world, int xCoord, int yCoord, int zCoord)
	{
		IIcon var27;
		DataLayer rockLayer1 = TFC_Climate.getManager(world).getRockLayerAt(xCoord, zCoord, 0);
		DataLayer rockLayer2 = TFC_Climate.getManager(world).getRockLayerAt(xCoord, zCoord, 1);
		DataLayer rockLayer3 = TFC_Climate.getManager(world).getRockLayerAt(xCoord, zCoord, 2);

		if(yCoord <= TFCOptions.RockLayer3Height)
			var27 = rockLayer3.block.getIcon(5, rockLayer3.data2);
		else if(yCoord <= TFCOptions.RockLayer2Height)
			var27 = rockLayer2.block.getIcon(5, rockLayer2.data2);
		else
			var27 = rockLayer1.block.getIcon(5, rockLayer1.data2);
		return var27;
	}

	public static boolean RenderMolten(Block block, int i, int j, int k, RenderBlocks renderblocks)
	{
		renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderblocks.renderStandardBlock(block, i, j, k);
		//renderblocks.func_83020_a(0.0F, 0.0F, 0.0F, 0.001F, 0.001F, 0.001F);
		return true;
	}

	public static boolean renderFirepit(Block block, int i, int j, int k, RenderBlocks renderblocks)
	{
		IBlockAccess blockAccess = renderblocks.blockAccess;
		renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.02F, 1.0F);
		renderblocks.renderStandardBlock(block, i, j, k);
		renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.02F, 1.0F);		
		return true;
	}

	public static boolean renderForge(Block block, int i, int j, int k, RenderBlocks renderblocks)
	{
		IBlockAccess blockAccess = renderblocks.blockAccess;
		renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9F, 1.0F);
		renderblocks.renderStandardBlock(block, i, j, k);
		renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9F, 1.0F);
		return true;
	}

	public static boolean RenderSluice(Block block, int i, int j, int k, RenderBlocks renderblocks)
	{

		double blockMinX = block.getBlockBoundsMinX();
		double blockMaxX = block.getBlockBoundsMaxX();
		double blockMinY = block.getBlockBoundsMinY();
		double blockMaxY = block.getBlockBoundsMaxY();
		double blockMinZ = block.getBlockBoundsMinZ();
		double blockMaxZ = block.getBlockBoundsMaxZ();
		IBlockAccess blockAccess = renderblocks.blockAccess;
		Tessellator tessellator = Tessellator.instance;
		int l = blockAccess.getBlockMetadata(i, j, k);
		int i1 = BlockSluice.getDirectionFromMetadata(l);
		float f = 0.5F;
		float f1 = 1.0F;
		float f2 = 0.8F;
		float f3 = 0.6F;
		int j1 = block.getMixedBrightnessForBlock(blockAccess, i, j, k);
		tessellator.setBrightness(j1);
		tessellator.setColorOpaque_F(f, f, f);

		IIcon texture = block.getIcon(blockAccess, i, j, k, 0);
		double texMinX = texture.getMinU();
		double texMaxX = texture.getMaxU();
		double texMinY = texture.getMinV();
		double texMaxY = texture.getMaxV();

		double minX = i + blockMinX;
		double maxX = i + blockMaxX;
		double minY = j + blockMinY;
		double minZ = k + blockMinZ;
		double maxZ = k + blockMaxZ;
		double maxY = j + blockMaxY;

		int var10 = blockAccess.getBiomeGenForCoords(i, k).waterColorMultiplier;
		int waterR = (var10 & 16711680) >> 16;
		int waterG = (var10 & 65280) >> 8;
		int waterB = var10 & 255;

		//render ramp
		if(!BlockSluice.isBlockFootOfBed(l))
		{
			if(i1 == 0)
			{
				//ribs
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.75F, 1F, 0.75F, 0.8F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.45F, 1F, 0.9F, 0.5F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(minX, maxY, minZ, texMaxX, texMinY);//d,d3
				tessellator.addVertexWithUV(minX, minY+0.5F, maxZ, texMaxX, texMaxY);//d,d2
				tessellator.addVertexWithUV(maxX, minY+0.5f, maxZ, texMinX, texMaxY);//d1,d2
				tessellator.addVertexWithUV(maxX, maxY, minZ, texMinX, texMinY);//d1,d3
				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getIcon(0, 4);
					l = block.colorMultiplier(blockAccess, i, j, k);
					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();

					//draw water plane
					//tessellator.setColorOpaque_F(0.8F, 0.8F, 0.8F);
					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(minX, maxY, minZ, texMaxX, texMinY);//d,d3
					tessellator.addVertexWithUV(minX, minY+0.6F, maxZ, texMaxX, texMaxY);//d,d2
					tessellator.addVertexWithUV(maxX, minY+0.6f, maxZ, texMinX, texMaxY);//d1,d2
					tessellator.addVertexWithUV(maxX, maxY, minZ, texMinX, texMinY);//d1,d3
				}
			}
			else if(i1 == 1)
			{
				//ribs
				renderblocks.setRenderBounds(0.2F, 0.0F, 0.0F, 0.25F, 0.75F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.5F, 0.0F, 0.0F, 0.55F, 0.9F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(minX, minY+0.5F, maxZ, texMinX, texMaxY);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, texMinX, texMinY);
				tessellator.addVertexWithUV(maxX, maxY, minZ, texMaxX, texMinY);
				tessellator.addVertexWithUV(minX, minY+0.5F, minZ, texMaxX, texMaxY);

				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getIcon(0, 4);
					l = block.colorMultiplier(blockAccess, i, j, k);
					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();

					//draw water plane
					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(minX, minY+0.6F, maxZ, texMinX, texMaxY);
					tessellator.addVertexWithUV(maxX, maxY, maxZ, texMinX, texMinY);
					tessellator.addVertexWithUV(maxX, maxY, minZ, texMaxX, texMinY);
					tessellator.addVertexWithUV(minX, minY+0.6F, minZ, texMaxX, texMaxY);
				}
			}
			else if(i1 == 2)
			{
				//ribs
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.2F, 1F, 0.75F, 0.25F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.5F, 1F, 0.9F, 0.55F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(minX, minY+0.5F, minZ, texMinX, texMaxY);
				tessellator.addVertexWithUV(minX, maxY, maxZ, texMinX, texMinY);
				tessellator.addVertexWithUV(maxX, maxY, maxZ, texMaxX, texMinY);
				tessellator.addVertexWithUV(maxX, minY+0.5F, minZ, texMaxX, texMaxY);

				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getIcon(0, 4);
					l = block.colorMultiplier(blockAccess, i, j, k);
					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();

					//draw water plane
					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(minX, minY+0.6F, minZ, texMinX, texMaxY);
					tessellator.addVertexWithUV(minX, maxY, maxZ, texMinX, texMinY);
					tessellator.addVertexWithUV(maxX, maxY, maxZ, texMaxX, texMinY);
					tessellator.addVertexWithUV(maxX, minY+0.6F, minZ, texMaxX, texMaxY);
				}


			}
			else if(i1 == 3)
			{
				//ribs
				renderblocks.setRenderBounds(0.75F, 0.0F, 0.0F, 0.8F, 0.75F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.45F, 0.0F, 0.0F, 0.5F, 0.9F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(maxX, minY+0.5f, minZ, texMinX, texMaxY);
				tessellator.addVertexWithUV(minX, maxY, minZ, texMinX, texMinY);
				tessellator.addVertexWithUV(minX, maxY, maxZ, texMaxX, texMinY);
				tessellator.addVertexWithUV(maxX, minY+0.5F, maxZ, texMaxX, texMaxY);

				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getBlockTextureFromSide(0);
					l = block.colorMultiplier(blockAccess, i, j, k);
					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();

					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(maxX, minY+0.6f, minZ, texMinX, texMaxY);
					tessellator.addVertexWithUV(minX, maxY, minZ, texMinX, texMinY);
					tessellator.addVertexWithUV(minX, maxY, maxZ, texMaxX, texMinY);
					tessellator.addVertexWithUV(maxX, minY+0.6F, maxZ, texMaxX, texMaxY);
				}
			}
		}
		else
		{
			if(i1 == 0)
			{
				//ribs
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.70F, 1F, 0.3F, 0.75F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.4F, 1F, 0.45F, 0.45F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.1F, 1F, 0.6F, 0.15F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(minX, minY+0.5F, minZ, texMaxX, texMinY);//d,d3
				tessellator.addVertexWithUV(minX, minY, maxZ, texMaxX, texMaxY);//d,d2
				tessellator.addVertexWithUV(maxX, minY, maxZ, texMinX, texMaxY);//d1,d2
				tessellator.addVertexWithUV(maxX, minY+0.5F, minZ, texMinX, texMinY);//d1,d3

				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getIcon(0, 4);
					l = block.colorMultiplier(blockAccess, i, j, k);
					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();

					//draw water plane
					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(minX, minY+0.6F, minZ, texMaxX, texMinY);//d,d3
					tessellator.addVertexWithUV(minX, minY, maxZ, texMaxX, texMaxY);//d,d2
					tessellator.addVertexWithUV(maxX, minY, maxZ, texMinX, texMaxY);//d1,d2
					tessellator.addVertexWithUV(maxX, minY+0.6F, minZ, texMinX, texMinY);//d1,d3
				}
			}
			if(i1 == 1)
			{
				//ribs
				renderblocks.setRenderBounds(0.9F, 0.0F, 0.0F, 0.95F, 0.6F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.6F, 0.0F, 0.0F, 0.65F, 0.45F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.3F, 0.0F, 0.0F, 0.35F, 0.3F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(minX, minY, maxZ, texMinX, texMaxY);
				tessellator.addVertexWithUV(maxX, minY+0.5F, maxZ, texMinX, texMinY);
				tessellator.addVertexWithUV(maxX, minY+0.5F, minZ, texMaxX, texMinY);
				tessellator.addVertexWithUV(minX, minY, minZ, texMaxX, texMaxY);

				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getIcon(0, 4);
					l = block.colorMultiplier(blockAccess, i, j, k);
					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();

					//draw water plane
					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(minX, minY, maxZ, texMinX, texMaxY);
					tessellator.addVertexWithUV(maxX, minY+0.6F, maxZ, texMinX, texMinY);
					tessellator.addVertexWithUV(maxX, minY+0.6F, minZ, texMaxX, texMinY);
					tessellator.addVertexWithUV(minX, minY, minZ, texMaxX, texMaxY);
				}
			}
			if(i1 == 2)
			{
				//ribs
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.3F, 1F, 0.3F, 0.35F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.6F, 1F, 0.45F, 0.65F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.0F, 0.0F, 0.9F, 1F, 0.6F, 0.95F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(minX, minY, minZ, texMinX, texMaxY);
				tessellator.addVertexWithUV(minX, minY+0.5f, maxZ, texMinX, texMinY);
				tessellator.addVertexWithUV(maxX, minY+0.5f, maxZ, texMaxX, texMinY);
				tessellator.addVertexWithUV(maxX, minY, minZ, texMaxX, texMaxY);

				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getBlockTextureFromSide(0);
					l = block.colorMultiplier(blockAccess, i, j, k);

					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();

					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(minX, minY, minZ, texMinX, texMaxY);
					tessellator.addVertexWithUV(minX, minY+0.6f, maxZ, texMinX, texMinY);
					tessellator.addVertexWithUV(maxX, minY+0.6f, maxZ, texMaxX, texMinY);
					tessellator.addVertexWithUV(maxX, minY, minZ, texMaxX, texMaxY);
				}
			}
			if(i1 == 3)
			{
				//ribs
				renderblocks.setRenderBounds(0.7F, 0.0F, 0.0F, 0.75F, 0.3F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.4F, 0.0F, 0.0F, 0.45F, 0.45F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);
				renderblocks.setRenderBounds(0.1F, 0.0F, 0.0F, 0.15F, 0.6F, 1.0F);
				renderblocks.renderStandardBlock(block, i, j, k);

				tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
				tessellator.addVertexWithUV(maxX, minY, minZ, texMinX, texMaxY);
				tessellator.addVertexWithUV(minX, minY+0.5f, minZ, texMinX, texMinY);
				tessellator.addVertexWithUV(minX, minY+0.5f, maxZ, texMaxX, texMinY);
				tessellator.addVertexWithUV(maxX, minY, maxZ, texMaxX, texMaxY);

				if(((BlockSluice)block).getIsRecievingWater(l))
				{
					//get water texture
					texture = TFCBlocks.SaltWater.getBlockTextureFromSide(0);
					l = block.colorMultiplier(blockAccess, i, j, k);
					//reassign the uv coords
					texMinX = texture.getMinU();
					texMaxX = texture.getMaxU();
					texMinY = texture.getMinV();
					texMaxY = texture.getMaxV();
					tessellator.setColorOpaque(waterR, waterG, waterB);
					tessellator.addVertexWithUV(maxX, minY, minZ, texMinX, texMaxY);
					tessellator.addVertexWithUV(minX, minY+0.6f, minZ, texMinX, texMinY);
					tessellator.addVertexWithUV(minX, minY+0.6f, maxZ, texMaxX, texMinY);
					tessellator.addVertexWithUV(maxX, minY, maxZ, texMaxX, texMaxY);
				}
			}
		}

		//set the block collision box
		renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		return true;
	}

	public static boolean RenderBlockWithCustomColorMultiplier(Block block, RenderBlocks renderBlocks, int xCoord, int yCoord, int zCoord, int colorMultiplier)
	{
		int l = colorMultiplier;
		float f = (l >> 16 & 255) / 255.0F;
		float f1 = (l >> 8 & 255) / 255.0F;
		float f2 = (l & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}

		return Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0 ? 
				(renderBlocks.partialRenderBounds ? 
						renderBlocks.renderStandardBlockWithAmbientOcclusion(block, xCoord, yCoord, zCoord, f, f1, f2) : 
							renderBlocks.renderStandardBlockWithAmbientOcclusion(block, xCoord, yCoord, zCoord, f, f1, f2)) : 
								renderBlocks.renderStandardBlockWithColorMultiplier(block, xCoord, yCoord, zCoord, f, f1, f2);
	}

	public static boolean RenderFruitLeaves(Block block, int xCoord, int yCoord, int zCoord, RenderBlocks renderblocks)
	{
		int meta = renderblocks.blockAccess.getBlockMetadata(xCoord, yCoord, zCoord);
		if(meta >= 8)
			meta-=8;

		FloraManager manager = FloraManager.getInstance();
		FloraIndex index = manager.findMatchingIndex(BlockFruitLeaves.getType(block, meta));

		renderblocks.renderStandardBlock(block, xCoord, yCoord, zCoord);
		if(index!= null && index.inBloom(TFC_Time.getSeasonAdjustedMonth(zCoord)) || index.inHarvest(TFC_Time.getSeasonAdjustedMonth(zCoord)))
		{
			renderblocks.overrideBlockTexture = getFruitTreeOverlay(renderblocks.blockAccess,xCoord,yCoord,zCoord);
			if(renderblocks.overrideBlockTexture != null)
				RenderBlockWithCustomColorMultiplier(block, renderblocks, xCoord, yCoord, zCoord, 16777215);
			renderblocks.clearOverrideBlockTexture();
		}
		return true;
	}

	public static boolean RenderSeaPlant(Block par1Block, int par2, int par3, int par4, RenderBlocks renderblocks)
	{
		boolean substrateRender = false;
		boolean plantRender = false;
		TileEntity te = renderblocks.blockAccess.getTileEntity(par2, par3, par4);
		if(te instanceof TEWaterPlant){
			TEWaterPlant wp = (TEWaterPlant) te;
			if(wp.getBlockFromType() != null){
				substrateRender = renderblocks.renderStandardBlockWithColorMultiplier(wp.getBlockFromType(), par2, par3, par4,1,1,1);
				plantRender = RenderFlora.render(par1Block, par2, par3, par4, renderblocks);
			}
		}
		return substrateRender && plantRender;
	}

	public static IIcon getFruitTreeOverlay(IBlockAccess world, int x, int y, int z)
	{
		IIcon out = null;
		int meta = world.getBlockMetadata(x, y, z);
		Block id = world.getBlock(x, y, z);
		int offset = id == TFCBlocks.fruitTreeLeaves ? 0 : 8;

		FloraManager manager = FloraManager.getInstance();
		FloraIndex index = manager.findMatchingIndex(BlockFruitLeaves.getType(id, meta & 7));
		if(index != null)
		{
			if(index.inBloom(TFC_Time.getSeasonAdjustedMonth(z)))//blooming
				out = BlockFruitLeaves.iconsFlowers[(meta & 7)+offset];
			else if(meta >= 8)//fruit
				out = BlockFruitLeaves.iconsFruit[(meta & 7)+offset];
		}
		return out;
	}

	private static void drawCrossedSquares(Block block, int x, int y, int z, RenderBlocks renderblocks)
	{
		Tessellator var9 = Tessellator.instance;

		var9.setColorOpaque_F(1.0f, 1.0f, 1.0f);
		GL11.glColor3f(1, 1, 1);

		IIcon index = block.getIcon(renderblocks.blockAccess, x, y, z, 0);

		double minX = index.getMinU();
		double maxX = index.getMaxU();
		double minY = index.getMinV();
		double maxY = index.getMaxV();

		double xMin = x + 0.5D - 0.45D;
		double xMax = x + 0.5D + 0.45D;
		double zMin = z + 0.5D - 0.45D;
		double zMax = z + 0.5D + 0.45D;

		var9.addVertexWithUV(xMin, y + 0, zMin, minX, minY);
		var9.addVertexWithUV(xMin, y + 0.0D, zMin, minX, maxY);
		var9.addVertexWithUV(xMax, y + 0.0D, zMax, maxX, maxY);
		var9.addVertexWithUV(xMax, y + 0, zMax, maxX, minY);

		var9.addVertexWithUV(xMax, y + 0, zMax, minX, minY);
		var9.addVertexWithUV(xMax, y + 0.0D, zMax, minX, maxY);
		var9.addVertexWithUV(xMin, y + 0.0D, zMin, maxX, maxY);
		var9.addVertexWithUV(xMin, y + 0, zMin, maxX, minY);

		var9.addVertexWithUV(xMin, y + 0, zMax, minX, minY);
		var9.addVertexWithUV(xMin, y + 0.0D, zMax, minX, maxY);
		var9.addVertexWithUV(xMax, y + 0.0D, zMin, maxX, maxY);
		var9.addVertexWithUV(xMax, y + 0, zMin, maxX, minY);

		var9.addVertexWithUV(xMax, y + 0, zMin, minX, minY);
		var9.addVertexWithUV(xMax, y + 0.0D, zMin, minX, maxY);
		var9.addVertexWithUV(xMin, y + 0.0D, zMax, maxX, maxY);
		var9.addVertexWithUV(xMin, y + 0, zMax, maxX, minY);
	}
}
