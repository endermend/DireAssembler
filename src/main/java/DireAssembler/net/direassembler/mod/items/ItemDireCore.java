package DireAssembler.net.direassembler.mod.items;

import java.util.List;

import javax.xml.soap.Text;

import com.myname.mymodid.Tags;

import DireAssembler.net.direassembler.mod.MyMod;
import DireAssembler.net.direassembler.mod.common.DireStrings;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDireCore extends Item{
	public ItemDireCore()
	{
		// Can not be damaged
		this.setMaxDamage( 0 );

		// Has no subtypes
		this.setHasSubtypes( false );

		// Can not stack
		this.setMaxStackSize( 1 );
		
		setCreativeTab(MyMod.tab);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(final ItemStack kCore, final EntityPlayer player, final List tooltip,
			final boolean advancedItemTooltips) {
	}

	@Override
	public String getUnlocalizedName() {
		return DireStrings.Item_DireCore.getUnlocalized();
	}

	@Override
	public String getUnlocalizedName(final ItemStack itemStack) {
		return DireStrings.Item_DireCore.getUnlocalized();
	}

	/**
	 * Registers and sets the core icon
	 */
	@Override
	public void registerIcons(final IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(Tags.MODID + ":dire.core");
	}
}
