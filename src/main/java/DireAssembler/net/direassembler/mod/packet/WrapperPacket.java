package DireAssembler.net.direassembler.mod.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public abstract class WrapperPacket implements IMessage {
	private DireBasePacket embeddedPacket;

	/**
	 * Required.
	 */
	public WrapperPacket() {
	}

	/**
	 * Constructs the wrapper, wrapping the specified packet.
	 *
	 * @param packet
	 */
	public WrapperPacket(final DireBasePacket packet) {
		this.embeddedPacket = packet;
	}

	/**
	 * Executes the packet.
	 */
	public void execute() {
		if (this.embeddedPacket != null) {
			this.embeddedPacket.execute();
		}
	}

	@Override
	public void fromBytes(final ByteBuf stream) {
		// Read the id
		short id = stream.readShort();
		if (id == -1) {
			// Invalid packet
			return;
		}

		// Get the class for that id
		Class epClass = NetworkHandler.getPacketClassFromID(id);
		if (epClass == null) {
			return;
		}

		// Construct the class
		try {
			this.embeddedPacket = (DireBasePacket) epClass.newInstance();

			// Pass to packet
			this.embeddedPacket.fromBytes(stream);
		} catch (Exception e) {
			// Packet did not have default constructor
		}
	}

	@Override
	public void toBytes(ByteBuf stream) {
		if (this.embeddedPacket != null) {
			// Write the id
			short id = NetworkHandler.getPacketID(this.embeddedPacket);
			stream.writeShort(id);

			// Call embedded
			this.embeddedPacket.toBytes(stream);
		} else {
			// Write -1
			stream.writeShort(-1);
		}
	}
}
