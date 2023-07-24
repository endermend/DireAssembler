package EnderMod.net.endermod.mod.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import EnderMod.net.endermod.mod.MyMod;
import EnderMod.net.endermod.mod.tile.TileEntityIchorCollector;

public class GUIIchor extends GuiContainer {

    public static final ResourceLocation texture = new ResourceLocation(
        MyMod.MOD_ID,
        "textures/gui/ichor_collector_gui.png");
    private TileEntityIchorCollector tile;

    public GUIIchor(InventoryPlayer player_inventory, TileEntityIchorCollector tile) {
        super(new ContainerIchor(player_inventory, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = StatCollector.translateToLocal("container.ichor_collector");
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 0xcdab4b);
        this.fontRendererObj.drawString(
            StatCollector.translateToLocal("container.timeToCreateIchorShard") + ": "
                + tile.timeToCreateShard() / 1200
                + ":"
                + (tile.timeToCreateShard() / 20) % 60,
            50,
            20,
            0xcdab4b);
        this.fontRendererObj
            .drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96, 0xcdab4b);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    }
}
