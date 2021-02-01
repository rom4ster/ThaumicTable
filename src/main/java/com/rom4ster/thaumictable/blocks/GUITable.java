package com.rom4ster.thaumictable.blocks;

import com.rom4ster.thaumictable.util.Reference;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUITable extends GuiContainer{
	private static final ResourceLocation GUI_TABLE = new ResourceLocation(Reference.MOD_ID + ":textures/gui/table.png");
	private final InventoryPlayer playerInventory;
	private final TileEntityTable te;
	
	public GUITable(InventoryPlayer inventory, TileEntityTable tileEntity, EntityPlayer player) {
		super(new ContainerTable(inventory, tileEntity, player));
		this.playerInventory = inventory;
		this.te = tileEntity;
		
		this.xSize = 179;
		this.ySize = 256;
	}

	
	protected void drawGuiContainerForegroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 16086784);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 92, 16086784);
		
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(GUI_TABLE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

}
