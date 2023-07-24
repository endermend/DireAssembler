package EnderMod.net.endermod.mod.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class tabEnderMod extends CreativeTabs {

    private static tabEnderMod instance = new tabEnderMod();

    public static tabEnderMod getInstance() {
        return instance;
    }

    private tabEnderMod() {
        super("EnderMod");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return Item.getItemFromBlock(Blocks.anvil);
    }
}
