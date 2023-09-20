package DireAssembler.net.direassembler.mod.tiles;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;

import java.util.ArrayList;

import appeng.api.AEApi;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class DireCraftingPattern implements ICraftingPatternDetails {
	private static final String NBTKEY_INGREDIENT_NUM = "input#", NBTKEY_RESULT = "output",
			NBTKEY_INGREDIENT_TYPE = "ThE_crafting_type", NBTKEY_ARRAY_SIZE = "size";
	private static final int NBTKEY_ARRAYTYPE = 2, NBTKEY_ITEMTYPE = 1;

	private static final int GRID_SIZE = 81;

	/**
	 * Ingredients.
	 */
	protected Object[] ingredients = new Object[DireCraftingPattern.GRID_SIZE];

	/**
	 * Crafting result.
	 */
	protected IAEItemStack result;

	/**
	 * The dire core this pattern belongs to.
	 */
	protected ItemStack direCoreHost;

	/**
	 * Ingredients as AE itemstacks.
	 */
	protected IAEItemStack[] ingredientsAE = null;

	/**
	 * List of all output items, results + containers
	 */
	protected IAEItemStack[] allResults = null;

	/**
	 * True if the recipe is valid.
	 */
	protected boolean isValid = false;

	/**
	 *
	 * @param direCore
	 * @param craftingResult
	 * @param craftingIngredients
	 */
	public DireCraftingPattern(final ItemStack direCore, final ItemStack craftingResult,
			final Object[] craftingIngredients) {
		// Set the core
		this.direCoreHost = direCore;

		// Set the result
		this.result = AEApi.instance().storage().createItemStack(craftingResult);

		// Set the ingredients
		boolean hasAtLeastOneValidInput = false;
		for (int index = 0; ((index < this.ingredients.length) && (index < craftingIngredients.length)); ++index) {
			// Get the next ingredient
			Object nextIng = craftingIngredients[index];

			// Add to the ingredients if not null
			if (nextIng != null) {
				this.ingredients[index] = nextIng;
				hasAtLeastOneValidInput = true;
			}
		}

		// Set validity
		this.setPatternValidity((this.result != null) && hasAtLeastOneValidInput);
	}

	/**
	 *
	 * @param direCore
	 * @param data
	 */
	public DireCraftingPattern(final ItemStack direCore, final NBTTagCompound data) {
		// Set the host
		this.direCoreHost = direCore;

		// Read the data
		this.readFromNBT(data);
	}

	/**
	 * Checks if one item can be substituted for another.
	 *
	 * @param target
	 * @param input
	 * @return
	 */
	private static boolean canSubItem(final ItemStack target, final ItemStack input) {
		// Do the itemstacks directly match?
		if (ItemStack.areItemStacksEqual(target, input)) {
			return true;
		}

		if (target.getItem() != input.getItem()) {
			// Mismatched items
			return false;
		}
		// Items match

		// Does the item damage mean something besides damaged?
		if (!target.isItemStackDamageable()) {
			return ((target.getItemDamage() == OreDictionary.WILDCARD_VALUE)
					|| (target.getItemDamage() == input.getItemDamage()));
		}

		// Items match, has no tag or tags match, and damage is damage
		return true;
	}

	/**
	 * Checks if the input item can be substituted for the target
	 *
	 * @param target
	 * @param input
	 * @return
	 */
	public static boolean canSubstitueFor(final Object target, final ItemStack input) {
		// What type is the ingredient?
		if (target instanceof ItemStack) {
			// Cast to itemstack
			return DireCraftingPattern.canSubItem((ItemStack) target, input);
		} else if (target instanceof ArrayList) {
			// Cast to list
			@SuppressWarnings("unchecked")
			ArrayList<ItemStack> items = (ArrayList<ItemStack>) target;

			// Check each item
			for (ItemStack item : items) {
				if (DireCraftingPattern.canSubItem(item, input)) {
					return true;
				}
			}

		}

		// Unknown type
		return false;
	}

	/**
	 * Converts the itemlist into it's AE counterpart
	 */
	private void setupAEIngredientList() {
		this.ingredientsAE = new AEItemStack[DireCraftingPattern.GRID_SIZE];

		for (int index = 0; index < DireCraftingPattern.GRID_SIZE; index++) {
			// Get the ingredient
			Object ing = this.ingredients[index];

			// Check for null
			if (ing == null) {
				this.ingredientsAE[index] = null;
				continue;
			}

			// Itemstack?
			if (ing instanceof ItemStack) {
				this.ingredientsAE[index] = AEApi.instance().storage().createItemStack((ItemStack) ing);
			}
			// Array
			else if (ing instanceof ArrayList) {
				// Use the first ingredient
				@SuppressWarnings("unchecked")
				ItemStack firstIng = ((ArrayList<ItemStack>) ing).get(0);

				this.ingredientsAE[index] = AEApi.instance().storage().createItemStack(firstIng);
			}
		}
	}

	/**
	 * Sets up all results, including container items.
	 */
	private void setupAEResults() {
		// Ensure the ingredient list is made
		if (this.ingredientsAE == null) {
			this.setupAEIngredientList();
		}

		ArrayList<IAEItemStack> results = new ArrayList<IAEItemStack>();

		// Add any container items
		for (IAEItemStack stack : this.ingredientsAE) {
			if (stack == null) {
				continue;
			}

			// Container?
			if (stack.getItem().hasContainerItem(stack.getItemStack())) {
				results.add(AEApi.instance().storage()
						.createItemStack(stack.getItem().getContainerItem(stack.getItemStack())));
			}

			// Multiplier?
			else if (stack.getStackSize() > 1) {
				results.add(stack.copy().setStackSize(stack.getStackSize() - 1));
			}

		}

		// Add result
		results.add(this.result);

		// Set the outputs
		this.allResults = results.toArray(new IAEItemStack[results.size()]);
	}

	protected void setPatternValidity(final boolean valid) {
		// Set
		this.isValid = valid;

		if (!valid) {
			// Clear all items.
			this.result = null;
			this.ingredientsAE = null;
			for (int index = 0; index < DireCraftingPattern.GRID_SIZE; ++index) {
				this.ingredients[index] = null;
			}
		}
	}

	@Override
	public boolean canSubstitute() {
		return true;
	}

	/**
	 * This includes container items.
	 *
	 * @return
	 */
	public IAEItemStack[] getAllResults() {
		if (this.allResults == null) {
			this.setupAEResults();
		}
		return this.allResults;
	}

	@Override
	public IAEItemStack[] getCondensedInputs() {
		// Create a temp array
		ArrayList<IAEItemStack> cond = new ArrayList<IAEItemStack>();

		if (this.ingredientsAE == null) {
			this.setupAEIngredientList();
		}

		// Add non-null ingredients
		for (int index = 0; index < this.ingredientsAE.length; index++) {
			if (this.ingredientsAE[index] != null) {
				cond.add(this.ingredientsAE[index]);
			}
		}

		// Return the ingredients
		return cond.toArray(new IAEItemStack[cond.size()]);
	}

	@Override
	public IAEItemStack[] getCondensedOutputs() {
		// Is the result null?
		if (this.result == null) {
			// Return empty array
			return new IAEItemStack[0];
		}

		return new IAEItemStack[] { this.result };
	}

	@Override
	public IAEItemStack[] getInputs() {
		if (this.ingredientsAE == null) {
			this.setupAEIngredientList();
		}

		return this.ingredientsAE;
	}

	@Override
	public ItemStack getOutput(final InventoryCrafting craftingInv, final World world) {
		return this.result.getItemStack();
	}

	@Override
	public IAEItemStack[] getOutputs() {
		return new IAEItemStack[] { this.result };
	}

	@Override
	public ItemStack getPattern() {
		return this.direCoreHost;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	/**
	 * Returns the result of the craft.
	 *
	 * @return
	 */
	public IAEItemStack getResult() {
		return this.result;
	}

	@Override
	public boolean isCraftable() {
		// Returning false prevents substitutions.
		return true;
	}

	/**
	 * True if the recipe is valid.
	 *
	 * @return
	 */
	public boolean isPatternValid() {
		return this.isValid;
	}

	@Override
	public boolean isValidItemForSlot(final int slotIndex, final ItemStack input, final World world) {
		// Get the ingredient
		Object ing = this.ingredients[slotIndex];

		// Ensure nothing is null
		if ((ing == null) || (input == null) || (input.getItem() == null)) {
			// Null detected
			return false;
		}

		return DireCraftingPattern.canSubstitueFor(ing, input);

	}

	public void readFromNBT(final NBTTagCompound data) {
		// Sanity check.
		if (data == null) {
			return;
		}

		// Assume the recipe is valid
		this.setPatternValidity(true);

		// Read ingredients
		for (int slotNumber = 0; (this.isValid && (slotNumber < DireCraftingPattern.GRID_SIZE)); slotNumber++) {
			// Is there an ingredient for this slot?
			if (!data.hasKey(DireCraftingPattern.NBTKEY_INGREDIENT_NUM + slotNumber)) {
				// No ingredient
				continue;
			}

			// Get the tag
			NBTTagCompound ingData = data.getCompoundTag(DireCraftingPattern.NBTKEY_INGREDIENT_NUM + slotNumber);

			// Validate the tag
			if ((ingData == null) || (ingData.hasNoTags())) {
				// Invalid tag
				this.setPatternValidity(false);
				return;
			}

			// Does the tag have a type?
			if (!ingData.hasKey(DireCraftingPattern.NBTKEY_INGREDIENT_TYPE)) {
				// Old AE tag
				try {
					// Load the AE stack
					IAEItemStack ingAE = AEItemStack.loadItemStackFromNBT(ingData);

					// Null check
					if (ingAE == null) {
						this.setPatternValidity(false);
						return;
					}

					// Get the item stack
					ItemStack ing = ingAE.getItemStack();

					// Null check
					if (ing == null) {
						this.setPatternValidity(false);
						return;
					}

					// Set the stack
					this.ingredients[slotNumber] = ing;
				} catch (Exception e) {
					// Recipe is invalid.
					this.setPatternValidity(false);
					return;
				}
				continue;
			}

			// What type is the tag?
			int type = ingData.getInteger(DireCraftingPattern.NBTKEY_INGREDIENT_TYPE);

			// Itemstack
			if (type == DireCraftingPattern.NBTKEY_ITEMTYPE) {
				try {
					// Get the item
					ItemStack ing = ItemStack.loadItemStackFromNBT(ingData);

					// Null check
					if (ing == null) {
						this.setPatternValidity(false);
						return;
					}

					// Set the stack
					this.ingredients[slotNumber] = ing;
				} catch (Exception e) {
					this.setPatternValidity(false);
					return;
				}
			}
			// Array
			else if (type == DireCraftingPattern.NBTKEY_ARRAYTYPE) {
				// Create the list
				ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();

				// Has count?
				if (!ingData.hasKey(DireCraftingPattern.NBTKEY_ARRAY_SIZE)) {
					this.setPatternValidity(false);
					return;
				}

				// Get the count
				int count = ingData.getInteger(DireCraftingPattern.NBTKEY_ARRAY_SIZE);

				for (int arrayIndex = 0; arrayIndex < count; ++arrayIndex) {
					// Get the subtag
					NBTTagCompound subTag = ingData
							.getCompoundTag(DireCraftingPattern.NBTKEY_INGREDIENT_NUM + arrayIndex);

					if ((subTag == null) || (subTag.hasNoTags())) {
						// Ignore invalid entry
						continue;
					}

					try {
						// Load the stack
						ItemStack stack = ItemStack.loadItemStackFromNBT(subTag);
						if (stack != null) {
							itemList.add(stack);
						}
					} catch (Exception e) {
						// Ignore invalid items
					}
				}

				// Were any items loaded?
				if (itemList.size() == 0) {
					this.setPatternValidity(false);
					return;
				}

				// Set the ingredient to the list
				this.ingredients[slotNumber] = itemList;
			} else {
				// Unknown type
				this.setPatternValidity(false);
				return;
			}

		}

		// Read the result
		if (data.hasKey(DireCraftingPattern.NBTKEY_RESULT)) {
			// Get the result
			NBTTagCompound resultData = data.getCompoundTag(DireCraftingPattern.NBTKEY_RESULT);

			// Validate
			if ((resultData == null) || (resultData.hasNoTags())) {
				this.setPatternValidity(false);
				return;
			}

			try {
				// Get result item
				this.result = AEItemStack.loadItemStackFromNBT(resultData);

				// Check item
				if (this.result == null) {
					this.setPatternValidity(false);
					return;
				}
			} catch (Exception e) {
				this.setPatternValidity(false);
				return;
			}
		}
	}

	/**
	 * Sets the dire core.
	 *
	 * @param direCore
	 */
	public void setDireCore(final ItemStack direCore) {
		this.direCoreHost = direCore;
	}

	@Override
	public void setPriority(final int priority) {
		// Ignored.
	}

	/**
	 * Write's the pattern to the NBT tag.
	 *
	 * @param data
	 * @return
	 */
	public NBTTagCompound writeToNBT(final NBTTagCompound data) {

		// Write the ingredients
		for (int index = 0; index < DireCraftingPattern.GRID_SIZE; index++) {
			// Ensure it is not null
			if (this.ingredients[index] == null) {
				continue;
			}

			// Get the ingredient
			Object ing = this.ingredients[index];

			// Create the tag
			NBTTagCompound ingData = new NBTTagCompound();

			// What type is the ingredient?
			if (ing instanceof IAEItemStack) {
				// Write the ingredient data
				((IAEItemStack) ing).getItemStack().writeToNBT(ingData);
				ingData.setInteger(DireCraftingPattern.NBTKEY_INGREDIENT_TYPE, DireCraftingPattern.NBTKEY_ITEMTYPE);
			} else if (ing instanceof ItemStack) {
				((ItemStack) ing).writeToNBT(ingData);
				ingData.setInteger(DireCraftingPattern.NBTKEY_INGREDIENT_TYPE, DireCraftingPattern.NBTKEY_ITEMTYPE);
			} else if (ing instanceof ArrayList) {
				// Cast to array list
				@SuppressWarnings("unchecked")
				ArrayList<ItemStack> ingList = (ArrayList<ItemStack>) ing;

				// Set type and count
				ingData.setInteger(DireCraftingPattern.NBTKEY_INGREDIENT_TYPE,
						DireCraftingPattern.NBTKEY_ARRAYTYPE);
				ingData.setInteger(DireCraftingPattern.NBTKEY_ARRAY_SIZE, ingList.size());

				// Add each item
				for (int i = 0; i < ingList.size(); ++i) {
					// Create a new tag
					NBTTagCompound subTag = new NBTTagCompound();

					// Write the item
					ingList.get(i).writeToNBT(subTag);

					// Add the sub tag
					ingData.setTag(DireCraftingPattern.NBTKEY_INGREDIENT_NUM + i, subTag);
				}

			}

			// Write into the main data tag
			if (!ingData.hasNoTags()) {
				data.setTag(DireCraftingPattern.NBTKEY_INGREDIENT_NUM + index, ingData);
			}

		}

		// Write the result
		NBTTagCompound outData = new NBTTagCompound();
		this.result.writeToNBT(outData);
		data.setTag(DireCraftingPattern.NBTKEY_RESULT, outData);

		return data;
	}
}
