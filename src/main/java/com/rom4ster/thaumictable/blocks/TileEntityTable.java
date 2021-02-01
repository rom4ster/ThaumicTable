package com.rom4ster.thaumictable.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.rom4ster.thaumictable.Main;
import com.rom4ster.thaumictable.util.CustomFuncs;
import com.rom4ster.thaumictable.util.Reference;
import com.rom4ster.thaumictable.util.handlers.GhostHandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import scala.actors.threadpool.Arrays;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IArcaneWorkbench;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.internal.IInternalMethodHandler;

public class TileEntityTable extends TileEntity implements IArcaneWorkbench, ITickable{
	
	
	private static final int TABLE_SIZE = 15;
	private static final int STACK_LIMIT = 64;
	private TileEntityLockableLoot unused;
	private ItemStackHandler standard;
	private ItemStackHandler dummy;
	private ItemStack output = ItemStack.EMPTY; 
	private UUID player;
	private List<IArcaneRecipe> recipeList;
	private int counter=0;
	private static final int TICK_FREQ = 20;
	private boolean checkServ = false;
	private boolean checkClient = false;
	
	private ItemStackHandler itemStackHandler = new ItemStackHandler(TABLE_SIZE+2) {
		
	    
		
	      @Override
	        protected void onContentsChanged(int slot) {
	            // We need to tell the tile entity that something has changed so
	            // that the chest contents is persisted
	            TileEntityTable.this.markDirty();
	               
	         
	        }
	      
	      
	      @Override
	      @Nonnull
	      public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	      {
	          
	    	  if (slot >= TABLE_SIZE) return ItemStack.EMPTY;
	    	  if (stack.isEmpty())
	              return ItemStack.EMPTY;

	          validateSlotIndex(slot);

	          ItemStack existing = this.stacks.get(slot);

	          int limit = getStackLimit(slot, stack);

	          if (!existing.isEmpty())
	          {
	              if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
	                  return stack;

	              limit -= existing.getCount();
	          }

	          if (limit <= 0)
	              return stack;

	          boolean reachedLimit = stack.getCount() > limit;

	          if (!simulate)
	          {
	              if (existing.isEmpty())
	              {
	                  this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
	              }
	              else
	              {
	                  existing.grow(reachedLimit ? limit : stack.getCount());
	              }
	              onContentsChanged(slot);
	          }

	          return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
	      }

	      @Override 
	      @Nonnull
	      public ItemStack extractItem(int slot, int amount, boolean simulate)
	      {
	    	  
	    	  
	    	  
	          
	    	  if (slot < TABLE_SIZE+1) return ItemStack.EMPTY;
	    	  if (amount == 0)
	              return ItemStack.EMPTY;

	          validateSlotIndex(slot);

	          ItemStack existing = this.stacks.get(slot);

	          if (existing.isEmpty())
	              return ItemStack.EMPTY;

	          int toExtract = Math.min(amount, existing.getMaxStackSize());

	          if (existing.getCount() <= toExtract)
	          {
	              if (!simulate)
	              {
	                  this.stacks.set(slot, ItemStack.EMPTY);
	                  onContentsChanged(slot);
	              }
	              return existing;
	          }
	          else
	          {
	              if (!simulate)
	              {
	                  this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
	                  onContentsChanged(slot);
	              }

	              return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
	          }
	      }

	      @Override
	      public int getSlotLimit(int slot)
	      {
	          return 1;
	      }

	      
	      

	};


	//private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(72,ItemStack.EMPTY);
	//In case its hard to understand, do not use this method.
	@Deprecated
	private static Object doNotUse() {
		
		Object x = ThaumcraftApi.getCraftingRecipes();
		Object y = new Object();
		Object z = ((IThaumcraftRecipe) y).getResearch();
		
		if (y instanceof String ) return z;
		return x;
		
	}
	
	

	
	public void setOutputInEntity(ItemStack stack) {
		this.output = stack.copy();
		
		
	}
	
	public void setList(ItemStack stack) {
		this.recipeList = CustomFuncs.tRecList(stack);
	}
	
	public void setPlayer(UUID player) {
		this.player = player;
	}
	
