package ws.zettabyte.oldowanobsidian.item;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import ws.zettabyte.oldowanobsidian.OldowanObsidian;
import ws.zettabyte.oldowanobsidian.compat.TConstructCompat;
import ws.zettabyte.oldowanobsidian.util.DamageSourcePure;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
	
	public static ArrayList<String> skeleableMobs = new ArrayList<String>(8);
	public static int bonusSkeletonChance = 35;
	
	public static final int boneRepairRarity = 1; //1 in X. 0 to disable.
	public static final float boneRepairAmt = 0.10F; //Portion of total durability rather than flat amount.

	private static DamageSourcePure sacDamageSource = new DamageSourcePure();
	
	public static enum MACUAHUITL_STYLE {
		KNOCKBACK, SACRIFICE;
	}
	
	public static MACUAHUITL_STYLE macStyle = MACUAHUITL_STYLE.SACRIFICE;
	
	protected Random random = new Random();
	
	//public static float creeperbonusMacuahuitl = 0.4F;
	public ObsidianToolsModule(Configuration conf, Logger log) {
		config = conf;
		logger = log;

		skeleableMobs.add(EntityZombie.class.getName());
		skeleableMobs.add(EntityVillager.class.getName());
		skeleableMobs.add(EntityWitch.class.getName());
		skeleableMobs.add(EntityPigZombie.class.getName()); //Perhaps a separate wither skellie list later?
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
    	

		Property conf_skele = config.get("Tools", "SkeletonChance", bonusSkeletonChance);
		conf_skele.comment = "Chance for bone tools and weapons to spawn unarmed skeletons on killing certain other mobs (0 to disable)";
		bonusSkeletonChance = conf_skele.getInt(bonusSkeletonChance);
		
		Property conf_mac_style = config.get("Tools", "MacuahuitlSacrifice", true);
		conf_mac_style.comment = "When set to true, shift-right-clicking with a Macuahuitl will harm the player and reward them with buffs.\n"
				+ "When set to false, the Macuahuitl will have no right-click effect but have higher knockback and bonus damage to creepers.";
		if(conf_mac_style.getBoolean(true))
		{
			macStyle = MACUAHUITL_STYLE.SACRIFICE;
		}
		else
		{
			macStyle = MACUAHUITL_STYLE.KNOCKBACK;
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

    //"Adjust" refers to damage.
    public ItemMacuahuitl macForMat(ToolMaterial mat, Object materialItem, Object stickItem, float adjust)
    {
    	final float swordConstant = 4.0F;
    	
    	ItemMacuahuitl macuahuitl = new ItemMacuahuitl(mat, (mat.getDamageVsEntity() + swordConstant) - adjust);
    	macuahuitl.setUnlocalizedName(mat.toString().toLowerCase() + "_macuahuitl");
    	macuahuitl.setTextureName("oldowanobsidian:" + mat.toString().toLowerCase() + "_macuahuitl");

    	if(this.macStyle == MACUAHUITL_STYLE.KNOCKBACK)
    	{
	    	macuahuitl.addTooltipLocalize("tooltip.tool.macu.flavor1");
	    	macuahuitl.addTooltipLocalize("tooltip.tool.macu.info1");
    	}
    	else if(this.macStyle == MACUAHUITL_STYLE.SACRIFICE)
    	{
	    	macuahuitl.addTooltipLocalize("tooltip.tool.macu.flavor2");
	    	macuahuitl.addTooltipLocalize("tooltip.tool.macu.info2");
    	}
    	
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
    	
    	//Initialize these differently for different styles.
    	if(macStyle == MACUAHUITL_STYLE.KNOCKBACK)
    	{
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
	    	
	    	diamondMac.addTooltipLocalize("tooltip.tool.macu.diamond.flavor1");
	    	diamondMac.addTooltipLocalize("tooltip.tool.macu.diamond.info1");
	    	
	    	macuahuitls.add(diamondMac);
	    	GameRegistry.registerItem(diamondMac, diamondMac.getUnlocalizedName());
	    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diamondMac, 1), new Object[]{
	    	    "HSH", 
	    	    "HSH", 
	    	    " S ",
	    	    'H', Items.diamond, 
	    	    'S', "stickWood"}));
    	}
    	else
    	{
    		float lowKnockback = 1.1F;
    		float lowCreeperDamage = 0.2F;
    		//600 ticks is 30 seconds. TODO: Confirm this is in ticks.
	    	macForMat(obsidianMat, OldowanObsidian.shardRecipeName, 1.0F).setCreeperBonus(lowCreeperDamage)
	    	.setKnockbackBoost(lowKnockback).addTooltipLocalize("tooltip.tool.macu.obsidian.extra")
    		.addBuff(new PotionEffect(Potion.digSpeed.getId(),600, 0))
    		.addBuff(new PotionEffect(Potion.damageBoost.getId(),80, 1))
    		.addBuff(new PotionEffect(Potion.resistance.getId(),600, 0));
	    	
	    	macForMat(flintMat, Items.flint, 2.0F).setCreeperBonus(0.2F).setKnockbackBoost(1.0F)
    		.addBuff(new PotionEffect(Potion.digSpeed.getId(),600, 0))
    		.addBuff(new PotionEffect(Potion.damageBoost.getId(),80, 0))
    		.addBuff(new PotionEffect(Potion.fireResistance.getId(),100, 0));
	    	
	    	macForMat(boneMat, Items.bone, 2.5F).setCreeperBonus(0.2F).setKnockbackBoost(1.0F)
	    	.addTooltipLocalize("tooltip.tool.bone.flavor").addTooltipLocalize("tooltip.tool.bone.info")
    		.addBuff(new PotionEffect(Potion.damageBoost.getId(),60, 2))
    		.addBuff(new PotionEffect(Potion.resistance.getId(),100, 3))
    		.addBuff(new PotionEffect(Potion.regeneration.getId(),600, 0))
    		.addBuff(new PotionEffect(Potion.nightVision.getId(),600, 0));
	    	
	    	
	    	//Do the diamond one manually because Minecraft calls it Emerald for some reason.
	    	ItemMacuahuitl diamondMac = new ItemMacuahuitl(ToolMaterial.EMERALD, (ToolMaterial.EMERALD.getDamageVsEntity() + 4.0F) - 0.5F);
	    	diamondMac.setUnlocalizedName("diamond_macuahuitl");
	    	diamondMac.setTextureName("oldowanobsidian:diamond_macuahuitl");
	    	
	    	diamondMac.setCreeperBonus(0.4F).setKnockbackBoost(2.5F); //Homerun bat.
	    	
	    	diamondMac.addTooltipLocalize("tooltip.tool.macu.diamond.flavor2");
	    	diamondMac.addTooltipLocalize("tooltip.tool.macu.diamond.info2");
	    	
	    	diamondMac
	    	.addBuff(new PotionEffect(Potion.damageBoost.getId(),140, 1))
    		.addBuff(new PotionEffect(Potion.digSpeed.getId(),600, 1))
    		.addBuff(new PotionEffect(Potion.resistance.getId(),600, 1))
    		.addBuff(new PotionEffect(Potion.moveSpeed.getId(),400, 0));
	    	
	    	macuahuitls.add(diamondMac);
	    	GameRegistry.registerItem(diamondMac, diamondMac.getUnlocalizedName());
	    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(diamondMac, 1), new Object[]{
	    	    "HSH", 
	    	    "HSH", 
	    	    " S ",
	    	    'H', Items.diamond, 
	    	    'S', "stickWood"}));
    	}
    	
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
		if (! (macStyle == MACUAHUITL_STYLE.KNOCKBACK)) return; //No need for onhits with sacrifice style.
		if (event.entityLiving == null ) return;
		if (!(event.entityLiving instanceof EntityCreeper)) return;
		
		EntityDamageSource source = (EntityDamageSource) event.source;
		if(source.getSourceOfDamage() instanceof EntityPlayer)
		{
			EntityPlayer attacker = (EntityPlayer) source.getSourceOfDamage();
			if( (attacker.getCurrentEquippedItem() != null) && 
					(attacker.getCurrentEquippedItem().getItem() instanceof ItemMacuahuitl ))
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
	//Bone weapon skeleton spawn & repair-on-kill behavior.
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
		if (! (event.source instanceof EntityDamageSource)) return;
		if (! (event.source.getSourceOfDamage() instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)(event.source.getSourceOfDamage());
		String mat = null;

		ItemStack stack = player.getHeldItem();
		if(stack == null) return;

		Item item = stack.getItem();
        if(item == null) return;

		int swordAdj = 0;
		if(item instanceof ItemTool)
		{
			mat = ((ItemTool)item).getToolMaterialName();
		}
		if(item instanceof ItemSword)
		{
			mat = ((ItemSword)item).getToolMaterialName();
			swordAdj = 1;
		}
		if((mat != null) && (mat == "BONE"))
		{
			//Code for spawning skeleton.
			if (skeleableMobs.contains(event.entityLiving.getClass().getName()))
			{
				if((bonusSkeletonChance != 0) && (random.nextInt(100) <= bonusSkeletonChance))
				{
						EntitySkeleton toSpawn = new EntitySkeleton(event.entityLiving.worldObj);
						toSpawn.setPosition(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ);
						toSpawn.setHealth(toSpawn.getHealth()/2.0F);
						event.entityLiving.worldObj.spawnEntityInWorld(toSpawn);
						toSpawn.spawnExplosionParticle();
				}
			}
			//Code for repairing bone tools on kill.
			if((boneRepairRarity != 0) && (random.nextInt(boneRepairRarity + swordAdj) == 0))
			{
				int missing = stack.getItemDamage();
				int restore = (int) Math.ceil((float)stack.getMaxDamage() * boneRepairAmt) + swordAdj;
				if(restore > missing) restore = missing; //Clamp it.
				
				stack.setItemDamage(missing - restore);
			}
		}
	}
}
