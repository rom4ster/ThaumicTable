package com.rom4ster.thaumictable.init;

import java.util.ArrayList;
import java.util.List;

import com.rom4ster.thaumictable.blocks.BlockBase;
import com.rom4ster.thaumictable.blocks.TableBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks {
	
	public static final List<Block> BLOCKS= new ArrayList<Block>();
	
	public static final Block fail_block = new BlockBase("fail_block",Material.IRON);
	public static final Block table = new TableBlock("table",Material.WOOD);

}
