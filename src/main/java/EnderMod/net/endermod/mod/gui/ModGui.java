package EnderMod.net.endermod.mod.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import EnderMod.net.endermod.mod.tile.TileAdminiumAlchemyFurnace;
import EnderMod.net.endermod.mod.tile.TileAdvancedAlchemyFurnace;
import EnderMod.net.endermod.mod.tile.TileBetterAlchemyFurnace;
import EnderMod.net.endermod.mod.tile.TileEntityIchorCollector;
import EnderMod.net.endermod.mod.tile.TileImprovedAlchemyFurnace;
import cpw.mods.fml.common.network.IGuiHandler;

public class ModGui implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            return new ContainerIchor(player.inventory, (TileEntityIchorCollector) world.getTileEntity(x, y, z));
        }
        if (ID == 1) {
            return new ContainerBetterAlchemyFurnace(player.inventory, (TileBetterAlchemyFurnace) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            return new GUIIchor(player.inventory, (TileEntityIchorCollector) world.getTileEntity(x, y, z));
        }
        if (ID == 1) {
        	TileEntity tile = world.getTileEntity(x, y, z);
        	if(tile instanceof TileBetterAlchemyFurnace) {
        		if(tile instanceof TileImprovedAlchemyFurnace) {
        			return new GUIBetterAlchemyFurnace(player.inventory, (TileBetterAlchemyFurnace) tile, "textures/gui/improved_alchemy_furnace_gui.png");
        		}
        		if(tile instanceof TileAdvancedAlchemyFurnace) {
        			return new GUIBetterAlchemyFurnace(player.inventory, (TileBetterAlchemyFurnace) tile, "textures/gui/advanced_alchemy_furnace_gui.png");
        		}
        		if(tile instanceof TileAdminiumAlchemyFurnace) {
        			return new GUIBetterAlchemyFurnace(player.inventory, (TileBetterAlchemyFurnace) tile, "textures/gui/adminium_alchemy_furnace_gui.png");
        		}
        		return new GUIBetterAlchemyFurnace(player.inventory, (TileBetterAlchemyFurnace) tile, "");
        	}
            
        }
        return null;
    }
}
