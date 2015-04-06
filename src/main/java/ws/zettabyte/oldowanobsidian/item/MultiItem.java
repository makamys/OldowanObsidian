package ws.zettabyte.oldowanobsidian.item;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class MultiItem extends Item {

	protected ArrayList<SubItem> subItems = new ArrayList<SubItem>(8);
	protected static final String modTexPrefix = "oldowanobsidian";
	
	public String iconFolder = "";
	@Override
	public boolean getHasSubtypes() {
		if(subItems.size() > 1) return true;
		return super.getHasSubtypes();
	}
	
	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage (int meta)
	{
		if(meta >= subItems.size()) return null;
	
		return subItems.get(meta).getIcon();
	
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons (IIconRegister iconRegister)
	{
		for (int i = 0; i < subItems.size(); ++i)
		{
			subItems.get(i).registerIcon(modTexPrefix, iconFolder, iconRegister);
		}
	}
	
	@Override
	public String getUnlocalizedName (ItemStack stack)
	{
		int meta = stack.getItemDamage();
		if((meta >= 0) && (meta < subItems.size()))
		{
			return getUnlocalizedName() + "." + subItems.get(meta).unlocalizedName;
		}
		else
		{
			/* If modded Minecraft were a saner environment I'd throw an exception.
			 * As-is, I'd rather not 
			 */
			return getUnlocalizedName() + "." + "ERROR"; 
		}
	}
	
	@Override
	public void getSubItems (Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < subItems.size(); ++i)
		{
			if (subItems.get(i).inCTab == true)
			{
				list.add(new ItemStack(item, 1, i));
			}
		}
	}
	
	public void addSubItem(SubItem item)
	{
		/*Remember: subItems.size() is equal to "highest index + 1", or "new highest index." */
		item.meta = subItems.size();
		subItems.add(item);
	}
}
