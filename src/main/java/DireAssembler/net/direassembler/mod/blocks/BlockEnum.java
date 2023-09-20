package DireAssembler.net.direassembler.mod.blocks;

import net.minecraft.block.Block;

public enum BlockEnum {
	DIRE_ASSEMBLER("block.dire_assembler", new BlockDireAssembler()),
	DIRE_INSCRIBER("block.dire_inscriber", new BlockDireInscriber());

	public static final BlockEnum[] VALUES = BlockEnum.values();

	private final Block block;

	private final String unlocalizedName;

	private BlockEnum(final String unlocalizedName, final Block block) {
		this.unlocalizedName = unlocalizedName;
		this.block = block;
		block.setBlockName(this.unlocalizedName);
	}

	public Block getBlock() {
		return this.block;
	}

	// Return the name
	public String getUnlocalizedName() {
		return this.unlocalizedName;
	}
	
}
