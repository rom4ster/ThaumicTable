package com.rom4ster.thaumictable.items;

import com.rom4ster.thaumictable.Main;
import com.rom4ster.thaumictable.init.ModItems;
import com.rom4ster.thaumictable.util.IHasModel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel {

	
	public ItemBase(String name) {
		
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MISC);
		
		ModItems.ITEMS.add(this);
	}
	
	
	@Override
	public void registerModels() {
		
		Main.proxy.registerItemRenderer(this,0, "inventory");
		
		
	}
	
	

}
