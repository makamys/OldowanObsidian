package ws.zettabyte.oldowanobsidian;


import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import ws.zettabyte.oldowanobsidian.block.BlockBlackGravel;
import ws.zettabyte.oldowanobsidian.block.BlockBlackGravel.DropEntry;
import ws.zettabyte.oldowanobsidian.compat.TConstructCompat;
import ws.zettabyte.oldowanobsidian.item.ItemModPick;
import ws.zettabyte.oldowanobsidian.item.MultiItem;
import ws.zettabyte.oldowanobsidian.item.ObsidianToolsModule;
import ws.zettabyte.oldowanobsidian.item.SubItem;
import ws.zettabyte.oldowanobsidian.world.BlackGravelWorldgen;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.ExistingSubstitutionException;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.Type;
import cpw.mods.fml.common.registry.IncompatibleSubstitutionException;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mod(modid = OldowanObsidian.MODID, version = OldowanObsidian.VERSION, dependencies = OldowanObsidian.DEPS)
public class OldowanObsidian
{
    public static final String MODID = "oldowanobsidian";
    public static final String VERSION = "0.0.1";
    
    public static final String DEPS = "after:TConstruct;";

	@Mod.Instance(MODID)
	public static OldowanObsidian instance;

	private static final Random random = new Random();
	
	public static Configuration config;
	public static final Logger logger = LogManager.getLogger(MODID);
	public static boolean obsidianRework = true;
	
	public static MultiItem material = new MultiItem();
	public static SubItem shardLogic = new SubItem("obsidianshard", "obsidianshard");
	public static ItemStack shardStack;
	
	public static BlockBlackGravel blackGravel = new BlockBlackGravel();
	public static int gravelChancePerChunk = 24;
	
	
	public static boolean compatTCon = true;
	
	public static boolean forceBreakRecipe = false;
	public static boolean easyShards = false;

	public static boolean useTools = true;
	public static boolean doWorldgen = true;
	
	public static String shardRecipeName;
	
	public static ObsidianToolsModule  toolModule = null;
	
    public OldowanObsidian() {
    	instance = this;
	}

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		config = new Configuration(event.getSuggestedConfigurationFile());
		
		Property conf_toolMod = config.get("Modules", "Tools", true);
		conf_toolMod.comment = "Enable or disable tool module. Includes obsidian tools, flint tools, spears, macuahutils.";
		useTools = conf_toolMod.getBoolean(true);
		
		Property conf_tcon = config.get("Compatibility", "TCOn", true);
		conf_tcon.comment = "Registers OO obsidian shards as an equivalent of TCon obsidian shards.";
		compatTCon = conf_tcon.getBoolean(true);
		
		Property conf_eShard = config.get("Compatibility", "EasyShards", false);
		conf_eShard.comment = "Get more shards when breaking Obsidian. WARNING: Infinite Obsidian exploit when enabled with TCon!";
		easyShards = conf_eShard.getBoolean(false);

		Property conf_oR = config.get("Obsidian Harvesting", "EnableRework", true);
		conf_oR.comment = "Enable Obsidian harvest changes (disabling this enables an Obsidian -> Obsidian Shards crafting recipe)";
		obsidianRework = conf_oR.getBoolean(true);
		
		double obHardness = config.get("Obsidian Harvesting", "Hardness", 2.0).getDouble(2.0);
		int obHLevel = config.get("Obsidian Harvesting", "HarvestLevel", ToolMaterial.IRON.getHarvestLevel()).getInt(ToolMaterial.IRON.getHarvestLevel());

		Property conf_fB = config.get("Recipes", "ForceShardCrafting", false);
		conf_fB.comment = "Force-enable obsidian-breaking recipe (even when the harvest changes are enabled)";
		forceBreakRecipe = conf_fB.getBoolean(false);
		
		doWorldgen = config.get("Modules", "BlackGravelWorldgen", doWorldgen).getBoolean(doWorldgen);
		if(doWorldgen)
		{
			Property conf_genchance = config.get("World", "BlackGravelRarity", gravelChancePerChunk);
			conf_genchance.comment = "Chance per chunk to generate a black gravel cluster is 1/this";
			gravelChancePerChunk = conf_genchance.getInt(gravelChancePerChunk);
		}
		config.save();
		
		//Little sanity checks
		if(obHLevel < 0) obHLevel = 0;
		if(obHardness < 0) obHardness = 0;
		
        //I was going to use
		//GameRegistry.addSubstitutionAlias("minecraft:obsidian", Type.BLOCK, OldowanObsidian.blockObsidianOverride);
    	//and then realized that was unnecessary. Kept for reference.

