package DireAssembler.net.direassembler.mod.packet;

import io.netty.buffer.ByteBuf;
import DireAssembler.net.direassembler.mod.gui.GuiDireInscriber;
import DireAssembler.net.direassembler.mod.containers.ContainerDireInscriber.CoreSaveState;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

public class Packet_C_DireInscriber extends DireClientPacket{
	private static final byte MODE_SENDSAVE = 0;

	private static final CoreSaveState[] SAVE_STATES = CoreSaveState.values();

	private CoreSaveState saveState;

	private boolean justSaved;

	public static void sendSaveState(final EntityPlayer player, final CoreSaveState saveState,
			final boolean justSaved) {
		Packet_C_DireInscriber packet = new Packet_C_DireInscriber();

		// Set the player
		packet.player = player;

		// Set the mode
		packet.mode = Packet_C_DireInscriber.MODE_SENDSAVE;

		// Set the state
		packet.saveState = saveState;
		packet.justSaved = justSaved;

		// Send it
		NetworkHandler.sendPacketToClient(packet);
	}

	@Override
	protected void readData(final ByteBuf stream) {
		this.saveState = Packet_C_DireInscriber.SAVE_STATES[stream.readInt()];
		this.justSaved = stream.readBoolean();
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void wrappedExecute() {
		// Get the gui
		Gui gui = Minecraft.getMinecraft().currentScreen;

		// Ensure it is the knowledge inscriber
		if (gui instanceof GuiDireInscriber) {
			((GuiDireInscriber) gui).onReceiveSaveState(this.saveState, this.justSaved);
		}

	}

	@Override
	protected void writeData(final ByteBuf stream) {
		stream.writeInt(this.saveState.ordinal());
		stream.writeBoolean(this.justSaved);
	}

}
