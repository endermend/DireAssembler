package EnderMod.net.endermod.mod.items;

import net.minecraft.item.Item;

import EnderMod.net.endermod.mod.MyMod;

public class IchorShard extends Item {

    public IchorShard() {
        setUnlocalizedName("IchorShard");
        setTextureName(MyMod.MOD_ID + ":" + getUnlocalizedName().substring(5));
        setMaxStackSize(64);
        setCreativeTab(MyMod.tab);
    }
}
