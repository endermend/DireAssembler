package DireAssembler.net.direassembler.mod.gui.buttons;

import java.util.List;

import DireAssembler.net.direassembler.mod.common.DireStrings;
import DireAssembler.net.direassembler.mod.containers.ContainerDireInscriber.CoreSaveState;
import DireAssembler.net.direassembler.mod.gui.AEStateIconsEnum;

public class GuiButtonSaveDelete extends DireStateButton {

	private String cachedTooltip;

	public GuiButtonSaveDelete(final int ID, final int xPosition, final int yPosition,
			final CoreSaveState initialState) {
		// Call super
		super(ID, xPosition, yPosition, 16, 16, null, 0, 0, AEStateIconsEnum.REGULAR_BUTTON);

		// Initial state
		this.setSaveState(initialState);
	}

	@Override
	public void getTooltip(final List<String> tooltip) {
		tooltip.add(this.cachedTooltip);
	}

	/**
	 * Sets the save state of the button.
	 *
	 * @param saveState
	 */
	public void setSaveState(final CoreSaveState saveState) {
		switch (saveState) {
		case Disabled_InvalidRecipe:
			this.enabled = false;
			this.stateIcon = null;
			this.cachedTooltip = DireStrings.TooltipButton_InscriberInvalid.getLocalized();
			break;

		case Disabled_CoreFull:
			this.enabled = false;
			this.stateIcon = null;
			this.cachedTooltip = DireStrings.TooltipButton_InscriberFull.getLocalized();
			break;

		case Enabled_Delete:
			this.enabled = true;
			this.stateIcon = AEStateIconsEnum.DELETE;
			this.cachedTooltip = DireStrings.TooltipButton_InscriberDelete.getLocalized();
			break;

		case Enabled_Save:
			this.enabled = true;
			this.stateIcon = AEStateIconsEnum.SAVE;
			this.cachedTooltip = DireStrings.TooltipButton_InscriberSave.getLocalized();
			break;

		case Disabled_MissingCore:
			this.enabled = false;
			this.stateIcon = null;
			this.cachedTooltip = DireStrings.TooltipButton_InscriberMissing.getLocalized();
			break;

		}
	}
}
