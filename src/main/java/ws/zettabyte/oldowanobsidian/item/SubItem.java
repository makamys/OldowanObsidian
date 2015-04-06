package ws.zettabyte.oldowanobsidian.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class SubItem
{
	
	public String unlocalizedName;
	public String textureName;
	protected IIcon icon = null;
	public boolean inCTab = true; //In creative tab?
	public int meta; //Should only be set by the MultiItem registering it.

	public SubItem() {};
	public SubItem(String unlName, String texName)
	{
		unlocalizedName = unlName;
		textureName = texName;
	};
	
	@SideOnly(Side.CLIENT)
	public void registerIcon (String modPrefix, String folder, IIconRegister iconRegister)
	{
		if(iconRegister == null) throw new IllegalArgumentException();
		if(folder == null) throw new IllegalArgumentException();
		if(modPrefix == null) throw new IllegalArgumentException();
		
		icon = iconRegister.registerIcon(modPrefix + ":" + folder + textureName);
		
	}
	
	@SideOnly(Side.CLIENT)
	IIcon getIcon()
	{
		return icon;
	}
}
