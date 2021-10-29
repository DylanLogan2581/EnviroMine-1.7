package envirorot.handlers;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import envirorot.core.EM_ConfigHandler;
import envirorot.core.EM_Settings;
import envirorot.core.EnviroRot;
import envirorot.network.packet.PacketEnviroRot;
import envirorot.trackers.properties.ItemProperties;
import envirorot.trackers.properties.RotProperties;
import envirorot.world.EM_WorldData;

public class EM_EventManager
{
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(EM_Settings.foodSpoiling)
		{
			if(event.entity instanceof EntityItem)
			{
				EntityItem item = (EntityItem)event.entity;
				ItemStack rotStack = RotHandler.doRot(event.world, item.getEntityItem());
				
				if(item.getEntityItem() != rotStack)
				{
					item.setEntityItemStack(rotStack);
				}
			} else if(event.entity instanceof EntityPlayer)
			{
				IInventory invo = ((EntityPlayer)event.entity).inventory;
				RotHandler.rotInvo(event.world, invo);
			} else if(event.entity instanceof IInventory)
			{
				IInventory invo = (IInventory)event.entity;
				RotHandler.rotInvo(event.world, invo);
			}
		}
		
		if(event.entity instanceof EntityLivingBase)
		{
			// Ensure that only one set of trackers are made per Minecraft instance.
			boolean allowTracker = !(event.world.isRemote && EnviroRot.proxy.isClient() && Minecraft.getMinecraft().isIntegratedServerRunning());
		} else
		{
		}
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		ItemStack item = event.entityPlayer.getCurrentEquippedItem();
		if(event.action == Action.RIGHT_CLICK_BLOCK && EM_Settings.foodSpoiling)
		{
			TileEntity tile = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
			if(tile != null & tile instanceof IInventory)
			{
				RotHandler.rotInvo(event.entityPlayer.worldObj, (IInventory)tile);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event)
	{
		if(event.isCanceled() || event.entityPlayer.worldObj.isRemote)
		{
			return;
		}
		
		if(!EM_Settings.foodSpoiling)
		{
			return;
		}
		
		if(event.target != null && event.target instanceof IInventory && EM_Settings.foodSpoiling)
		{
			IInventory chest = (IInventory)event.target;
			RotHandler.rotInvo(event.entityPlayer.worldObj, chest);
		}
	}
	
	@SubscribeEvent
	public void onPlayerUseItem(PlayerUseItemEvent.Finish event)
	{
		ItemStack item = event.item;
		if(EM_Settings.itemProperties.containsKey(Item.itemRegistry.getNameForObject(item.getItem())) || EM_Settings.itemProperties.containsKey(Item.itemRegistry.getNameForObject(item.getItem()) + "," + item.getItemDamage()))
		{
			ItemProperties itemProps;
			if(EM_Settings.itemProperties.containsKey(Item.itemRegistry.getNameForObject(item.getItem()) + "," + item.getItemDamage()))
			{
				itemProps = EM_Settings.itemProperties.get(Item.itemRegistry.getNameForObject(item.getItem()) + "," + item.getItemDamage());
			} else
			{
				itemProps = EM_Settings.itemProperties.get(Item.itemRegistry.getNameForObject(item.getItem()));
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.entityLiving.isDead)
		{
			return;
		}
		
		if(event.entityLiving.worldObj.isRemote)
		{
			return;
		}
		
		if(event.entityLiving instanceof EntityPlayer)
		{
			InventoryPlayer invo = (InventoryPlayer)((EntityPlayer)event.entityLiving).inventory;
			if(EM_Settings.foodSpoiling)
			{
				RotHandler.rotInvo(event.entityLiving.worldObj, invo);
			}
		}
	}
	
	private static boolean firstload = false;
	@SubscribeEvent
	public void onWorldLoad(Load event)
	{
		if(event.world.isRemote)
		{
			return;
		}
		
		//Load Custom Configs
		if (!firstload) 
		{
			EM_ConfigHandler.initProfile(); 
			firstload = true;
		}
		MinecraftServer server = MinecraftServer.getServer();
		if(EM_Settings.worldDir == null && server.isServerRunning())
		{
			if(EnviroRot.proxy.isClient())
			{
				EM_Settings.worldDir = MinecraftServer.getServer().getFile("saves/" + server.getFolderName());
			} else
			{
				EM_Settings.worldDir = server.getFile(server.getFolderName());
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(Unload event)
	{
		if(!event.world.isRemote)
		{
			if(!MinecraftServer.getServer().isServerRunning())
			{
				if(EM_Settings.worldDir != null)
				{
				}
				EM_Settings.worldDir = null;
			}
		}
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		if(event.world.isRemote)
		{
			return;
		}
	}
	
	/* Client only events */
	
        //@SideOnly(Side.CLIENT)
	//HashMap<String, EntityLivingBase> playerMob = new HashMap<String, EntityLivingBase>();
	
	float partialTicks = 1F;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void RenderTickEvent(TickEvent.RenderTickEvent event)
	{
		partialTicks = event.renderTickTime;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onItemTooltip(ItemTooltipEvent event)
	{
		if(event.itemStack != null && event.itemStack.hasTagCompound())
		{
			if(event.itemStack.getTagCompound().getLong("EM_ROT_DATE") > 0 && EM_Settings.foodSpoiling)
			{
				double rotDate = event.itemStack.getTagCompound().getLong("EM_ROT_DATE");
				double rotTime = event.itemStack.getTagCompound().getLong("EM_ROT_TIME");
				double curTime = event.entity.worldObj.getTotalWorldTime();
				
				if(curTime - rotDate <= 0)
				{
					event.toolTip.add(new ChatComponentTranslation("misc.envirorot.tooltip.rot", "0%" , MathHelper.floor_double((curTime - rotDate)/24000L) , MathHelper.floor_double(rotTime/24000L)).getUnformattedText());
				} else
				{
					event.toolTip.add(new ChatComponentTranslation("misc.envirorot.tooltip.rot", MathHelper.floor_double((curTime - rotDate)/rotTime * 100D) + "%", MathHelper.floor_double((curTime - rotDate)/24000L), MathHelper.floor_double(rotTime/24000L)).getUnformattedText());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onCrafted(ItemCraftedEvent event) // Prevents exploit of making foods with almost rotten food to prolong total life of food supplies
	{
		if(event.player.worldObj.isRemote || event.crafting == null || event.crafting.getItem() == null)
		{
			return;
		}
	
		RotProperties rotProps = null;
		long rotTime = (long)(EM_Settings.foodRotTime * 24000L);
		
		if(EM_Settings.rotProperties.containsKey("" + Item.itemRegistry.getNameForObject(event.crafting.getItem())))
		{
			rotProps = EM_Settings.rotProperties.get("" + Item.itemRegistry.getNameForObject(event.crafting.getItem()));
			rotTime = (long)(rotProps.days * 24000L);
		} else if(EM_Settings.rotProperties.containsKey("" + Item.itemRegistry.getNameForObject(event.crafting.getItem()) + "," + event.crafting.getItemDamage()))
		{
			rotProps = EM_Settings.rotProperties.get("" + Item.itemRegistry.getNameForObject(event.crafting.getItem()) + "," + event.crafting.getItemDamage());
			rotTime = (long)(rotProps.days * 24000L);
		}
		
		if(rotProps == null)
		{
			return; // Crafted item is not a rotting food
		}
		
		long lowestDate = -1L;
		
		for(int i = 0; i < event.craftMatrix.getSizeInventory(); i++)
		{
			ItemStack stack = event.craftMatrix.getStackInSlot(i);
			
			if(stack == null || stack.getItem() == null || stack.getTagCompound() == null)
			{
				continue;
			}
			
			if(stack.getTagCompound().hasKey("EM_ROT_DATE") && (lowestDate < 0 || stack.getTagCompound().getLong("EM_ROT_DATE") < lowestDate))
			{
				lowestDate = stack.getTagCompound().getLong("EM_ROT_DATE");
			}
		}
		
		if(lowestDate >= 0)
		{
			if(event.crafting.getTagCompound() == null)
			{
				event.crafting.setTagCompound(new NBTTagCompound());
			}
			
			event.crafting.getTagCompound().setLong("EM_ROT_DATE", lowestDate);
			event.crafting.getTagCompound().setLong("EM_ROT_TIME", rotTime);
		}
	}
}
