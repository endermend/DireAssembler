package DireAssembler.net.direassembler.mod.common;

import DireAssembler.net.direassembler.mod.blocks.BlockEnum;
import DireAssembler.net.direassembler.mod.items.ItemEnum;
import DireAssembler.net.direassembler.mod.tiles.TileDireAssembler;
import DireAssembler.net.direassembler.mod.tiles.TileDireInscriber;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemBlock;

public class CommonProxy {

	private void registerBlocks() {
		for (BlockEnum block : BlockEnum.VALUES) {
			GameRegistry.registerBlock(block.getBlock(), ItemBlock.class, block.getUnlocalizedName());
		}
	}

	private void registerItems() {
		for (ItemEnum item : ItemEnum.VALUES) {
			GameRegistry.registerItem(item.getItem(), item.getUnlocalizedName());
		}
	}

	public void preInit(FMLPreInitializationEvent event) {
		Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
		registerBlocks();
		registerItems();
		 GameRegistry.registerTileEntity(TileDireAssembler.class, "container.tiledireassembler");
		 GameRegistry.registerTileEntity(TileDireInscriber.class, "container.tiledireinscriber");
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

}
