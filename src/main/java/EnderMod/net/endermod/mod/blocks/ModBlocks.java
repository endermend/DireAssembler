package EnderMod.net.endermod.mod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static final IchorCollector ICHOR_COLLECTOR = new IchorCollector();
    
    public static final BetterAlchemyFurnace IMPROVED_ALCHEMY_FURNACE = new ImprovedAlchemyFurnace();
    public static final BetterAlchemyFurnace ADVANCED_ALCHEMY_FURNACE = new AdvancedAlchemyFurnace();
    public static final BetterAlchemyFurnace ADMINIUM_ALCHEMY_FURNACE = new AdminiumAlchemyFurnace();

    public static void register() {
        GameRegistry.registerBlock(ICHOR_COLLECTOR, "IchorCollector");
        GameRegistry.registerBlock(IMPROVED_ALCHEMY_FURNACE, "ImprovedAlchemyFurnace");
        GameRegistry.registerBlock(ADVANCED_ALCHEMY_FURNACE, "AdvancedAlchemyFurnace");
        GameRegistry.registerBlock(ADMINIUM_ALCHEMY_FURNACE, "AdminiumAlchemyFurnace");
    }
}
