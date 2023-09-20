package DireAssembler.net.direassembler.mod.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class HandlerClient implements IMessageHandler<WrapperPacket_C, IMessage> {
	@Override
	public IMessage onMessage(final WrapperPacket_C message, final MessageContext ctx) {
		message.execute();
		return null;
	}
}
