package DireAssembler.net.direassembler.mod.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import com.google.common.base.Charsets;
import appeng.api.parts.IPartHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class DireBasePacket implements IMessage {
	/**
	 * Starting size of buffers compressed buffers, 1 Megabyte
	 */
	private static final int COMPRESSED_BUFFER_SIZE = (1024 * 1024) * 1;

	/**
	 * Player entity
	 */
	public EntityPlayer player;

	/**
	 * Used by subclasses to distinguish what's in the stream.
	 */
	protected byte mode;

	/**
	 * If true will compress subclass data streams.
	 */
	protected boolean useCompression;

	/**
	 * Creates an empty packet (called from the netty core)
	 */
	public DireBasePacket() {
		this.player = null;
		this.mode = -1;
		this.useCompression = false;
	}

	/**
	 * Gets the loaded world on the client side.
	 *
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	private static World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	/**
	 * Reads an AE itemstack from the stream.
	 *
	 * @param stream
	 * @return
	 */
	protected static IAEItemStack readAEItemStack(final ByteBuf stream) {
		IAEItemStack itemStack;
		try {
			itemStack = AEItemStack.loadItemStackFromPacket(stream);

			return itemStack;
		} catch (IOException e) {
		}

		return null;

	}

	/**
	 * Reads an itemstack from the stream.
	 *
	 * @param stream
	 * @return
	 */
	protected static ItemStack readItemstack(final ByteBuf stream) {
		return ByteBufUtils.readItemStack(stream);
	}

	/**
	 * Reads a player entity from the stream.
	 *
	 * @param stream
	 * @return
	 */
	protected static EntityPlayer readPlayer(final ByteBuf stream) {
		EntityPlayer player = null;

		if (stream.readBoolean()) {
			World playerWorld = readWorld(stream);
			player = playerWorld.getPlayerEntityByName(readString(stream));
		}

		return player;
	}

	/**
	 * Reads a string from the stream.
	 *
	 * @param stream
	 * @return
	 */
	protected static String readString(final ByteBuf stream) {
		byte[] stringBytes = new byte[stream.readInt()];

		stream.readBytes(stringBytes);

		return new String(stringBytes, Charsets.UTF_8);
	}

	/**
	 * Reads a tile entity from the stream.
	 *
	 * @param stream
	 * @return
	 */
	protected static TileEntity readTileEntity(final ByteBuf stream) {
		World world = DireBasePacket.readWorld(stream);

		return world.getTileEntity(stream.readInt(), stream.readInt(), stream.readInt());
	}

	/**
	 * Reads a world from the stream.
	 *
	 * @param stream
	 * @return
	 */
	protected static World readWorld(final ByteBuf stream) {
		World world = DimensionManager.getWorld(stream.readInt());

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (world == null) {
				world = getClientWorld();
			}
		}

		return world;
	}

	/**
	 * Writes an AE itemstack to the stream.
	 *
	 * @param itemStack
	 * @param stream
	 */
	protected static void writeAEItemStack(final IAEItemStack itemStack, final ByteBuf stream) {
		// Do we have a valid stack?
		if (itemStack != null) {
			// Write into the stream
			try {
				itemStack.writeToPacket(stream);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Writes an itemstack into the stream.
	 *
	 * @param stack
	 * @param stream
	 */
	protected static void writeItemstack(final ItemStack stack, final ByteBuf stream) {
		ByteBufUtils.writeItemStack(stream, stack);
	}

	/**
	 * Writes a player to the stream.
	 *
	 * @param player
	 * @param stream
	 */
	@SuppressWarnings("null")
	protected static void writePlayer(final EntityPlayer player, final ByteBuf stream) {
		boolean validPlayer = (player != null);

		stream.writeBoolean(validPlayer);

		if (validPlayer) {
			writeWorld(player.worldObj, stream);
			writeString(player.getCommandSenderName(), stream);
		}
	}

	/**
	 * Writes a string to the stream.
	 *
	 * @param string
	 * @param stream
	 */
	protected static void writeString(final String string, final ByteBuf stream) {
		byte[] stringBytes = string.getBytes(Charsets.UTF_8);

		stream.writeInt(stringBytes.length);

		stream.writeBytes(stringBytes);
	}

	/**
	 * Writes a tile entity to the stream.
	 *
	 * @param entity
	 * @param stream
	 */
	protected static void writeTileEntity(final TileEntity entity, final ByteBuf stream) {
		writeWorld(entity.getWorldObj(), stream);
		stream.writeInt(entity.xCoord);
		stream.writeInt(entity.yCoord);
		stream.writeInt(entity.zCoord);
	}

	/**
	 * Writes a world to the stream.
	 *
	 * @param world
	 * @param stream
	 */
	protected static void writeWorld(final World world, final ByteBuf stream) {
		stream.writeInt(world.provider.dimensionId);
	}

	private void fromCompressedBytes(final ByteBuf packetStream) {
		// Create a new data stream
		ByteBuf decompressedStream = Unpooled.buffer(DireBasePacket.COMPRESSED_BUFFER_SIZE);

		try (InputStream inStream = new InputStream() {

			@Override
			public int read() throws IOException {
				// Is there anymore data to read from the packet stream?
				if (packetStream.readableBytes() <= 0) {
					// Return end marker
					return -1;
				}

				// Return the byte
				return packetStream.readByte() & 0xFF;
			}
		};

				// Create the decompressor
				GZIPInputStream decompressor = new GZIPInputStream(inStream)) {

			// Create a temporary holding array
			byte[] holding = new byte[512];

			// Decompress
			while (decompressor.available() != 0) {
				// Read into the holding array
				int bytesRead = decompressor.read(holding);

				// Did we read any data?
				if (bytesRead > 0) {
					// Write the holding array into the decompressed stream
					decompressedStream.writeBytes(holding, 0, bytesRead);
				}
			}

			// Close the decompressor
			decompressor.close();

			// Reset stream position
			decompressedStream.readerIndex(0);

			// Pass to subclass
			this.readData(decompressedStream);

		} catch (IOException e) {
		}
	}

	/**
	 * Creates a new stream, calls to the subclass to write into it, then compresses
	 * it into the packet stream.
	 *
	 * @param packetStream
	 */
	private void toCompressedBytes(final ByteBuf packetStream) {
		// Create a new data stream
		ByteBuf streamToCompress = Unpooled.buffer(DireBasePacket.COMPRESSED_BUFFER_SIZE);

		// Pass to subclass
		this.writeData(streamToCompress);

		// Create the compressor
		try (OutputStream outStream = new OutputStream() {

			@Override
			public void write(final int byteToWrite) throws IOException {
				// Write the byte to the packet stream
				packetStream.writeByte(byteToWrite & 0xFF);
			}
		};

				GZIPOutputStream compressor = new GZIPOutputStream(outStream) {
					{
						this.def.setLevel(Deflater.BEST_COMPRESSION);
					}
				}) {

			// Compress
			compressor.write(streamToCompress.array(), 0, streamToCompress.writerIndex());

			// Close the compressor
			compressor.close();
		} catch (IOException e) {
			// Failed
		}
	}

	/**
	 * Return true if the player should be included in the data stream.
	 *
	 * @return
	 */
	protected abstract boolean includePlayerInStream();

	/**
	 * Allows subclasses to read data from the specified stream.
	 *
	 * @param stream
	 */
	protected abstract void readData(ByteBuf stream);

	/**
	 * Allows subclasses to write data into the specified stream.
	 *
	 * @param stream
	 */
	protected abstract void writeData(ByteBuf stream);

	/**
	 * Packet has been read and action can now take place.
	 */
	public abstract void execute();

	/**
	 * Reads data from the packet stream.
	 */
	@Override
	public void fromBytes(final ByteBuf stream) {
		// Read mode
		this.mode = stream.readByte();

		// Read player
		if (this.includePlayerInStream()) {
			this.player = DireBasePacket.readPlayer(stream);
		}

		// Read compression
		this.useCompression = stream.readBoolean();

		// Is there a compressed substream?
		if (this.useCompression) {
			this.fromCompressedBytes(stream);
		} else {
			// Pass stream directly to subclass
			this.readData(stream);
		}
	}

	/**
	 * Writes data into the packet stream.
	 */
	@Override
	public void toBytes(final ByteBuf stream) {
		// Write the mode
		stream.writeByte(this.mode);

		// Write the player
		if (this.includePlayerInStream()) {
			DireBasePacket.writePlayer(this.player, stream);
		}

		// Write if there is a compressed sub-stream.
		stream.writeBoolean(this.useCompression);

		// Is compression enabled?
		if (this.useCompression) {
			this.toCompressedBytes(stream);
		} else {
			// No compression, subclass writes directly into stream.
			this.writeData(stream);
		}
	}
}
