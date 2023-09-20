package DireAssembler.net.direassembler.mod.packet;

public abstract class DireServerPacket extends DireBasePacket {
	@Override
	protected final boolean includePlayerInStream() {
		return true;
	}
}
