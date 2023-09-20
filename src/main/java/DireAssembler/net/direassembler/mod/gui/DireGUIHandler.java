package DireAssembler.net.direassembler.mod.gui;

import DireAssembler.net.direassembler.mod.MyMod;
import DireAssembler.net.direassembler.mod.containers.ContainerDireAssembler;
import DireAssembler.net.direassembler.mod.containers.ContainerDireInscriber;
import appeng.api.parts.IPartHost;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DireGUIHandler implements IGuiHandler {
	public static final int DIRE_ASSEMBLER_ID = 0;
	public static final int DIRE_INSCRIBER_ID = 1;

	public static void launchGui(final int ID, final EntityPlayer player, final World world, final int x, final int y,
			final int z) {
		player.openGui(MyMod.instance, ID, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int ID, final EntityPlayer player, final World world, final int x, final int y,
			final int z) {

		// Check basic ID's
		switch (ID) {
		// Is this the dire assembler?
		case DireGUIHandler.DIRE_ASSEMBLER_ID:
			return new GuiDireAssembler(player, world, x, y, z);

		// Is this the knowledge inscriber?
		case DireGUIHandler.DIRE_INSCRIBER_ID:
			return new GuiDireInscriber(player, world, x, y, z);

		default:
			return null;
		}
		// No matching GUI element found
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		// Is this the dire assembler?
		case DireGUIHandler.DIRE_ASSEMBLER_ID:
			return new ContainerDireAssembler(player, world, x, y, z);
		// Is this the dire inscriber?
		case DireGUIHandler.DIRE_INSCRIBER_ID:
			return new ContainerDireInscriber(player, world, x, y, z);

		default:
			return null;
		}
	}
}
