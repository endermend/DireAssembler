package DireAssembler.net.direassembler.mod.blocks;

import java.util.ArrayList;

import DireAssembler.net.direassembler.mod.MyMod;
import DireAssembler.net.direassembler.mod.gui.DireGUIHandler;
import DireAssembler.net.direassembler.mod.textures.BlockTextureManager;
import DireAssembler.net.direassembler.mod.tiles.TileDireAssembler;
import appeng.api.implementations.items.IMemoryCard;
import appeng.tile.AEBaseInvTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class BlockDireAssembler extends BlockAEWrenchable {
	public static final int MAX_SPEED_UPGRADES = 4;

	public BlockDireAssembler() {
		// Call super with material machine (iron)
		super(Material.iron);

		// Basic hardness
		this.setHardness(1.0f);

		// Sound of metal
		this.setStepSound(Block.soundTypeMetal);

		// Place in the ThE creative tab
		this.setCreativeTab(MyMod.tab);
	}

	/**
	 * Called when the assembler is right-clicked
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param player
	 * @return
	 */
	@Override
	protected boolean onBlockActivated(final World world, final int x, final int y, final int z,
			final EntityPlayer player) {
		// Get what the player is holding
		ItemStack playerHolding = player.inventory.getCurrentItem();

		// Get the tile
		TileEntity tileAssembler = world.getTileEntity(x, y, z);

		if (!(tileAssembler instanceof TileDireAssembler)) {
			return true;
		}
		// Are they holding a memory card?
		if ((playerHolding != null) && (playerHolding.getItem() instanceof IMemoryCard)) {
			// Inform the tile of the event
			((TileDireAssembler) tileAssembler).onMemoryCardActivate(player, (IMemoryCard) playerHolding.getItem(),
					playerHolding);
		}
		// Can the player interact with the assembler?
		else if (((AEBaseInvTile) tileAssembler).isUseableByPlayer(player)) {
			// Launch the gui.
			DireGUIHandler.launchGui(DireGUIHandler.DIRE_ASSEMBLER_ID, player, world, x, y, z);
		}

		return true;
	}

	/**
	 * Called when the assembler is dismantled via wrench.
	 */
	@Override
	protected ItemStack onDismantled(final World world, final int x, final int y, final int z) {
		// Create the itemstack
		ItemStack itemStack = new ItemStack(this);

		// Get the tile
		TileEntity tileAssembler = world.getTileEntity(x, y, z);

		if (!(tileAssembler instanceof TileDireAssembler)) {
			return itemStack;
		}
		// Create a compound tag
		NBTTagCompound data = new NBTTagCompound();
		// Set the itemstack tag
		if (!data.hasNoTags()) {
			itemStack.setTagCompound(data);
		}

		return itemStack;
	}

	/**
	 * Called when the block is broken.
	 */
	@Override
	public void breakBlock(final World world, final int x, final int y, final int z, final Block block,
			final int metaData) {
		// Is this server side?
		if (world.isRemote) {
			super.breakBlock(world, x, y, z, block, metaData);
			return;
		}
		// Get the tile
		TileEntity tileAssembler = world.getTileEntity(x, y, z);

		if (!(tileAssembler instanceof TileDireAssembler)) {
			super.breakBlock(world, x, y, z, block, metaData);
			return;
		}
		// Get the drops
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		((TileDireAssembler) tileAssembler).getDrops(world, x, y, z, drops);

		for (ItemStack drop : drops) {
			world.spawnEntityInWorld(new EntityItem(world, 0.5 + x, 0.5 + y, 0.2 + z, drop));
		}

		// Inform the tile it is being broken
		((TileDireAssembler) tileAssembler).onBreak();

		// Call super
		super.breakBlock(world, x, y, z, block, metaData);
	}

	/**
	 * Creates a new Arcane Assembler tile
	 */
	@Override
	public TileEntity createNewTileEntity(final World world, final int metadata) {
		return new TileDireAssembler();
	}

	/**
	 * Gets the standard block icon.
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(final int side, final int meta) {
		// Sides
		return BlockTextureManager.DIRE_ASSEMBLER.getTexture(0);
	}

	/**
	 * Gets the unlocalized name of the block.
	 */
	@Override
	public String getUnlocalizedName() {
		return BlockEnum.DIRE_ASSEMBLER.getUnlocalizedName();
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean isSideSolid(final IBlockAccess world, final int x, final int y, final int z,
			final ForgeDirection side) {
		return false;
	}

	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase player,
			final ItemStack itemStack) {
		// Get the tile
		TileEntity tileAssembler = world.getTileEntity(x, y, z);

		if (!(tileAssembler instanceof TileDireAssembler && player instanceof EntityPlayer)) {
			return;
		}
		// Set the owner
		((TileDireAssembler) tileAssembler).setOwner((EntityPlayer) player);
	}

	/**
	 * Taken care of by texture manager
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(final IIconRegister register) {
		// Ignored
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	/**
	 * Prevents MC from using the default block renderer.
	 */
	@Override
	public boolean shouldSideBeRendered(final IBlockAccess iblockaccess, final int i, final int j, final int k,
			final int l) {
		return false;
	}
}
