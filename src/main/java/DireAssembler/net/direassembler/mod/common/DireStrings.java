package DireAssembler.net.direassembler.mod.common;

import com.myname.mymodid.Tags;

import net.minecraft.util.StatCollector;

public enum DireStrings {
	Item_DireCore("item.dire_core", false),
	TooltipButton_InscriberInvalid("tooltip.button.inscriber.invalid", false),
	TooltipButton_InscriberFull("tooltip.button.inscriber.full", false),
	TooltipButton_InscriberDelete("tooltip.button.inscriber.delete", false),
	TooltipButton_InscriberSave("tooltip.button.inscriber.save", false),
	TooltipButton_InscriberMissing("tooltip.button.inscriber.missing", false),
	Title_DireInscriber("gui.dire.inscriber.title", false),
	Title_DireAssembler("gui.dire.assembler.title", false);

	private String unlocalized;
	private boolean isDotName;

	private DireStrings(final String unloc, final boolean isDotName) {
		this.unlocalized = Tags.MODID + "." + unloc;
		this.isDotName = isDotName;
	}

	/**
	 * Gets the localized string.
	 *
	 * @return
	 */
	public String getLocalized() {
		if (this.isDotName) {
			return StatCollector.translateToLocal(this.unlocalized + ".name");
		}

		return StatCollector.translateToLocal(this.unlocalized);
	}

	/**
	 * Gets the unlocalized string.
	 *
	 * @return
	 */
	public String getUnlocalized() {
		return this.unlocalized;
	}
}
