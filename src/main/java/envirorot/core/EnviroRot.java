package envirorot.core;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import envirorot.core.proxies.EM_CommonProxy;
import envirorot.handlers.ObjectHandler;
import envirorot.handlers.Legacy.LegacyHandler;
import envirorot.network.packet.PacketAutoOverride;
import envirorot.network.packet.PacketEnviroRot;
import envirorot.network.packet.PacketServerOverride;
import envirorot.world.EM_WorldData;

@Mod(modid = EM_Settings.ModID, name = EM_Settings.Name, version = EM_Settings.Version)
public class EnviroRot
{
	public static Logger logger;
	public static EnviroTab enviroTab;
	@Instance(EM_Settings.ModID)
	public static EnviroRot instance;
	@SidedProxy(clientSide = EM_Settings.Proxy + ".EM_ClientProxy", serverSide = EM_Settings.Proxy + ".EM_CommonProxy")
	public static EM_CommonProxy proxy;
	public SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		enviroTab = new EnviroTab("envirorot.enviroTab");
		LegacyHandler.preInit();
		LegacyHandler.init();
		proxy.preInit(event);
		ObjectHandler.initItems();
		ObjectHandler.registerItems();
		ObjectHandler.initBlocks();
		ObjectHandler.registerBlocks();
		
		// Load Configuration files And Custom files
		EM_ConfigHandler.initConfig();
		ObjectHandler.registerEntities();
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(EM_Settings.Channel);
		this.network.registerMessage(PacketEnviroRot.HandlerServer.class, PacketEnviroRot.class, 0, Side.SERVER);
		this.network.registerMessage(PacketEnviroRot.HandlerClient.class, PacketEnviroRot.class, 1, Side.CLIENT);
		this.network.registerMessage(PacketAutoOverride.Handler.class, PacketAutoOverride.class, 2, Side.CLIENT);
		this.network.registerMessage(PacketServerOverride.Handler.class, PacketServerOverride.class, 3, Side.CLIENT);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
		proxy.registerTickHandlers();
		proxy.registerEventHandlers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
		EM_ConfigHandler.initConfig(); // Second pass for object initialized after pre-init
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		MinecraftServer server = MinecraftServer.getServer();
		ICommandManager command = server.getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
	}
}
