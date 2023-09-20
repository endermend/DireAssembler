package DireAssembler.net.direassembler.mod.textures;

import com.myname.mymodid.Tags;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public enum BlockTextureManager {

	DIRE_ASSEMBLER (new String[] { "dire.assembler.fallback" }),
	DIRE_INSCRIBER (new String[] { "dire.inscriber.side", "dire.inscriber.top", "dire.inscriber.bottom" });

	public static final BlockTextureManager[] VALUES = BlockTextureManager.values();

	private String[] textureNames;
	private IIcon[] textures;

	private BlockTextureManager(final String... textureNames) {
		this.textureNames = textureNames;
		this.textures = new IIcon[this.textureNames.length];
	}

	public IIcon getTexture(int index) {
		return this.textures[index];
	}

	public IIcon[] getTextures() {
		return this.textures;
	}

	public void registerTexture(final TextureMap textureMap) {
		if (textureMap.getTextureType() != 0) {
			return;
		}

		String header = Tags.MODID + ":";
		for (int i = 0; i < this.textureNames.length; i++) {
			this.textures[i] = textureMap.registerIcon(header + this.textureNames[i]);
		}

	}
}
