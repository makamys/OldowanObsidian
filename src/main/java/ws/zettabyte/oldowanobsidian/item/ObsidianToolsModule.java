package ws.zettabyte.oldowanobsidian.item;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import ws.zettabyte.oldowanobsidian.OldowanObsidian;
import ws.zettabyte.oldowanobsidian.compat.TConstructCompat;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ObsidianToolsModule {

	private final Configuration config;
	private final Logger logger;

	public static ToolMaterial obsidianMat;
	public static ToolMaterial boneMat;
	public static ToolMaterial flintMat;

	//public static ItemModPick obsidianPick;
	//public static ItemSpade obsidianShovel;
	//public static ItemModAxe obsidianAxe;

	public static ArrayList<ItemModPick> picks = new ArrayList<ItemModPick>(3);
	public static ArrayList<ItemModShovel> shovels = new ArrayList<ItemModShovel>(3);
	public static ArrayList<ItemModAxe> axes = new ArrayList<ItemModAxe>(3);

	//public static ItemMacuahuitl flintMac;
	//public static ItemMacuahuitl boneMac;
	//public static ItemMacuahuitl obsidianMac;
	//public static ItemMacuahuitl diamondMac;

	public static ArrayList<ItemMacuahuitl> macuahuitls = new ArrayList<ItemMacuahuitl>(4);
	
	//public static float creeperbonusMacuahuitl = 0.4F;
	public ObsidianToolsModule(Configuration conf, Logger log) {
		config = conf;
		logger = log;
	}

    public void preInit(FMLPreInitializationEvent event)
    {
    	//We don't want to crash if the material doesn't exist already.
    	//Use it if it does, make it if it doesn't.
    	try {
    		obsidianMat = ToolMaterial.valueOf("OBSIDIAN");
    	}
    	catch (Exception e) {
    		obsidianMat = EnumHelper.addToolMaterial("OBSIDIAN", 
    			ToolMaterial.STONE.getHarvestLevel(),
    			((ToolMaterial.EMERALD.getMaxUses()*7)/8), 
    			ToolMaterial.STONE.getEfficiencyOnProperMaterial(), 
    			ToolMaterial.IRON.getDamageVsEntity() + 0.5F,
    			12);
    	}
    	try {
    		flintMat = ToolMaterial.valueOf("FLINT");
    	}
    	catch (Exception e) {
    		flintMat = EnumHelper.addToolMaterial("FLINT", 
    			ToolMaterial.WOOD.getHarvestLevel(),
    			(int)(ToolMaterial.STONE.getMaxUses()*0.8F), 
    			ToolMaterial.IRON.getEfficiencyOnProperMaterial(), 
    			ToolMaterial.IRON.getDamageVsEntity() - 0.5F,
    			6);
    	}
    	try {
    		boneMat = ToolMaterial.valueOf("BONE");
    	}
    	catch (Exception e) {
    		boneMat = EnumHelper.addToolMaterial("BONE", 
    			ToolMaterial.WOOD.getHarvestLevel(),
    			(int)(ToolMaterial.STONE.getMaxUses()*1.2F), 
    			ToolMaterial.STONE.getEfficiencyOnProperMaterial(), 
    			ToolMaterial.STONE.getDamageVsEntity(),
    			18);
    	}
    }
    public void toolsForMat(ToolMaterial mat, Object materialItem, Object stickItem, String[] tooltipTags)
    {

    	ItemModPick pick = new ItemModPick(mat);
    	pick.setUnlocalizedName(mat.toString().toLowerCase() + "_pick");
    	pick.setTextureName("oldowanobsidian:" + mat.toString().toLowerCase() + "_pick");
    	
    	picks.add(pick);
    	GameRegistry.registerItem(pick, pick.getUnlocalizedName());
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(pick, 1), new Object[]{
    	    "HSH", 
    	    " S ", 
    	    " S ",
    	    'H', materialItem, 
    	    'S', stickItem}));
    	
    	//ItemModAxe axe = new ItemModAxe(mat);
    	//axe.setUnlocalizedName(mat.toString().toLowerCase() + "_axe");
    	//axe.setTextureName("oldowanobsidian:" + mat.toString().toLowerCase() + "_axe");
    	
    	//Macuahuitls are a crazy saw so let's skip this bit. 
    	//axes.add(axe);
    	//GameRegistry.registerItem(axe, axe.getUnlocalizedName());
    	//GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(axe, 1), new Object[]{
    	//    "HS ", "HS ", " S ",
    	//    'H', materialOredict, 
    	//    'S', "stickWood"}));
    	
    	
    	ItemModShovel shovel = new ItemModShovel(mat);
    	shovel.setUnlocalizedName(mat.toString().toLowerCase() + "_shovel");
    	shovel.setTextureName("oldowanobsidian:" + mat.toString().toLowerCase() + "_shovel");
    	
    	shovels.add(shovel);
    	GameRegistry.registerItem(shovel, shovel.getUnlocalizedName());
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(shovel, 1), new Object[]{
    	    " H ", 
    	    " S ", 
    	    " S ",
    	    'H', materialItem, 
    	    'S', stickItem}));
    	

    	if(tooltipTags != null)
    	{
    		for(int i = 0; i < tooltipTags.length; ++i)
    		{
    			pick.addTooltipLocalize(tooltipTags[i]);
    			//axe.addTooltipLocalize(tooltipTags[i]);
    			shovel.addTooltipLocalize(tooltipTags[i]);
    		}
    	}
    }
    public void toolsForMat(ToolMaterial mat, Object materialItem, String[] tTags)
    {
    	toolsForMat(mat, materialItem, "stickWood", tTags);
    }

    public void toolsForMat(ToolMaterial mat, Object materialItem)
    {
    	toolsForMat(mat, materialItem, "stickWood", null);
    }

    public ItemMacuahuitl macForMat(ToolMaterial mat, Object materialItem, Object stickItem, float adjust)
    {
    	final float swordConstant = 4.0F;
    	
    	ItemMacuahuitl macuahuitl = new ItemMacuahuitl(obsidianMat, (obsidianMat.getDamageVsEntity() + swordConstant) - adjust);
    	macuahuitl.setUnlocalizedName(mat.toString().toLowerCase() + "_macuahuitl");
    	macuahuitl.setTextureName("oldowanobsidian:" + mat.toString().toLowerCase() + "_macuahuitl");

    	macuahuitl.addTooltipLocalize("tooltip.tool.macu.flavor");
    	macuahuitl.addTooltipLocalize("tooltip.tool.macu.info");
    	
    	macuahuitls.add(macuahuitl);
    	GameRegistry.registerItem(macuahuitl, macuahuitl.getUnlocalizedName());
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(macuahuitl, 1), new Object[]{
    	    "HSH", 
    	    "HSH", 
    	    " S ",
    	    'H', materialItem, 
    	    'S', stickItem}));
    	return macuahuitl;
    }
    
    public ItemMacuahuitl macForMat(ToolMaterial mat, Object materialItem, float adjust)
    {
    	return macForMat(mat, materialItem, "stickWood", adjust);
    }
    
    public ItemMacuahuitl macForMat(ToolMaterial mat, Object materialItem)
    {
    	return macForMat(mat, materialItem, "stickWood", 0.0F);
    }

    public void init(FMLInitializationEvent event)
    {
    	obsidianMat.setRepairItem(new ItemStack(OldowanObsidian.material, 1, OldowanObsidian.shardLogic.meta));
    	boneMat.setRepairItem(new ItemStack(Items.bone, 1, 0));
    	flintMat.setRepairItem(new ItemStack(Items.flint, 1, 0));
    	
    	//----- TOOLS -----
    	toolsForMat(obsidianMat, OldowanObsidian.shardRecipeName, /*"ingotIron",*/ new String[]{"tooltip.tool.obsidian.info"});
    	toolsForMat(flintMat, Items.flint);
    	toolsForMat(boneMat, Items.bone, new String[]{"tooltip.tool.bone.flavor", "tooltip.tool.bone.info"});
    	
    	//----- MACUAHUITLS -----
    	//What the ItemSword class adds by default to tool material damage values.

    	macForMat(obsidianMat, OldowanObsidian.shardRecipeName, 1.0F).setCreeperBonus(0.4F)
    		.addTooltipLocalize("tooltip.tool.macu.obsidian.extra");
    	macForMat(flintMat, Items.flint, 2.0F).setCreeperBonus(0.6F).setKnockbackBoost(1.8F);
    	macForMat(boneMat, Items.bone, 2.5F).setCreeperBonus(0.4F)
    	.addTooltipLocalize("tooltip.tool.bone.flavor").addTooltipLocalize("tooltip.tool.bone.info");
    	
    	
    	//Do the diamond one manually because Minecraft calls it Emerald for some reason.
    	ItemMacuahuitl diamondMac = new ItemMacuahuitl(ToolMaterial.EMERALD, (ToolMaterial.EMERALD.getDamageVsEntity() + 4.0F) - 0.5F);
    	diamondMac.setUnlocalizedName("diamond_macuahuitl");
    	diamondMac.setTextureName("oldowanobsidian:diamond_macuahuitl");
    	
    	diamondMac.setCreeperBonus(1.0F).setKnockbackBoost(4.0F); //Homerun bat.
    	
    	diamondMac.addTooltipLocalize("tooltip.tool.macu.diamond.flavor");
    	diamondMac.addTooltipLocalize("tooltip.tool.macu.diamond.info");
    	
    	macuahuitls.add(diamondMac);
    	GameRegistry.registerItem(diamondMac, diamondMac.getUnlocalizedName());
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diamondMac, 1), new Object[]{
    	    "HSH", 
    	    "HSH", 
    	    " S ",
    	    'H', Items.diamond, 
    	    'S', "stickWood"}));

    	//Hella introns follow:
    	// -- OBSIDIAN
    	/*obsidianPick = new ItemModPick(obsidianMat);
    	obsidianPick.setUnlocalizedName("obsidian_pick");
    	obsidianPick.setTextureName("oldowanobsidian:obsidian_pick");
    	obsidianPick.setCreativeTab(CreativeTabs.tabTools);
    	
    	obsidianShovel = new ItemSpade(obsidianMat);
    	obsidianShovel.setUnlocalizedName("obsidian_shovel");
    	obsidianShovel.setTextureName("oldowanobsidian:obsidian_shovel");
    	obsidianShovel.setCreativeTab(CreativeTabs.tabTools);

    	obsidianAxe = new ItemModAxe(obsidianMat);
    	obsidianAxe.setUnlocalizedName("obsidian_axe");
    	obsidianAxe.setTextureName("oldowanobsidian:obsidian_axe");
    	obsidianAxe.setCreativeTab(CreativeTabs.tabTools);*/
    	//obsidianMac.setUnlocalizedName("obsidian_macuahuitl");
    	//obsidianMac.setTextureName("oldowanobsidian:obsidian_macuahuitl");
    	//obsidianMac.setCreativeTab(CreativeTabs.tabCombat);

    	//boneMac = new ItemMacuahuitl(boneMat, (ToolMaterial.IRON.getDamageVsEntity() + swordConstant) - 1.0F);
    	//boneMac.setUnlocalizedName("bone_macuahuitl");
    	//boneMac.setTextureName("oldowanobsidian:bone_macuahuitl");
    	//boneMac.setCreativeTab(CreativeTabs.tabCombat);
    	
    	//flintMac = new ItemMacuahuitl(boneMat, (ToolMaterial.IRON.getDamageVsEntity() + swordConstant) - 1.0F);
    	//flintMac.setUnlocalizedName("bone_macuahuitl");
    	//flintMac.setTextureName("oldowanobsidian:bone_macuahuitl");
    	//flintMac.setCreativeTab(CreativeTabs.tabCombat);

    	//GameRegistry.registerItem(obsidianPick, obsidianPick.getUnlocalizedName());
    	//GameRegistry.registerItem(obsidianShovel, obsidianShovel.getUnlocalizedName());
    	//GameRegistry.registerItem(obsidianAxe, obsidianAxe.getUnlocalizedName());
    	
    	//GameRegistry.registerItem(obsidianMac, obsidianMac.getUnlocalizedName());
    }
    
    public void postInit(FMLPostInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(this);
    	FMLCommonHandler.instance().bus().register(this);
    	
    	//obsidianMat.setRepairItem(new ItemStack(OldowanObsidian.material, 4, OldowanObsidian.shardLogic.meta));
    }
    

    //Forge events follow. --------------------------------
	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event)
	{
		if (! (event.source instanceof EntityDamageSource)) return;
		EntityDamageSource source = (EntityDamageSource) event.source;
		if(source.getSourceOfDamage() instanceof EntityPlayer)
		{
			EntityPlayer attacker = (EntityPlayer) source.getSourceOfDamage();
			if( attacker.getCurrentEquippedItem().getItem() instanceof ItemMacuahuitl )
			{
				ItemMacuahuitl mac = (ItemMacuahuitl)attacker.getCurrentEquippedItem().getItem();
				//Respect invulnerability frames.
				if(event.entityLiving.hurtResistantTime <= 0)
				{
					//Prevent us from killing the creeper without proper events / noises / etc
					float newHP = (float) (event.entityLiving.getHealth() - (event.ammount * mac.getCreeperBonus()));
					event.entityLiving.setHealth((newHP >= 0) ? newHP : 0.5F);
				}
			}
		}
	}
}
