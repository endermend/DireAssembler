package DireAssembler.net.direassembler.mod.blocks;

import DireAssembler.net.direassembler.mod.MyMod;
import DireAssembler.net.direassembler.mod.gui.DireGUIHandler;
import DireAssembler.net.direassembler.mod.textures.BlockTextureManager;
import DireAssembler.net.direassembler.mod.tiles.TileDireInscriber;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDireInscriber extends BlockAEWrenchable {
	public BlockDireInscriber() {
		super(Material.iron);

		this.setHardness(1F);

		this.setStepSound(Block.soundTypeMetal);

		this.setCreativeTab(MyMod.tab);
	}

	@Override
	protected boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player) {
		DireGUIHandler.launchGui(DireGUIHandler.DIRE_INSCRIBER_ID, player, world, x, y, z);
		return true;
	}

	/**
	 * If has core, drop it
	 */
	@Override
	public void breakBlock(final World worldIn, final int x, final int y, final int z, final Block blockBroken,
			final int meta) {
		if (worldIn.isRemote) {
			super.breakBlock(worldIn, x, y, z, blockBroken, meta);
			return;
		}
		TileEntity tile = worldIn.getTileEntity(x, y, z);

		if (!(tile instanceof TileDireInscriber)) {
			super.breakBlock(worldIn, x, y, z, blockBroken, meta);
			return;
		}

		TileDireInscriber inscriber = (TileDireInscriber) tile;

		if (!inscriber.hasDireCore()) {
			super.breakBlock(worldIn, x, y, z, blockBroken, meta);
			return;
		}

		worldIn.spawnEntityInWorld(new EntityItem(worldIn, x + 0.5, y + 0.5, z + 0.5,
				inscriber.getStackInSlot(TileDireInscriber.DIRE_CORE_SLOT)));

		super.breakBlock(worldIn, x, y, z, blockBroken, meta);
	}

	@Override
	public TileEntity createNewTileEntity(final World worldIn, final int meta) {
		return new TileDireInscriber();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(final int side, final int meta) {

		if (side == ForgeDirection.OPPOSITES[meta]) {
			return BlockTextureManager.DIRE_INSCRIBER.getTexture(1);
		}

		if (ForgeDirection.VALID_DIRECTIONS[side] == ForgeDirection.UP
				|| ForgeDirection.VALID_DIRECTIONS[side] == ForgeDirection.DOWN) {
			return BlockTextureManager.DIRE_INSCRIBER.getTexture(2);
		}

		return BlockTextureManager.DIRE_INSCRIBER.getTexture(0);
	}

	@Override
	public String getUnlocalizedName() {
		return BlockEnum.DIRE_INSCRIBER.getUnlocalizedName();
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		worldIn.setBlockMetadataWithNotify(x, y, z, 0, 2);
	}

	/**
	 * Taken care of by texture manager
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(final IIconRegister register) {
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
		int newMeta = (worldObj.getBlockMetadata(x, y, z) + 1) % ForgeDirection.VALID_DIRECTIONS.length;
		worldObj.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
		return true;
	}
}
