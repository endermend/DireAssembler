package DireAssembler.net.direassembler.mod.containers;

import DireAssembler.net.direassembler.mod.blocks.BlockDireAssembler;
import DireAssembler.net.direassembler.mod.slots.SlotRestrictive;
import DireAssembler.net.direassembler.mod.tiles.TileDireAssembler;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.container.slot.SlotInaccessible;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerDireAssembler extends ContainerWithNetworkTool {
	/**
	 * Y position for the player inventory.
	 */
	private static final int PLAYER_INV_POSITION_Y = 115;

	/**
	 * Y position for the hotbar inventory.
	 */
	private static final int HOTBAR_INV_POSITION_Y = PLAYER_INV_POSITION_Y + 58;

	/**
	 * Starting position of the pattern slots.
	 */
	private static final int PATTERN_SLOT_X = 26, PATTERN_SLOT_Y = 25;

	/**
	 * Number of rows and columns of the pattern slots.
	 */
	private static final int PATTERN_ROWS = 3, PATTERN_COLS = 7;

	/**
	 * Position of the dire core slot.
	 */
	private static final int DIRE_CORE_SLOT_X = 187, DIRE_CORE_SLOT_Y = 8;

	/**
	 * Upgrade slots.
	 */
	private static final int UPGRADE_SLOT_COUNT = BlockDireAssembler.MAX_SPEED_UPGRADES, UPGRADE_SLOT_X = 187,
			UPGRADE_SLOT_Y = 26;

	/**
	 * Target slot.
	 */
	private static final int TARGET_SLOT_X = 14, TARGET_SLOT_Y = 87;

	/**
	 * Reference to the arcane assembler
	 */
	public final TileDireAssembler assembler;

	/**
	 * Knowledge Core slot.
	 */
	private final SlotRestrictive direCoreSlot;

	/**
	 * Pattern slots
	 */
	private final SlotInaccessible[] patternSlots = new SlotInaccessible[PATTERN_ROWS * PATTERN_COLS];

	public ContainerDireAssembler(final EntityPlayer player, final World world, final int X, final int Y, final int Z) {
		// Call super
		super(player);

		// Get the assembler
		this.assembler = (TileDireAssembler) world.getTileEntity(X, Y, Z);

		// Get the assemblers inventory
		IInventory assemblerInventory = this.assembler.getInternalInventory();

		// Bind to the players inventory
		this.bindPlayerInventory(player.inventory, ContainerDireAssembler.PLAYER_INV_POSITION_Y,
				ContainerDireAssembler.HOTBAR_INV_POSITION_Y);

		// Bind to network tool
		//this.bindToNetworkTool(player.inventory, this.assembler.getLocation(), 0, 35);

		// Add the kcore slot
		this.direCoreSlot = new SlotRestrictive(assemblerInventory, TileDireAssembler.DIRE_CORE_SLOT_INDEX,
				ContainerDireAssembler.DIRE_CORE_SLOT_X, ContainerDireAssembler.DIRE_CORE_SLOT_Y);
		this.addSlotToContainer(this.direCoreSlot);

		// Create the pattern slots
		for (int row = 0; row < ContainerDireAssembler.PATTERN_ROWS; row++) {
			for (int col = 0; col < ContainerDireAssembler.PATTERN_COLS; col++) {
				// Calculate the index
				int index = (row * ContainerDireAssembler.PATTERN_COLS) + col;
				int invIndex = TileDireAssembler.PATTERN_SLOT_INDEX + index;

				// Add the slot
				this.addSlotToContainer(this.patternSlots[index] = new SlotInaccessible(assemblerInventory, invIndex,
						ContainerDireAssembler.PATTERN_SLOT_X + (18 * col),
						ContainerDireAssembler.PATTERN_SLOT_Y + (18 * row)));
			}
		}

		// Create the upgrade slots
		this.addUpgradeSlots(this.assembler.getUpgradeInventory(), ContainerDireAssembler.UPGRADE_SLOT_COUNT,
				ContainerDireAssembler.UPGRADE_SLOT_X, ContainerDireAssembler.UPGRADE_SLOT_Y);

		// Create the target slot
		this.addSlotToContainer(new SlotInaccessible(assemblerInventory, TileDireAssembler.TARGET_SLOT_INDEX,
				ContainerDireAssembler.TARGET_SLOT_X, ContainerDireAssembler.TARGET_SLOT_Y));
	}

	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		// Ensure there is an assembler
		if (this.assembler != null) {
			// Ask it if this player can interact
			return this.assembler.isUseableByPlayer(player);
		}

		return false;
	}

	/**
	 * Called when the player shift+clicks on a slot
	 */
	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber) {
		// Is this client side?
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			// Do nothing.
			return null;
		}

		// Get the slot that was shift-clicked
		Slot slot = this.getSlotOrNull(slotNumber);

		// Is there a valid slot with and item?
		if ((slot != null) && (slot.getHasStack())) {
			boolean didMerge = false;

			// Get the itemstack in the slot
			ItemStack slotStack = slot.getStack();

			// Was the slot clicked in the player or hotbar inventory?
			if (this.slotClickedWasInPlayerInventory(slotNumber) || this.slotClickedWasInHotbarInventory(slotNumber)) {
				// Skip upgrade cards, let the super class handle them
				if (!(slotStack.getItem() instanceof IUpgradeModule)) {
					// Attempt to merge with kcore slot
					if (this.direCoreSlot.isItemValid(slotStack)) {
						didMerge = this.mergeItemStack(slotStack, this.direCoreSlot.slotNumber,
								this.direCoreSlot.slotNumber + 1, false);
					}
					// Was the stack merged?
					if (!didMerge) {
						// Attempt to merge with player inventory
						didMerge = this.swapSlotInventoryHotbar(slotNumber, slotStack);
					}
				}
			}
			// Was the slot clicked the KCore slot or armor slots?
			else if (this.direCoreSlot.slotNumber == slotNumber) {
				// Attempt to merge with player hotbar
				didMerge = this.mergeSlotWithHotbarInventory(slotStack);

				// Was the stack merged?
				if (!didMerge) {
					// Attempt to merge with the player inventory
					didMerge = this.mergeSlotWithPlayerInventory(slotStack);
				}
			}

			// Was the stack merged?
			if (didMerge) {

				// Did the merger drain the stack?
				if ((slotStack == null) || (slotStack.stackSize == 0)) {
					// Set the slot to have no item
					slot.putStack(null);
				} else {
					// Inform the slot its stack changed;
					slot.onSlotChanged();
				}

				// Send changes
				this.detectAndSendChanges();

				return null;
			}
		}

		// Call super
		return super.transferStackInSlot(player, slotNumber);
	}
}
