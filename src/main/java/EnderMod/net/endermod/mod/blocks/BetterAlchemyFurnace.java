package EnderMod.net.endermod.mod.blocks;

import EnderMod.net.endermod.mod.MyMod;
import EnderMod.net.endermod.mod.tile.TileBetterAlchemyFurnace;
import EnderMod.net.endermod.mod.tile.TileEntityIchorCollector;
import EnderMod.net.endermod.mod.tile.TileImprovedAlchemyFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BetterAlchemyFurnace extends BlockContainer {
	public IIcon[] iconFurnace;

	public BetterAlchemyFurnace() {
		super(Material.rock);
		this.iconFurnace = new IIcon[5];
		setHardness(3.0F);
		setResistance(25.0F);
		setBlockName("BetterAlchemyFurnace");
		setStepSound(Block.soundTypeStone);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		setCreativeTab(MyMod.tab);
	}

	@SideOnly(Side.CLIENT)
	public abstract void registerBlockIcons(IIconRegister ir);

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
		if (side == 0) {
			return this.iconFurnace[0];
		}
		TileBetterAlchemyFurnace machine = (TileBetterAlchemyFurnace) worldIn.getTileEntity(x, y, z);
		if (machine != null) {
			if (side == 1) {
				return this.iconFurnace[machine.isEmpty() ? 1 : 4];
			}
			return this.iconFurnace[machine.isBurning() ? 3 : 2];
		}
		if (side == 1) {
			return this.iconFurnace[1];
		}
		return this.iconFurnace[2];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return this.iconFurnace[0];
		}
		if (side == 1) {
			return this.iconFurnace[(metadata == 2 || metadata == 3) ? 4 : 1];
		}	
		return this.iconFurnace[(metadata == 1 || metadata == 3) ? 3 : 2];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer activator, int side, float hitX,
			float hitY, float hitZ) {
		if (!world.isRemote) {
			openGUI(activator,MyMod.instance, world, x, y, z);
		}
		return true;

	}
	
	protected void openGUI(EntityPlayer activator,Object mod, World world, int x, int y, int z) {
		activator.openGui(MyMod.instance, 1, world, x, y, z);
	}
}
