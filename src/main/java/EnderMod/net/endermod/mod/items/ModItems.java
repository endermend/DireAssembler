package EnderMod.net.endermod.mod.items;

import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {

    public static final Item ICHOR_SHARD = new IchorShard();

    public static void register() {
        GameRegistry.registerItem(ICHOR_SHARD, "IchorShard");
    }
}
