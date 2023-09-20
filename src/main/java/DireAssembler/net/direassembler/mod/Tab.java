package DireAssembler.net.direassembler.mod;

import DireAssembler.net.direassembler.mod.common.DireAll;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class Tab extends CreativeTabs {
	private static Tab instance = new Tab();
	public static Tab getInstance() {
		return instance;
	}

	private Tab() {
		super("DireAssembler");
		
	}


	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return DireAll.instance().blocks().DireAssembler.getItem();
	}
}
