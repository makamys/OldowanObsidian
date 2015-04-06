package ws.zettabyte.oldowanobsidian.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemModPick extends ItemPickaxe {

	public ItemModPick(ToolMaterial mat) {
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
	public ItemModPick addTooltip(String str)
	{
		extraInfo.add(str);
		return this;
	}
	
	public ItemModPick addTooltipLocalize(String str)
	{
		return addTooltip(StatCollector.translateToLocal(str));
	}
	
	//Tool efficiency for block.
	@Override
    public float func_150893_a(ItemStack stack, Block block)
    {
    	final Material blockMaterial = block.getMaterial();
        return ((blockMaterial == Material.rock) ||
        		(blockMaterial == Material.anvil) ||
        		(blockMaterial == Material.circuits) ||
        		(blockMaterial == Material.iron) ||
        		(blockMaterial == Material.piston) ||
        		(blockMaterial == Material.ice) ||
        		(blockMaterial == Material.glass))
        		? this.efficiencyOnProperMaterial : 1.0F;
    }
    //public ItemStack getRepairStack()
    //{
    //	return toolMaterial;
    //}
}
