package DireAssembler.net.direassembler.mod.gui.buttons;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.text.WordUtils;
import DireAssembler.net.direassembler.mod.gui.IStateIconTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;

public abstract class DireStateButton extends DireGuiButtonBase {
	private IStateIconTexture backgroundIcon;

	/**
	 * Icon to draw on the button
	 */
	protected IStateIconTexture stateIcon;

	/**
	 * Offset from the top-left corner of the button to draw the icon.
	 */
	public int iconXOffset = 0, iconYOffset = 0;

	/**
	 *
	 * @param ID
	 * @param xPosition
	 * @param yPosition
	 * @param buttonWidth
	 * @param buttonHeight
	 * @param icon
	 * @param iconXOffset
	 * @param iconYOffset
	 * @param backgroundIcon
	 */
	public DireStateButton(final int ID, final int xPosition, final int yPosition, final int buttonWidth,
			final int buttonHeight, final IStateIconTexture icon, final int iconXOffset, final int iconYOffset,
			final IStateIconTexture backgroundIcon) {
		// Call super
		super(ID, xPosition, yPosition, buttonWidth, buttonHeight, "");

		// Set the icon
		this.stateIcon = icon;

		// Set the offsets
		this.iconXOffset = iconXOffset;
		this.iconYOffset = iconYOffset;

		// Set background
		this.backgroundIcon = backgroundIcon;
	}

	/**
	 * Draws a textured rectangle at the stored z-value, the texture will be scaled
	 * to fit within the width and height
	 */
	private void drawScaledTexturedModalRect(final int xPosition, final int yPosition, final int u, final int v,
			final int width, final int height, final int textureWidth, final int textureHeight) {
		// No idea what this is
		float magic_number = 0.00390625F;

		// Calculate the UV's
		float minU = u * magic_number;
		float maxU = (u + textureWidth) * magic_number;
		float minV = v * magic_number;
		float maxV = (v + textureHeight) * magic_number;

		// Get the tessellator
		Tessellator tessellator = Tessellator.instance;

		// Start drawing
		tessellator.startDrawingQuads();

		// Top left corner
		tessellator.addVertexWithUV(xPosition, yPosition + height, this.zLevel, minU, maxV);

		// Top right corner
		tessellator.addVertexWithUV(xPosition + width, yPosition + height, this.zLevel, maxU, maxV);

		// Bottom right corner
		tessellator.addVertexWithUV(xPosition + width, yPosition, this.zLevel, maxU, minV);

		// Bottom left corner
		tessellator.addVertexWithUV(xPosition, yPosition, this.zLevel, minU, minV);

		// Draw
		tessellator.draw();
	}

	/**
	 * Adds info to the tooltip as a white header, and grey body. The body is broken
	 * down into lines of length 30.
	 *
	 * @param tooltip
	 * @param title
	 * @param text
	 */
	protected void addAboutToTooltip(final List<String> tooltip, final String title, final String text) {
		// Title
		tooltip.add(EnumChatFormatting.WHITE + title);

		// Body
		for (String line : Splitter.on("\n").split(WordUtils.wrap(text, 30, "\n", false))) {
			tooltip.add(EnumChatFormatting.GRAY + line.trim());
		}
	}

	protected void drawIcon(final Minecraft minecraftInstance, final IStateIconTexture icon, final int xPos,
			final int yPos, final int iconWidth, final int iconHeight) {
// Bind the sheet
		minecraftInstance.getTextureManager().bindTexture(icon.getTexture());

// Draw the icon
		this.drawScaledTexturedModalRect(xPos, yPos, icon.getU(), icon.getV(), iconWidth, iconHeight, icon.getWidth(),
				icon.getHeight());
	}

	@Override
	public void drawButton(final Minecraft minecraftInstance, final int x, final int y) {
		// Full white
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (this.backgroundIcon != null) {
			// Draw the background
			this.drawIcon(minecraftInstance, this.backgroundIcon, this.xPosition, this.yPosition, this.width,
					this.height);
		}

		if (this.stateIcon != null) {
			// Draw the overlay icon
			this.drawIcon(minecraftInstance, this.stateIcon, this.xPosition + this.iconXOffset,
					this.yPosition + this.iconYOffset, this.width, this.height);
		}

	}
}
