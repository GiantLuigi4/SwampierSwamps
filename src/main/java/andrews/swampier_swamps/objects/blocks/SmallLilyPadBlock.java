package andrews.swampier_swamps.objects.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallLilyPadBlock extends WaterlilyBlock
{
    private static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);

    public SmallLilyPadBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        return AABB;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        super.entityInside(state, level, pos, entity);
        if (level instanceof ServerLevel && (entity instanceof Boat || (entity instanceof LivingEntity livingEntity && livingEntity.getBbHeight() > 1F)))
        {
            level.destroyBlock(new BlockPos(pos), false, entity);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        FluidState fluidState = level.getFluidState(context.getClickedPos().below());
        FluidState fluidStateAbove = level.getFluidState(context.getClickedPos());

        if ((fluidState.getType() == Fluids.WATER || state.getMaterial() == Material.ICE) && fluidStateAbove.getType() == Fluids.EMPTY)
            return this.defaultBlockState();
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos posBelow = pos.below();
//        if (state.getBlock() == this)
//            return level.getBlockState(posBelow).canSustainPlant(level, posBelow, Direction.UP, this);

        return this.mayPlaceOn(level.getBlockState(posBelow), level, posBelow);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        FluidState fluidState = level.getFluidState(pos);
        FluidState fluidStateAbove = level.getFluidState(pos.above());
        return (fluidState.getType() == Fluids.WATER || state.getMaterial() == Material.ICE) && fluidStateAbove.getType() == Fluids.EMPTY;
    }
}
