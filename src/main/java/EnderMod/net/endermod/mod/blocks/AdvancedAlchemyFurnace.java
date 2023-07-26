package EnderMod.net.endermod.mod.blocks;

import EnderMod.net.endermod.mod.MyMod;
import EnderMod.net.endermod.mod.tile.TileAdvancedAlchemyFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AdvancedAlchemyFurnace extends BetterAlchemyFurnace{
	public AdvancedAlchemyFurnace() {
		super();
		setBlockName("AdvancedAlchemyFurnace");
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		this.iconFurnace[0] = ir.registerIcon(MyMod.MOD_ID + ":advanced_al_furnace_side");
		this.iconFurnace[1] = ir.registerIcon(MyMod.MOD_ID + ":advanced_al_furnace_top");
		this.iconFurnace[2] = ir.registerIcon(MyMod.MOD_ID + ":advanced_al_furnace_front_off");
		this.iconFurnace[3] = ir.registerIcon(MyMod.MOD_ID + ":advanced_al_furnace_front_on");
		this.iconFurnace[4] = ir.registerIcon(MyMod.MOD_ID + ":advanced_al_furnace_top_filled");
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileAdvancedAlchemyFurnace();
	}
}
