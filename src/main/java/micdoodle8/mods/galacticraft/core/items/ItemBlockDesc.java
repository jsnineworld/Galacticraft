//package micdoodle8.mods.galacticraft.core.items;
//
//import micdoodle8.mods.galacticraft.core.GCBlocks;
//import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
//import net.minecraft.block.Block;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.player.ClientPlayerEntity;
//import net.minecraft.client.util.ITooltipFlag;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraft.world.World;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
//public class ItemBlockDesc extends ItemBlockGC
//{
//    public ItemBlockDesc(Block block)
//    {
//        super(block);
//    }
//
//    @Override
//    public void onCreated(ItemStack stack, World world, PlayerEntity player)
//    {
//        if (!world.isRemote)
//        {
//            return;
//        }
//
//        //The player could be a FakePlayer made by another mod e.g. LogisticsPipes
//        if (player instanceof ClientPlayerEntity)
//        {
//            if (this.getBlock() == GCBlocks.fuelLoader)
//            {
//                ClientProxyCore.playerClientHandler.onBuild(4, (ClientPlayerEntity) player);
//            }
//            else if (this.getBlock() == GCBlocks.fuelLoader)
//            {
//                ClientProxyCore.playerClientHandler.onBuild(6, (ClientPlayerEntity) player);
//            }
//        }
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
//    {
//        if (this.getBlock() instanceof IShiftDescription && ((IShiftDescription) this.getBlock()).showDescription(stack))
//        {
//            if (Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown())
//            {
//                List<String> descString = Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(((IShiftDescription) this.getBlock()).getShiftDescription(stack), 150);
//                for (String string : descString)
//                {
//                    tooltip.add(new StringTextComponent(string));
//                }
//            }
//            else
//            {
//                /*if (this.getBlock() instanceof BlockTileGC) TODO Other GC blocks
//                {
//                    TileEntity te = ((BlockTileGC) this.getBlock()).createTileEntity(null, getBlock().getStateFromMeta(stack.getItemDamage() & 12));
//                    if (te instanceof TileBaseElectricBlock)
//                    {
//                        float powerDrawn = ((TileBaseElectricBlock) te).storage.getMaxExtract();
//                        if (powerDrawn > 0)
//                        {
//                            tooltip.add(TextFormatting.GREEN + GCCoreUtil.translateWithFormat("item_desc.powerdraw.name", EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20)));
//                        }
//                    }
//                }
//                else if (this.getBlock() instanceof BlockAdvancedTile)
//                {
//                    TileEntity te = ((BlockAdvancedTile) this.getBlock()).createTileEntity(worldIn, getBlock().getStateFromMeta(stack.getItemDamage() & 12));
//                    if (te instanceof TileBaseElectricBlock)
//                    {
//                        float powerDrawn = ((TileBaseElectricBlock) te).storage.getMaxExtract();
//                        if (powerDrawn > 0)
//                        {
//                            tooltip.add(TextFormatting.GREEN + GCCoreUtil.translateWithFormat("item_desc.powerdraw.name", EnergyDisplayHelper.getEnergyDisplayS(powerDrawn * 20)));
//                        }
//                    }
//                }
//                tooltip.add(GCCoreUtil.translateWithFormat("item_desc.shift.name", Minecraft.getInstance().gameSettings.keyBindSneak.getLocalizedName()));*/
//            }
//        }
//    }
//}
