package DireAssembler.net.direassembler.mod.common;

import DireAssembler.net.direassembler.mod.render.RenderTileDireAssembler;
import DireAssembler.net.direassembler.mod.render.TileAsItemRenderer;
import DireAssembler.net.direassembler.mod.textures.BlockTextureManager;
import DireAssembler.net.direassembler.mod.tiles.TileDireAssembler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		ClientRegistry.bindTileEntitySpecialRenderer(TileDireAssembler.class,
				(TileEntitySpecialRenderer) new RenderTileDireAssembler());
		MinecraftForgeClient.registerItemRenderer(DireAll.instance().blocks().DireAssembler.getItem(),
				(IItemRenderer) new TileAsItemRenderer((TileEntitySpecialRenderer) new RenderTileDireAssembler(),
						new TileDireAssembler()));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@SubscribeEvent
	public void registerTextures(final TextureStitchEvent.Pre event) {
		// Register all block textures
		for (BlockTextureManager texture : BlockTextureManager.VALUES) {
			texture.registerTexture(event.map);
		}

	}
}
