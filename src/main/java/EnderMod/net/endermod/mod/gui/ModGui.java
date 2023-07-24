package EnderMod.net.endermod.mod.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import EnderMod.net.endermod.mod.tile.TileEntityIchorCollector;
import cpw.mods.fml.common.network.IGuiHandler;

public class ModGui implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            return new ContainerIchor(player.inventory, (TileEntityIchorCollector) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            return new GUIIchor(player.inventory, (TileEntityIchorCollector) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
