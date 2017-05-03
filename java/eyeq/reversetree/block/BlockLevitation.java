package eyeq.reversetree.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockLevitation extends Block {
    public BlockLevitation(Material material) {
        super(material);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleUpdate(pos, this, this.tickRate(world));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        AxisAlignedBB aabb = new AxisAlignedBB(-1.0D, -1.0D, -1.0D, 2.0D, 2.0D, 2.0D).offset(pos);
        for(EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, aabb)) {
            entity.fallDistance = 0.0F;
            entity.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 10, 20));
        }
        world.scheduleUpdate(pos, this, this.tickRate(world));
    }
}
