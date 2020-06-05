package micdoodle8.mods.galacticraft.core.schematic;

import micdoodle8.mods.galacticraft.api.recipe.SchematicPage;
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiSchematicInput;
import micdoodle8.mods.galacticraft.core.inventory.ContainerSchematic;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SchematicAdd extends SchematicPage
{
    @Override
    public int getPageID()
    {
        return ConfigManagerCore.idSchematicAddSchematic;
    }

    @Override
    public int getGuiID()
    {
        return GuiIdsCore.NASA_WORKBENCH_NEW_SCHEMATIC;
    }

    @Override
    public ItemStack getRequiredItem()
    {
        return null;  //This null is OK, it's used only as a flag by SchematicRegistry calling code
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Screen getResultScreen(PlayerEntity player, BlockPos pos)
    {
        return new GuiSchematicInput(player.inventory, pos);
    }

    @Override
    public Container getResultContainer(PlayerEntity player, BlockPos pos)
    {
        return new ContainerSchematic(player.inventory, pos);
    }
}
