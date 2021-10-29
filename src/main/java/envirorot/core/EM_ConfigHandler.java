package envirorot.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.registry.EntityRegistry;
import envirorot.handlers.ObjectHandler;
import envirorot.trackers.properties.ItemProperties;
import envirorot.trackers.properties.RotProperties;
import envirorot.trackers.properties.helpers.PropertyBase;
import envirorot.utils.ModIdentification;
import envirorot.world.EM_WorldData;

public class EM_ConfigHandler
{
	// Dirs for Custom Files
	public static String configPath = "config/envirorot/";
	public static String customPath = "CustomProperties/";
	
	public static String profilePath = configPath + "profiles/";
	public static String defaultProfile = profilePath +"default/";
	
	/**
	 * Configuration version number. If changed the version file will be reset to defaults to prevent glitches
	 */
	public static final String CONFIG_VERSION = "1.0.0";
	
	/**
	 * The version of the configs last loaded from file. This will be compared to the version number above when determining whether a reset is necessary
	 */
	
	
	public static String loadedProfile = defaultProfile;
	
	static HashMap<String, PropertyBase> propTypes;
	public static HashMap<String, PropertyBase> globalTypes;
	
	public static List loadedConfigs = new ArrayList();
	
	
	
	/**
	 * Register all property types and their category names here. The rest is handled automatically.
	 */
	static
	{
		propTypes = new HashMap<String, PropertyBase>();
		
		propTypes.put(RotProperties.base.categoryName(), RotProperties.base);
		
		globalTypes = new HashMap<String, PropertyBase>();
		
	}

	
	public static void initProfile()
	{	
		/*EM_WorldData theWorldEM = EnviroRot.theWorldEM;
		
		String profile = theWorldEM.getProfile();
		
		// if profile is overriden than switch profiles
		if(EM_Settings.profileOverride) 
		{
			profile = EM_Settings.profileSelected;
			theWorldEM.setProfile(profile);
		}*/
		
		String profile = EM_Settings.profileSelected;
		
		System.out.println("LOADING PROFILE: " + profile);
		
		File profileDir = new File(profilePath + profile +"/"+ customPath);

		//CheckDir(profileDir);
		
		if(!profileDir.exists())
		{
			try
			{
				profileDir.mkdirs();
			} catch(Exception e)
			{
				EnviroRot.logger.log(Level.ERROR, "Unable to create directories for profile", e);
			}
		}
		
		if(!profileDir.exists())
		{
			EnviroRot.logger.log(Level.ERROR, "Failed to load Profile:"+ profile +". Loading Default");	
			profileDir = new File(defaultProfile + customPath);
			loadedProfile = defaultProfile;
		}else 
		{
			loadedProfile = profilePath + profile +"/";
			EnviroRot.logger.log(Level.INFO, "Loading Profile: "+ profile);
		}
		
		File ProfileSettings = new File(loadedProfile + profile +"_Settings.cfg");
		loadProfileConfig(ProfileSettings);
		
                // Now load Files from "Custom Objects"
		File[] customFiles = GetFileList(loadedProfile + customPath);
		for(int i = 0; i < customFiles.length; i++)
		{
			LoadCustomObjects(customFiles[i]);
		}
				
				
			
				
		Iterator<PropertyBase> iterator = propTypes.values().iterator();
				
		// Load non standard property files
		while(iterator.hasNext())
		{
			PropertyBase props = iterator.next();
				
			if(!props.useCustomConfigs())
			{
				props.customLoad();
			}
		}
		

		EnviroRot.logger.log(Level.INFO, "Loaded " + EM_Settings.itemProperties.size() + " item properties");
		EnviroRot.logger.log(Level.INFO, "Loaded " + EM_Settings.rotProperties.size() + " rot properties");

	}
	
	public static int initConfig()
	{
		// Check for Data Directory 
		//CheckDir(new File(customPath));
		
		EnviroRot.logger.log(Level.INFO, "Loading configs...");
	
		// Load Global Configs
		File configFile = new File(configPath + "Global_Settings.cfg");
		loadGlobalConfig(configFile);
		
		Iterator<PropertyBase> iterator = globalTypes.values().iterator();
		
		// Load non standard property files
		while(iterator.hasNext())
		{
			PropertyBase props = iterator.next();
				
			if(!props.useCustomConfigs())
			{
				props.customLoad();
			}
		}
		
		int Total = EM_Settings.itemProperties.size();
		
		return Total;
	}
	
	
	private static void loadGlobalConfig(File file)
	{
		Configuration config;
		try
		{
			config = new Configuration(file, true);
		} catch(Exception e)
		{
			EnviroRot.logger.log(Level.WARN, "Failed to load main configuration file!", e);
			return;
		}
		
		config.load();
	
		config.get("Do not Edit", "Current Config Version", CONFIG_VERSION).getString();

		// Config Options

		String ConSetCat = "Config";
		
		//Default Profile Override
		EM_Settings.profileSelected = config.get(ConSetCat, "Profile", EM_Settings.profileSelected).getString();
		EM_Settings.profileOverride = config.get(ConSetCat, "Override Profile", EM_Settings.profileOverride,  "Override Profile. It Can be used for servers to force profiles on servers or modpack. This Overrides any world loaded up. Name is Case Sensitive!").getBoolean(false);
		EM_Settings.enableConfigOverride = config.get(ConSetCat, "Client Config Override (SMP)", EM_Settings.enableConfigOverride, "[DISABLED][WIP] Temporarily overrides client configurations with the server's (NETWORK INTESIVE!)").getBoolean(EM_Settings.enableConfigOverride);
		
		config.save();
		
	
	}
	
