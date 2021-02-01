package com.rom4ster.thaumictable.util.handlers;

import com.rom4ster.thaumictable.blocks.ContainerTable;
import com.rom4ster.thaumictable.blocks.GUITable;
import com.rom4ster.thaumictable.blocks.TileEntityTable;
import com.rom4ster.thaumictable.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == Reference.GUI_TABLE) return new ContainerTable(player.inventory, (TileEntityTable)world.getTileEntity(new BlockPos(x,y,z)), player);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == Reference.GUI_TABLE) return new GUITable(player.inventory, (TileEntityTable)world.getTileEntity(new BlockPos(x,y,z)), player);
		return null;
	}

}
