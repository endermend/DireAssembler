package DireAssembler.net.direassembler.mod.gui.buttons;

import java.util.List;

import DireAssembler.net.direassembler.mod.gui.AEStateIconsEnum;
import appeng.core.localization.ButtonToolTips;
import net.minecraft.util.EnumChatFormatting;

public class GuiButtonClearCraftingGrid extends DireStateButton{
	/**
	 * Clear grid icon
	 */
	private static final AEStateIconsEnum CLEAR_ICON = AEStateIconsEnum.CLEAR_GRID;

	private final boolean showTooltip;

	/**
	 * Creates the button
	 *
	 * @param ID
	 * @param xPosition
	 * @param yPosition
	 * @param width
	 * @param height
	 */
	public GuiButtonClearCraftingGrid(final int ID, final int xPosition, final int yPosition, final int width,
			final int height, final boolean showTooltip) {
		// Call super
		super(ID, xPosition, yPosition, width, height, GuiButtonClearCraftingGrid.CLEAR_ICON, 0, 0,
				AEStateIconsEnum.REGULAR_BUTTON);

		// Set if the tooltip is shown or not
		this.showTooltip = showTooltip;
	}

	@Override
	public void getTooltip(final List<String> tooltip) {
		if (this.showTooltip) {
			this.addAboutToTooltip(tooltip, ButtonToolTips.Stash.getLocal(),
					EnumChatFormatting.GRAY.toString() + ButtonToolTips.StashDesc.getLocal());
		}
	}
}
