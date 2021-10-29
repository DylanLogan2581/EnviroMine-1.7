package envirorot.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import envirorot.blocks.BlockEsky;
import envirorot.blocks.BlockFreezer;
import envirorot.blocks.tiles.TileEntityEsky;
import envirorot.blocks.tiles.TileEntityFreezer;
import envirorot.core.EnviroRot;
import envirorot.items.ItemSpoiledMilk;
import envirorot.items.RottenFood;

public class ObjectHandler
{
	public static Item rottenFood;
	public static Item spoiledMilk;
	
	public static Block esky;
	public static Block freezer;
	
	public static int renderSpecialID;
	
	public static void initItems()
	{
		rottenFood = new RottenFood(1).setMaxStackSize(64).setUnlocalizedName("envirorot.rottenfood").setCreativeTab(EnviroRot.enviroTab).setTextureName("envirorot:rot");
		spoiledMilk = new ItemSpoiledMilk().setUnlocalizedName("envirorot.spoiledmilk").setCreativeTab(EnviroRot.enviroTab).setTextureName("bucket_milk");
	}
	
	public static void registerItems()
	{
		GameRegistry.registerItem(rottenFood, "rottenFood");
		GameRegistry.registerItem(spoiledMilk, "spoiledMilk");
	}
	
	public static void initBlocks()
	{

		esky = new BlockEsky(Material.iron).setBlockName("envirorot.esky").setCreativeTab(EnviroRot.enviroTab);
		freezer = new BlockFreezer(Material.iron).setBlockName("envirorot.freezer").setCreativeTab(EnviroRot.enviroTab);
		
		Blocks.redstone_torch.setLightLevel(0.9375F);
	}
	
	public static void registerBlocks()
	{
		GameRegistry.registerBlock(esky, "esky");
		GameRegistry.registerBlock(freezer, "freezer");
		
	}
	
	public static void registerEntities()
	{
		GameRegistry.registerTileEntity(TileEntityEsky.class, "envirorot.tile.esky");
		GameRegistry.registerTileEntity(TileEntityFreezer.class, "envirorot.tile.freezer");
	}
}