	private static void loadProfileConfig(File file)
	{
		Configuration config;
		try
		{
			config = new Configuration(file, true);
		} catch(Exception e)
		{
			EnviroRot.logger.log(Level.WARN, "Failed to load main configuration file!", e);
			return;
		}
		
		config.load();
		
		//General Settings
		EM_Settings.foodSpoiling = config.get(Configuration.CATEGORY_GENERAL, "Enable food spoiling", EM_Settings.foodSpoiling).getBoolean(EM_Settings.foodSpoiling);
		EM_Settings.foodRotTime = config.get(Configuration.CATEGORY_GENERAL, "Default spoil time (days)", EM_Settings.foodRotTime).getInt(EM_Settings.foodRotTime);
			
		// Config Options
		String ConSetCat = "Config";
		Property genConfig = config.get(ConSetCat, "Generate Blank Configs", false, "Will attempt to find and generate blank configs for any custom items/blocks/etc loaded before EnviroRot. Pack developers are highly encouraged to enable this! (Resets back to false after use)");
		if(!EM_Settings.genConfigs)
		{
			EM_Settings.genConfigs = genConfig.getBoolean(false);
		}
		genConfig.set(false);
		
		Property genDefault = config.get(ConSetCat, "Generate Defaults", true, "Generates EnviroRots initial default files");
		if(!EM_Settings.genDefaults)
		{
			EM_Settings.genDefaults = genDefault.getBoolean(true);
		}
		genDefault.set(false);
		
		EM_Settings.enableConfigOverride = config.get(ConSetCat, "Client Config Override (SMP)", EM_Settings.enableConfigOverride, "[DISABLED][WIP] Temporarily overrides client configurations with the server's (NETWORK INTESIVE!)").getBoolean(EM_Settings.enableConfigOverride);
		config.save();	
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
	
	//#######################################
	//#          Get File List              #                 
	//#This Grabs Directory List for Custom #
	//#######################################
	private static File[] GetFileList(String path)
	{
		
		// Will be used Auto Load Custom Objects from ??? Dir 
		File f = new File(path);
		File[] list = f.listFiles();
		list = list != null? list : new File[0];
		
		return list;
	}
	
	private static boolean isCFGFile(File file)
	{
		String fileName = file.getName();
		
		if(file.isHidden()) return false;
		
		//Matcher
		String patternString = "(.*\\.cfg$)";
		
		Pattern pattern;
		Matcher matcher;
		// Make Sure its a .cfg File
		pattern = Pattern.compile(patternString);
		matcher = pattern.matcher(fileName);
		
		String MacCheck = ".DS_Store.cfg";
		
		if (matcher.matches() && matcher.group(0).toString().toLowerCase() == MacCheck.toLowerCase()) { return false;}
		
		return matcher.matches();
	}
	
	//###################################
	//#           Check Dir             #                 
	//#  Checks for, or makes Directory #
	//###################################	
	public static void CheckDir(File Dir)
	{
		boolean dirFlag = false;
		
		// create File object
		
		if(Dir.exists())
		{
			EnviroRot.logger.log(Level.INFO, "Dir already exist:"+ Dir.getName());
			return;
		}
		
		try
		{
			Dir.setWritable(true);
			dirFlag = Dir.mkdirs();
			EnviroRot.logger.log(Level.INFO, "Created new Folder "+ Dir.getName());
		} catch(Exception e)
		{
			EnviroRot.logger.log(Level.ERROR, "Error occured while creating config directory: " + Dir.getAbsolutePath(), e);
		}
		
		if(!dirFlag)
		{
			EnviroRot.logger.log(Level.ERROR, "Failed to create config directory: " + Dir.getAbsolutePath());
		}
	}
	
	/**
	 * Load Custom Objects          
	 * Used to Load Custom Blocks,Armor
	 * Entitys, & Items from Custom Config Dir        
	 */
	private static void LoadCustomObjects(File customFiles)
	{
		boolean datFile = isCFGFile(customFiles);
		
		// Check to make sure this is a Data File Before Editing
		if(datFile == true)
		{
			Configuration config;
			try
			{
				config = new Configuration(customFiles, true);
				
				//EnviroRot.logger.log(Level.INFO, "Loading Config File: " + customFiles.getAbsolutePath());
	
				config.load();


			// 	Grab all Categories in File
			List<String> catagory = new ArrayList<String>();
			Set<String> nameList = config.getCategoryNames();
			Iterator<String> nameListData = nameList.iterator();
			
			// add Categories to a List 
			while(nameListData.hasNext())
			{
				catagory.add(nameListData.next());
			}
			
			for(int x = 0; x < catagory.size(); x++)
			{
				String CurCat = catagory.get(x);
				
				if(!CurCat.isEmpty() && CurCat.contains(Configuration.CATEGORY_SPLITTER))
				{
					String parent = CurCat.split("\\" + Configuration.CATEGORY_SPLITTER)[0];
					
					if(propTypes.containsKey(parent) && propTypes.get(parent).useCustomConfigs())
					{
						PropertyBase property = propTypes.get(parent);
						property.LoadProperty(config, catagory.get(x));
					} else
					{
						EnviroRot.logger.log(Level.WARN, "Failed to load object " + CurCat);
					}
					
				}
			}
			
			config.save();
			
			// Add to list of loaded Config files
			loadedConfigs.add(config.getConfigFile().getName());
			
			} catch(Exception e)
			{
				e.printStackTrace();
				EnviroRot.logger.log(Level.ERROR, "FAILED TO LOAD CUSTOM CONFIG: " + customFiles.getName() + "\nNEW SETTINGS WILL BE IGNORED!", e);
				return;
			}
		}
			
			
	}
	
	public static ArrayList<String> getSubCategories(Configuration config, String mainCat)
	{
		ArrayList<String> category = new ArrayList<String>();
		Set<String> nameList = config.getCategoryNames();
		Iterator<String> nameListData = nameList.iterator();
		
		// add Categories to a List 
		while(nameListData.hasNext())
		{
			String catName = nameListData.next();
			
			if(catName.startsWith(mainCat + "."))
			{
				category.add(catName);
			}
		}
		
		return category;
	}
	
	public static String getProfileName()
	{
		return getProfileName(loadedProfile);
	}
	
	public static String getProfileName(String profile)
	{
		return profile.substring(profilePath.length(),profile.length()-1).toUpperCase();		
	}
	
	public static boolean ReloadConfig()
	{
				 	try
				 	{

				 		EM_Settings.itemProperties.clear();
				 		EM_Settings.rotProperties.clear();
			
				 		int Total = initConfig();
			
				 		initProfile();
			
				 		return true;
				 		
				 	} //try
					catch(NullPointerException e)
					{
						return false;
					}
	            

	}
	
	public static void loadDefaultProperties()
	{
		Iterator<PropertyBase> iterator = propTypes.values().iterator();
		
		while(iterator.hasNext())
		{
			iterator.next().GenDefaults();
		}
	}

	public static Configuration getConfigFromObj(Object obj)
	{
		String ModID = ModIdentification.idFromObject(obj);
	
		File configFile = new File(loadedProfile+ customPath + ModID +".cfg");
		
		Configuration config;
		try
		{
			config = new Configuration(configFile, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroRot.logger.log(Level.WARN, "FAILED TO LOAD Config from OBJECT TO "+ModID+".CFG");
			return null;
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroRot.logger.log(Level.WARN, "FAILED TO LOAD Config from OBJECT TO "+ModID+".CFG");
			return null;
		}


		return config;
	}
	
	public static String SaveMyCustom(Object obj)
	{
		return SaveMyCustom(obj, null);
	}
	
	public static String SaveMyCustom(Object obj, Object type)
	{
		
		String ModID = ModIdentification.idFromObject(obj);

		
		// Check to make sure this is a Data File Before Editing
		File configFile = new File(loadedProfile+ customPath + ModID +".cfg");
		
		Configuration config;
		try
		{
			config = new Configuration(configFile, true);
		} catch(NullPointerException e)
		{
			e.printStackTrace();
			EnviroRot.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO "+ModID+".CFG");
			return "Failed to Open "+ModID+".cfg";
		} catch(StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			EnviroRot.logger.log(Level.WARN, "FAILED TO SAVE NEW OBJECT TO "+ModID+".CFG");
			return "Failed to Open "+ModID+".cfg";
		}

		config.load();

		String returnValue = "";
		

		if(obj instanceof Item && type == null )
		{
				ItemProperties.base.generateEmpty(config, obj);
				returnValue = "(Item) Saved to "+ ModID + ".cfg on Profile "+ getProfileName();
		}
		
		config.save();
		
		
		return returnValue;
		
		//return null;
	}
	
	private void removeProperty(Configuration config, String oldCat, String propName)
	{
		String remove = "Remove";
		config.moveProperty(oldCat, propName, remove);
		config.removeCategory(config.getCategory(remove));
	}

} // End of Page

