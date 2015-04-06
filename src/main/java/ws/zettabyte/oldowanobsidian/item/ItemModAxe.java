package ws.zettabyte.oldowanobsidian.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemModAxe extends ItemAxe {
	//Why that constructor is protected I will never know.
	public ItemModAxe(ToolMaterial p_i45327_1_) {
		super(p_i45327_1_);
	}

	protected ArrayList<String> extraInfo = new ArrayList<String>(2);
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag)
	{
		super.addInformation(itemstack, entityplayer, list, flag);
		for(int i = 0; i < extraInfo.size(); ++i)
		{
			list.add(extraInfo.get(i));
		}
	}
	public ItemModAxe addTooltip(String str)
	{
		extraInfo.add(str);
		return this;
	}
	
	public ItemModAxe addTooltipLocalize(String str)
	{
		return addTooltip(StatCollector.translateToLocal(str));
	}
	
}
