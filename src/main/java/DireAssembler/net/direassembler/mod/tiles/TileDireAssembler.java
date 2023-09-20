package DireAssembler.net.direassembler.mod.tiles;

import com.myname.mymodid.Tags;

import DireAssembler.net.direassembler.mod.blocks.BlockDireAssembler;
import DireAssembler.net.direassembler.mod.blocks.BlockEnum;
import DireAssembler.net.direassembler.mod.common.DireAll;
import DireAssembler.net.direassembler.mod.items.ItemDireCore;
import DireAssembler.net.direassembler.mod.common.DireLog;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.*;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.definitions.IMaterials;
import appeng.api.implementations.items.IMemoryCard;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.api.implementations.items.MemoryCardMessages;
import appeng.api.networking.GridFlags;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.core.localization.WailaText;
import appeng.core.sync.packets.PacketAssemblerAnimation;
import appeng.me.GridAccessException;
import appeng.parts.automation.BlockUpgradeInventory;
import appeng.parts.automation.UpgradeInventory;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileDireAssembler extends AENetworkInvTile implements ICraftingProvider {

	private class DireAssemblerInventory extends TheInternalInventory {

		public DireAssemblerInventory() {
			super("DireAssemblerInventory", TileDireAssembler.SLOT_COUNT, 64);
		}

		@Override
		public boolean isItemValidForSlot(final int slotIndex, final ItemStack itemStack) {
			if (slotIndex == TileDireAssembler.DIRE_CORE_SLOT_INDEX) {
				return (itemStack == null) || (itemStack.getItem() instanceof ItemDireCore);
			}

			return true;
		}

		@Override
		public void markDirty() {
			TileDireAssembler.this.markDirty();
		}

		@Override
		public void setInventorySlotContents(final int slotIndex, final ItemStack itemStack) {
			// Call super
			super.setInventorySlotContents(slotIndex, itemStack);

			// Core changed?
			if (slotIndex == TileDireAssembler.DIRE_CORE_SLOT_INDEX) {
				TileDireAssembler.this.flag_CoreChanged = true;
			}
		}

	}

	private static final String NBTKEY_UPGRADES = "upgradeCount", NBTKEY_UPGRADEINV = "upgrades",
			NBTKEY_CRAFTING = "isCrafting", NBTKEY_CRAFTING_PATTERN = "pattern";

	private static final int BASE_TICKS_PER_CRAFT = 20;

	public static final int SLOT_COUNT = 27;

	public static final int DIRE_CORE_SLOT_INDEX = 0, PATTERN_SLOT_INDEX = 1, TARGET_SLOT_INDEX = 22;

	public static double IDLE_POWER = 0.0D, ACTIVE_POWER = 1.5D, WARP_POWER_PERCENT = 0.15;

	private final DireAssemblerInventory internalInventory;

	private UpgradeInventory upgradeInventory;

	private boolean isCrafting = false;

	private DireCraftingPattern currentPattern = null;

	private final HandlerDireCore direCoreHandler;

	/**
	 * Source of all actions.
	 */
	private MachineSource mySource;

	/**
	 * True when the assembler is online.
	 */
	private boolean isActive = false;

	/**
	 * The number of installed upgrades.
	 */
	private int upgradeCount = 0;

	/**
	 * How much additional power is required due to warp.
	 */
	private float warpPowerMultiplier = 1.0F;

	/**
	 * If true the network should be informed of changes to available patterns.
	 */
	private boolean stalePatterns = false;

	/**
	 * When true will send network update periodically.
	 */
	private boolean delayedUpdate = false;

	private int craftTickCounter = 0, delayTickCounter = 0;

	/**
	 * True when the core has changed.
	 */
	boolean flag_CoreChanged = false;

	public TileDireAssembler() {
		// Create the internal inventory
		this.internalInventory = new DireAssemblerInventory();

		// Create the upgrade inventory
		this.upgradeInventory = new BlockUpgradeInventory(DireAll.instance().blocks.DireAssembler.getBlock(),
				(IAEAppEngInventory) this, BlockDireAssembler.MAX_SPEED_UPGRADES) {
			@Override
			public boolean isItemValidForSlot(int index, ItemStack slotStack) {
				return slotStack.getItem() instanceof IUpgradeModule && slotStack.getUnlocalizedName().contains("Speed");
			}
		};

		// Set the machine source
		this.mySource = new MachineSource((IActionHost) this);

		// Create the handler
		this.direCoreHandler = new HandlerDireCore();

		// Set idle power usage
		this.getProxy().setIdlePowerUsage(TileDireAssembler.IDLE_POWER);

		// Require a channel
		this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
	}

	/**
	 * Get's the NBT tag for the just-crafted assembler.
	 *
	 * @return
	 */
	public static NBTTagCompound getCraftTag() {
		NBTTagCompound data = new NBTTagCompound();

		return data;
	}

	private void craftingTick() {
		// Null check
		if (this.currentPattern == null) {
			DireLog.info("No pattern! " + this.xCoord + " " + this.yCoord + " " + this.zCoord);
			this.isCrafting = false;
		}

		// Has the assembler finished crafting?
		if (this.craftTickCounter >= this.ticksPerCraft()) {
			try {
				// Get the storage grid
				IStorageGrid storageGrid = this.getProxy().getStorage();
				DireLog.info("Got Storage grid " + this.xCoord + " " + this.yCoord + " " + this.zCoord);
				// Simulate placing the items
				boolean rejected = false;
				for (IAEItemStack output : this.currentPattern.getAllResults()) {
					IAEItemStack rejectedResult = storageGrid.getItemInventory().injectItems(output,
							Actionable.SIMULATE, this.mySource);
					if ((rejectedResult != null) && (rejectedResult.getStackSize() > 0)) {
						rejected = true;
						break;
					}
				}

				// Were all items accepted?
				if (!rejected) {
					DireLog.info("All items accepted! " + this.xCoord + " " + this.yCoord + " " + this.zCoord);
					// Inject into the network
					for (IAEItemStack output : this.currentPattern.getAllResults()) {
						storageGrid.getItemInventory().injectItems(output, Actionable.MODULATE, this.mySource);
					}

					// Mark the assembler as no longer crafting
					this.isCrafting = false;
					this.internalInventory.setInventorySlotContents(TileDireAssembler.TARGET_SLOT_INDEX, null);
					this.currentPattern = null;

					// Mark for network update
					this.markForDelayedUpdate();
				}
			} catch (GridAccessException e) {
				DireLog.info("You fucked up! " + e.getStackTrace() + " " + this.xCoord + " " + this.yCoord + " "
						+ this.zCoord);
			}
		} else {
			try {
				// Calculate power required
				double powerRequired = (TileDireAssembler.ACTIVE_POWER
						+ ((TileDireAssembler.ACTIVE_POWER * this.upgradeCount) / 2.0D)) * this.warpPowerMultiplier;

				// Attempt to take power
				IEnergyGrid eGrid = this.getProxy().getEnergy();
				double powerExtracted = eGrid.extractAEPower(powerRequired, Actionable.MODULATE,
						PowerMultiplier.CONFIG);

				if ((powerExtracted - powerRequired) <= 0.0D) {
					// Increment the counter
					this.craftTickCounter++;

					if (this.craftTickCounter >= this.ticksPerCraft()) {
						this.markForDelayedUpdate();
					}
				}
			} catch (GridAccessException e) {
			}
		}

	}

	private boolean isActive() {
		// Are we server side?
		if (!worldObj.isRemote) {
			// Do we have a proxy and grid node?
			if ((this.getProxy() != null) && (this.getProxy().getNode() != null)) {
				// Get the grid node activity
				this.isActive = this.getProxy().getNode().isActive();
			}
		}

		return this.isActive;
	}

	private void markForDelayedUpdate() {
		this.delayedUpdate = true;
	}

	private int ticksPerCraft() {
		return TileDireAssembler.BASE_TICKS_PER_CRAFT - (4 * this.upgradeCount);
	}

	/**
	 * Updates the handler to the current core.
	 */
	private void updateCoreHandler() {
		this.flag_CoreChanged = false;
		// Is there a kcore?
		ItemStack direCore = this.internalInventory.getStackInSlot(TileDireAssembler.DIRE_CORE_SLOT_INDEX);
		if (direCore != null) {
			// Is it a new core?
			if (!TileDireAssembler.this.direCoreHandler.isHandlingCore(direCore)) {
				// Open the core
				TileDireAssembler.this.direCoreHandler.open(direCore);

				// Update the pattern slots
				TileDireAssembler.this.updatePatternSlots();
			}
		} else {
			// Was there a core?
			if (TileDireAssembler.this.direCoreHandler.hasCore()) {
				// No more core
				TileDireAssembler.this.direCoreHandler.close();

				// Update the pattern slots
				TileDireAssembler.this.updatePatternSlots();
			}
		}
	}

	/**
	 * Updates the pattern slots to match the kcore patterns.
	 */
	private void updatePatternSlots() {
		Iterator<ItemStack> pIterator = null;

		// Get the list
		if (this.direCoreHandler != null) {
			ArrayList<ItemStack> patternOutputs = this.direCoreHandler.getStoredOutputs();
			pIterator = patternOutputs.iterator();
		}

		// Set pattern slots
		for (int index = TileDireAssembler.PATTERN_SLOT_INDEX; index < (TileDireAssembler.PATTERN_SLOT_INDEX
				+ HandlerDireCore.MAXIMUM_STORED_PATTERNS); index++) {
			if ((pIterator != null) && (pIterator.hasNext())) {
				// Set to pattern result
				this.internalInventory.setInventorySlotContents(index, pIterator.next());
			} else {
				// Clear slot
				this.internalInventory.setInventorySlotContents(index, null);
			}
		}

		this.stalePatterns = true;
	}

	@Override
	protected ItemStack getItemFromTile(final Object obj) {
		// Return the itemstack the visually represents this tile
		return DireAll.instance().blocks.DireAssembler.getStack();

	}

	@MENetworkEventSubscribe
	public final void channelEvent(final MENetworkChannelsChanged event) {
		// Mark for update
		this.markForUpdate();
	}

	@Override
	public int[] getAccessibleSlotsBySide(final ForgeDirection whichSide) {
		return new int[0];
	}

	@Override
	public AECableType getCableConnectionType(final ForgeDirection dir) {
		return AECableType.SMART;
	}

	/**
	 * Called when the block is broken to get any additional items.
	 *
	 * @return
	 */

	public void getDrops(final World world, final int x, final int y, final int z, final ArrayList<ItemStack> drops) {
		// Add the kCore
		ItemStack kCore = this.internalInventory.getStackInSlot(TileDireAssembler.DIRE_CORE_SLOT_INDEX);
		if (kCore != null) {
			drops.add(kCore);
		}

		// Add upgrades
		for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {
			ItemStack upgrade = this.upgradeInventory.getStackInSlot(i);

			if (upgrade != null) {
				drops.add(upgrade);
			}
		}
	}

	@Override
	public IInventory getInternalInventory() {
		return this.internalInventory;
	}

	/**
	 * Get's the current dire core handler.
	 *
	 * @return
	 */
	public HandlerDireCore getKCoreHandler() {
		return this.direCoreHandler;
	}

	@Override
	public DimensionalCoord getLocation() {
		return new DimensionalCoord(this);
	}

	public float getPercentComplete() {
		float percent = 0.0F;

		if (this.isCrafting) {
			percent = Math.min(((float) this.craftTickCounter / this.ticksPerCraft()), 1.0F);
		}

		return percent;
	}

	public UpgradeInventory getUpgradeInventory() {
		return this.upgradeInventory;
	}

	@Override
	public boolean isBusy() {
		return this.isCrafting;
	}

	@Override
	public boolean isItemValidForSlot(final int slotId, final ItemStack itemStack) {
		// Is the slot being assigned the knowledge core slot?
		if (slotId == TileDireAssembler.DIRE_CORE_SLOT_INDEX) {
			// Ensure the item is a knowledge core.
			return ((itemStack == null) || (itemStack.getItem() instanceof ItemDireCore));
		}

		// Assume it is not valid
		return false;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {
		if (worldObj.isRemote) {
			// Ignored on the client side.
			return false;
		}

		// Check basic usability
		if (!this.internalInventory.isUseableByPlayer(player, this)) {
			return false;
		}

		// Check the security grid
		try {
			// Get the security grid
			ISecurityGrid sGrid = this.getProxy().getSecurity();

			// Return true if the player has inject and extract permissions
			return ((sGrid.hasPermission(player, SecurityPermissions.INJECT))
					&& (sGrid.hasPermission(player, SecurityPermissions.EXTRACT)));
		} catch (GridAccessException e) {
			return false;
		}
	}

	/**
	 * Called when the tile entity is about to be destroyed by a block break.
	 */
	public void onBreak() {
		this.isCrafting = false;
	}

	@Override
	public void onChangeInventory(final IInventory inv, final int slot, final InvOperation mc, final ItemStack removed,
			final ItemStack added) {
		// Reset the upgrade count
		this.upgradeCount = 0;

		IMaterials aeMaterals = AEApi.instance().definitions().materials();

		// Look for speed cards
		for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {
			ItemStack slotStack = this.upgradeInventory.getStackInSlot(i);

			if (slotStack != null) {
				if (aeMaterals.cardSpeed().isSameAs(slotStack)) {
					this.upgradeCount++;
				}
			}
		}

		if (!worldObj.isRemote) {
			// Mark for save
			this.markDirty();

			// Mark for network update
			this.markForUpdate();
		}
	}

	public void onMemoryCardActivate(final EntityPlayer player, final IMemoryCard memoryCard,
			final ItemStack playerHolding) {
		// Get the stored name
		String settingsName = memoryCard.getSettingsName(playerHolding);
		// Is the memory card empty?
		if (settingsName.equals("gui.appliedenergistics2.Blank")) {
			// Clear the source info

			// Inform the user
			memoryCard.notifyUser(player, MemoryCardMessages.SETTINGS_CLEARED);

			// Mark dirty
			this.markDirty();
		}
	}

	@Override
	public void onReady() {
		// Call super
		super.onReady();

		// Setup the kcore handler
		this.updateCoreHandler();

		// Is there a knowledge core?
		if (this.internalInventory.getHasStack(TileDireAssembler.DIRE_CORE_SLOT_INDEX)) {
			// Is there a pattern?
			if (this.currentPattern != null) {
				// Set the patterns core
				this.currentPattern
						.setDireCore(this.internalInventory.getStackInSlot(TileDireAssembler.DIRE_CORE_SLOT_INDEX));
			}
		} else if (this.currentPattern != null) {
			// Clear the pattern
			this.isCrafting = false;
			this.currentPattern = null;
		}

	}

	@TileEvent(TileEventType.NETWORK_READ)
	@SideOnly(Side.CLIENT)
	public boolean onReceiveNetworkData(final ByteBuf stream) {
		// Read the active state
		this.isActive = stream.readBoolean();

		// Read the crafting status
		this.isCrafting = stream.readBoolean();
		if (this.isCrafting) {
			// Read the crafting progress
			this.craftTickCounter = stream.readInt();

		}

		// Read upgrade count
		this.upgradeCount = stream.readInt();

		return true;
	}

	@TileEvent(TileEventType.NETWORK_WRITE)
	public void onSendNetworkData(final ByteBuf stream) throws IOException {
		// Write the active state
		stream.writeBoolean(this.isActive());

		// Write if the assembler is crafting
		stream.writeBoolean(this.isCrafting);
		if (this.isCrafting) {
			// Write the crafting progress
			stream.writeInt(this.craftTickCounter);
		}

		// Write the upgrade count
		stream.writeInt(this.upgradeCount);
	}

	@TileEvent(TileEventType.TICK)
	public void onTick() {

		if (worldObj.isRemote) {
			if ((this.isCrafting) && (this.craftTickCounter < TileDireAssembler.BASE_TICKS_PER_CRAFT)) {
				this.craftTickCounter++;
			}

			// Ignore the rest on client side.
			return;
		}

		// Core changed?
		if (this.flag_CoreChanged) {
			this.updateCoreHandler();
		}

		// Ensure the assembler is active
		if (!this.isActive()) {
			return;
		}

		// Are the network patterns stale?
		if (this.stalePatterns && this.getProxy().isReady()) {
			try {
				// Inform the network
				this.getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this, this.getActionableNode()));

				// Mark they are no longer stale
				this.stalePatterns = false;
			} catch (GridAccessException e) {
			}
		}

		// Is there a delayed update queued?
		if (this.delayedUpdate) {
			// Increase the counter
			this.delayTickCounter++;

			// Have 5 ticks elapsed?
			if (this.delayTickCounter >= 5) {
				// Mark for an update
				this.markForUpdate();

				// Reset the trackers
				this.delayedUpdate = false;
				this.delayTickCounter = 0;
			}
		}

		// Is the assembler crafting?
		if (this.isCrafting) {
			// Tick crafting
			this.craftingTick();
		}
	}

	@MENetworkEventSubscribe
	public final void powerEvent(final MENetworkPowerStatusChange event) {
		// Mark for update
		this.markForUpdate();
	}

	@Override
	public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
		if (this.direCoreHandler == null) {

		}
		// Get the list of patterns
		ArrayList<DireCraftingPattern> corePatterns = this.direCoreHandler.getPatterns();

		// Add each pattern
		for (DireCraftingPattern pattern : corePatterns) {
			if (pattern != null) {
				craftingTracker.addCraftingOption(this, pattern);
			}
		}
	}

	@Override
	public boolean pushPattern(final ICraftingPatternDetails patternDetails, final InventoryCrafting table) {
		DireLog.info("Pattern pushed");
		if (this.isCrafting || !(patternDetails instanceof DireCraftingPattern)) {
			return false;
		}
		// Mark that crafting has begun
		this.isCrafting = true;

		// Reset the crafting tick counter
		this.craftTickCounter = 0;

		// Set the pattern that is being crafted
		this.currentPattern = (DireCraftingPattern) patternDetails;

		// Set the target item
		this.internalInventory.setInventorySlotContents(TileDireAssembler.TARGET_SLOT_INDEX,
				this.currentPattern.getResult().getItemStack());

		// AE effects
		try {
			NetworkRegistry.TargetPoint where = new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId,
					this.xCoord, this.yCoord, this.zCoord, 32.0D);
			appeng.core.sync.network.NetworkHandler.instance.sendToAllAround(new PacketAssemblerAnimation(this.xCoord,
					this.yCoord, this.zCoord, (byte) (10 + (9 * this.upgradeCount)), this.currentPattern.getResult()),
					where);
		} catch (IOException e) {
		}

		return true;
	}

	@TileEvent(TileEventType.WORLD_NBT_READ)
	public void readFromNBT_ArcaneAssembler(final NBTTagCompound data) {

		// Read upgrade count
		if (data.hasKey(TileDireAssembler.NBTKEY_UPGRADES)) {
			this.upgradeCount = data.getInteger(TileDireAssembler.NBTKEY_UPGRADES);
		}

		// Read upgrade inventory
		this.upgradeInventory.readFromNBT(data, TileDireAssembler.NBTKEY_UPGRADEINV);

		// Read the crafting status
		if (data.hasKey(TileDireAssembler.NBTKEY_CRAFTING)) {
			this.isCrafting = data.getBoolean(TileDireAssembler.NBTKEY_CRAFTING);
		}

		// Read the pattern
		if (data.hasKey(TileDireAssembler.NBTKEY_CRAFTING_PATTERN)) {
			this.currentPattern = new DireCraftingPattern(null,
					data.getCompoundTag(TileDireAssembler.NBTKEY_CRAFTING_PATTERN));
		}

	}

	public void setOwner(final EntityPlayer player) {
		this.getProxy().setOwner(player);
	}

	@TileEvent(TileEventType.WORLD_NBT_WRITE)
	public void writeToNBT_ArcaneAssembler(final NBTTagCompound data) {

		// Write the number of upgrades
		data.setInteger(TileDireAssembler.NBTKEY_UPGRADES, this.upgradeCount);

		// Write the upgrade inventory
		this.upgradeInventory.writeToNBT(data, TileDireAssembler.NBTKEY_UPGRADEINV);

		// Write the crafting state
		data.setBoolean(TileDireAssembler.NBTKEY_CRAFTING, this.isCrafting);

		// Write the current pattern
		if (this.currentPattern != null) {
			data.setTag(TileDireAssembler.NBTKEY_CRAFTING_PATTERN,
					this.currentPattern.writeToNBT(new NBTTagCompound()));
		}
	}

}
