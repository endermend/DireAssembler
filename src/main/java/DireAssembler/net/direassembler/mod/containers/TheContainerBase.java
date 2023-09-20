package DireAssembler.net.direassembler.mod.containers;

import java.util.HashMap;

import DireAssembler.net.direassembler.mod.common.DireLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public abstract class TheContainerBase extends Container {
	/**
	 * Maps slotNumber -> Slot
	 */
	private final HashMap<Integer, Slot> slotMap = new HashMap<Integer, Slot>();

	/**
	 * Set if the player is an MP player.
	 */
	private final EntityPlayerMP playerMP;

	/**
	 * The player interacting with this container.
	 */
	protected final EntityPlayer player;

	public TheContainerBase(final EntityPlayer player) {
		// Set the player
		this.player = player;
		if (player instanceof EntityPlayerMP) {
			this.playerMP = (EntityPlayerMP) player;
		} else {
			this.playerMP = null;
		}
	}

	/**
	 * Adds a slot to the container and the slot map.
	 */
	@Override
	protected Slot addSlotToContainer(final Slot slot) {
		// Call super
		super.addSlotToContainer(slot);

		// Map the slot
		if (this.slotMap.put(slot.slotNumber, slot) != null) {
			DireLog.warning("Duplicate Slot Number Detected: %d", slot.slotNumber);
		}

		return slot;
	}

	/**
	 * Detects server side changes to send to the player.<br/>
	 * When modifying slots, return true to set
	 * {@code playerMP.isChangingQuantityOnly} to {@code false}, or set it directly
	 * with the player argument.
	 *
	 * @param playerMP
	 *
	 * @return
	 */
	protected boolean detectAndSendChangesMP(final EntityPlayerMP playerMP) {
		return false;
	}

	@Override
	public void detectAndSendChanges() {
		// Call super
		super.detectAndSendChanges();

		// MP player?
		if (this.playerMP != null) {
			if (this.detectAndSendChangesMP(this.playerMP)) {
				this.playerMP.isChangingQuantityOnly = false;

				// Call super
				super.detectAndSendChanges();
			}
		}
	}

	/**
	 * Use getSlotOrNull.
	 */
	@Override
	@Deprecated
	public Slot getSlot(final int slotNumber) {
		return super.getSlot(slotNumber);
	}

	/**
	 * Returns the slot with the specified slot number or null.
	 *
	 * @param slotNumber
	 * @return
	 */
	public Slot getSlotOrNull(final int slotNumber) {
		return this.slotMap.get(slotNumber);
	}

	/**
	 * Clears the slot map.
	 */
	@Override
	public void onContainerClosed(final EntityPlayer player) {
		// Call super
		super.onContainerClosed(player);

		// Clear the map
		this.slotMap.clear();
	}
}
