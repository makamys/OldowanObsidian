package ws.zettabyte.oldowanobsidian.compat;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.TConstruct;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.crafting.Smeltery;
import tconstruct.tools.TinkerTools;
import ws.zettabyte.oldowanobsidian.OldowanObsidian;

public class TConstructCompat {
	public static void initialize() {
		PatternBuilder pb = PatternBuilder.instance;
		pb.registerMaterial(new ItemStack(OldowanObsidian.material, 1, OldowanObsidian.shardLogic.meta), 2, "Obsidian");
        Smeltery.addMelting(FluidType.getFluidType("Obsidian"), new ItemStack(OldowanObsidian.material, 1, OldowanObsidian.shardLogic.meta), 0, TConstruct.chunkLiquidValue);
        
    	OreDictionary.registerOre("chunkObsidian", new ItemStack(TinkerTools.toolShard, 1, 6));
    	
	}
}
