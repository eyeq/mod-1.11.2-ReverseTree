package eyeq.reversetree.block;

import eyeq.reversetree.ReverseTree;
import eyeq.reversetree.world.gen.WorldGenReverseTree;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockSaplingReverse extends BlockSapling {
    public static final PropertyBool FAKE = PropertyBool.create("fake");

    public BlockSaplingReverse() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(FAKE, Boolean.FALSE));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    public String getLocalizedName() {
        return I18n.translateToLocal(this.getUnlocalizedName() + ".name");
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        if(world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            pos = pos.up();
            IBlockState soil = world.getBlockState(pos);
            return soil.getBlock().canSustainPlant(soil, world, pos, EnumFacing.DOWN, this);
        }
        return false;
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        if(state.getBlock() == this) {
            pos = pos.up();
            IBlockState soil = world.getBlockState(pos);
            return soil.getBlock().canSustainPlant(soil, world, pos, EnumFacing.DOWN, this);
        }
        return this.canSustainBush(world.getBlockState(pos));
    }

    @Override
    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand) {
        if(!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(world, rand, pos)) return;
        if(state.getValue(FAKE) && rand.nextInt(20) != 0) {
            world.setBlockToAir(pos);
            spawnAsEntity(world, pos, new ItemStack(Blocks.DEADBUSH));
            return;
        }
        WorldGenerator generator = new WorldGenReverseTree(true, 4, Blocks.LOG.getDefaultState(), ReverseTree.reverseLeaves.getDefaultState(), false);
        world.setBlockToAir(pos);
        if(!generator.generate(world, rand, pos)) {
            world.setBlockState(pos, state, 4);
        }
    }

    @Override
    public boolean isTypeAt(World world, BlockPos pos, BlockPlanks.EnumType type) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this && BlockPlanks.EnumType.OAK == type;
    }

    protected Item getItem(IBlockState state) {
        Block block;
        if(getMetaFromState(state) == 0) {
            block = this;
        } else {
            block = Blocks.SAPLING;
        }
        return Item.getItemFromBlock(block);
    }

    @Override
    @Deprecated
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        Item item = getItem(state);
        return item == null ? null : new ItemStack(item, 1, this.damageDropped(state));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return getItem(state);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FAKE, (meta & 1) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FAKE) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, STAGE, FAKE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(item));
    }
}
