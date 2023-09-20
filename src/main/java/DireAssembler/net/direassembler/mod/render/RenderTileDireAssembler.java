package DireAssembler.net.direassembler.mod.render;

import org.lwjgl.opengl.GL11;

import com.myname.mymodid.Tags;

import DireAssembler.net.direassembler.mod.common.DireAll;
import DireAssembler.net.direassembler.mod.tiles.TileDireAssembler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderTileDireAssembler extends TileEntitySpecialRenderer {
	private final ModelDireAssembler assemblerModel = new ModelDireAssembler();
	private final ResourceLocation assemblerTexture = new ResourceLocation(Tags.MODID,
			"textures/models/dire.assembler.png");
	private final Block assemblerBlock = DireAll.instance().blocks().DireAssembler.getBlock();

	private void renderAssembler( final TileDireAssembler assemblerTile, final World world, final int x, final int y, final int z )
	{
		// Ensure there is a world object
		if( world != null )
		{
			// Get the block lightning
			float mixedBrightness = this.assemblerBlock.getMixedBrightnessForBlock( world, x, y, z );
			int light = world.getLightBrightnessForSkyBlocks( x, y, z, 0 );

			int l1 = light % 65536;
			int l2 = light / 65536;

			// Set the color based on the mixed brightness
			Tessellator.instance.setColorOpaque_F( mixedBrightness, mixedBrightness, mixedBrightness );

			// Set the lightmap coords
			OpenGlHelper.setLightmapTextureCoords( OpenGlHelper.lightmapTexUnit, l1, l2 );
		}
		else
		{
			// No world object, render at full brightness
			Tessellator.instance.setColorOpaque_F( 1.0F, 1.0F, 1.0F );
		}

		// Push the matrix
		GL11.glPushMatrix();

		// Center the model
		GL11.glTranslatef( 0.5F, 0.5F, 0.5F );

		// Bind the model texture
		Minecraft.getMinecraft().renderEngine.bindTexture( this.assemblerTexture );

		// Scale down
		GL11.glScalef( 0.047F, 0.047F, 0.047F );

		// Render the assembler
		this.assemblerModel.render( null, 0, 0, -0.1F, 0, 0, 0.625F );

		// Pop the matrix
		GL11.glPopMatrix();
	}

	/**
	 * Called when the assembler needs to be rendered.
	 */
	@Override
	public void renderTileEntityAt( final TileEntity tileEntity, final double d, final double d1, final double d2, final float f )
	{
		// Push the GL matrix
		GL11.glPushMatrix();

		// Computes the proper place to draw
		GL11.glTranslatef( (float)d, (float)d1, (float)d2 );

		// Get the assembler
		TileDireAssembler assemblerTile = (TileDireAssembler)tileEntity;

		// Render the gearbox
		this.renderAssembler( assemblerTile, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord );

		// Pop the GL matrix
		GL11.glPopMatrix();

	}

}
