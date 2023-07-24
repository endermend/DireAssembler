package EnderMod.net.endermod.mod.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

import EnderMod.net.endermod.mod.tile.TileEntityIchorCollector;

public class ContainerIchor extends Container {

    private TileEntityIchorCollector tileIchor;

    public ContainerIchor(InventoryPlayer player, TileEntityIchorCollector machine) {
        this.tileIchor = machine;
        this.addSlotToContainer(new SlotFurnace(player.player, machine, 2, 20, 30));
        int i = 0;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tileIchor.isUseableByPlayer(player);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotNumber);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }
        ItemStack copy = slot.getStack();
        itemStack = copy.copy();

        if (slotNumber == 0) {
            if (!this.mergeItemStack(copy, 1, 37, true)) {
                return null;
            }

            slot.onSlotChange(copy, itemStack);
        } else {
            if (slotNumber < 28) {
                if (!this.mergeItemStack(copy, 28, 37, false)) {
                    return null;
                }
            } else if (slotNumber < 37 && !this.mergeItemStack(copy, 1, 28, false)) {
                return null;
            }
        }

        if (copy.stackSize == 0) {
            slot.putStack((ItemStack) null);
        } else {
            slot.onSlotChanged();
        }

        if (copy.stackSize == itemStack.stackSize) {
            return null;
        }
        slot.onPickupFromSlot(player, copy);
        return itemStack;
    }
}
