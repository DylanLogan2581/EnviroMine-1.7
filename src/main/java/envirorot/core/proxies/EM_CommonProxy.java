package envirorot.core.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import envirorot.handlers.EM_EventManager;
import envirorot.handlers.EM_ServerScheduledTickHandler;
import envirorot.world.EM_WorldData;

public class EM_CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public boolean isOpenToLAN()
	{
		return false;
	}
	
	public void registerTickHandlers() {
		FMLCommonHandler.instance().bus().register(new EM_ServerScheduledTickHandler());
	}
	
	public void registerEventHandlers()
	{
		EM_EventManager eventManager = new EM_EventManager();
		MinecraftForge.EVENT_BUS.register(eventManager);
		FMLCommonHandler.instance().bus().register(eventManager);
	}
	
	public void preInit(FMLPreInitializationEvent event)
	{
		
	}
	
	public void init(FMLInitializationEvent event)
	{
		
	}
	
	public void postInit(FMLPostInitializationEvent event)
	{
	
	}
}
