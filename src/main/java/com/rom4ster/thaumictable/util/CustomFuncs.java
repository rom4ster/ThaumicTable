package com.rom4ster.thaumictable.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class CustomFuncs {
	

	//private static HashMap<ItemStack, List<IArcaneRecipe>> newHash;
	
	/*public static void doThaumMap() {
		HashMap<ResourceLocation, IThaumcraftRecipe> thaumHash = ThaumcraftApi.getCraftingRecipes();
		thaumHash.forEach( (a,b) -> {
			if (b instanceof ShapedArcaneRecipe ) {
				ShapedArcaneRecipe c = (ShapedArcaneRecipe) b;
				ItemStack out = c.getRecipeOutput();
				if (!newHash.containsKey(out)) {
					newHash.put(out, new ArrayList<IArcaneRecipe>());
				} 
				newHash.get(out).add(c);
				
			}
			else if (b instanceof ShapelessArcaneRecipe) {
				ShapelessArcaneRecipe c = (ShapelessArcaneRecipe) b;
				ItemStack out = c.getRecipeOutput();
				if (!newHash.containsKey(out)) {
					newHash.put(out, new ArrayList<IArcaneRecipe>());
				} 
				newHash.get(out).add(c);
			}
		});
		
		
	} */
	
	public static List<IArcaneRecipe> tRecList(ItemStack item) {
		
		
		
		List<IArcaneRecipe> rec = new ArrayList<IArcaneRecipe>();
		if (item.isEmpty()) return rec;
		//HashMap<ResourceLocation, IThaumcraftRecipe> 
		
		ForgeRegistries.RECIPES.forEach((b) -> {
			System.out.println("TYPE: "+ b.getClass().getName() );
			if (b instanceof ShapedArcaneRecipe ) {
				
				ShapedArcaneRecipe c = (ShapedArcaneRecipe) b;
				System.out.print("###########################################"+c.getRecipeOutput().getItem().getRegistryName());
				if (item.getItem().getUnlocalizedName().equals(c.getRecipeOutput().getItem().getUnlocalizedName())) {
					if (item.getItemDamage() == c.getRecipeOutput().getItemDamage()) rec.add(c);
				}
			}
			else if (b instanceof ShapelessArcaneRecipe) {
				ShapelessArcaneRecipe c = (ShapelessArcaneRecipe) b;
				if (item.getItem().getUnlocalizedName().equals(c.getRecipeOutput().getItem().getUnlocalizedName())) {
					if (item.getItemDamage() == c.getRecipeOutput().getItemDamage()) rec.add(c);
				}
			} 
		});
		return rec;
		
	}
	
}
	