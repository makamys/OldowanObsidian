package ws.zettabyte.oldowanobsidian.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemModShovel extends ItemSpade {
	
	public ItemModShovel(ToolMaterial mat) {
		super(mat);
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
	public ItemModShovel addTooltip(String str)
	{
		extraInfo.add(str);
		return this;
	}
	
	public ItemModShovel addTooltipLocalize(String str)
	{
		return addTooltip(StatCollector.translateToLocal(str));
	}
}
