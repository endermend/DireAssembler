package DireAssembler.net.direassembler.mod.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class HandlerServer implements IMessageHandler<WrapperPacket_S, IMessage> {
	@Override
	public IMessage onMessage(final WrapperPacket_S message, final MessageContext ctx) {
		message.execute();
		return null;
	}
}
