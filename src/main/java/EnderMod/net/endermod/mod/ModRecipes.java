package EnderMod.net.endermod.mod;

import net.minecraft.item.ItemStack;

import EnderMod.net.endermod.mod.items.ModItems;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModRecipes {

    public static void registerRecipes() {

        GameRegistry.addShapedRecipe(
            new ItemStack(GameRegistry.findItem("ThaumicTinkerer", "kamiResource")),
            "III",
            "III",
            "III",
            'I',
            ModItems.ICHOR_SHARD);

    }

}
