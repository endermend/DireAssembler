package DireAssembler.net.direassembler.mod.items;

import net.minecraft.item.Item;

public enum ItemEnum {
	DIRE_CORE("item.dire.core", new ItemDireCore());
	
	public static final ItemEnum[] VALUES = ItemEnum.values();
	
	private final Item item;
	private final String unlocalizedName;
	
	private ItemEnum(final String unlocalizedName, final Item item) {
		this.unlocalizedName = unlocalizedName;
		this.item = item;
		item.setUnlocalizedName(this.unlocalizedName);
	}
	public Item getItem() {
		return this.item;
	}

	// Return the name
	public String getUnlocalizedName() {
		return this.unlocalizedName;
	}
}
