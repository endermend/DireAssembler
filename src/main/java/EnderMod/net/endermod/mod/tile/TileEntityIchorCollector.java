package EnderMod.net.endermod.mod.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import EnderMod.net.endermod.mod.items.ModItems;

public class TileEntityIchorCollector extends TileEntity implements IInventory {

    private static final String INV_TAG = "Ichor";
    private ItemStack stack;
    private int facing = 2;
    private int progress = 0;
    private final int progress_timer = 3600;

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (++progress >= progress_timer) {
            progress = 0;
            if (stack == null) {
                stack = new ItemStack(ModItems.ICHOR_SHARD, 1);
            } else if (stack.stackSize < 64) {
                stack.stackSize++;
            }
            markDirty();
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        writeExtendedData(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        readExtendedData(nbt);
    }

    private void writeExtendedData(NBTTagCompound nbt) {
        nbt.setInteger("Progress", this.progress);
        nbt.setShort("Facing", (short) this.facing);
        if (stack != null) {
            NBTTagCompound inventoryTag = new NBTTagCompound();
            stack.writeToNBT(inventoryTag);
            nbt.setTag(INV_TAG, inventoryTag);
        }
    }

    private void readExtendedData(NBTTagCompound nbt) {
        this.progress = nbt.getInteger("Progress");
        this.facing = nbt.getShort("Facing");
        if (nbt.hasKey(INV_TAG, Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound inventoryTag = nbt.getCompoundTag(INV_TAG);
            stack = ItemStack.loadItemStackFromNBT(inventoryTag);
        }
    }

    public int getFacing() {
        return this.facing;
    }

    public void setFacing(int dir) {
        this.facing = dir;
    }

    public boolean hasStack() {
        return stack != null;
    }

    private void removeFromStack(EntityPlayer player) {
        if (!player.inventory.addItemStackToInventory(this.stack)) {
            player.dropPlayerItemWithRandomChoice(this.stack, false);
        } else {
            player.inventoryContainer.detectAndSendChanges();
        }
        this.stack = null;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeExtendedData(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        TileEntity tile = worldObj
            .getTileEntity(packet.func_148856_c(), packet.func_148855_d(), packet.func_148854_e());
        if (tile instanceof TileEntityIchorCollector) {
            ((TileEntityIchorCollector) tile).readExtendedData(packet.func_148857_g());
        }
    }

    public int timeToCreateShard() {
        return progress_timer - progress;
    }

    public void handleInputStack(EntityPlayer player, ItemStack stack) {
        if (hasStack()) {
            removeFromStack(player);
        }
        markDirty();
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (stack == null) {
            return null;
        }
        if (count < stack.stackSize) {
            ItemStack take = stack.splitStack(count);
            if (stack.stackSize <= 0) {
                stack = null;
            }
            return take;
        }
        ItemStack take = stack.copy();
        stack = null;
        return take;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public String getInventoryName() {
        return "container.ichor";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player
            .getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D)
            <= 64.0D;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }
}
