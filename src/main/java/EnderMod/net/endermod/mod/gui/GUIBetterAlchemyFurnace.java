package EnderMod.net.endermod.mod.gui;

import EnderMod.net.endermod.mod.MyMod;
import EnderMod.net.endermod.mod.tile.TileBetterAlchemyFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.gui.GuiAlchemyFurnace;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.tiles.TileAlchemyFurnace;
import org.lwjgl.opengl.GL11;

public class GUIBetterAlchemyFurnace extends GuiContainer {
	public static final ResourceLocation texture = new ResourceLocation(MyMod.MOD_ID,
			"textures/gui/better_alchemy_furnace_gui.png");
	private TileBetterAlchemyFurnace furnaceInventory;

	public GUIBetterAlchemyFurnace(InventoryPlayer player, TileBetterAlchemyFurnace tileEntityFurnace) {
		super(new ContainerBetterAlchemyFurnace(player, tileEntityFurnace));
		this.furnaceInventory = tileEntityFurnace;
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
	}

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		UtilsFX.bindTexture("textures/gui/better_alchemy_furnace_gui.png");
		this.mc.getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		GL11.glEnable(3042);
		drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		if (this.furnaceInventory.isBurning()) {
			int i = this.furnaceInventory.getBurnTimeRemainingScaled(20);
			drawTexturedModalRect(k + 80, l + 26 + 20 - i, 176, 20 - i, 16, i);
		}
		int i1 = this.furnaceInventory.getCookProgressScaled(46);
		drawTexturedModalRect(k + 106, l + 13 + 46 - i1, 216, 46 - i1, 9, i1);
		i1 = this.furnaceInventory.getContentsScaled(48);
		drawTexturedModalRect(k + 61, l + 12 + 48 - i1, 200, 48 - i1, 8, i1);
		drawTexturedModalRect(k + 60, l + 8, 232, 0, 10, 55);
		GL11.glDisable(3042);
	}

}
