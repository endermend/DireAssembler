package DireAssembler.net.direassembler.mod.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IThEItemDescription {

	/**
	 * Gets the block of this item, if it has one.
	 *
	 * @return
	 */
	Block getBlock();

	/**
	 * Gets the damage, or meta, value of the item.
	 *
	 * @return
	 */
	int getDamage();

	/**
	 * Gets the item.
	 *
	 * @return
	 */
	Item getItem();

	/**
	 * Gets a stack of size 1.
	 *
	 * @return
	 */
	ItemStack getStack();

	/**
	 * Gets a stack of the specified size.
	 *
	 * @param amount
	 * @return
	 */
	ItemStack getStacks(int amount);
}