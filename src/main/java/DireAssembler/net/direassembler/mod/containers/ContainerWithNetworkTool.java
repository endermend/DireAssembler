package DireAssembler.net.direassembler.mod.containers;

import java.util.Iterator;

import com.google.common.base.Optional;
import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.implementations.guiobjects.IGuiItem;
import DireAssembler.net.direassembler.mod.common.DireAll;
import DireAssembler.net.direassembler.mod.common.DireLog;
import DireAssembler.net.direassembler.mod.slots.SlotNetworkTool;
import DireAssembler.net.direassembler.mod.slots.SlotRestrictive;
import appeng.api.implementations.guiobjects.INetworkTool;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.api.util.DimensionalCoord;
import appeng.parts.automation.BlockUpgradeInventory;
import appeng.parts.automation.UpgradeInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerWithNetworkTool extends ContainerWithPlayerInventory {
	/**
	 * Number of rows in the network tool
	 */
	private static final int TOOL_ROWS = 3;

	/**
	 * Number of columns in the network tool
	 */
	private static final int TOOL_COLUMNS = 3;

	/**
	 * X position to start drawing slots
	 */
	private static final int TOOL_SLOT_X_OFFSET = 187;

	/**
	 * Y position to start drawing slots
	 */
	private static final int TOOL_SLOT_Y_OFFSET = 102;

	/**
	 * How far apart each slot should be drawn
	 */
	private static int UPGRADE_Y_POSITION_MULTIPLIER = 18;

	/**
	 * Index of the first network tool slot
	 */
	private int firstToolSlotNumber = -1;

	/**
	 * Index of the last network tool slot
	 */
	private int lastToolSlotNumber = -1;

	/**
	 * Index of the first upgrade slot
	 */
	private int firstUpgradeSlot = -1;

	/**
	 * Index of the first upgrade slot
	 */
	private int lastUpgradeSlot = -1;

	/**
	 * True if there is a network tool
	 */
	protected boolean hasNetworkTool = false;

	public ContainerWithNetworkTool(final EntityPlayer player) {
		super(player);
	}

	/**
	 * Adds upgrade slots to the container.
	 *
	 * @param upgradeInventory
	 * @param count
	 * @param xPosition
	 * @param yPosition
	 */
	protected void addUpgradeSlots(final UpgradeInventory upgradeInventory, final int count, final int xPosition,
			final int yPosition) {
		Slot upgradeSlot = null;
				
		// Add the upgrade slots
		for (int slotIndex = 0; slotIndex < count; slotIndex++) {
			// Create the slot
			upgradeSlot = new SlotRestrictive(upgradeInventory, slotIndex, xPosition,
					yPosition + (slotIndex * ContainerWithNetworkTool.UPGRADE_Y_POSITION_MULTIPLIER));

			// Add the slot
			this.addSlotToContainer(upgradeSlot);

			// Check first
			if (slotIndex == 0) {
				this.firstUpgradeSlot = upgradeSlot.slotNumber;
			}
		}

		// Set last
		if (upgradeSlot != null) {
			this.lastUpgradeSlot = upgradeSlot.slotNumber;
		}
	}

	protected void bindToNetworkTool(final InventoryPlayer playerInventory, final DimensionalCoord partLocation,
			final int slotOffsetX, final int slotOffsetY) {
		// Get the networkTool or null if absent (e.g. disabled in AE's config-file)
		ItemStack nwTool = AEApi.instance().definitions().items().networkTool().maybeStack(0).orNull();

		// First of all is there a networkTool?
		if (nwTool != null) {
			// Check the player inventory for the network tool
			for (int slotIndex = 0; slotIndex < playerInventory.getSizeInventory(); slotIndex++) {
				// Get the item in the current slot
				ItemStack stack = playerInventory.getStackInSlot(slotIndex);

				// Is it the network tool?
				if ((stack != null) && (stack
						.isItemEqual(AEApi.instance().definitions().items().networkTool().maybeStack(1).get()))) {
					// Get the gui item for the tool
					IGuiItem guiItem = (IGuiItem) stack.getItem();

					// Get the gui for the tool
					INetworkTool networkTool = (INetworkTool) guiItem.getGuiObject(stack, partLocation.getWorld(),
							partLocation.x, partLocation.y, partLocation.z);

					Slot toolSlot = null;

					// Add a slot for each network tool slot
					for (int column = 0; column < ContainerWithNetworkTool.TOOL_COLUMNS; column++) {
						for (int row = 0; row < ContainerWithNetworkTool.TOOL_ROWS; row++) {
							// Calculate the tools slot index
							int slotToolIndex = column + (row * ContainerWithNetworkTool.TOOL_COLUMNS);

							// Create the slot
							toolSlot = new SlotNetworkTool(networkTool, slotToolIndex,
									ContainerWithNetworkTool.TOOL_SLOT_X_OFFSET + slotOffsetX
											+ (column * ContainerWithPlayerInventory.SLOT_SIZE),
									(row * ContainerWithPlayerInventory.SLOT_SIZE)
											+ ContainerWithNetworkTool.TOOL_SLOT_Y_OFFSET + slotOffsetY);

							// Add the slot
							this.addSlotToContainer(toolSlot);

							// Check first
							if (slotToolIndex == 0) {
								this.firstToolSlotNumber = toolSlot.slotNumber;
							}
						}
					}

					// Set last
					if (toolSlot != null) {
						this.lastToolSlotNumber = toolSlot.slotNumber;
					}

					// Mark that we have a network tool
					this.hasNetworkTool = true;

					// Done
					return;
				}
			}
		}
	}

	/**
	 * Attempt to merge the specified slot stack with the tool inventory
	 *
	 * @param slotStack
	 * @return
	 */
	protected boolean mergeSlotWithNetworkTool(final ItemStack slotStack) {
		if (!this.hasNetworkTool) {
			return false;
		}
		// Is the item an upgrade card?
		if (!(slotStack.getItem() instanceof IUpgradeModule)) {
			// Not an upgrade module
			return false;
		}

		return this.mergeItemStack(slotStack, this.firstToolSlotNumber, this.lastToolSlotNumber + 1, false);

	}

	/**
	 * Attempt to merge the specified slot stack with the upgrade inventory
	 *
	 * @param slotStack
	 * @return
	 */
	protected boolean mergeSlotWithUpgrades(final ItemStack slotStack) {
		boolean didMerge = false;

		// Are there any open slots in the upgrade inventory?
		for (int index = this.firstUpgradeSlot; index <= this.lastUpgradeSlot; index++) {
			// Get the slot
			Slot upgradeSlot = (Slot) this.inventorySlots.get(index);

			// Is the slot empty?
			if (upgradeSlot == null || upgradeSlot.getHasStack() || !upgradeSlot.isItemValid(slotStack)) {
				continue;
			}
			// Create an itemstack of size 1
			ItemStack upgradeStack = slotStack.copy();
			upgradeStack.stackSize = 1;
			// Place the stack in the upgrade slot
			upgradeSlot.putStack(upgradeStack);

			// Decrement the slot stack
			slotStack.stackSize--;

			// Mark that we merged
			didMerge = true;

			// Is the slot stack at zero?
			if (slotStack.stackSize == 0) {
				// Break the loop
				break;
			}

		}

		return didMerge;
	}

	/**
	 * Checks if the slot clicked was in the network tools inventory
	 *
	 * @param slotNumber
	 * @return True if it was in the tools inventory, false otherwise.
	 */
	protected boolean slotClickedWasInNetworkTool(final int slotNumber) {
		return this.hasNetworkTool && (slotNumber >= this.firstToolSlotNumber)
				&& (slotNumber <= this.lastToolSlotNumber);
	}

	protected boolean slotClickedWasInUpgrades(final int slotNumber) {
		return (slotNumber >= this.firstUpgradeSlot) && (slotNumber <= this.lastUpgradeSlot);
	}

	public boolean hasNetworkTool() {
		return this.hasNetworkTool;
	}

	/**
	 * Checks if the shift+click happend in the network tool.
	 */
	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber) {
		// Note: mergeItemStack args: ItemStack stack, int slotStart, int
		// slotEnd(exclusive), boolean reverse

		// Get the slot that was clicked on
		Slot slot = this.getSlotOrNull(slotNumber);

		boolean didMerge = false;

		// Did we get a slot, and does it have a valid item?
		if ((slot != null) && (slot.getHasStack())) {
			// Get the slots item stack
			ItemStack slotStack = slot.getStack();

			// Was the slot clicked in the player or hotbar inventory?
			if (this.slotClickedWasInPlayerInventory(slotNumber) || this.slotClickedWasInHotbarInventory(slotNumber)) {
				// Attempt to merge with the upgrade inventory
				didMerge = this.mergeSlotWithUpgrades(slotStack);

				// Did we merge?
				if (!didMerge) {
					// Attempt to merge with the network tool
					didMerge = this.mergeSlotWithNetworkTool(slotStack);
				}

				// Did we merge?
				if (!didMerge) {
					// Attempt to swap
					didMerge = this.swapSlotInventoryHotbar(slotNumber, slotStack);
				}
			}
			// Was the slot clicked in the upgrades?
			else if (this.slotClickedWasInUpgrades(slotNumber)) {
				// Attempt to merge with the network tool
				didMerge = this.mergeSlotWithNetworkTool(slotStack);

				// Did we merge with the network tool?
				if (!didMerge) {
					// Attempt to merge with the player inventory
					didMerge = this.mergeSlotWithPlayerInventory(slotStack);
				}
			}
			// Was the slot clicked in the network tool?
			else if (this.hasNetworkTool && this.slotClickedWasInNetworkTool(slotNumber)) {
				// Attempt to merge with the upgrade inventory
				didMerge = this.mergeSlotWithUpgrades(slotStack);

				// Did we merge with the upgrades?
				if (!didMerge) {
					// Attempt to merge with the player inventory
					didMerge = this.mergeSlotWithPlayerInventory(slotStack);
				}
			}

			// Were we able to merge?
			if (!didMerge) {
				return null;
			}

			// Did the merger drain the stack?
			if (slotStack.stackSize == 0) {
				// Set the slot to have no item
				slot.putStack(null);
			}

			// Inform the slot its stack changed;
			slot.onSlotChanged();

			// Sync
			this.detectAndSendChanges();
		}

		// Done ( returning null prevents retrySlotClick from being called )
		return null;
	}
}
