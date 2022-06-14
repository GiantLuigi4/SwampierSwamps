package andrews.swampier_swamps.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin implements SimpleWaterloggedBlock
{
    @Shadow protected abstract BlockState copyRandomFaces(BlockState state1, BlockState state2, RandomSource random);
    @Shadow protected abstract boolean hasHorizontalConnection(BlockState state);
    @Shadow protected abstract boolean canSupportAtFace(BlockGetter blockGetter, BlockPos pos, Direction direction);

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void VineBlock(BlockBehaviour.Properties properties, CallbackInfo ci)
    {
        ((VineBlock)(Object)this).defaultBlockState = ((VineBlock)(Object) this).stateDefinition.any().setValue(VineBlock.UP, false).setValue(VineBlock.NORTH, false).setValue(VineBlock.EAST, false).setValue(VineBlock.SOUTH, false).setValue(VineBlock.WEST, false).setValue(BlockStateProperties.WATERLOGGED, false);
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1, shift = At.Shift.AFTER))
    public void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random, CallbackInfo ci)
    {
        BlockPos posBelow = pos.below();
        BlockState blockstate = serverLevel.getBlockState(posBelow);
        if (blockstate.isAir() || blockstate.getBlock().equals(Blocks.WATER) || blockstate.is(((VineBlock)(Object)this)))
        {
            BlockState blockstate1 = (blockstate.isAir() || blockstate.getBlock().equals(Blocks.WATER)) ? ((VineBlock)(Object)this).defaultBlockState() : blockstate;
            BlockState blockstate2 = copyRandomFaces(state, blockstate1, random);
            if (blockstate1 != blockstate2 && hasHorizontalConnection(blockstate2))
            {
                serverLevel.setBlock(posBelow, blockstate2.setValue(BlockStateProperties.WATERLOGGED, true), 2);
            }
        }
    }

    @Inject(method = "createBlockStateDefinition", at = @At(value = "TAIL"))
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition, CallbackInfo ci)
    {
        stateDefinition.add(BlockStateProperties.WATERLOGGED);
    }

    @Inject(method = "getStateForPlacement", at = @At(value = "RETURN"), cancellable = true)
    public void getStateForPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir)
    {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        if(cir.getReturnValue() != null)
            cir.setReturnValue(cir.getReturnValue().setValue(BlockStateProperties.WATERLOGGED, fluidstate.getType() == Fluids.WATER));
    }

    @Inject(method = "updateShape", at = @At(value = "HEAD"))
    public void updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1, CallbackInfoReturnable<BlockState> cir)
    {
        if (state.getValue(BlockStateProperties.WATERLOGGED))
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
    }
}