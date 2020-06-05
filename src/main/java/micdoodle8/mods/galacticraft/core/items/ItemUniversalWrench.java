package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvanced;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import micdoodle8.mods.miccore.Annotations;
import net.minecraft.block.BlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ItemUniversalWrench extends Item implements ISortableItem
{
    public ItemUniversalWrench(Item.Properties properties)
    {
        super(properties);
        this.setUnlocalizedName(assetName);
        this.setMaxStackSize(1);
        this.setMaxDamage(256);
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.tools.IToolWrench", modID = CompatibilityManager.modidBuildcraft)
    public boolean canWrench(PlayerEntity player, Hand hand, ItemStack wrench, RayTraceResult rayTrace)
    {
        return true;
    }

    @Annotations.RuntimeInterface(clazz = "buildcraft.api.tools.IToolWrench", modID = CompatibilityManager.modidBuildcraft)
    public void wrenchUsed(PlayerEntity player, Hand hand, ItemStack wrench, RayTraceResult rayTrace)
    {
        ItemStack stack = player.inventory.getCurrentItem();

        if (!stack.isEmpty())
        {
            stack.damageItem(1, player);

            if (stack.getItemDamage() >= stack.getMaxDamage())
            {
                stack.shrink(1);
            }

            if (stack.getCount() <= 0)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            }
        }
    }

    public void wrenchUsed(PlayerEntity entityPlayer, BlockPos pos)
    {

    }

//    @Override
//    public ItemGroup getCreativeTab()
//    {
//        return GalacticraftCore.galacticraftItemsTab;
//    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Rarity getRarity(ItemStack par1ItemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return true;
    }

    @Override
    public void onCreated(ItemStack stack, World world, PlayerEntity player)
    {
        if (world.isRemote && player instanceof ClientPlayerEntity)
        {
            ClientProxyCore.playerClientHandler.onBuild(3, (ClientPlayerEntity) player);
        }
    }

    @Override
    public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, Hand hand)
    {
        if (world.isRemote || player.isSneaking())
        {
            return ActionResultType.PASS;
        }

        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockAdvanced)
        {
            if (((BlockAdvanced) state.getBlock()).onUseWrench(world, pos, player, hand, player.getHeldItem(hand), side, hitX, hitY, hitZ))
            {
                return ActionResultType.SUCCESS;
            }
        }

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet())
        {
            IProperty<?> iProperty = entry.getKey();
            if (iProperty instanceof PropertyEnum && iProperty.getName().equals("facing") && state.get(iProperty) instanceof Direction)
            {
                EnumProperty<Direction> property = (EnumProperty<Direction>) iProperty;
                Collection<Direction> values = property.getAllowedValues();
                if (values.size() > 0)
                {
                    boolean done = false;
                    Direction currentFacing = state.get(property);
                    
                    // Special case: horizontal facings should be rotated around the Y axis - this includes most of GC's own blocks
                    if (values.size() == 4 && !values.contains(Direction.UP) && !values.contains(Direction.DOWN))
                    {
                        Direction newFacing = currentFacing.rotateY();
                        if (values.contains(newFacing))
                        {
                            world.setBlockState(pos, state.with(property, newFacing));
                            done = true;
                        }
                    }
                    if (!done)
                    {
                        // General case: rotation will follow the order in FACING (may be a bit jumpy)
                        List<Direction> list = Arrays.asList(values.toArray(new Direction[0]));
                        int i = list.indexOf(currentFacing) + 1;
                        Direction newFacing = list.get(i >= list.size() ? 0 : i);
                        world.setBlockState(pos, state.with(property, newFacing));
                    }

                    ItemStack stack = player.getHeldItem(hand).copy();
                    stack.damageItem(1, player);
                    player.setHeldItem(hand, stack);

                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof TileBaseUniversalElectrical)
                        ((TileBaseUniversalElectrical) tile).updateFacing();

                    return ActionResultType.SUCCESS;
                }
                return ActionResultType.PASS;
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.TOOLS;
    }
}
