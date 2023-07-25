package EnderMod.net.endermod.mod.blocks;

import EnderMod.net.endermod.mod.MyMod;
import EnderMod.net.endermod.mod.tile.TileBetterAlchemyFurnace;
import EnderMod.net.endermod.mod.tile.TileEntityIchorCollector;
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

public class BetterAlchemyFurnace extends BlockContainer {
	public IIcon[] iconFurnace;

	public BetterAlchemyFurnace() {
		super(Material.rock);
		this.iconFurnace = new IIcon[5];
		setHardness(3.0F);
		setResistance(25.0F);
		setStepSound(Block.soundTypeStone);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		setCreativeTab(MyMod.tab);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		this.iconFurnace[0] = ir.registerIcon(MyMod.MOD_ID+":better_al_furnace_side");
		this.iconFurnace[1] = ir.registerIcon(MyMod.MOD_ID+":better_al_furnace_top");
		this.iconFurnace[2] = ir.registerIcon(MyMod.MOD_ID+":better_al_furnace_front_off");
		this.iconFurnace[3] = ir.registerIcon(MyMod.MOD_ID+":better_al_furnace_front_on");
		this.iconFurnace[4] = ir.registerIcon(MyMod.MOD_ID+":better_al_furnace_top_filled");
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
		if (side == 0) {
            return this.iconFurnace[0];
        }
        TileBetterAlchemyFurnace machine = (TileBetterAlchemyFurnace) worldIn.getTileEntity(x, y, z);
        if(machine != null) {
        	if (side == 1) {
                return this.iconFurnace[machine.isBurning()? 4 : 1];
            }
        	return this.iconFurnace[machine.isBurning()? 3 : 2];
        }
        if (side == 1) {
            return this.iconFurnace[1];
        }
    	return this.iconFurnace[2];
    }
	
	
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        if (side == 0) return this.iconFurnace[0];
        if (side == 1) return this.iconFurnace[1];
        return this.iconFurnace[2];
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBetterAlchemyFurnace();
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer activator, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            activator.openGui(MyMod.instance, 1, world, x, y, z);
        }
        return true;

    }
}
