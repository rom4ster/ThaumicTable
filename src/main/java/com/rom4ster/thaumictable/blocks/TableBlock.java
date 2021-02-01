package com.rom4ster.thaumictable.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.rom4ster.thaumictable.Main;
import com.rom4ster.thaumictable.init.ModBlocks;
import com.rom4ster.thaumictable.init.ModItems;
import com.rom4ster.thaumictable.util.Reference;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TableBlock extends BlockBase implements ITileEntityProvider {
	
	
	
	public TableBlock(String name, Material material) {
		super( name,material);
		


	}
	
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) 
	{
		TileEntityTable tileentity = (TileEntityTable)worldIn.getTileEntity(pos);
		//InventoryHelper.dropInventoryItems(worldIn, pos, tileentity.);
		IItemHandler items = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null);
		//NonNullList<ItemStack> drops = NonNullList.<ItemStack>withSize(items.getSlots()+1,ItemStack.EMPTY);
		//List<ItemStack> drops2 = new ArrayList<ItemStack>(); 
		
		for (int i =0; i < items.getSlots(); i++) {
			if (i == items.getSlots()-2) continue;
			ItemStack is = items.getStackInSlot(i);
			if (is != null && !is.isEmpty()) {
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), is);
			}
		}
		super.breakBlock(worldIn, pos, state);
	}
	
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(stack.hasDisplayName() || true)
		{
			TileEntity tileentity = worldIn.getTileEntity(pos);
			
			if(tileentity instanceof TileEntityTable)
			{
				//((TileEntityTable)tileentity).setCustomName(stack.getDisplayName());
				UUID uuid = null;
				if (placer instanceof EntityPlayer) {
					uuid = ((EntityPlayer) placer).getUniqueID();
				}
                 ((TileEntityTable) tileentity).setPlayer(uuid);
                 tileentity.markDirty();

			}	
		}
	}
	
	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state) {
		return new TileEntityTable();
		
	}
	
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) 
	{
		if(!worldIn.isRemote)
		{
			TileEntity te = worldIn.getTileEntity(pos);
	        if (!(te instanceof TileEntityTable)) {
	            return false;
	        }
			playerIn.openGui(Main.instance, Reference.GUI_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		
		return true;
	}

	

	
	/*@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		
		return Item.getItemFromBlock(ModBlocks.table);
		
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) 
	{
		return true;
	}
	
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		
		return new ItemStack(ModBlocks.table);
	}

	
	public static void setState(boolean active, World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		
		if (tileentity !=null) {
			
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
			
		}
	}*/


	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityTable();
	}
	

	@Override
	public boolean isFullBlock(IBlockState state) 
	{
		return true;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return true;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

}