	public void fillState(Container c) {
		
		
		/*for (int i =0; i < c.inventorySlots.size(); i++) {
			Slot s = c.getSlot(i);
			if (i < TABLE_SIZE && s instanceof SlotItemHandler) {
				s.putStack(itemStackHandler.getStackInSlot(i));
				
				
				
			}
		}*/
		
	}
	
	private List<Item> getSlotItems() {
		List<Item> it = new ArrayList<Item>();
		for (int i =0; i < itemStackHandler.getSlots()-2;i++) {
			if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
				it.add(itemStackHandler.getStackInSlot(i).getItem());
			}
		}
		return it;
	}
	
	
	private List<Item> checkItems(IArcaneRecipe rec) {
		NonNullList<Ingredient> ingList =rec.getIngredients();
		List<Item> actList = getSlotItems();
		if (actList.isEmpty()) return null;
		List<Item> retList = new ArrayList<Item>();
		
		for (Ingredient ing : ingList) {
			ItemStack[] ingArr = ing.getMatchingStacks();
			if (ingArr.length == 0) continue;
			for (ItemStack item : ingArr) {
				Item aItem = item.getItem();
				if (actList.contains(aItem)) {
					retList.add(aItem);
					break;
				}
				
				return null;
				
			}
	
			
			
			
			
		}

	
		return retList;
	}
	
	private List<ItemStack> checkCrystals(IArcaneRecipe rec) {
		AspectList asplist = rec.getCrystals();
		Aspect[] asparr = asplist.getAspects();
		ItemStack[] aspitem = new ItemStack[asparr.length];
		List<ItemStack> retList = new ArrayList<ItemStack>();
		
		for (int i = 0; i < asparr.length; i++) aspitem[i] = ThaumcraftApiHelper.makeCrystal(asparr[i]);
		for (ItemStack item : aspitem) {
			ItemStack match = null;
			for (int i =0; i < itemStackHandler.getSlots()-2;i++) {
				ItemStack is = itemStackHandler.getStackInSlot(i).copy();
				if (is.isEmpty()) continue;
				is.setCount(1);
				if (ItemStack.areItemsEqual(is, item) && ItemStack.areItemStackTagsEqual(is, item)) {
					match = is;
					break;
				}
				
			}
			if (match==null) return null;
			retList.add(match);
		}
		return retList;
	}
	
	private List<Integer> itemSlots(List<Item> itemList) {
		return itemSlots(itemList, false);
	}
	
	private List<Integer> itemSlots(List<Item> itemList, boolean nbtcheck) {
		List<Integer> intList = new ArrayList<Integer>();
		List<Integer> calc = new ArrayList<Integer>();
		for (int i =0; i < itemStackHandler.getSlots()-2;i++) {
			ItemStack istack = itemStackHandler.getStackInSlot(i);
			if (!istack.isEmpty() && itemList.contains(istack.getItem())) {
				intList.add(i);
				if (intList.contains(i) && !calc.contains(i)) {
					int count = 0;
					for (Integer cme : intList) if (cme == i) count++;
					if (itemStackHandler.getStackInSlot(i).getCount() < count) return null;
					calc.add(i);
				}
			}
		}
		return intList;
	}
	
	private boolean containsSpecial(ItemStack stack, List<ItemStack> list) {
		for(ItemStack l : list){
			if (ItemStack.areItemsEqual(stack, l) && ItemStack.areItemStackTagsEqual(stack, l)){
			return true;
			}
		}
		return false;
	}
	private boolean containsSpecial(ItemStack stack, ItemStack[] list) {
		List<ItemStack> nlist = new ArrayList<ItemStack>();
		for (int i =0; i < list.length; i++) nlist.add(list[i]);
		return containsSpecial(stack,nlist);
	}
	
	private List<Item> onlyItem(List<ItemStack> list) {
		List<Item> ret = new ArrayList<Item>(); 
		list.forEach(l -> ret.add(l.getItem()));
		return ret;
	}
	
   private void craft(List<Item> itemlist, List<ItemStack> aspectList, List<Integer> itemSlot,List<Integer> aspectSlot) {

	   for (Integer num : itemSlot) {
		   ItemStack curr = itemStackHandler.getStackInSlot(num);
		   if (curr == null || curr.isEmpty()) return;
		   if (itemlist.contains(curr.getItem())) {
	
			   if (curr.getCount() <= 1) {
				   //curr = ItemStack.EMPTY;
			   }
			   
				   curr.setCount(curr.getCount()-1);
	
			  
			 
			   itemlist.remove(curr.getItem());
			   
			   
		   }
		   
	   }
	   
   }
	
	
	
	private void doDirty() {
		TileEntityTable.this.markDirty();
	}
	
	  public void dummySoiler(ItemStackHandler dummy) {
		  this.dummy=dummy;
		  doDirty();
	  }
	  
	  public void standardSoiler(ItemStackHandler standard) {
		  this.standard = standard;
		  doDirty();
	  }
	  
	  public ItemStackHandler getDummy() {
		  return dummy;
	  }
	  public ItemStackHandler getStandard() {
		  return standard;
	  }

	  @Override
	    public void readFromNBT(NBTTagCompound compound) {
	        super.readFromNBT(compound);
	        //String pname;
	        if (compound.hasKey("items")) {
	            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
	        }
	        if (compound.hasKey("output")) {
	            output.deserializeNBT((NBTTagCompound) compound.getTag("output"));
	            output = itemStackHandler.getStackInSlot(TABLE_SIZE);
	        }
	        
	        if (compound.hasKey("player")) {
	        	player = UUID.fromString(compound.getTag("player").toString().replaceAll("\"", ""));
	        	
	        }
	        setList(output);
	     /*   if (compound.hasKey("standard")) {
	            standard.deserializeNBT((NBTTagCompound) compound.getTag("standard"));
	        }
	        if (compound.hasKey("dummy")) {
	            dummy.deserializeNBT((NBTTagCompound) compound.getTag("dummy"));
	        }*/

	    }
	    @Override
	    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	        
	        compound.setTag("items", itemStackHandler.serializeNBT());
	         compound.setTag("output", output.serializeNBT());
	         
	        if (player !=null) compound.setTag("player", new NBTTagString(player.toString().replaceAll("\"", "")));
	        //if (standard != null) compound.setTag("standard", standard.serializeNBT());
	        //if (dummy !=null) compound.setTag("dummy", dummy.serializeNBT());
	        super.writeToNBT(compound);
	        return compound;
	    }
	    
	    public boolean canInteractWith(EntityPlayer playerIn) {
	        // If we are too far away from this tile entity you cannot use it
	    	
	        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	    }

	    @Override
	    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
	        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
	            return true;
	        }
	        return super.hasCapability(capability, facing);
	    }

	    @Override
	    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
	        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
	            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
	        }
	        return super.getCapability(capability, facing);
	    }
	
	    
	    
	public boolean checkInProgress( Boolean set) {
		
		if (set == null) return this.world.isRemote ? this.checkClient : this.checkServ;
		return this.world.isRemote ? (this.checkClient = set.booleanValue()) : (this.checkServ = set.booleanValue());
		
	}
	
	public void checkSync(boolean val) {
		this.checkClient = val;
		this.checkServ = val;
	}
	    
	
	public void update() {
		
		if (counter++ > TICK_FREQ) {
			counter=0;
			//System.out.println("CHECK");
			
		} else return;
		if (this.recipeList== null || this.recipeList.isEmpty()) {
			if (this.output == null) return;
			this.setList(output);
		}  
		if (this.checkInProgress(null)) return;
		checkInProgress(true);
		for (IArcaneRecipe rec : this.recipeList) {
			
			if (check(rec) && itemStackHandler.getStackInSlot(TABLE_SIZE+1).isEmpty()) {
				// check chunk vis 
				float vis = -1;
				//if (!world.isRemote) {
					 vis = ThaumcraftApi.internalMethods.getVis(this.world, this.pos);
					if (vis <= rec.getVis()) continue;
				//}
				
				if (player == null) {
					this.checkInProgress(false);
					return;}
				EntityPlayer placer = this.world.getPlayerEntityByUUID(player);
				// check research
				if (!thaumcraft.api.capabilities.ThaumcraftCapabilities.knowsResearch(placer, rec.getResearch())) {
					continue;
				}
				
				//clear items
				for (int i = 0; i < TABLE_SIZE; i++) itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
				
				this.markDirty();
				
				//remove vis
				
				if (!world.isRemote) {
					if (vis <= rec.getVis()) continue;
					ThaumcraftApi.internalMethods.drainVis(this.world, this.pos, rec.getVis(), false);
				}
				
				//set output
				itemStackHandler.setStackInSlot(TABLE_SIZE+1, rec.getRecipeOutput().copy());
				checkSync(false);
				
				this.markDirty();

				return;
			}

		} 
		checkSync(false);
		
		
	}




	private boolean check(IArcaneRecipe rec) {
		
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (int i =0; i< TABLE_SIZE; i++) {
			ItemStack is =itemStackHandler.getStackInSlot(i);
			if (!is.isEmpty() && is != null) items.add(is);
		}
		List<Ingredient> ilist = new ArrayList<Ingredient>();
		for(Ingredient mying : rec.getIngredients()) if (mying.getMatchingStacks().length != 0) ilist.add(mying);
		for (Ingredient ing : ilist) {
			boolean match = false;
			for (ItemStack itm : items) {
				if (ingMatch(ing,itm) || itm.getUnlocalizedName().contains("crystal")) {
					match = true;
					ingRemove(ing,items);
					break;
				}
			}
			if (!match) return false;
		}

		Aspect[] a =rec.getCrystals().getAspects();
		ItemStack[] aarr = new ItemStack[a.length];
		
		for (int i = 0; i < a.length; i++) aarr[i] = ThaumcraftApiHelper.makeCrystal(a[i]);
		
		for (ItemStack ais : aarr) {
			
			boolean match = false;
			for (int i =0; i<items.size(); i++) {
				if ( ItemStack.areItemsEqual(items.get(i), ais) && ItemStack.areItemStackTagsEqual(items.get(i), ais) ) {
					items.remove(i);
					match = true;
					break;
				}
				
			}
			if (!match) return false;
			
			
		}
		
		return true;
		
		
			
		
		
		
	}
	
	private boolean ingMatch(Ingredient ing, ItemStack is) {
		
		for (ItemStack i : ing.getMatchingStacks()) if (ItemStack.areItemsEqual(is, i)) return true;
		return false;
	}
	
	private void ingRemove(Ingredient ing, List<ItemStack> is) {
		for (int i =0; i< is.size(); i++) {
			if (ingMatch(ing,is.get(i))) is.remove(i);
		}
	}
	
	
