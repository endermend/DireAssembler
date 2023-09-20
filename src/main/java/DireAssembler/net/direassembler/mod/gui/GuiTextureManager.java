package DireAssembler.net.direassembler.mod.gui;

import com.myname.mymodid.Tags;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public enum GuiTextureManager {
	DIRE_ASSEMBLER("dire.assembler"),
	DIRE_INSCRIBER("dire.inscriber");

	private ResourceLocation texture;

	private GuiTextureManager(final String textureName) {
		// Create the resource location
		this.texture = new ResourceLocation(Tags.MODID, "textures/gui/" + textureName + ".png");
	}
	
	public ResourceLocation getTexture()
	{
		return this.texture;
	}
}
