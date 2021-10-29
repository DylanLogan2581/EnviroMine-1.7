package envirorot.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import envirorot.handlers.ObjectHandler;

public class EnviroTab extends CreativeTabs
{
	ArrayList<ItemStack> rawStacks = new ArrayList<ItemStack>();
	
	public EnviroTab(String par2Str) {
		super(par2Str);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ObjectHandler.rottenFood;
	}
	
	public void addRawStack(ItemStack stack)
	{
		rawStacks.add(stack);
	}

    /**
     * only shows items which have tabToDisplayOn == this
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
    public void displayAllReleventItems(List list)
    {
        super.displayAllReleventItems(list);
        
        list.addAll(rawStacks);
    }
}
