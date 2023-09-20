package DireAssembler.net.direassembler.mod.gui;

import DireAssembler.net.direassembler.mod.MyMod;
import DireAssembler.net.direassembler.mod.common.DireLog;
import DireAssembler.net.direassembler.mod.containers.ContainerDireInscriber;
import fox.spiteful.avaritia.crafting.ExtremeCraftingManager;
import fox.spiteful.avaritia.tile.TileEntityDireCrafting;
import fox.spiteful.avaritia.tile.inventory.InventoryDireCrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class DireRecipeHelper {
	/**
	 * Singleton.
	 */
	public static final DireRecipeHelper INSTANCE = new DireRecipeHelper();

	public DireRecipeHelper() {
	}

	/**
	 * Creates a workbench with its crafting grid set to match the source inventory.
	 *
	 * @param sourceInventory
	 * @param firstSlotIndex
	 * @param gridSize
	 * @return
	 */
	private TileEntityDireCrafting createBridgeInventory(final IInventory sourceInventory, final int firstSlotIndex) {
		// Create a new workbench tile
		TileEntityDireCrafting workbenchTile = new TileEntityDireCrafting();

		// Load the workbench inventory
		for (int slotIndex = 0; slotIndex < ContainerDireInscriber.CRAFTING_GRID_SIZE; slotIndex++) {
			// Set the slot
			workbenchTile.func_70299_a(slotIndex, sourceInventory.getStackInSlot(slotIndex + firstSlotIndex));
		}

		return workbenchTile;
	}

	public IRecipe findMatchingExtremeRecipe(final IInventory sourceInventory, World worldIn, int firstSlotIndex) {
		TileEntityDireCrafting workbenchTile = this.createBridgeInventory(sourceInventory, firstSlotIndex);
		InventoryDireCrafting craftMatrix = new InventoryDireCrafting(null, workbenchTile);
		for (Object recipe : ExtremeCraftingManager.getInstance().getRecipeList()) {
			IRecipe irecipe = (IRecipe) recipe;
			if (irecipe.matches(craftMatrix, worldIn)) {
				//DireLog.info("It can craft" + irecipe.getRecipeOutput().getDisplayName());
				return irecipe;
			}

			//DireLog.info("It can craft nothing");
		}

		return null;
	}

	public ItemStack getRecipeOutput(final IInventory sourceInventory, final int firstSlotIndex, final IRecipe recipe) {
		// Ensure the recipe is valid
		if (recipe == null) {
			return null;
		}
		TileEntityDireCrafting workbenchTile = this.createBridgeInventory(sourceInventory, firstSlotIndex);

		InventoryDireCrafting craftMatrix = new InventoryDireCrafting(null, workbenchTile);
		return recipe.getCraftingResult(craftMatrix);
	}

}
