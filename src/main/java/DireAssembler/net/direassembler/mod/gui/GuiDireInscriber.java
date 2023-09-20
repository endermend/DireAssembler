package DireAssembler.net.direassembler.mod.gui;

import org.lwjgl.opengl.GL11;

import com.myname.mymodid.Tags;

import DireAssembler.net.direassembler.mod.common.DireStrings;
import DireAssembler.net.direassembler.mod.containers.ContainerDireInscriber;
import DireAssembler.net.direassembler.mod.containers.ContainerDireInscriber.CoreSaveState;
import DireAssembler.net.direassembler.mod.gui.buttons.GuiButtonClearCraftingGrid;
import DireAssembler.net.direassembler.mod.gui.buttons.GuiButtonSaveDelete;
import DireAssembler.net.direassembler.mod.packet.Packet_S_DireInscriber;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class GuiDireInscriber extends DireBaseGui{
	/**
	 * Gui size.
	 */
	private static final int GUI_WIDTH = 233, GUI_HEIGHT = 256;

	/**
	 * Save/Delete button position.
	 */
	private static final int BUTTON_SAVE_POS_X = 194, BUTTON_SAVE_POS_Y = 86;

	/**
	 * Clear button position.
	 */
	private static final int BUTTON_CLEAR_POS_X = 196, BUTTON_CLEAR_POS_Y = 62;

	/**
	 * Position of the title string.
	 */
	private static final int TITLE_POS_X = 177, TITLE_POS_Y = 6;

	/**
	 * Player viewing the GUI.
	 */
	private final EntityPlayer player;


	/**
	 * GUI Title
	 */
	private final String title;

	/**
	 * Save / Delete button.
	 */
	private GuiButtonSaveDelete buttonSave;

	/**
	 * Clear grid button.
	 */
	private GuiButtonClearCraftingGrid buttonClear;

	/**
	 * State of the save button.
	 */
	private CoreSaveState saveState = CoreSaveState.Disabled_MissingCore;

	public GuiDireInscriber( final EntityPlayer player, final World world, final int x, final int y, final int z )
	{
		// Call super
		super( new ContainerDireInscriber( player, world, x, y, z ) );

		// Set the player
		this.player = player;

		// Set the GUI size
		this.xSize = GuiDireInscriber.GUI_WIDTH;
		this.ySize = GuiDireInscriber.GUI_HEIGHT;

		// Set title
		this.title = DireStrings.Title_DireInscriber.getLocalized();
	}

	/**
	 * Draw background
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(final float alpha, final int mouseX, final int mouseY) {
		// Calculate the color shifts
		long slowTime = (System.currentTimeMillis() / 100);
		float redBounce = Math.abs(1.0F - (((slowTime % 50) / 50.0F) * 2.0F));
		float greenBounce = 1.0F - redBounce;

		// Shift color
		GL11.glColor4f(redBounce, greenBounce, 1.0F, 1.0F);

		// Bind the research background
		Minecraft.getMinecraft().renderEngine.bindTexture(
				new ResourceLocation(Tags.MODID, "textures/research/Research.Background.png"));

		// Calculate the X position
		int xpos = (int) (slowTime % 10);

		// Draw the background
		this.drawTexturedModalRect(this.guiLeft + 80, this.guiTop + 106, 55 - xpos, 25, 35, 20);

		// Full white
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Bind the workbench gui texture
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiTextureManager.DIRE_INSCRIBER.getTexture());

		// Draw the gui texture
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	/**
	 * Draw the foreground
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		// Draw the title
		this.fontRendererObj.drawString(this.title, GuiDireInscriber.TITLE_POS_X,
				GuiDireInscriber.TITLE_POS_Y, 0);
	}

	@Override
	protected void onButtonClicked(final GuiButton button, final int mouseButton) {
		// Was the clicked button the save button?
		if (button == this.buttonSave) {
			// Send the request to the server
			Packet_S_DireInscriber.sendSaveDelete(this.player);
		} else if (button == this.buttonClear) {
			// Send the request to the server
			Packet_S_DireInscriber.sendClearGrid(this.player);
		}
	}

	@Override
	public void initGui() {
		// Call super
		super.initGui();

		this.buttonList.clear();

		// Create the save/delete button
		this.buttonSave = new GuiButtonSaveDelete(0, this.guiLeft + BUTTON_SAVE_POS_X, this.guiTop + BUTTON_SAVE_POS_Y,
				this.saveState);
		this.buttonList.add(this.buttonSave);

		// Create the clear grid button
		this.buttonClear = new GuiButtonClearCraftingGrid(1, this.guiLeft + BUTTON_CLEAR_POS_X,
				this.guiTop + BUTTON_CLEAR_POS_Y, 8, 8, false);
		this.buttonList.add(this.buttonClear);

		// Request full update
		//Packet_S_DireInscriber.sendFullUpdateRequest(this.player);
	}

	/**
	 * Called when the server sends a change in the save/load button functionality.
	 *
	 * @param saveState
	 * @param justSaved
	 */
	public void onReceiveSaveState(final CoreSaveState saveState, final boolean justSaved) {
		// Set the state
		this.saveState = saveState;

		// Update the button
		if (this.buttonSave != null) {
			this.buttonSave.setSaveState(saveState);
		}
	}
}
