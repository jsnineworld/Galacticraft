package micdoodle8.mods.galacticraft.core.client.gui.container;

import micdoodle8.mods.galacticraft.api.recipe.ISchematicResultPage;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.inventory.ContainerBuggyBench;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

public class GuiSchematicBuggy extends GuiPositionedContainer<ContainerBuggyBench> implements ISchematicResultPage
{
    private static final ResourceLocation buggyBenchTexture = new ResourceLocation(Constants.MOD_ID_CORE, "textures/gui/buggybench.png");

    private int pageIndex;

    public GuiSchematicBuggy(PlayerInventory playerInv, BlockPos pos)
    {
        super(new ContainerBuggyBench(playerInv, pos, Minecraft.getInstance().player), playerInv, new TranslationTextComponent("schematic.moonbuggy.name"), pos);
        this.ySize = 221;
    }

    @Override
    protected void init()
    {
        super.init();
        this.buttons.clear();
        this.buttons.add(new Button(this.width / 2 - 130, this.height / 2 - 110, 40, 20, GCCoreUtil.translate("gui.button.back.name"), (button) -> {
            SchematicRegistry.flipToLastPage(this, this.pageIndex);
        }));
        this.buttons.add(new Button(this.width / 2 - 130, this.height / 2 - 110 + 25, 40, 20, GCCoreUtil.translate("gui.button.next.name"), (button) -> {
            SchematicRegistry.flipToNextPage(this, this.pageIndex);
        }));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.font.drawString(GCCoreUtil.translate("schematic.moonbuggy.name"), 7, -20 + 27, 4210752);
        this.font.drawString(GCCoreUtil.translate("container.inventory"), 8, 202 - 104 + 2 + 27, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bindTexture(GuiSchematicBuggy.buggyBenchTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - 221) / 2;
        this.blit(var5, var6, 0, 0, this.xSize, 220);
    }

    @Override
    public void setPageIndex(int index)
    {
        this.pageIndex = index;
    }
}
