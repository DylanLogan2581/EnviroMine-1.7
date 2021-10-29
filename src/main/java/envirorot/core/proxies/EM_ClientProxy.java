package envirorot.core.proxies;


import java.lang.reflect.Method;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import envirorot.blocks.tiles.TileEntityEsky;
import envirorot.blocks.tiles.TileEntityFreezer;
import envirorot.client.renderer.tileentity.RenderSpecialHandler;
import envirorot.client.renderer.tileentity.TileEntityEskyRenderer;
import envirorot.client.renderer.tileentity.TileEntityFreezerRenderer;
import envirorot.core.EM_Settings;
import envirorot.core.EnviroRot;
import envirorot.handlers.ObjectHandler;

public class EM_ClientProxy extends EM_CommonProxy
{
	
	
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public boolean isOpenToLAN()
	{
		if (Minecraft.getMinecraft().isIntegratedServerRunning())
		{
			return Minecraft.getMinecraft().getIntegratedServer().getPublic();
		} else
		{
			return false;
		}
	}
	
	@Override
	public void registerTickHandlers()
	{
		super.registerTickHandlers();
	}
	
	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new ObjectHandler());
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
        
		initRenderers();
		
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRenderers()
	{
		ObjectHandler.renderSpecialID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(ObjectHandler.renderSpecialID, new RenderSpecialHandler());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEsky.class, new TileEntityEskyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFreezer.class, new TileEntityFreezerRenderer());
		
		
		try 
		{
			boolean isLoadedRenderApi = false;
			if (Loader.isModLoaded("RenderPlayerAPI"))
			{
				//	ModelPlayerAPI.register(EM_Settings.ModID, ModelPlayerEM.class);
				//	RenderPlayerAPI.register(EM_Settings.ModID, RenderPlayerEM.class);
			 
				EnviroRot.logger.log(Level.WARN, "Envirorot Doesn't support Player-API/Render-API yet! Config setting \"Render 3D Gear\" set to false");
			 
				isLoadedRenderApi = true;
			}
		
		}catch(ClassCastException e)
		{
			EnviroRot.logger.log(Level.ERROR, "Tried to Render Envirorot Gear, but Failed! Known issues with  Render Player API.:- "+ e);
		}
		

	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

		VoxelMenu();
	}
	
	
	public void VoxelMenu()
	{
		try
		{

			Class<? extends GuiMainMenu> ingameGuiClass = (Class<? extends GuiMainMenu>) Class.forName("com.thevoxelbox.voxelmenu.ingame.GuiIngameMenu");
			Method mRegisterCustomScreen = ingameGuiClass.getDeclaredMethod("registerCustomScreen", String.class, Class.class, String.class);
			
		
		} catch (ClassNotFoundException ex) { // This means VoxelMenu does not
			// 	exist
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
