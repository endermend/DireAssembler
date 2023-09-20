package DireAssembler.net.direassembler.mod.common;

import java.lang.reflect.Method;

import cpw.mods.fml.common.LoaderState;

public class DireAll {
	protected static DireAll instance = null;

	public final DireBlocks blocks;

	private DireAll() {
		this.blocks = new DireBlocks();
	}

	/**
	 * Gets the Thaumic Energistics API. Note: Only available after the PREINIT
	 * event.
	 */
	public static DireAll instance() {
		// Has the singleton been created?
		if (DireAll.instance == null) {
			// Create the singleton.
			DireAll.instance = new DireAll();
		}

		return DireAll.instance;
	}

	public DireBlocks blocks() {
		return this.blocks;
	}
}
