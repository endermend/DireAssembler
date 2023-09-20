package DireAssembler.net.direassembler.mod.common;

import DireAssembler.net.direassembler.mod.blocks.BlockEnum;
import DireAssembler.net.direassembler.mod.items.IThEItemDescription;
import DireAssembler.net.direassembler.mod.items.ThEItemDescription;

public class DireBlocks {

	public IThEItemDescription DireAssembler;
	public IThEItemDescription DireInscriber;

	public DireBlocks() {
		this.DireAssembler = new ThEItemDescription(BlockEnum.DIRE_ASSEMBLER.getBlock());
		this.DireInscriber = new ThEItemDescription(BlockEnum.DIRE_INSCRIBER.getBlock());
	}
}
