package EnderMod.net.endermod.mod.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileBellows;

public class TileBetterAlchemyFurnace extends TileThaumcraft implements ISidedInventory {

	private static final int[] slots_bottom = { 1 };
	private static final int[] slots_top = new int[0];
	private static final int[] slots_sides = { 0 };

	public AspectList aspects = new AspectList();
	public int vis;
	private int maxVis = 1000;
	public int smeltTime = 100;
	int bellows = -1;
	boolean speedBoost = false;

	private ItemStack[] furnaceItemStacks = new ItemStack[2];
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;
	private String customName;

	private int count = 0;

	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slotIn) {
		return this.furnaceItemStacks[slotIn];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.furnaceItemStacks[index] == null) {
			return null;
		}
		if (this.furnaceItemStacks[index].stackSize <= count) {
			ItemStack itemstack = this.furnaceItemStacks[index];
			this.furnaceItemStacks[index] = null;
			return itemstack;
		}
		ItemStack itemstack = this.furnaceItemStacks[index].splitStack(count);

		if (this.furnaceItemStacks[index].stackSize == 0) {
			this.furnaceItemStacks[index] = null;
		}

		return itemstack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		if (this.furnaceItemStacks[index] != null) {
			ItemStack itemstack = this.furnaceItemStacks[index];
			this.furnaceItemStacks[index] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.furnaceItemStacks[index] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
	}

	@Override
	public String getInventoryName() {
		return hasCustomInventoryName() ? this.customName : "container.betteralchemyfurnace";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return (this.customName != null && this.customName.length() > 0);
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return (this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) ? false
				: ((player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D));
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (index == 0) {
			AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
			al = ThaumcraftCraftingManager.getBonusTags(stack, al);
			if (al != null && al.size() > 0)
				return true;
		}
		return (index == 1) ? isItemFuel(stack) : false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return (p_94128_1_ == 0) ? slots_bottom : ((p_94128_1_ == 1) ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		// TODO Auto-generated method stub
		return (p_102007_3_ == 1) ? false : isItemValidForSlot(p_102007_1_, p_102007_2_);
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
		return (p_102008_3_ != 0 || p_102008_1_ != 1 || p_102008_2_.getItem() == Items.bucket);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		this.furnaceBurnTime = nbt.getShort("BurnTime");
		this.vis = nbt.getShort("Vis");
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		nbt.setShort("BurnTime", (short) this.furnaceBurnTime);
		nbt.setShort("Vis", (short) this.vis);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		this.furnaceItemStacks = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttag = nbttaglist.getCompoundTagAt(i);
			byte index = nbttag.getByte("Slot");
			if (index >= 0 && index < this.furnaceItemStacks.length) {
				this.furnaceItemStacks[index] = ItemStack.loadItemStackFromNBT(nbttag);
			}
		}
		this.speedBoost = nbt.getBoolean("speedBoost");
		this.furnaceCookTime = nbt.getShort("CookTime");
		this.currentItemBurnTime = TileEntityFurnace.getItemBurnTime(this.furnaceItemStacks[1]);
		if (nbt.hasKey("CustomName"))
			this.customName = nbt.getString("CustomName");
		this.aspects.readFromNBT(nbt);
		this.vis = this.aspects.visSize();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		super.writeToNBT(nbt);
		nbt.setBoolean("speedBoost", this.speedBoost);
		nbt.setShort("CookTime", (short) this.furnaceCookTime);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < this.furnaceItemStacks.length; i++) {
			if (this.furnaceItemStacks[i] != null) {
				NBTTagCompound nbttag = new NBTTagCompound();
				nbttag.setByte("Slot", (byte) i);
				this.furnaceItemStacks[i].writeToNBT(nbttag);
				nbttaglist.appendTag((NBTBase) nbttag);
			}
		}
		nbt.setTag("Items", (NBTBase) nbttaglist);
		if (hasCustomInventoryName())
			nbt.setString("CustomName", this.customName);
		this.aspects.writeToNBT(nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		if (this.worldObj != null)
			this.worldObj.updateLightByType(EnumSkyBlock.Block, this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int scale) {
		if (this.smeltTime <= 0) {
			this.smeltTime = 1;
		}
		return this.furnaceCookTime * scale / this.smeltTime;

	}

	@SideOnly(Side.CLIENT)
	public int getContentsScaled(int scale) {
		return this.vis * scale / this.maxVis;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int scale) {
		if (this.currentItemBurnTime == 0)
			this.currentItemBurnTime = 200;
		return this.furnaceBurnTime * scale / this.currentItemBurnTime;
	}

	@Override
	public void updateEntity() {
		boolean flag = (this.furnaceBurnTime > 0);
		boolean flag1 = false;
		this.count++;
		if (this.furnaceBurnTime > 0)
			this.furnaceBurnTime--;
		if (!this.worldObj.isRemote) {
			if (this.bellows < 0)
				getBellows();
			if (this.count % (this.speedBoost ? 20 : 40) == 0 && this.aspects.size() > 0) {
				AspectList exlude = new AspectList();
				int deep = 0;
				TileEntity tile = null;
				while (deep < 5) {
					deep++;
					tile = this.worldObj.getTileEntity(this.xCoord, this.yCoord + deep, this.zCoord);
					if (tile instanceof TileAlembic) {
						TileAlembic alembic = (TileAlembic) tile;
						if (alembic.aspect != null && alembic.amount < alembic.maxAmount
								&& this.aspects.getAmount(alembic.aspect) > 0) {
							takeFromContainer(alembic.aspect, 1);
							alembic.addToContainer(alembic.aspect, 1);
							exlude.merge(alembic.aspect, 1);
							this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
							this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord + deep, this.zCoord);
						}
						tile = null;
					}
				}
				deep = 0;
				while (deep < 5) {
					deep++;
					tile = this.worldObj.getTileEntity(this.xCoord, this.yCoord + deep, this.zCoord);
					if (tile instanceof TileAlembic) {
						TileAlembic alembic = (TileAlembic) tile;
						if (alembic.aspect == null || alembic.amount == 0) {
							Aspect as = null;
							if (alembic.aspectFilter == null) {
								as = takeRandomAspect(exlude);
							} else if (takeFromContainer(alembic.aspectFilter, 1)) {
								as = alembic.aspectFilter;
							}
							if (as != null) {
								alembic.addToContainer(as, 1);
								this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
								this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord + deep, this.zCoord);
								break;
							}
						}
					}
				}
			}
			if (this.furnaceBurnTime == 0 && canSmelt()) {
				this.currentItemBurnTime = this.furnaceBurnTime = TileEntityFurnace
						.getItemBurnTime(this.furnaceItemStacks[1]);
				if (this.furnaceBurnTime > 0) {
					flag1 = true;
					this.speedBoost = false;
					if (this.furnaceItemStacks[1] != null) {
						if (this.furnaceItemStacks[1].isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 0)))
							this.speedBoost = true;
						(this.furnaceItemStacks[1]).stackSize--;
						if ((this.furnaceItemStacks[1]).stackSize == 0)
							this.furnaceItemStacks[1] = this.furnaceItemStacks[1].getItem()
									.getContainerItem(this.furnaceItemStacks[1]);
					}
				}
			}
			if (isBurning() && canSmelt()) {
				this.furnaceCookTime++;
				if (this.furnaceCookTime >= this.smeltTime) {
					this.furnaceCookTime = 0;
					smeltItem();
					flag1 = true;
				}
			} else {
				this.furnaceCookTime = 0;
			}
			if (flag != ((this.furnaceBurnTime > 0))) {
				flag1 = true;
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}
		if (flag1)
			markDirty();
	}

	public boolean isBurning() {
		return (this.furnaceBurnTime > 0);
	}

	public void setGuiDisplayName(String GUIName) {
		this.customName = GUIName;
	}

	private boolean canSmelt() {
		if (this.furnaceItemStacks[0] == null)
			return false;
		AspectList al = ThaumcraftCraftingManager.getObjectTags(this.furnaceItemStacks[0]);
		al = ThaumcraftCraftingManager.getBonusTags(this.furnaceItemStacks[0], al);
		if (al == null || al.size() == 0)
			return false;
		int vs = al.visSize();
		if (vs > this.maxVis - this.vis)
			return false;
		this.smeltTime = (int) ((vs * 10) * (1.0F - 0.125F * this.bellows));
		return true;
	}

	public void getBellows() {
		this.bellows = TileBellows.getBellows(this.worldObj, this.xCoord, this.yCoord, this.zCoord,
				ForgeDirection.VALID_DIRECTIONS);
	}

	public void smeltItem() {
		if (canSmelt()) {
			AspectList al = ThaumcraftCraftingManager.getObjectTags(this.furnaceItemStacks[0]);
			al = ThaumcraftCraftingManager.getBonusTags(this.furnaceItemStacks[0], al);
			for (Aspect a : al.getAspects())
				this.aspects.add(a, al.getAmount(a));
			this.vis = this.aspects.visSize();
			(this.furnaceItemStacks[0]).stackSize--;
			if ((this.furnaceItemStacks[0]).stackSize <= 0)
				this.furnaceItemStacks[0] = null;
		}
	}

	public static boolean isItemFuel(ItemStack par0ItemStack) {
		return (TileEntityFurnace.getItemBurnTime(par0ItemStack) > 0);
	}

	public Aspect takeRandomAspect(AspectList exlude) {
		if (this.aspects.size() > 0) {
			AspectList temp = this.aspects.copy();
			if (exlude.size() > 0)
				for (Aspect a : exlude.getAspects())
					temp.remove(a);
			if (temp.size() > 0) {
				Aspect tag = temp.getAspects()[this.worldObj.rand.nextInt((temp.getAspects()).length)];
				this.aspects.remove(tag, 1);
				this.vis--;
				return tag;
			}
		}
		return null;
	}

	public boolean takeFromContainer(Aspect tag, int amount) {
		if (this.aspects != null && this.aspects.getAmount(tag) >= amount) {
			this.aspects.remove(tag, amount);
			this.vis -= amount;
			return true;
		}
		return false;
	}
	
}
