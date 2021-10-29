package envirorot.handlers.Legacy;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.logging.log4j.Level;

import envirorot.core.EM_ConfigHandler;
import envirorot.core.EM_Settings;
import envirorot.core.EnviroRot;

public class ConfigLegacy extends LegacyHandler
{
	// Dirs for Custom Files
	private static String configPath = "config/envirorot/";
	private static String customPath = configPath + "CustomProperties/";
	private static File configFile = new File(configPath + "EnviroRot.cfg");
	private static Configuration config;
	private static boolean didRun = false;
	
	@Override
	public boolean initCheck() 
	{

		if(configFile.exists() && !configFile.isDirectory())
		{
			try
			{
				config = new Configuration(configFile, true);
				config.load();
			} catch(Exception e)
			{
				EnviroRot.logger.log(Level.WARN, "Failed to load Legacy configuration file!", e);
				return false;
			}
			
		    EnviroRot.logger.log(Level.INFO, "Legacy: Config File Loaded");
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void runLegacy() 
	{
		// Version 0
		loadGeneralConfig(configFile);
		MoveCustomProperties();
		this.didRun = true;
	}
	
	
	@Override
	public boolean didRun() 
	{
		// TODO Auto-generated method stub
		return this.didRun;
	}
	
	private static void MoveCustomProperties()
	{
		File customsDir = new File(customPath);
		
		if(customsDir.exists() && customsDir.listFiles().length > 0)
		{
			
			try
			{
				File sourceDir = new File(customPath);
				
				CopyOption[] options = new CopyOption[]{};
				
				File newPath = new File(EM_ConfigHandler.defaultProfile + EM_ConfigHandler.customPath);
				
				
				if(!newPath.isDirectory())Files.createDirectories(newPath.toPath());
				
				for(File file : sourceDir.listFiles())
				{
					Files.move(sourceDir.toPath().resolve(file.getName()) , newPath.toPath().resolve(file.getName()), options);	
				}
				
				if(sourceDir.isDirectory())Files.delete(sourceDir.toPath());
				
				
				//Files.move(source, target, options)
				
			}
			catch(IOException e)
			{
				EnviroRot.logger.log(Level.ERROR, "Legacy Failed to Copy Custom Configs to New Dir! " + e);
			}
			
		}
		
	}
	
	
	/**
	 * Register all property types and their category names here. The rest is handled automatically.
	 */
	
	private static void loadGeneralConfig(File file)
	{	
		//General Settings
		EM_Settings.foodRotTime = config.get(Configuration.CATEGORY_GENERAL, "Default spoil time (days)", 7).getInt(7);
		
		// Config Options
		String ConSetCat = "Config";
		
		config = null;

		try {
			Files.delete(configFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			EnviroRot.logger.log(Level.ERROR, "Legacy Tried to Remove Envirorot.cfg... But failed! " +  e);
		}

	}
	
	/**
	 * @deprecated Use config.getInt(...) instead as it provides min & max value caps
	 */
	@Deprecated
	private static int getConfigIntWithMinInt(Property prop, int min)
	{
		if (prop.getInt(min) >= min) {
			return prop.getInt(min);
		} else {
			prop.set(min);
			return min;
		}
	}
}
