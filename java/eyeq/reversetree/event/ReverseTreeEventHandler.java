package eyeq.reversetree.event;

import eyeq.reversetree.ReverseTree;
import eyeq.util.entity.player.EntityPlayerUtils;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReverseTreeEventHandler {
    @SubscribeEvent
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        if(itemStack.getItem() != Item.getItemFromBlock(Blocks.SAPLING) || BlockPlanks.EnumType.OAK != Blocks.SAPLING.getStateFromMeta(itemStack.getMetadata()).getValue(BlockSapling.TYPE)) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        BlockPos pos = event.getPos().down();
        if(Blocks.SAPLING.canPlaceBlockAt(world, pos)) {
            return;
        }
        EnumFacing facing = event.getFace();
        if(EnumFacing.DOWN == facing) {
            if(ReverseTree.reverseSapling.canPlaceBlockAt(world, pos)) {
                ItemStack dummy = new ItemStack(ReverseTree.reverseSapling, itemStack.getCount(), 1);
                EntityPlayerUtils.onItemUse(player, world, dummy, pos, facing, new Vec3d(pos), event.getHand());
                itemStack.setCount(dummy.getCount());
                event.setCanceled(true);
            }
        }
    }
}
