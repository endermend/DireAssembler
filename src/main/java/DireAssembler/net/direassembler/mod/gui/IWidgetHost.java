package DireAssembler.net.direassembler.mod.gui;

import net.minecraft.client.gui.FontRenderer;

public interface IWidgetHost {
	/**
	 * Gets the font renderer for the GUI.
	 *
	 * @return
	 */
	FontRenderer getFontRenderer();

	/**
	 * Return the left of the GUI.
	 *
	 * @return
	 */
	int guiLeft();

	/**
	 * Return the top of the GUI.
	 *
	 * @return
	 */
	int guiTop();
}
