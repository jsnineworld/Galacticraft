package micdoodle8.mods.galacticraft.planets.venus.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock;
import micdoodle8.mods.galacticraft.core.items.IShiftDescription;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTorchWeb extends Block implements IShearable, IShiftDescription, ISortableBlock
{
    public static final EnumProperty<EnumWebType> WEB_TYPE = EnumProperty.create("webtype", EnumWebType.class);
    protected static final VoxelShape AABB_WEB = Block.makeCuboidShape(0.35, 0.0, 0.35, 0.65, 1.0, 0.65);
    protected static final VoxelShape AABB_WEB_TORCH = Block.makeCuboidShape(0.35, 0.25, 0.35, 0.65, 1.0, 0.65);

    public enum EnumWebType implements IStringSerializable
    {
        WEB_0(0, "web_torch_0"),
        WEB_1(1, "web_torch_1");

        private final int meta;
        private final String name;

        EnumWebType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta()
        {
            return this.meta;
        }

        private final static EnumWebType[] values = values();
        public static EnumWebType byMetadata(int meta)
        {
            return values[meta % values.length];
        }

        @Override
        public String getName()
        {
            return this.name;
        }
    }

    public BlockTorchWeb(Properties builder)
    {
        super(builder);
    }

    @Override
    public void getSubBlocks(ItemGroup tab, NonNullList<ItemStack> list)
    {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (state.get(WEB_TYPE) == EnumWebType.WEB_1)
        {
            return AABB_WEB_TORCH;
        }

        return AABB_WEB;
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos)
    {
        if (state.get(WEB_TYPE) == EnumWebType.WEB_1)
        {
            return 15;
        }

        return 0;
    }

//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public ItemGroup getCreativeTabToDisplayOn()
//    {
//        return GalacticraftCore.galacticraftBlocksTab;
//    }

    @Override
    public boolean isOpaqueCube(BlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(BlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        if (world.getBlockState(pos).getBlock().isReplaceable(world, pos) && this.canBlockStay(world, pos, this.getStateFromMeta(meta)))
        {
            return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        }
        return world.getBlockState(pos);
    }

    private boolean canBlockStay(World world, BlockPos pos, BlockState state)
    {
        BlockState blockUp = world.getBlockState(pos.up());
        return blockUp.getMaterial().isSolid() || blockUp.getBlock() == this && blockUp.getValue(WEB_TYPE) == EnumWebType.WEB_0;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.checkAndDropBlock(world, pos, state);
    }

    @Override
    public void updateTick(World world, BlockPos pos, BlockState state, Random rand)
    {
        this.checkAndDropBlock(world, pos, state);
    }

    protected void checkAndDropBlock(World world, BlockPos pos, BlockState state)
    {
        if (!this.canBlockStay(world, pos, state))
        {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.AIR);
    }

    @Override
    public int quantityDropped(Random rand)
    {
        return 0;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos))));
        return ret;
    }

    @Override
    public String getShiftDescription(int meta)
    {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }

    @Override
    public boolean showDescription(int meta)
    {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().with(WEB_TYPE, EnumWebType.byMetadata(meta));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(WEB_TYPE);
    }

    @Override
    public EnumSortCategoryBlock getCategory(int meta)
    {
        return EnumSortCategoryBlock.GENERAL;
    }
}
