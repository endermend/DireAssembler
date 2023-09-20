package DireAssembler.net.direassembler.mod.gui;

import net.minecraft.util.ResourceLocation;

public interface IStateIconTexture {
	/**
	 * Height of the icon.
	 *
	 * @return
	 */
	int getHeight();

	/**
	 * Texture the icon is in.
	 *
	 * @return
	 */
	ResourceLocation getTexture();

	/**
	 * U coordinate of the icon.
	 *
	 * @return
	 */
	int getU();

	/**
	 * V coordinate of the icon.
	 *
	 * @return
	 */
	int getV();

	/**
	 * Width of the icon.
	 *
	 * @return
	 */
	int getWidth();
}
