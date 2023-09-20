package DireAssembler.net.direassembler.mod.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class DireTileInventory extends TileEntity implements IInventory {
	protected final TheInternalInventory inventory;

	public DireTileInventory(final String invName, final int invSize, final int invStackLimit) {
		this.inventory = new TheInternalInventory(invName, invSize, invStackLimit);
	}

	@Override
	public void closeInventory() {

	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return this.inventory.decrStackSize(index, count);
	}

	@Override
	public String getInventoryName() {
		return this.inventory.getInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}

	@Override
	public int getSizeInventory() {
		return this.inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(final int slotIndex) {
		return this.inventory.getStackInSlot(slotIndex);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slotIndex) {
		return this.inventory.getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.inventory.hasCustomInventoryName();
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {
		return this.inventory.isUseableByPlayer(player, this);
	}

	@Override
	public void markDirty() {
		this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void setInventorySlotContents(final int slotIndex, final ItemStack itemStack) {
		this.inventory.setInventorySlotContents(slotIndex, itemStack);
	}
}
