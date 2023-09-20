package DireAssembler.net.direassembler.mod.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDireAssembler extends ItemBlock {
	public ItemBlockDireAssembler(final Block block) {
		super(block);
	}

	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List displayList,
			final boolean advancedItemTooltips) {
		// Ensure the stack has a tag
		if (!stack.hasTagCompound()) {
			return;
		}
	}
}
