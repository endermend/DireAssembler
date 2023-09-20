package DireAssembler.net.direassembler.mod.containers;

import java.util.ArrayList;
import java.util.Iterator;

import DireAssembler.net.direassembler.mod.common.DireLog;
import DireAssembler.net.direassembler.mod.gui.DireRecipeHelper;
import DireAssembler.net.direassembler.mod.items.ItemDireCore;
import DireAssembler.net.direassembler.mod.packet.Packet_C_DireInscriber;
import DireAssembler.net.direassembler.mod.slots.SlotRestrictive;
import DireAssembler.net.direassembler.mod.tiles.DireCraftingPattern;
import DireAssembler.net.direassembler.mod.tiles.HandlerDireCore;
import DireAssembler.net.direassembler.mod.tiles.TheInternalInventory;
import DireAssembler.net.direassembler.mod.tiles.TileDireInscriber;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.slot.SlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.container.slot.SlotInaccessible;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ContainerDireInscriber extends ContainerWithPlayerInventory {

	public enum CoreSaveState {
		Disabled_InvalidRecipe, Disabled_CoreFull, Disabled_MissingCore, Enabled_Save, Enabled_Delete
	}

	/**
	 * Maximum number of patterns.
	 */
	private static final int MAXIMUM_PATTERNS = HandlerDireCore.MAXIMUM_STORED_PATTERNS;

	/**
	 * Y position for the player and hotbar inventory.
	 */
	private static final int PLAYER_INV_POSITION_Y = 174,
			HOTBAR_INV_POSITION_Y = ContainerDireInscriber.PLAYER_INV_POSITION_Y + 58;

	/**
	 * Knowledge core slot.
	 */
	public static final int DIRE_CORE_SLOT_X = 194, DIRE_CORE_SLOT_Y = 104;

	/**
	 * Pattern slots.
	 */
	private static final int PATTERN_SLOT = 0, PATTERN_SLOT_X = 176, PATTERN_SLOT_Y = 124, PATTERN_ROWS = 7,
			PATTERN_COLS = 3, PATTERN_SLOT_SPACING = 18;

	/**
	 * Crafting slots
	 */
	public static final int CRAFTING_MATRIX_SLOT = ContainerDireInscriber.MAXIMUM_PATTERNS
			+ ContainerDireInscriber.PATTERN_SLOT, CRAFTING_SLOT_X = 8, CRAFTING_SLOT_Y = 8, CRAFTING_ROWS = 9,
			CRAFTING_COLS = 9,
			CRAFTING_GRID_SIZE = ContainerDireInscriber.CRAFTING_ROWS * ContainerDireInscriber.CRAFTING_COLS,
			CRAFTING_SLOT_SPACING = 18, CRAFTING_RESULT_SLOT = ContainerDireInscriber.CRAFTING_MATRIX_SLOT
					+ ContainerDireInscriber.CRAFTING_GRID_SIZE;

	/**
	 * Handles interaction with the knowledge core.
	 */
	private final HandlerDireCore direCoreHandler;

	/**
	 * Slots
	 */
	private final SlotRestrictive direCoreSlot;
	private final SlotFake resultSlot;
	private final SlotInaccessible[] patternSlots = new SlotInaccessible[ContainerDireInscriber.MAXIMUM_PATTERNS];
	private final SlotFakeCraftingMatrix[] craftingSlots = new SlotFakeCraftingMatrix[ContainerDireInscriber.CRAFTING_GRID_SIZE];

	/**
	 * The current recipe, if any.
	 */
	private IRecipe activeRecipe = null;

	/**
	 * Inscriber tile entity.
	 */
	private TileDireInscriber inscriber;

	/**
	 * Inventory for the patterns and crafting matrix
	 */
	private final TheInternalInventory internalInventory;

	private final World worldIn;

	public ContainerDireInscriber(final EntityPlayer player, final World world, final int x, final int y, final int z) {
		// Call super
		super(player);
		worldIn = world;
		// Bind to the players inventory
		this.bindPlayerInventory(player.inventory, ContainerDireInscriber.PLAYER_INV_POSITION_Y,
				ContainerDireInscriber.HOTBAR_INV_POSITION_Y);

		// Get the inscriber
		this.inscriber = (TileDireInscriber) world.getTileEntity(x, y, z);

		// Create the Kcore slot
		this.direCoreSlot = new SlotRestrictive(this.inscriber, TileDireInscriber.DIRE_CORE_SLOT,
				ContainerDireInscriber.DIRE_CORE_SLOT_X, ContainerDireInscriber.DIRE_CORE_SLOT_Y);
		this.addSlotToContainer(this.direCoreSlot);

		// Setup the internal inventory
		this.internalInventory = new TheInternalInventory("cki", ContainerDireInscriber.CRAFTING_RESULT_SLOT + 1, 64);

		// Create pattern slots
		this.initPatternSlots();

		// Create crafting slots
		this.initCraftingSlots();

		// Create the result slot
		this.resultSlot = new SlotFake(this.internalInventory, ContainerDireInscriber.CRAFTING_RESULT_SLOT, 192, 44);
		this.addSlotToContainer(this.resultSlot);

		// Create the handler
		this.direCoreHandler = new HandlerDireCore();
	}

	/**
	 * Determines the current save state.
	 *
	 * @return
	 */
	private CoreSaveState getSaveState() {
		CoreSaveState saveState;

		// Is there a core handler?
		if (!this.direCoreHandler.hasCore()) {
			saveState = CoreSaveState.Disabled_MissingCore;
		}
		// Is there a valid recipe?
		else if (this.activeRecipe == null) {
			saveState = CoreSaveState.Disabled_InvalidRecipe;
		} else {
			// Get the recipe output
			ItemStack recipeOutput = activeRecipe.getRecipeOutput();

			// Ensure there is an output
			if (recipeOutput == null) {
				saveState = CoreSaveState.Disabled_InvalidRecipe;
			} else {
				// Does the core already have this recipe, or one that produces the same result,
				// stored?
				boolean isNew = !this.direCoreHandler.hasPatternFor(recipeOutput);

				// Would the recipe be a new pattern?
				if (isNew) {
					// Is there room for the recipe?
					if (this.direCoreHandler.hasRoomToStorePattern()) {
						// Enable saving
						saveState = CoreSaveState.Enabled_Save;
					} else {
						// Core is full
						saveState = CoreSaveState.Disabled_CoreFull;
					}
				} else {
					// Enable deleting
					saveState = CoreSaveState.Enabled_Delete;
				}
			}
		}

		return saveState;
	}

	private void initCraftingSlots() {
		int slotIndex;
		// Create the crafting slots
		slotIndex = ContainerDireInscriber.CRAFTING_MATRIX_SLOT;
		for (int row = 0; row < ContainerDireInscriber.CRAFTING_ROWS; row++) {
			for (int column = 0; column < ContainerDireInscriber.CRAFTING_COLS; column++) {
				// Calculate the array index
				int index = (row * ContainerDireInscriber.CRAFTING_COLS) + column;

				// Calculate the position
				int posX = ContainerDireInscriber.CRAFTING_SLOT_X
						+ (ContainerDireInscriber.CRAFTING_SLOT_SPACING * column);
				int posY = ContainerDireInscriber.CRAFTING_SLOT_Y
						+ (ContainerDireInscriber.CRAFTING_SLOT_SPACING * row);

				// Add the slot
				this.addSlotToContainer(this.craftingSlots[index] = new SlotFakeCraftingMatrix(this.internalInventory,
						slotIndex++, posX, posY));

			}
		}
	}

	private void initPatternSlots() {
		int slotIndex;
		// Create the pattern slots
		slotIndex = ContainerDireInscriber.PATTERN_SLOT;
		for (int row = 0; row < ContainerDireInscriber.PATTERN_ROWS; row++) {
			for (int column = 0; column < ContainerDireInscriber.PATTERN_COLS; column++) {
				// Calculate the array index
				int index = (row * ContainerDireInscriber.PATTERN_COLS) + column;

				// Calculate the position
				int posX = ContainerDireInscriber.PATTERN_SLOT_X
						+ (ContainerDireInscriber.PATTERN_SLOT_SPACING * column);
				int posY = ContainerDireInscriber.PATTERN_SLOT_Y + (ContainerDireInscriber.PATTERN_SLOT_SPACING * row);

				// Add to the array
				this.patternSlots[index] = new SlotInaccessible(this.internalInventory, slotIndex++, posX, posY);

				// Add the slot
				this.addSlotToContainer(this.patternSlots[index]);
			}
		}
	}

	private void loadPattern(final DireCraftingPattern pattern) {
		if ((pattern == null) || (!pattern.isPatternValid())) {
			return;
		}
		// Set the slots
		for (int index = 0; index < this.craftingSlots.length; index++) {
			IAEItemStack ingStack = pattern.getInputs()[index];
			if (ingStack != null) {
				this.craftingSlots[index].putStack(ingStack.getItemStack());
			} else {
				this.craftingSlots[index].putStack(null);
			}
		}
	}

	/**
	 * Prepares input to a pattern from the current recipe
	 *
	 * @param input
	 * @param slotNumber
	 * @return
	 */
	private Object preparePatternInput(final Object input, final int slotNumber) {
		if (input instanceof ArrayList) {
			// Get the prefered item
			ItemStack preferedItem = this.craftingSlots[slotNumber].getStack();

			// Create the list
			ArrayList<ItemStack> ingList = new ArrayList<ItemStack>();

			// Add the prefered item first
			if (preferedItem != null) {
				ingList.add(preferedItem);
			}

			// Add the rest
			ArrayList<ItemStack> inputList = (ArrayList<ItemStack>) input;
			for (ItemStack item : inputList) {
				if ((item == null) || (ItemStack.areItemStacksEqual(preferedItem, item))) {
					continue;
				}
				ingList.add(item);
			}

			return ingList;
		}
		// Is this a wildcard item?
		else if ((input instanceof ItemStack)
				&& (((ItemStack) input).getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
			// Create a list to hold the users preferred item, and the wildcard item
			ArrayList<ItemStack> ingList = new ArrayList<ItemStack>();
			ingList.add(this.craftingSlots[slotNumber].getStack());
			ingList.add((ItemStack) input);
			return ingList;
		}

		return input;
	}

	/**
	 * Updates the slots to reflect the stored patterns.
	 */
	private void updatePatternSlots() {
		Iterator<ItemStack> iterator = null;

		// Get the list of stored pattern results
		if (this.direCoreHandler.hasCore()) {
			ArrayList<ItemStack> storedResults = this.direCoreHandler.getStoredOutputs();
			iterator = storedResults.iterator();
		}

		// Loop over all pattern slots
		for (Slot patternSlot : this.patternSlots) {
			// Is there an itemstack to put?
			if ((iterator != null) && (iterator.hasNext())) {
				// Put the result
				patternSlot.putStack(iterator.next());
			} else {
				// Clear the slot
				patternSlot.putStack(null);
			}

			// Update clients with change
			for (int cIndex = 0; cIndex < this.crafters.size(); ++cIndex) {
				((ICrafting) this.crafters.get(cIndex)).sendSlotContents(this, patternSlot.slotNumber,
						patternSlot.getStack());
			}
		}
	}

	/**
	 * Checks for core insertion or removal.
	 *
	 * @param playerMP
	 * @return
	 */
	@Override
	protected boolean detectAndSendChangesMP(final EntityPlayerMP playerMP) {
		// Has a core changed?
		if (!this.direCoreHandler.isHandlingCore(this.direCoreSlot.getStack())) {
			if (this.direCoreSlot.getHasStack()) {
				// Setup the handler
				this.direCoreHandler.open(this.direCoreSlot.getStack());
			} else {
				// Close the handler
				this.direCoreHandler.close();
			}

			// Update the slots
			this.updatePatternSlots();

			// Update the save state
			this.sendSaveState(false);

			return true;
		}

		return false;
	}

	/**
	 * Can interact with anyone
	 *
	 * @param player
	 * @return
	 */
	@Override
	public boolean canInteractWith(final EntityPlayer player) {
		if (this.inscriber != null) {
			return this.inscriber.isUseableByPlayer(player);
		}
		return false;
	}

	public void onClientRequestClearGrid() {
		// Clear the grid
		for (int index = ContainerDireInscriber.CRAFTING_MATRIX_SLOT; index < ContainerDireInscriber.CRAFTING_RESULT_SLOT; ++index) {
			this.internalInventory.setInventorySlotContents(index, null);
		}

		// Update the matrix
		this.onCraftMatrixChanged(this.internalInventory);
	}

	/**
	 * Attempts to save or delete the active pattern from the kcore.
	 *
	 * @param player
	 */
	public void onClientRequestSaveOrDelete(final EntityPlayer player) {
		// Get the current save state
		CoreSaveState saveState = this.getSaveState();

		Object[] inputs = new Object[ContainerDireInscriber.CRAFTING_GRID_SIZE];

		if (saveState == CoreSaveState.Enabled_Save) {
			String matrix = "\n";

			for (int row = 0; row < ContainerDireInscriber.CRAFTING_ROWS; row++) {
				for (int col = 0; col < ContainerDireInscriber.CRAFTING_COLS; col++) {
					int index = row * ContainerDireInscriber.CRAFTING_COLS + col;
					if (craftingSlots[index] != null && craftingSlots[index].getStack() != null) {
						matrix += "[" + craftingSlots[index].getStack().getDisplayName() + "]";
					} else {
						matrix += "[NULL]";
					}
				}
				matrix += "\n";
			}
			DireLog.info(matrix);
			for (int index = 0; index < inputs.length; index++) {
				if (craftingSlots[index] != null && craftingSlots[index].getStack() != null) {
					inputs[index] = craftingSlots[index].getStack();
				} else {
					inputs[index] = null;
				}
			}

			// Create the pattern
			DireCraftingPattern pattern = new DireCraftingPattern(this.direCoreSlot.getStack(),
					this.resultSlot.getStack(), inputs);

			// Add the pattern
			this.direCoreHandler.addPattern(pattern);

			// Update the slots
			this.updatePatternSlots();
			this.loadPattern(pattern);

			// Update the save state
			this.sendSaveState(true);

			// Mark the inscriber as dirty
			this.inscriber.markDirty();
		} else if (saveState == CoreSaveState.Enabled_Delete) {
			// Get the pattern for the result item
			DireCraftingPattern pattern = this.direCoreHandler.getPatternForItem(this.resultSlot.getStack());

			// Ensure there is a pattern for it
			if (pattern != null) {
				// Remove it
				this.direCoreHandler.removePattern(pattern);

				// Update the slots
				this.updatePatternSlots();

				// Update the save state
				this.sendSaveState(false);

				// Mark the inscriber as dirty
				this.inscriber.markDirty();
			}
		}
	}

	/**
	 * A client has requested the save state.
	 *
	 * @param player
	 * @param justSaved
	 */
	public void onClientRequestSaveState() {
		// Update the client
		this.sendSaveState(false);
	}

	/**
	 * Updates the arcane recipe.
	 *
	 * @param inv
	 */
	@Override
	public void onCraftMatrixChanged(final IInventory inv) {
		// Set the active recipe
		this.activeRecipe = DireRecipeHelper.INSTANCE.findMatchingExtremeRecipe(inv, worldIn,
				ContainerDireInscriber.CRAFTING_MATRIX_SLOT - 1);

		ItemStack craftResult = null;

		// Set the result slot
		if (this.activeRecipe != null) {
			craftResult = activeRecipe.getRecipeOutput();
		}

		this.resultSlot.putStack(craftResult);

		// Update the save state
		if (!worldIn.isRemote) {
			this.sendSaveState(false);
		}

		// Sync
		this.detectAndSendChanges();
	}

	/**
	 * Sends the save-state to the client.
	 */
	public void sendSaveState(final boolean justSaved) {
		Packet_C_DireInscriber.sendSaveState(this.player, this.getSaveState(), justSaved);
	}
	
	/**
	 * Creates 'ghost' items when a crafting slot is clicked.
	 *
	 * @param slotNumber
	 * @param buttonPressed
	 * @param flag
	 * @param player
	 * @return
	 */
	@Override
	public ItemStack slotClick(final int slotNumber, final int buttonPressed, final int flag,
			final EntityPlayer player) {
		// Get the itemstack the player is holding with the mouse
		ItemStack draggingStack = player.inventory.getItemStack();

		// Was the clicked slot a crafting slot?
		for (Slot slot : this.craftingSlots) {
			if (slot.slotNumber == slotNumber) {
				// Is the player holding anything?
				if (draggingStack != null) {
					ItemStack copiedStack = draggingStack.copy();
					copiedStack.stackSize = 1;

					// Place a copy of the stack into the clicked slot
					slot.putStack(copiedStack);
				} else {
					// Clear the slot
					slot.putStack(null);
				}

				// Update the matrix
				this.onCraftMatrixChanged(slot.inventory);

				return draggingStack;
			}
		}

		// Was the clicked slot a pattern slot?
		for (Slot slot : this.patternSlots) {
			if (slot.slotNumber == slotNumber) {
				// Does the slot correspond to a stored pattern?
				if (slot.getHasStack()) {
					// Load the pattern
					this.loadPattern(this.direCoreHandler.getPatternForItem(slot.getStack()));

					// Update the matrix
					this.onCraftMatrixChanged(slot.inventory);
				}

				return draggingStack;
			}
		}

		// Pass to super
		return super.slotClick(slotNumber, buttonPressed, flag, player);

	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber) {
		// Is this client side?
		if (worldIn.isRemote) {
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
				// Attempt to merge with kcore slot
				if (slotStack.getItem() instanceof ItemDireCore) {
					didMerge = this.mergeItemStack(slotStack, this.direCoreSlot.slotNumber,
							this.direCoreSlot.slotNumber + 1, false);
				}

				// Was the stack merged?
				if (!didMerge) {
					// Attempt to merge with player inventory
					didMerge = this.swapSlotInventoryHotbar(slotNumber, slotStack);
				}
			}
			// Was the slot clicked the KCore slot?
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
			}
		}

		// All done.
		return null;

	}
}
