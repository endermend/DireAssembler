package EnderMod.net.endermod.mod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final IchorCollector ICHOR_COLLECTOR = new IchorCollector();
    
    public static final BetterAlchemyFurnace BETTER_ALCHEMY_FURNACE = new BetterAlchemyFurnace();

    public static void register() {
        GameRegistry.registerBlock(ICHOR_COLLECTOR, "IchorCollector");
        GameRegistry.registerBlock(BETTER_ALCHEMY_FURNACE, "BetterAlchemyFurnace");
    }
}
