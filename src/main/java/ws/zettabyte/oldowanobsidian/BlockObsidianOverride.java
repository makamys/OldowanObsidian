package ws.zettabyte.oldowanobsidian;

import java.util.Random;

import net.minecraft.block.BlockObsidian;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

//TODO: Delete this after next commit.
@Deprecated
public class BlockObsidianOverride extends BlockObsidian {
	// Kept for reference.
	@Override
	public Item getItemDropped(int metadata, Random random, int fortune) {
		// TODO actual obsidian shards
		return Items.flint;
	}

	@Override
	public int damageDropped(int metadata) {
		return 0;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int fortuneLevel) {
		super.harvestBlock(world, player, x, y, z, fortuneLevel);
	}

	@Override
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		// TODO Auto-generated method stub
		super.registerBlockIcons(p_149651_1_);
	}

	@Override
	public int quantityDropped(int meta, int fortuneLevel, Random random) {
		return 4 + random.nextInt(fortuneLevel + 1);
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
		Random temprand = new Random();
		//Make sure we're not silk touching this thing.
		if (this.getItemDropped(metadata, temprand, fortune) != Item.getItemFromBlock(this))
			return 2 + temprand.nextInt(3);
		return 0;
	}

}
