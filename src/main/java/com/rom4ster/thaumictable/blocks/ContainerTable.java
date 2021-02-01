package com.rom4ster.thaumictable.blocks;

import javax.annotation.Nonnull;

import com.rom4ster.thaumictable.util.CustomFuncs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerTable extends Container {
	
	private final int ROW_COUNT;
	private final int ROW_SIZE=3;
	private final int CRYSTAL_COUNT=6;
	private final int TABLE_SIZE = 1;
	private final TileEntityTable inventory;
	private ItemStackHandler standard;
	private ItemStackHandler dummy;
	private int COUNT_TICK = 5;
	private int TICK_CURR =0;
	private boolean CONSTRUCTING = true;
	public InventoryCrafting craftMatrix;

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.inventory.canInteractWith(playerIn);
	}

	
	public ContainerTable(InventoryPlayer playerInv, TileEntityTable inventory,EntityPlayer player) {
		this.inventory = inventory;
		IItemHandler itemHandler = this.inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		
		ItemStackHandler dummyT = inventory.getDummy();
		ItemStackHandler standardT = inventory.getStandard();
		
		
	
		
		ROW_COUNT = (( itemHandler.getSlots() - CRYSTAL_COUNT-2 )/ROW_SIZE) + 1;
		craftMatrix = new InventoryCrafting(this,ROW_SIZE,ROW_COUNT+1);
		for (int i =0; i<ROW_SIZE*(1+ROW_COUNT); i++) {
			ItemStack slotItem = itemHandler.getStackInSlot(i);
			craftMatrix.setInventorySlotContents(i, slotItem);
		}
		 standard = standardT==null ? new ItemStackHandler(ROW_COUNT*(ROW_SIZE-1)+CRYSTAL_COUNT+1) {
		      @Override
		        protected void onContentsChanged(int slot) {
		            // We need to tell the tile entity that something has changed so
		            // that the chest contents is persisted
		    	  //inventory.fillState(ContainerTable.this);
		            inventory.standardSoiler(this);
		               
		         
		        }
		      
		      
		      
			  	@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					
			  		inventory.fillState(ContainerTable.this);
			  		return super.extractItem(slot,amount,simulate);
					
				}
				
				@Override
			    @Nonnull
			    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
			    {
					inventory.fillState(ContainerTable.this);

					return super.insertItem(slot, stack, simulate);


			    }
		} : standardT;
		
		
		
		dummy = dummyT == null ? new ItemStackHandler(itemHandler.getSlots()) {
			
		      @Override
		        protected void onContentsChanged(int slot) {
		            // We need to tell the tile entity that something has changed so
		            // that the chest contents is persisted
		    	    //inventory.fillState(ContainerTable.this);
		            inventory.dummySoiler(this);
		            super.onContentsChanged(slot);
		               
		         
		        }
		  	@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				
		  		inventory.setList(ItemStack.EMPTY);
		  		inventory.fillState(ContainerTable.this);
		  		this.stacks.set(slot, ItemStack.EMPTY);  
				return ItemStack.EMPTY;
				
			}
			
			@Override
		    @Nonnull
		    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		    {
		        
				inventory.setList(stack);
				inventory.fillState(ContainerTable.this);
				ItemStack newStack = stack.copy();
				newStack.setCount(1);
				super.insertItem(slot, newStack, false);
				onContentsChanged(slot);
				return stack.copy();


		    }
		} : dummyT;
		
		if (dummyT==null) {
			inventory.dummySoiler(dummy);
			
		}
		if (standardT==null) {
			inventory.dummySoiler(standard);
		}
		
		
		
		
		for (int i=0;i<ROW_COUNT-1;i++) {
			for(int j=0;j<ROW_SIZE;j++) {
				this.addSlotToContainer(new SlotItemHandler(itemHandler,i*ROW_SIZE+j,8+j*18,18+i*18) {
					@Override
					 public boolean canTakeStack(EntityPlayer playerIn)
				    {
				        return !ContainerTable.this.CextractItem(this.slotNumber, 1, true).isEmpty();
				    }
					
				    @Override
				    @Nonnull
				    public ItemStack decrStackSize(int amount)
				    {
				        return ContainerTable.this.CextractItem(this.slotNumber, amount, false);
				    }
				});
			}
		}
		
		for (int i =0; i < CRYSTAL_COUNT; i++) this.addSlotToContainer(new SlotItemHandler(itemHandler,(ROW_COUNT-1)*ROW_SIZE+i,8+i*18,18+(ROW_COUNT-1)*18) {
			
				@Override
				 public boolean canTakeStack(EntityPlayer playerIn)
			    {
			        return !ContainerTable.this.CextractItem(this.slotNumber, 1, true).isEmpty();
			    }
				
			    @Override
			    @Nonnull
			    public ItemStack decrStackSize(int amount)
			    {
			        return ContainerTable.this.CextractItem(this.slotNumber, amount, false);
			    }

		});
		 this.addSlotToContainer(new SlotItemHandler(itemHandler,(ROW_COUNT-1)*ROW_SIZE+CRYSTAL_COUNT,8+1*18,18+((ROW_COUNT))*18));
		 this.addSlotToContainer(new SlotItemHandler(itemHandler,(ROW_COUNT-1)*ROW_SIZE+CRYSTAL_COUNT+1,8+2*18,18+((ROW_COUNT))*18));
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(playerInv, x + y*9 + 9, 8 + x*18, 175 + y*18));
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x*18, 233));
		}
		for(int x =0; x <craftMatrix.getSizeInventory(); x++) {
			//this.addSlotToContainer(new SlotCrafting(player, craftMatrix, playerInv, x+1000, -1, -1)); 
			
		}
		CONSTRUCTING = false;
	}
	
	
	@Override 
	 public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		int index = slotId;
		if (index< (ROW_COUNT-1)*ROW_SIZE+CRYSTAL_COUNT || index> (ROW_COUNT-1)*ROW_SIZE+CRYSTAL_COUNT+ 1) {
			if (index< (ROW_COUNT-1)*ROW_SIZE+CRYSTAL_COUNT) {
				//super.slotClick(slotId, dragType, clickTypeIn, player);
				
				
			}
			ItemStack ret = super.slotClick(slotId, dragType, clickTypeIn, player);
			this.inventory.markDirty();
			return ret;
			
		}
		if (index == (ROW_COUNT-1)*ROW_SIZE+CRYSTAL_COUNT+ 1) {
			if (clickTypeIn != ClickType.PICKUP) {
				return super.slotClick(slotId, dragType, clickTypeIn, player);
			}
			return player.inventory.getItemStack();
		}
		Slot s = this.inventorySlots.get(index);
		ItemStack sendMe = player.inventory.getItemStack().copy();
		sendMe.setCount(1);
		s.putStack(sendMe);
		this.inventory.setOutputInEntity(sendMe);
		inventory.setList(sendMe);
		this.inventory.markDirty();
		return player.inventory.getItemStack();
	    
	}
	
	
	public ItemStack slotCustomClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		
		return null;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) 
	{
		
		super.onContainerClosed(playerIn);
		//inventory.closeInventory(playerIn);
	}
	


	
	
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        
        int invsize = (ROW_COUNT-1)*ROW_SIZE+CRYSTAL_COUNT+2;
        int usethis = invsize-2;

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < usethis)
            {
                if (!this.mergeItemStack(itemstack1, usethis, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, usethis, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
	}
	
	public TileEntityTable getChestInventory()
	{
		return this.inventory;
	}

/*
	@Override
	public void update() {
		if (CONSTRUCTING) return;
		if (TICK_CURR == COUNT_TICK) {
			inventory.fillState(ContainerTable.this);
			TICK_CURR =0;
		} else {
			TICK_CURR ++;
		}
		
	}*/
	
	
	
	
    private void validateSlotIndex(int slot, ItemStackHandler h)
    {
        if (slot < 0 || slot >= h.getSlots())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + h.getSlots() + ")");
    }
	
    @Nonnull
    public ItemStack CextractItem(int slot, int amount, boolean simulate)
    {
    	ItemStackHandler itemHandler = (ItemStackHandler) this.inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot, itemHandler);

        ItemStack existing = itemHandler.getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
                inventory.markDirty();
            }
            return existing;
        }
        else
        {
            if (!simulate)
            {
                itemHandler.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                inventory.markDirty();
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }
	
}
