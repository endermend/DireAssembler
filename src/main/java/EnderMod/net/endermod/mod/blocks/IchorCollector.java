package EnderMod.net.endermod.mod.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import EnderMod.net.endermod.mod.MyMod;
import EnderMod.net.endermod.mod.tile.TileEntityIchorCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IchorCollector extends BlockContainer {

    private IIcon top, sides, front, bottom;

    public IchorCollector() {
        super(Material.iron);
        setBlockName("IchorCollector");
        setStepSound(Block.soundTypeMetal);
        // setBlockTextureName(MyMod.MOD_ID + ":collector_front");
        setHardness(20.0f);
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(MyMod.tab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.top = iconRegister.registerIcon(MyMod.MOD_ID + ":collector_top");
        this.sides = iconRegister.registerIcon(MyMod.MOD_ID + ":collector_sides");
        this.front = iconRegister.registerIcon(MyMod.MOD_ID + ":collector_front");
        this.bottom = iconRegister.registerIcon(MyMod.MOD_ID + ":collector_bottom");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (side == 1) {
            return top;
        }
        if (side == 0) {
            return bottom;
        }
        int facing = 2;
        TileEntityIchorCollector machine = (TileEntityIchorCollector) worldIn.getTileEntity(x, y, z);
        if (machine != null) {
            facing = machine.getFacing();
        }
        if (facing == side) {
            return front;
        }
        return sides;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        if (side == 0) return bottom;
        if (side == 1) return top;
        if (side == 3) return front;
        return sides;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityIchorCollector();
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        super.updateTick(worldIn, x, y, z, random);
        int deltaTick = tickRate(worldIn);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer activator, int side, float hitX,
        float hitY, float hitZ) {
        if (!world.isRemote) {
            // TODO:add gui
            activator.openGui(MyMod.instance, 0, world, x, y, z);
        }
        return true;

    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityIchorCollector) {
            TileEntityIchorCollector machine = (TileEntityIchorCollector) tile;
            int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

            if (l == 0) machine.setFacing(2);

            if (l == 1) machine.setFacing(5);

            if (l == 2) machine.setFacing(3);

            if (l == 3) machine.setFacing(4);
        }

    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        Random random = new Random();
        TileEntityIchorCollector collector = (TileEntityIchorCollector) worldIn.getTileEntity(x, y, z);
        if (collector == null) {
            super.breakBlock(worldIn, x, y, z, blockBroken, meta);
            return;
        }

        ItemStack itemStack = collector.getStackInSlot(0);
        if (itemStack == null) {
            super.breakBlock(worldIn, x, y, z, blockBroken, meta);
            return;
        }
        double dx = random.nextDouble() * 0.8D + 0.1D;
        double dy = random.nextDouble() * 0.8D + 0.1D;
        double dz = random.nextDouble() * 0.8D + 0.1D;
        while (itemStack.stackSize > 0) {
            int dropSize = random.nextInt(22) + 10;

            if (dropSize > itemStack.stackSize) {
                dropSize = itemStack.stackSize;
            }
            itemStack.stackSize -= dropSize;

            EntityItem entityDrop = new EntityItem(
                worldIn,
                x + dx,
                y + dy,
                z + dz,
                new ItemStack(
                    itemStack.getItem(),
                    random.nextBoolean() ? dropSize : dropSize / 2,
                    itemStack.getItemDamage()));

            if (itemStack.hasTagCompound()) {
                entityDrop.getEntityItem()
                    .setTagCompound(
                        (NBTTagCompound) itemStack.getTagCompound()
                            .copy());
            }

            entityDrop.motionX = random.nextGaussian() * 0.05D;
            entityDrop.motionY = random.nextGaussian() * 0.05D + 0.2D;
            entityDrop.motionZ = random.nextGaussian() * 0.05D;
            worldIn.spawnEntityInWorld(entityDrop);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }
}
