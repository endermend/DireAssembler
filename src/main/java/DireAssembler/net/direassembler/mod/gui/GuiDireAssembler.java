package DireAssembler.net.direassembler.mod.gui;

import org.lwjgl.opengl.GL11;

import DireAssembler.net.direassembler.mod.common.DireStrings;
import DireAssembler.net.direassembler.mod.containers.ContainerDireAssembler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class GuiDireAssembler extends DireBaseGui {
	/**
	 * Gui size.
	 */
	private static final int FULL_GUI_WIDTH = 247, MAIN_GUI_WIDTH = 176, UPGRADE_GUI_WIDTH = 234 - MAIN_GUI_WIDTH,
			GUI_HEIGHT = 197, UPGRADE_GUI_HEIGHT = 104;

	/**
	 * Title position.
	 */
	private static final int TITLE_POS_X = 6, TITLE_POS_Y = 6;

	private String title;

	private boolean hasNetworkTool = false;

	public GuiDireAssembler(final EntityPlayer player, final World world, final int X, final int Y, final int Z) {
		// Call super
		super(new ContainerDireAssembler(player, world, X, Y, Z));

		this.hasNetworkTool = ((ContainerDireAssembler) this.inventorySlots).hasNetworkTool();

		// Set the GUI size
		this.xSize = (this.hasNetworkTool ? GuiDireAssembler.FULL_GUI_WIDTH
				: GuiDireAssembler.MAIN_GUI_WIDTH + GuiDireAssembler.UPGRADE_GUI_WIDTH);
		this.ySize = GuiDireAssembler.GUI_HEIGHT;

		// Set the title
		this.title = DireStrings.Title_DireAssembler.getLocalized();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(final float alpha, final int mouseX, final int mouseY) {
		// Full white
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Bind the workbench gui texture
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiTextureManager.DIRE_ASSEMBLER.getTexture());

		// Draw the gui texture
		if (this.hasNetworkTool) {
			// Draw the full gui
			this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		} else {
			// Draw main body
			this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, GuiDireAssembler.MAIN_GUI_WIDTH, this.ySize);

			// Draw upgrades
			this.drawTexturedModalRect(this.guiLeft + GuiDireAssembler.MAIN_GUI_WIDTH, this.guiTop,
					GuiDireAssembler.MAIN_GUI_WIDTH, 0, GuiDireAssembler.UPGRADE_GUI_WIDTH,
					GuiDireAssembler.UPGRADE_GUI_HEIGHT);
		}
		// Call super
		super.drawAEToolAndUpgradeSlots(alpha, mouseX, mouseY);
	}

	/**
	 * Draw the foreground
	 */
	@Override
	public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		// Draw the title
		this.fontRendererObj.drawString(this.title, GuiDireAssembler.TITLE_POS_X, GuiDireAssembler.TITLE_POS_Y, 0);
	}
}
