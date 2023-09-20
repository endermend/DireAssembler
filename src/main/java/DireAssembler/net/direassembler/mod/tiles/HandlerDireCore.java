package DireAssembler.net.direassembler.mod.tiles;

import java.util.ArrayList;

import DireAssembler.net.direassembler.mod.items.ItemDireCore;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class HandlerDireCore {
	/**
	 * NBT Keys.
	 */
	private static final String NBTKEY_PATTERNS = "Patterns";

	/**
	 * Maximum number of stored patterns.
	 */
	public static final int MAXIMUM_STORED_PATTERNS = 21;

	/**
	 * Array of stored patterns.
	 */
	private final ArrayList<DireCraftingPattern> patterns = new ArrayList<DireCraftingPattern>(MAXIMUM_STORED_PATTERNS);

	/**
	 * Knowledge core being handled.
	 */
	private ItemStack direCore;

	/**
	 * Creates a handler without a core. Use open() to set the core in the future.
	 */
	public HandlerDireCore() {
	}

	/**
	 * Creates the handler with the specified core.
	 *
	 * @param direCore
	 */
	public HandlerDireCore(final ItemStack dCore) {
		this.open(dCore);
	}

	/**
	 * Gets or creates the NBT tag for the knowledge core.
	 *
	 * @return
	 */
	private NBTTagCompound getOrCreateNBT() {
		if (!this.direCore.hasTagCompound()) {
			this.direCore.stackTagCompound = new NBTTagCompound();
		}

		return this.direCore.stackTagCompound;
	}

	/**
	 * Loads the data from the knowledge core's nbt.
	 */
	private void loadDireCoreData() {
		// Clear any existing data
		this.patterns.clear();

		// Get the data tag
		NBTTagCompound data = this.getOrCreateNBT();

		// Are there saved patterns?
		if (data.hasKey(NBTKEY_PATTERNS)) {
			// Get the list
			NBTTagList plist = data.getTagList(NBTKEY_PATTERNS, Constants.NBT.TAG_COMPOUND);

			// Read in each pattern
			for (int index = 0; index < plist.tagCount(); index++) {
				try {
					// Load the pattern
					DireCraftingPattern pattern = new DireCraftingPattern(this.direCore, plist.getCompoundTagAt(index));

					// Is the pattern valid?
					if (pattern.isPatternValid()) {
						// Add the pattern
						this.patterns.add(pattern);
					}
				} catch (Exception e) {
					// Ignore invalid patterns
				}
			}
		}

	}

	/**
	 * Saves the data to the dire core's nbt.
	 */
	private void saveDireCoreData() {
		// Get the data tag
		NBTTagCompound data = this.getOrCreateNBT();

		// Create the pattern list
		NBTTagList plist = new NBTTagList();

		// Save each pattern
		for (DireCraftingPattern pattern : this.patterns) {
			// Ensure the pattern is valid
			if ((pattern == null) || (!pattern.isPatternValid())) {
				continue;
			}

			// Write the pattern
			plist.appendTag(pattern.writeToNBT(new NBTTagCompound()));
		}

		// Write the list to the data
		if (plist.tagCount() > 0) {
			data.setTag(NBTKEY_PATTERNS, plist);
		} else {
			data.removeTag(NBTKEY_PATTERNS);
		}
	}

	/**
	 * Adds a pattern to the core.
	 */
	public void addPattern(final DireCraftingPattern pattern) {
		// Validate the pattern
		if ((pattern == null) || (!pattern.isPatternValid())) {
			return;
		}

		// Ensure there is room to store the pattern
		if (!this.hasRoomToStorePattern()) {
			return;
		}

		// Check for duplicate patterns
		DireCraftingPattern existingPattern = this.getPatternForItem(pattern.getResult().getItemStack());

		if (existingPattern == null) {
			// Add the pattern
			this.patterns.add(pattern);

			// Save
			this.saveDireCoreData();
		}

	}

	/**
	 * Instructs the handler to close.
	 */
	public void close() {
		this.direCore = null;
		this.patterns.clear();
	}

	/**
	 * Gets the pattern that produces the result.
	 *
	 * @param resultStack
	 * @return
	 */
	public DireCraftingPattern getPatternForItem(final ItemStack resultStack) {
		// Loop over all stored patterns
		for (DireCraftingPattern p : this.patterns) {
			// Does the pattern have a valid output?
			if ((p != null) && (p.getResult() != null)) {
				// Is the output equal to the specified result?
				if (ItemStack.areItemStacksEqual(p.getResult().getItemStack(), resultStack)) {
					// Found the pattern
					return p;
				}
			}
		}

		// No matching patterns
		return null;
	}

	/**
	 * Gets the list of stored patterns.
	 *
	 * @return
	 */
	public ArrayList<DireCraftingPattern> getPatterns() {
		return this.patterns;
	}

	/**
	 * Gets the results of all stored patterns.
	 *
	 * @return
	 */
	public ArrayList<ItemStack> getStoredOutputs() {
		// Create the array
		ArrayList<ItemStack> results = new ArrayList<ItemStack>();

		// Add each stored patterns output
		for (DireCraftingPattern p : this.patterns) {
			if ((p != null) && (p.getResult() != null)) {
				results.add(p.getResult().getItemStack());
			}
		}

		// Return the array
		return results;
	}

	/**
	 * Returns true if the handler has a core.
	 *
	 * @return
	 */
	public boolean hasCore() {
		return this.direCore != null;
	}

	/**
	 * Returns true if there is a pattern stored that produces the specified result.
	 *
	 * @param resultStack
	 * @return
	 */
	public boolean hasPatternFor(final ItemStack resultStack) {
		return (this.getPatternForItem(resultStack) != null);
	}

	/**
	 * Returns true if there is room to store a new pattern.
	 *
	 * @return
	 */
	public boolean hasRoomToStorePattern() {
		return (this.patterns.size() < HandlerDireCore.MAXIMUM_STORED_PATTERNS);
	}

	/**
	 * Return's true if this handler is handling the specified knowledge core.
	 *
	 * @param direCore
	 * @return
	 */
	public boolean isHandlingCore(final ItemStack direCore) {
		// Is the handler handling a core?
		if (this.direCore == null) {
			// Handler has been closed.
			return false;
		}

		// Is the specified itemstack valid?
		if ((direCore == null) || (direCore.getItem() == null)) {
			// Invalid itemstack
			return false;
		}

		// Is the specified item a knowledge core?
		if (!(direCore.getItem() instanceof ItemDireCore)) {
			// Invalid core
			return false;
		}

		return (ItemStack.areItemStacksEqual(direCore, this.direCore)
				&& (ItemStack.areItemStackTagsEqual(direCore, this.direCore)));
	}

	/**
	 * Closes the previous core, and opens the new core.
	 *
	 * @param kCore
	 */
	public void open(final ItemStack kCore) {
		// Close any existing core
		this.close();

		// Set the kCore
		this.direCore = kCore;

		// Load
		this.loadDireCoreData();
	}

	/**
	 * Removes the specified pattern from the core.
	 *
	 * @param pattern
	 */
	public void removePattern(final DireCraftingPattern pattern) {
		// Attempt to remove the pattern
		if (this.patterns.remove(pattern)) {
			// Save
			this.saveDireCoreData();
		}
	}
}
