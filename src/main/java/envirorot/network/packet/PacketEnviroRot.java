/**
 * @author thislooksfun
 */
package envirorot.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import envirorot.core.EM_Settings;
import envirorot.core.EnviroRot;
import envirorot.handlers.EM_EventManager;

public class PacketEnviroRot implements IMessage
{
	private NBTTagCompound tags;
	
	public PacketEnviroRot() {}
	public PacketEnviroRot(NBTTagCompound _tags) {
		this.tags = _tags;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.tags = ByteBufUtils.readTag(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, this.tags);
	}
	
	public static class HandlerServer implements IMessageHandler<PacketEnviroRot,IMessage>
	{
		@Override
		public IMessage onMessage(PacketEnviroRot packet, MessageContext ctx)
		{
			int id = packet.tags.hasKey("id")? packet.tags.getInteger("id") : -1;
			
			if(id == 1)
			{
				this.emptyRightClick(packet.tags);
			} else
			{
				EnviroRot.logger.log(Level.ERROR, "Received invalid packet on serverside!");
			}
			return null; //Reply
		}
		
		private void emptyRightClick(NBTTagCompound tags)
		{
		}
	}
	
	public static class HandlerClient implements IMessageHandler<PacketEnviroRot,IMessage>
	{
		@Override
		public IMessage onMessage(PacketEnviroRot packet, MessageContext ctx)
		{
			int id = packet.tags.hasKey("id")? packet.tags.getInteger("id") : -1;
			
			if(id == 0)
			{
				this.trackerSync(packet.tags);
			} else if(id == 3)
			{
			} else if(id == 4)
			{
			} else
			{
				EnviroRot.logger.log(Level.ERROR, "Received invalid packet on clientside!");
			}
			return null; //Reply
		}
		
		private void trackerSync(NBTTagCompound tags)
		{
		}
	}
}
