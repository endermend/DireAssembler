package DireAssembler.net.direassembler.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.myname.mymodid.Tags;

import DireAssembler.net.direassembler.mod.common.CommonProxy;
import DireAssembler.net.direassembler.mod.gui.DireGUIHandler;
import DireAssembler.net.direassembler.mod.packet.NetworkHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Tags.MODID,dependencies = "required-after:Avaritia;required-after:appliedenergistics2@[rv2-stable-10,)" ,version = "0.0.1")
public class MyMod {
	public static final String MOD_ID = Tags.MODID;
    public static final Logger LOG = LogManager.getLogger(MOD_ID);
    public static MyMod instance;
    
    @SidedProxy(
        clientSide = "DireAssembler.net.direassembler.mod.common.ClientProxy",
        serverSide = "DireAssembler.net.direassembler.mod.common.CommonProxy")
    public static CommonProxy proxy;
    public static final Tab tab = Tab.getInstance();
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	instance = this;
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new DireGUIHandler());
    	NetworkHandler.registerPackets();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