/*	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) 
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		else return false;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) 
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) this.handler;
		return super.getCapability(capability, facing);
	}
	
	
*/

/*	

	public boolean hasCustomName() {
		return false;
	}


	public void setCustomName(String customName) 
	{
		return;
	}








	// Bunch of crap going on here, i just copy pasted this. 
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != 
				this ? false : 
					player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}








	public int getField(int id) {
		return 0;
	}


	public void setField(int id, int value) {
		
	}

*/



	/*@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.contents=NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if(!this.checkLootAndRead(compound)) ItemStackHelper.loadAllItems(compound, contents);
		
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		if(!this.checkLootAndWrite(compound)) ItemStackHelper.saveAllItems(compound, contents);
		
		return compound;
	}
*/
	/*

	@Override
	public int getSizeInventory() {
		
		return TABLE_SIZE;
	}


	@Override
	public boolean isEmpty() {
		for (ItemStack stack : this.contents) {
			
			if (!stack.isEmpty()) return false;
			
		}
		return true;
	}


	@Override
	public int getInventoryStackLimit() {
		
		return STACK_LIMIT;
	}


	@Override
	public String getName() {
		return "container.table";
	}


	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerTable(playerInventory,this,playerIn);
	}


	@Override
	public String getGuiID() {
		return Reference.MOD_ID+":table";
	}


	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.contents;
	}
	
	
	*/
	
	

}