		if(obsidianRework)
		{
	        //Make it easier to mine.
	        //Blocks.obsidian.setHardness((float)obHardness).setHarvestLevel("pickaxe", obHLevel);
	        Blocks.obsidian.setHardness((float)obHardness).setHarvestLevel("pickaxe", obHLevel);
		}
		
		if(useTools)
		{
			toolModule = new ObsidianToolsModule(config, logger);
			toolModule.preInit(event);
		}

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    	material.addSubItem(shardLogic);
    	material.setUnlocalizedName("material");
    	material.setCreativeTab(CreativeTabs.tabMaterials);
    	
    	material.setNoRepair();
    	//obsidianPick.set
    	
    	GameRegistry.registerItem(material, "material");
    	
    	shardStack = new ItemStack(material, 1, shardLogic.meta);
    	
    	blackGravel.setCreativeTab(CreativeTabs.tabBlock);
    	blackGravel.setBlockTextureName("oldowanobsidian:black_gravel");
    	blackGravel.setBlockName("blackgravel");
    	blackGravel.setHardness(0.6F).setStepSound(Block.soundTypeGravel);
    	
    	blackGravel.addDrop(blackGravel.new DropEntry(shardStack, 12, 3));
    	
    	GameRegistry.registerBlock(blackGravel, "BlackGravel");
    	
    	if(compatTCon) {
	    	if(Loader.isModLoaded("TConstruct"))
	    	{
	    		logger.info("Tinker's Construct detected, doing intermod compatability for it.");
	    		(new TConstructCompat()).initialize();

	    		if(easyShards)
	    		{
	    			logger.warn("Easy shards enabled with TCon.");
	    			logger.warn("I hope you tweaked the casting or melting recipes, because otherwise you're in for some **FUN.**");
    			}
	    	}
	    	else
	    	{
	    		logger.info("Tinker's Construct not detected, skipping compatability.");
	    	}
    	}
    	initRecipes();
		
		if(useTools)
		{
			toolModule.init(event);
		}
		
		if(doWorldgen)
		{
			GameRegistry.registerWorldGenerator(new BlackGravelWorldgen(), 9999);
		}
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(this);
    	FMLCommonHandler.instance().bus().register(this);
		
		if(useTools)
		{
			toolModule.postInit(event);
		}
    }
    
    private void initRecipes()
    {
    	shardRecipeName = "chunkObsidian";
    	
    	OreDictionary.registerOre(shardRecipeName, shardStack);

    	if((!obsidianRework) || forceBreakRecipe)
    	{
    		if(easyShards)
    		{
    		GameRegistry.addShapelessRecipe(new ItemStack(material, 8, shardLogic.meta), new Object[]{
    	    	"A", 'A', Blocks.obsidian });
    		}
    		else
    		{
    		GameRegistry.addShapelessRecipe(new ItemStack(material, 4, shardLogic.meta), new Object[]{
    	    	"A", 'A', Blocks.obsidian });
    		}
    	}

    	//Obsidian reconstruction
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.obsidian, 1), new Object[]{
    	    "AAA", "ABA", "AAA",
    	    'A', shardRecipeName, 
    	    'B', Items.blaze_powder}));
    	//Arrows
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.arrow, 4), new Object[]{
    	    " H ", " S ", " F ",
    	    'H', shardRecipeName, 
    	    'S', "stickWood",
    	    'F', Items.feather}));
    }
    
    
    //Forge events follow. --------------------------------
	@SubscribeEvent
	public void onBlockHarvested(HarvestDropsEvent event)
	{
		//Don't mess with it if we've got silk touch.
		if (event.isSilkTouching)
			return;
		if(event.block == Blocks.obsidian)
		{
			if(obsidianRework)
			{
				//Iterate through the list of drops, since event.drops.clear() is broken for some reason.
				for(int i = 0; i < event.drops.size(); ++i)
				{
					event.drops.remove(i);
				}
				int max = 4;
				int guaranteed = 1;
				if(easyShards)
				{
					max = 8;
					guaranteed = 4;
				}

				int bonus = 1 + event.fortuneLevel;
				
				while(bonus > guaranteed)
				{
					--bonus;
					++guaranteed;
				}
				
				//For every point our possible value goes over max, remove a point from the largest pool (prio bonus).
				while((bonus + guaranteed) > max)
				{
					if(guaranteed > bonus)
					{
						--guaranteed;
					}
					else
					{
						--bonus;
					}
				}
				
				int dropCount = guaranteed + random.nextInt(bonus);
				event.drops.add(new ItemStack(material, dropCount, shardLogic.meta));
			}
		}
	}
}
