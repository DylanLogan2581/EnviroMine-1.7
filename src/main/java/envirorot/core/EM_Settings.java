package envirorot.core;

import envirorot.trackers.properties.*;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EM_Settings
{
	public static File worldDir = null;
	
	//Mod Data
	public static final String Version = "FWG_EM_VER";
	public static final String ModID = "envirorot";
	public static final String Channel = "EM_CH";
	public static final String Name = "EnviroRot";
	public static final String Proxy = "envirorot.core.proxies";
	
	/*
	public static int rottenFoodID = 5008;
	
	*/
	
	//Properties
	//@ShouldOverride("envirorot.network.packet.encoders.ArmorPropsEncoder")
	@ShouldOverride({String.class, ItemProperties.class})
	public static HashMap<String,ItemProperties> itemProperties = new HashMap<String,ItemProperties>();
	
	@ShouldOverride({String.class, RotProperties.class})
	public static HashMap<String,RotProperties> rotProperties = new HashMap<String,RotProperties>();
	
	public static int updateCap = 128;
	
	public static boolean updateCheck = true;
	//public static boolean useDefaultConfig = true;
	public static boolean genConfigs = false;
	public static boolean genDefaults = false;
	
	public static boolean foodSpoiling = true;
	public static int foodRotTime = 7;
	
	/** Whether or not this overridden with server settings */
	public static boolean isOverridden = false;
	public static boolean enableConfigOverride = false;
	public static boolean profileOverride = false;
	public static String profileSelected = "default";
	
	/**
	 * Tells the server that this field should be sent to the client to overwrite<br>
	 * Usage:<br>
	 * <tt>@ShouldOverride</tt> - for ints/booleans/floats/Strings<br>
	 * <tt>@ShouldOverride(Class[] value)</tt> - for ArrayList or HashMap types
	 * */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ShouldOverride
	{
		Class<?>[] value() default {};
	}
}
