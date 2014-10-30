/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class OreFinder {

    private final World world;
    private final int[][] blockPositions;
    public final List<int[]> orePositions;

    private int currentIndex;

    public OreFinder(World world, int originX, int originY, int originZ, int radius, int yRadius) {
        this.world = world;

        //Builds a list of block positions to check for ores
        blockPositions = new int[(radius * 2) * (radius * 2) * (yRadius * 2)][];
        int index = 0;
        for (int x = originX - radius; x < originX + radius; x++) {
            for (int z = originZ - radius; z < originZ + radius; z++) {
                for (int y = originY - yRadius; y < originY + yRadius; y++) {
                    blockPositions[index] = new int[]{x, y, z};
                    index++;
                }
            }
        }
        orePositions = new ArrayList<int[]>();
    }

    public void next(int count) {
        if (currentIndex + count > blockPositions.length) {
            count = blockPositions.length - currentIndex;
        }
        while (count > 0) {
            next();
            count--;
        }
    }

    public void next() {
        int[] currPos = blockPositions[currentIndex];
        int x = currPos[0], y = currPos[1], z = currPos[2];
        Block block = world.getBlock(x, y, z);
        if (block != null && !block.isAir(world, x, y, z)) {
            int[] oreIDs = OreDictionary.getOreIDs(new ItemStack(block, 1, world.getBlockMetadata(x, y, z)));
            if (oreIDs.length > 0) {
                for (int oreID : oreIDs) {
                    String oreName = OreDictionary.getOreName(oreID);
                    if (oreName.startsWith("gem") || oreName.startsWith("ore")) {
                        orePositions.add(currPos);
                    }
                }
            }
        }
        currentIndex++;
    }

    public boolean hasNext() {
        return currentIndex < blockPositions.length;
    }
}
