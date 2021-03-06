package com.bioxx.tfc.Blocks.Vanilla;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.IShearable;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.bioxx.tfc.Reference;
import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.TFCOptions;
import com.bioxx.tfc.api.Constant.Global;
import com.bioxx.tfc.Items.Tools.ItemCustomScythe;
import com.bioxx.tfc.Items.Tools.ItemCustomAxe;
import com.bioxx.tfc.Items.Tools.ItemShears;
import com.bioxx.tfc.Items.Tools.ItemKnife;

public class BlockCustomLeaves extends BlockLeaves implements IShearable
{
	protected int adjacentTreeBlocks[][][];
	protected String[] woodNames;
	protected IIcon[] icons;
	protected IIcon[] iconsOpaque;

	public BlockCustomLeaves()
	{
		super();
		this.setTickRandomly(false);
		this.woodNames = new String[16];
		System.arraycopy(Global.WOOD_ALL, 0, this.woodNames, 0, 16);
		this.icons = new IIcon[16];
		this.iconsOpaque = new IIcon[16];
		this.setTickRandomly(false);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tabs, List list)
	{
		// Leaves are not added to the creative tab
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess bAccess, int x, int y, int z)
	{
		return true;
	}

	@Override
	public int colorMultiplier(IBlockAccess bAccess, int x, int y, int z)
	{
		return TerraFirmaCraft.proxy.foliageColorMultiplier(bAccess, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		entity.motionX *= 0.1D;
		entity.motionZ *= 0.1D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		Block block = world.getBlock(x, y, z);
		/*if(!Minecraft.isFancyGraphicsEnabled() && block == this) 
			return false;*/
		if (side == 0 && this.minY > 0.0D)
			return true;
		else if (side == 1 && this.maxY < 1.0D)
			return true;
		else if (side == 2 && this.minZ > 0.0D)
			return true;
		else if (side == 3 && this.maxZ < 1.0D)
			return true;
		else if (side == 4 && this.minX > 0.0D)
			return true;
		else if (side == 5 && this.maxX < 1.0D)
			return true;
		else
			return !block.isOpaqueCube();
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		onNeighborBlockChange(world, x, y, z, null);
	}

	@Override
	public void beginLeavesDecay(World world, int x, int y, int z)
	{
		//We don't do vanilla leaves decay
	}

	@Override
	public void onNeighborBlockChange(World world, int xOrig, int yOrig, int zOrig, Block b)
	{
		if (!world.isRemote)
		{
			int var6 = world.getBlockMetadata(xOrig, yOrig, zOrig);

			byte searchRadius = 4;
			int maxDist = searchRadius + 1;
			byte searchDistance = 11;
			int center = searchDistance / 2;
			adjacentTreeBlocks = null;
			if (this.adjacentTreeBlocks == null)
				this.adjacentTreeBlocks = new int[searchDistance][searchDistance][searchDistance];

			if (world.checkChunksExist(xOrig - maxDist, yOrig - maxDist, zOrig - maxDist, xOrig + maxDist, yOrig + maxDist, zOrig + maxDist))
			{
				for (int xd = -searchRadius; xd <= searchRadius; ++xd)
				{
					int searchY = searchRadius - Math.abs(xd);
					for (int yd = -searchY; yd <= searchY; ++yd)
					{
						int searchZ = searchY - Math.abs(yd);
						for (int zd = -searchZ; zd <= searchZ; ++zd)
						{
							Block block = world.getBlock(xOrig + xd, yOrig + yd, zOrig + zd);

							if (block == TFCBlocks.logNatural || block == TFCBlocks.logNatural2 || block == TFCBlocks.woodVert || block == TFCBlocks.woodVert2)
								this.adjacentTreeBlocks[xd + center][yd + center][zd + center] = 0;
							else if (block == this && var6 == world.getBlockMetadata(xOrig + xd, yOrig + yd, zOrig + zd))
								this.adjacentTreeBlocks[xd + center][yd + center][zd + center] = -2;
							else
								this.adjacentTreeBlocks[xd + center][yd + center][zd + center] = -1;
						}
					}
				}

				for (int pass = 1; pass <= 4; ++pass)
				{
					for (int xd = -searchRadius; xd <= searchRadius; ++xd)
					{
						int searchY = searchRadius - Math.abs(xd);
						for (int yd = -searchY; yd <= searchY; ++yd)
						{
							int searchZ = searchY - Math.abs(yd);
							for (int zd = -searchZ; zd <= searchZ; ++zd)
							{
								if (this.adjacentTreeBlocks[xd + center][yd + center][zd + center] == pass - 1)
								{
									if (this.adjacentTreeBlocks[xd + center - 1][yd + center][zd + center] == -2)
										this.adjacentTreeBlocks[xd + center - 1][yd + center][zd + center] = pass;

									if (this.adjacentTreeBlocks[xd + center + 1][yd + center][zd + center] == -2)
										this.adjacentTreeBlocks[xd + center + 1][yd + center][zd + center] = pass;

									if (this.adjacentTreeBlocks[xd + center][yd + center - 1][zd + center] == -2)
										this.adjacentTreeBlocks[xd + center][yd + center - 1][zd + center] = pass;

									if (this.adjacentTreeBlocks[xd + center][yd + center + 1][zd + center] == -2)
										this.adjacentTreeBlocks[xd + center][yd + center + 1][zd + center] = pass;

									if (this.adjacentTreeBlocks[xd + center][yd + center][zd + center - 1] == -2)
										this.adjacentTreeBlocks[xd + center][yd + center][zd + center - 1] = pass;

									if (this.adjacentTreeBlocks[xd + center][yd + center][zd + center + 1] == -2)
										this.adjacentTreeBlocks[xd + center][yd + center][zd + center + 1] = pass;
								}
							}
						}
					}
				}
			}

			int res = this.adjacentTreeBlocks[center][center][center];

			if (res < 0)
			{
				if(world.getChunkFromBlockCoords(xOrig, zOrig) != null)
					this.destroyLeaves(world, xOrig, yOrig, zOrig);
			}
		}
	}

	private void destroyLeaves(World world, int x, int y, int z)
	{
		world.setBlockToAir(x, y, z);
	}
        /**
         * not use not. Previously used in AOE harvest by Scythe
         */
	private void removeLeaves(World world, int x, int y, int z)
	{
		dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);//dropBlockAsItemWithChance - empty
		if(world.rand.nextInt(100) < 30)
			dropBlockAsItem(world, x, y, z, new ItemStack(TFCItems.stick, 1));
		world.setBlockToAir(x, y, z);
	}

