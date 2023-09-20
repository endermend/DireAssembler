package DireAssembler.net.direassembler.mod.packet;

import java.util.HashMap;

import com.myname.mymodid.Tags;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

public class NetworkHandler {
	/**
	 * Channel used to send packets.
	 */
	private static SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MODID);

	/**
	 * Next packet ID.
	 */
	private static short nextID = 0;

	/**
	 * Maps a class to a unique id.
	 */
	private static HashMap<Class, Short> ClassToID = new HashMap<Class, Short>();

	/**
	 * Maps a unique id to a class.
	 */
	private static HashMap<Short, Class> IDToClass = new HashMap<Short, Class>();

	/**
	 * Registers a packet.
	 *
	 * @param packet
	 */
	private static void registerPacket(final Class<? extends DireBasePacket> packetClass) {
		NetworkHandler.ClassToID.put(packetClass, NetworkHandler.nextID);
		NetworkHandler.IDToClass.put(NetworkHandler.nextID, packetClass);
		++NetworkHandler.nextID;
	}

	/**
	 * Get's the class for the packet with the specified ID.
	 *
	 * @param id
	 * @return
	 */
	public static Class getPacketClassFromID(final Short id) {
		return NetworkHandler.IDToClass.get(id);
	}

	/**
	 * Gets the ID for the specified packet.
	 *
	 * @param packet
	 * @return
	 */
	public static short getPacketID(final DireBasePacket packet) {
		Object id = NetworkHandler.ClassToID.get(packet.getClass());
		if (id != null) {
			return (short) id;
		}
		return -1;
	}

	/**
	 * Registers all packets
	 */
	public static void registerPackets() {
		// Register channel client side handler
		NetworkHandler.channel.registerMessage(HandlerClient.class, WrapperPacket_C.class, 1, Side.CLIENT);

		// Register channel server side handler
		NetworkHandler.channel.registerMessage(HandlerServer.class, WrapperPacket_S.class, 2, Side.SERVER);

		// Knowledge inscriber
		registerPacket(Packet_C_DireInscriber.class);
		registerPacket(Packet_S_DireInscriber.class);
	}

	public static void sendAreaPacketToClients(final DireAreaPacket areaPacket, final float range) {
		// Create the wrapper packet
		WrapperPacket wrapper = new WrapperPacket_C(areaPacket);

		// Create the target point
		TargetPoint targetPoint = new TargetPoint(areaPacket.getDimension(), areaPacket.getX(), areaPacket.getY(),
				areaPacket.getZ(), range);

		// Send the packet
		NetworkHandler.channel.sendToAllAround(wrapper, targetPoint);

	}

	public static void sendPacketToClient(final DireClientPacket clientPacket) {
		// Create the wrapper packet
		WrapperPacket wrapper = new WrapperPacket_C(clientPacket);

		// Send the packet
		NetworkHandler.channel.sendTo(wrapper, (EntityPlayerMP) clientPacket.player);
	}

	public static void sendPacketToServer(final DireServerPacket serverPacket) {
		// Create the wrapper packet
		WrapperPacket wrapper = new WrapperPacket_S(serverPacket);

		// Send the packet
		NetworkHandler.channel.sendToServer(wrapper);
	}
}
