package DireAssembler.net.direassembler.mod.tiles;


import DireAssembler.net.direassembler.mod.items.ItemDireCore;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileDireInscriber extends DireTileInventory {
	public static final int DIRE_CORE_SLOT = 0;

	private static final String NBTKEY_DIRECORE = "direcore";

	public TileDireInscriber() {
		super("dire.inscriber", 1, 64);
	}

	public boolean hasDireCore() {
		return this.inventory.getHasStack(DIRE_CORE_SLOT);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (stack == null) {
			return true;
		}

		if (index != DIRE_CORE_SLOT) {
			return true;
		}

		return (stack.getItem() instanceof ItemDireCore);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if (!compound.hasKey(NBTKEY_DIRECORE)) {
			return;
		}
		this.inventory.setInventorySlotContents(DIRE_CORE_SLOT,
				ItemStack.loadItemStackFromNBT(compound.getCompoundTag(NBTKEY_DIRECORE)));
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		ItemStack direCore = inventory.getStackInSlot(DIRE_CORE_SLOT);

		if (direCore != null) {
			compound.setTag(NBTKEY_DIRECORE, direCore.writeToNBT(new NBTTagCompound()));
		}
	}
}
