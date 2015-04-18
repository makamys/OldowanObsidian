package ws.zettabyte.oldowanobsidian.world;

import java.util.Random;

import ws.zettabyte.oldowanobsidian.OldowanObsidian;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import cpw.mods.fml.common.IWorldGenerator;

public class BlackGravelWorldgen implements IWorldGenerator
{

	//These numbers represent tries, rather than actual values per chunk.
	public static final int highestY = 48;
	public static final int lowestY = 1;
	public static final int falloffBase = 1;
	public static final int falloffRand = 4;
	
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
    	// Only do this gen in the nether. TODO: Configgable list.
    	if(world.provider.dimensionId != -1) return;
    	
    	//Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
    	
    	//int added = perChunkBase + random.nextInt(perChunkRand);
    	
    	if(random.nextInt(OldowanObsidian.gravelChancePerChunk) == 0)
    	{
    		//Max radius
    		int cutoff = 3 - random.nextInt(2);
    		//Centerpoint.
    		int startX = (chunkX*16) + random.nextInt(15);
    		int startZ = (chunkZ*16) + random.nextInt(15);
    		int top = doColumn(world, OldowanObsidian.blackGravel, startX, startZ, highestY-random.nextInt(16)) - 1;
    		if(top > 2)
    		{
    			
    			//Start doing the rings around the center. (Unusual for-loop ahoy!)
    			for(int ring = 1; (top > 2) && (ring < cutoff); ++ring)
    			{
    				top = doRing(random, world, OldowanObsidian.blackGravel, startX, startZ, 
    						ring, top);
    			}
    		}
    	}
    }

    //Returns height of column. yLimit represents the height we're falling from - the previous ring's average height.
    private int doColumn(World world, Block b, int x, int z, int yLimit)
    {
    	int top = lowestY; //Height of column.
    	boolean started = false;
    	int y;
    	//Search for viable positions.
    	for(y = lowestY; y < yLimit; ++y)
    	{
    		if(Block.isEqualTo(world.getBlock(x, y, z), Blocks.air)
    				|| (world.getBlock(x, y, z).getMaterial() == Material.lava))
    		{
    			world.setBlock(x, y, z, b);
    			started = true;
    		}
    		else if(started == true) //Hitting a ceiling
    		{
    			break;
    		}
    	}
    	return y;
    }
    //Returns average topheight of ring. maxY represents the height we're falling from - the previous ring's average height.
    private int doRing(Random random, World world, Block b, int centerX, int centerZ, int innerRadius, int maxY)
    {
    	//int avgHeight = 0;
    	int totalHeight = 0;
    	int cCount = 0; //Column count.

    	//Side one.
    	for(int x = -innerRadius; x < innerRadius * 2; ++x)
    	{
    		int result = doColumn(world, b, centerX + x, centerZ - innerRadius, maxY - random.nextInt(falloffRand));
    		
    		if(result != 0)
    		{
    			totalHeight += result;
    			++cCount;
    		}
    	}
    	//Side two.
    	for(int x = -innerRadius; x < innerRadius * 2; ++x)
    	{
    		int result = doColumn(world, b, centerX + x, centerZ + innerRadius, maxY - random.nextInt(falloffRand));
    		
    		if(result != 0)
    		{
    			totalHeight += result;
    			++cCount;
    		}
    	}

    	//Side three.
    	for(int z = (-innerRadius)+1; z < (innerRadius * 2)-1; ++z)
    	{
    		int result = doColumn(world, b, centerX + innerRadius, centerZ+z, maxY - random.nextInt(falloffRand));
    		
    		if(result != 0)
    		{
    			totalHeight += result;
    			++cCount;
    		}
    	}
    	//Side four.
    	for(int z = (-innerRadius)+1; z < (innerRadius * 2)-1; ++z)
    	{
    		int result = doColumn(world, b, centerX - innerRadius, centerZ+z, maxY - random.nextInt(falloffRand));
    		
    		if(result != 0)
    		{
    			totalHeight += result;
    			++cCount;
    		}
    	}
    	
    	if(cCount == 0) return 0;
    	if((totalHeight/cCount) > maxY) throw new RuntimeException("Error: Impossible black gravel worldgen!");
    	return totalHeight/cCount;
    }

}
