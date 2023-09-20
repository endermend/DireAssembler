package DireAssembler.net.direassembler.mod.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

public abstract class DireClientPacket extends DireBasePacket{
	@SideOnly(Side.CLIENT)
	private final void preWrap() {
		// Set the player
		this.player = Minecraft.getMinecraft().thePlayer;

		// Execute the packet
		this.wrappedExecute();
	}

	@Override
	protected final boolean includePlayerInStream() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	protected abstract void wrappedExecute();

	@Override
	public final void execute() {
		// Ensure this is client side
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			this.preWrap();
		}
	}
}
