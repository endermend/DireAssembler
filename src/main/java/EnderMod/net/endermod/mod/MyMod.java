package EnderMod.net.endermod.mod;

import static EnderMod.net.endermod.mod.MyMod.MOD_ID;

import EnderMod.net.endermod.mod.common.CommonProxy;
import EnderMod.net.endermod.mod.tabs.tabEnderMod;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MOD_ID, dependencies = "required-after:Thaumcraft;required-after:ThaumicTinkerer")
public class MyMod {

    public static final String MOD_ID = "endermod";
    public static MyMod instance;
    @SidedProxy(
        clientSide = "EnderMod.net.endermod.mod.common.ClientProxy",
        serverSide = "EnderMod.net.endermod.mod.common.CommonProxy")
    public static CommonProxy proxy;
    public static final tabEnderMod tab = tabEnderMod.getInstance();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        instance = this;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
