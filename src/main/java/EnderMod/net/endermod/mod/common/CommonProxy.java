package EnderMod.net.endermod.mod.common;

import EnderMod.net.endermod.mod.ModRecipes;
import EnderMod.net.endermod.mod.MyMod;
import EnderMod.net.endermod.mod.blocks.ModBlocks;
import EnderMod.net.endermod.mod.gui.ModGui;
import EnderMod.net.endermod.mod.items.ModItems;
import EnderMod.net.endermod.mod.tile.ModTiles;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.register();
        ModItems.register();
        ModTiles.register();
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(MyMod.instance, new ModGui());
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModRecipes.registerRecipes();
    }

}