	@Override
	public int quantityDropped(Random rand)
	{
		return rand.nextInt(20) != 0 ? 0 : 1;
	}

	@Override
	public Item getItemDropped(int i, Random rand, int j)
	{
		return Item.getItemFromBlock(TFCBlocks.sapling);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i1)
	{
		// Do Nothing
	}

	@Override
	public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int meta)
	{
		if (world.isRemote) return;//ServerSide only
                
                ItemStack itemstack = entityplayer.inventory.getCurrentItem();
                //freehands high chance to drop stick 
                if (itemstack == null) {                            
                    harvest(world, entityplayer, i, j, k, meta, 0.125f, 30, 8, 0);
                }
                //scythe
                else if (itemstack.getItem() instanceof ItemCustomScythe) {
                    //harvest AOE see like com\bioxx\tfc\Blocks\Vanilla\BlockCustomTallGrass.java
                    for (int x = -1; x < 2; x++)
                        for (int z = -1; z < 2; z++)
                            for (int y = -1; y < 2; y++) {
                                int xx = i+x, yy = j+y, zz= k+z;
                                if (world.getBlock(xx, yy, zz).getMaterial() == Material.leaves) 
                                // && entityplayer.inventory.getStackInSlot(entityplayer.inventory.currentItem) != null 
                                {
                                    harvest(world, entityplayer, xx, yy, zz, meta, 0.045f, 
                                            35, // stickChance //old: chance 11 + 30 in removeLeaves() 
                                            4,  // saplingChance
                                            0); // leavesChance
                                    destroyLeaves(world, xx, yy, zz);//removeLeaves(world, xx, yy, zz);
                                    if (!canHarvestAfterDamageItem(entityplayer,itemstack))
                                        break;//dont work without scythe
                                }
                            }
                }
                //knife
                else if (itemstack.getItem() instanceof ItemKnife) {
                    harvest(world, entityplayer, i, j, k, meta, 0.025f, 45, 12, 0);
                    canHarvestAfterDamageItem(entityplayer, itemstack);
                }
                //axe
                else if (itemstack.getItem() instanceof ItemCustomAxe) {
                    harvest(world, entityplayer, i, j, k, meta, 0.025f, 30, 19, 0);
                    canHarvestAfterDamageItem(entityplayer, itemstack);
                }
                //shears chance for drop leaves block
                else if (itemstack.getItem() instanceof ItemShears) {
                    harvest(world, entityplayer, i, j, k, meta, 0.025f, 20, 3, 40/*leavesChance*/);
                    canHarvestAfterDamageItem(entityplayer, itemstack);
                }
                // other cases not free Hands //Only executes if scythe,shears,knife wasn't found
                else {
                    harvest(world, entityplayer, i, j, k, meta, 0.125f, 20, 5, 0);
                }
                //old way to check to has Scythe
                //int[] equipIDs = OreDictionary.getOreIDs(itemstack);
                //for (int id : equipIDs) {
                //        String name = OreDictionary.getOreName(id);
                //        if (name.startsWith("itemScythe")) {
                //            //action
                //            return;
                //        }
                //}
	}
        
        protected void harvest(World world, EntityPlayer entityplayer, int x, int y, int z, int meta, float exhaustion, int stickChanсe, int saplingChance, int leavesChance) 
        {
                //StatBase sb = StatList.mineBlockStatArray[blockId];
                entityplayer.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);//no-op mineBlockStatArray[id] return null 
                entityplayer.addExhaustion(exhaustion);//0.025F
                if (world.rand.nextInt(100) < stickChanсe)// 28
                        dropBlockAsItem(world, x, y, z, new ItemStack(TFCItems.stick, 1));
                else if (world.rand.nextInt(100) < saplingChance/*6*/ && TFCOptions.enableSaplingDrops)
                        dropSapling(world, x, y, z, meta);
                else if (leavesChance > 0 && world.rand.nextInt(100) < leavesChance) {
                        dropLeaves(world, x, y, z, meta);
                }
                super.harvestBlock(world, entityplayer, x, y, z, meta);// no-op call method sequence:
                //net.minecraft.block.BlockLeaves:harvestBlock() -> net.minecraft.block.Block:harvestBlock() -> dropBlockAsItem() -> dropBlockAsItemWithChance() -> no-op
        }
        protected boolean canHarvestAfterDamageItem(EntityPlayer entityplayer, ItemStack itemstack) {
                itemstack.damageItem(1, entityplayer);
                if (itemstack.stackSize == 0) {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                        return false;
                }
                return true;
        }
                
	protected void dropLeaves(World world, int x, int y, int z, int meta)
	{
		dropBlockAsItem(world, x, y, z, new ItemStack(this, 1, meta));//world.getBlockMetadata(x, y, z)
	}
        
	protected void dropSapling(World world, int x, int y, int z, int meta)
	{
		if (meta != 9 && meta != 15)
			dropBlockAsItem(world, x, y, z, new ItemStack(this.getItemDropped(0, null, 0), 1, meta));
	}

	@Override
	public int damageDropped(int dmg)
	{
		return dmg;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if(meta > woodNames.length - 1)
			meta = 0;
		if (TerraFirmaCraft.proxy.getGraphicsLevel())
			return this.icons[meta];
		else
			return this.iconsOpaque[meta];
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegisterer)
	{
		for(int i = 0; i < this.woodNames.length; i++)
		{
			this.icons[i] = iconRegisterer.registerIcon(Reference.MOD_ID + ":" + "wood/trees/" + this.woodNames[i] + " Leaves Fancy");
			this.iconsOpaque[i] = iconRegisterer.registerIcon(Reference.MOD_ID + ":" + "wood/trees/" + this.woodNames[i] + " Leaves");
		}
	}

	@Override
	public String[] func_150125_e()
	{
		return this.woodNames.clone();
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z)
	{
		return false;
	}
}
