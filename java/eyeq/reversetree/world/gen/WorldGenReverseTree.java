package eyeq.reversetree.world.gen;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class WorldGenReverseTree extends WorldGenAbstractTree {
    private final int minTreeHeight;
    private final IBlockState wood;
    private final IBlockState leaves;
    private final boolean vinesGrow;

    public WorldGenReverseTree(boolean notify, int minTreeHeight, IBlockState wood, IBlockState leaves, boolean vinesGrow) {
        super(notify);
        this.minTreeHeight = minTreeHeight;
        this.wood = wood;
        this.leaves = leaves;
        this.vinesGrow = vinesGrow;
    }

    public WorldGenReverseTree(boolean notify) {
        this(notify, 4, Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState(), false);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int treeHeight = rand.nextInt(3) + this.minTreeHeight;
        if(pos.getY() >= world.getHeight() || pos.getY() - treeHeight < 1) {
            return false;
        }
        for(int dy = 0; dy < treeHeight; dy++) {
            int d;
            if(dy == 0) {
                d = 0;
            } else if(dy >= treeHeight - 2) {
                d = 2;
            } else {
                d = 1;
            }

            for(int dx = -d; dx < d + 1; dx++) {
                for(int dz = -d; dz < d + 1; dz++) {
                    BlockPos blockPos = pos.add(dx, -dy, dz);
                    if(blockPos.getY() < 0 || world.getHeight() <= blockPos.getY()) {
                        return false;
                    }
                    if(!this.isReplaceable(world, blockPos)) {
                        return false;
                    }
                }
            }
        }

        IBlockState state = world.getBlockState(pos.up());
        if(!state.getBlock().canSustainPlant(state, world, pos.up(), EnumFacing.DOWN, (IPlantable) Blocks.SAPLING)) {
            return false;
        }
        this.setDirtAt(world, pos.up());
        for(int dy = -3; dy < 0; dy++) {
            int d = 1 - dy / 2;
            for(int dx = -d; dx < d + 1; dx++) {
                for(int dz = -d; dz < d + 1; dz++) {
                    if(Math.abs(dx) != d || Math.abs(dz) != d || rand.nextInt(2) != 0) {
                        BlockPos blockPos = pos.add(dx, -treeHeight - dy, dz);
                        state = world.getBlockState(blockPos);
                        if(world.isAirBlock(pos) || state.getMaterial() == Material.LEAVES || state.getMaterial() == Material.VINE) {
                            this.setBlockAndNotifyAdequately(world, blockPos, this.leaves);
                            if(this.vinesGrow) {
                                if(rand.nextInt(4) == 0 && world.isAirBlock(blockPos.offset(EnumFacing.EAST))) {
                                    this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.EAST), Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, Boolean.TRUE));
                                }
                                if(rand.nextInt(4) == 0 && world.isAirBlock(blockPos.offset(EnumFacing.WEST))) {
                                    this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.WEST), Blocks.VINE.getDefaultState().withProperty(BlockVine.WEST, Boolean.TRUE));
                                }
                                if(rand.nextInt(4) == 0 && world.isAirBlock(blockPos.offset(EnumFacing.SOUTH))) {
                                    this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.SOUTH), Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, Boolean.TRUE));
                                }
                                if(rand.nextInt(4) == 0 && world.isAirBlock(blockPos.offset(EnumFacing.NORTH))) {
                                    this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.NORTH), Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, Boolean.TRUE));
                                }
                            }
                        }
                    }
                }
            }
        }
        for(int dy = 0; dy < treeHeight; dy++) {
            BlockPos blockPos = pos.down(dy);
            state = world.getBlockState(blockPos);
            if(world.isAirBlock(blockPos) || state.getMaterial() == Material.LEAVES || state.getMaterial() == Material.VINE) {
                this.setBlockAndNotifyAdequately(world, blockPos, this.wood);
                if(this.vinesGrow && dy != 0) {
                    if(rand.nextInt(3) > 0 && world.isAirBlock(blockPos.offset(EnumFacing.EAST))) {
                        this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.EAST), Blocks.VINE.getDefaultState().withProperty(BlockVine.EAST, Boolean.TRUE));
                    }
                    if(rand.nextInt(3) > 0 && world.isAirBlock(blockPos.offset(EnumFacing.WEST))) {
                        this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.WEST), Blocks.VINE.getDefaultState().withProperty(BlockVine.WEST, Boolean.TRUE));
                    }
                    if(rand.nextInt(3) > 0 && world.isAirBlock(blockPos.offset(EnumFacing.SOUTH))) {
                        this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.SOUTH), Blocks.VINE.getDefaultState().withProperty(BlockVine.SOUTH, Boolean.TRUE));
                    }
                    if(rand.nextInt(3) > 0 && world.isAirBlock(blockPos.offset(EnumFacing.NORTH))) {
                        this.setBlockAndNotifyAdequately(world, blockPos.offset(EnumFacing.NORTH), Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, Boolean.TRUE));
                    }
                }
            }
        }
        if(this.vinesGrow && rand.nextInt(5) == 0 && treeHeight > 5) {
            for(int dy = 0; dy < 2; dy++) {
                if(rand.nextInt(4 - dy) == 0) {
                    BlockPos blockPos = pos.up(-treeHeight - dy + 5);
                    for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
                        this.setBlockAndNotifyAdequately(world, blockPos.offset(facing.getOpposite()), Blocks.COCOA.getDefaultState().withProperty(BlockCocoa.AGE, rand.nextInt(3)).withProperty(BlockCocoa.FACING, facing));
                    }
                }
            }
        }
        return true;
    }
}
