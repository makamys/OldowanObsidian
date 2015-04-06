package ws.zettabyte.oldowanobsidian.block;

import java.util.ArrayList;
import java.util.Random;

import ws.zettabyte.oldowanobsidian.OldowanObsidian;
import net.minecraft.block.BlockGravel;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockBlackGravel extends BlockGravel {

	protected ArrayList<DropEntry> drops = new ArrayList<DropEntry>(4);
	public BlockBlackGravel() {
		drops.add(new DropEntry(new ItemStack(Items.flint), 8, 1));
		drops.add(new DropEntry(new ItemStack(Items.coal), 8, 0));
		drops.add(new DropEntry(new ItemStack(Items.dye, 1, 0), 10, 4));
		drops.add(new DropEntry(new ItemStack(Items.magma_cream, 1, 0), 30, 0));
		drops.add(new DropEntry(new ItemStack(Items.netherbrick, 1, 0), 6, 0));
		drops.add(new DropEntry(new ItemStack(Items.diamond, 1, 0), 200, 0));
		drops.add(new DropEntry(new ItemStack(Items.emerald, 1, 0), 200, 0));
	}
	
	public class DropEntry {
		public ItemStack item;
		public int rarity; //Random a 0-rarity number. If 0, we drop the item.
		public int countBonus = 0;
		
		public DropEntry(ItemStack i, int r, int c)
		{
			item = i; rarity = r; countBonus = c;
		}
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_,
			int p_149650_3_) {
		//Should probably never get called..?
		return super.getItemDropped(p_149650_1_, p_149650_2_, p_149650_3_);
	}
	
	@Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        
        for(int i = 0; i < drops.size(); ++i)
        {
        	DropEntry drop = drops.get(i);
        	if((drop.item != null) && (world.rand != null))
        	{
	        	//Roll to get the drop.
	        	if(world.rand.nextInt(drop.rarity) <= fortune)
	        	{
	        		if((drop.countBonus + fortune) != 0)
	        		{
		        		//Roll for bonus.
		        		drop.item.stackSize += world.rand.nextInt(drop.countBonus + fortune);
	        		}
		            ret.add(drop.item.copy());
	        	}
        	}
        }
        return ret;
    }

	public void addDrop(DropEntry e)
	{
		drops.add(e);
	}
}
