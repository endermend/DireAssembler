package EnderMod.net.endermod.mod.tile;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModTiles {

    public static void register() {
        GameRegistry.registerTileEntity(TileEntityIchorCollector.class, "container.ichor");
        GameRegistry.registerTileEntity(TileBetterAlchemyFurnace.class, "container.betteralchemyfurnace");
    }
}
