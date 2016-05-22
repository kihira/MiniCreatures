/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
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
        orePositions = new ArrayList<>();
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

    private void next() {
        int[] currPos = blockPositions[currentIndex];
        BlockPos blockPos = new BlockPos(currPos[0], currPos[1], currPos[2]);

        if (!world.isAirBlock(blockPos)) {
            IBlockState state = world.getBlockState(blockPos);
            int[] oreIDs = OreDictionary.getOreIDs(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));

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
