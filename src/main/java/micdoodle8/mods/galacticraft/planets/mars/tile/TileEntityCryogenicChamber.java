package micdoodle8.mods.galacticraft.planets.mars.tile;

import java.util.LinkedList;
import java.util.List;

import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti;
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars;
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biomes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCryogenicChamber extends TileEntityMulti implements IMultiBlock
{
    public boolean isOccupied;
    private boolean initialised;

    public TileEntityCryogenicChamber()
    {
        super(null);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos().getX() - 1, getPos().getY(), getPos().getZ() - 1, getPos().getX() + 2, getPos().getY() + 3, getPos().getZ() + 2);
    }

    @Override
    public boolean onActivated(PlayerEntity entityPlayer)
    {
        if (this.world.isRemote)
        {
            return false;
        }

        PlayerEntity.SleepResult enumstatus = this.sleepInBedAt(entityPlayer, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());

        switch (enumstatus)
        {
        case OK:
            ((ServerPlayerEntity) entityPlayer).connection.setPlayerLocation(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, entityPlayer.rotationYaw, entityPlayer.rotationPitch);
            GalacticraftCore.packetPipeline.sendTo(new PacketSimpleMars(EnumSimplePacketMars.C_BEGIN_CRYOGENIC_SLEEP, GCCoreUtil.getDimensionID(entityPlayer.world), new Object[] { this.getPos() }), (ServerPlayerEntity) entityPlayer);
            return true;
        case NOT_POSSIBLE_NOW:
            GCPlayerStats stats = GCPlayerStats.get(entityPlayer);
            entityPlayer.sendMessage(new StringTextComponent(GCCoreUtil.translateWithFormat("gui.cryogenic.chat.cant_use", stats.getCryogenicChamberCooldown() / 20)));
            return false;
        default:
            return false;
        }
    }

    public PlayerEntity.SleepResult sleepInBedAt(PlayerEntity entityPlayer, int par1, int par2, int par3)
    {
        if (!this.world.isRemote)
        {
            if (entityPlayer.isPlayerSleeping() || !entityPlayer.isEntityAlive())
            {
                return PlayerEntity.SleepResult.OTHER_PROBLEM;
            }

            if (this.world.getBiome(new BlockPos(par1, par2, par3)) == Biomes.HELL)
            {
                return PlayerEntity.SleepResult.NOT_POSSIBLE_HERE;
            }

            GCPlayerStats stats = GCPlayerStats.get(entityPlayer);
            if (stats.getCryogenicChamberCooldown() > 0)
            {
                return PlayerEntity.SleepResult.NOT_POSSIBLE_NOW;
            }
        }

        if (entityPlayer.isRiding())
        {
            entityPlayer.dismountRidingEntity();
        }

        entityPlayer.setPosition(this.getPos().getX() + 0.5F, this.getPos().getY() + 1.9F, this.getPos().getZ() + 0.5F);

        entityPlayer.sleeping = true;
        entityPlayer.sleepTimer = 0;
        entityPlayer.bedLocation = new BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        entityPlayer.motionX = entityPlayer.motionZ = entityPlayer.motionY = 0.0D;

        if (!this.world.isRemote)
        {
            this.world.updateAllPlayersSleepingFlag();
        }

        return PlayerEntity.SleepResult.OK;
    }

//    @Override
//    public boolean canUpdate()
//    {
//        return true;
//    }

    @Override
    public void update()
    {
        if (!this.initialised)
        {
            this.initialised = this.initialiseMultiTiles(this.getPos(), this.world);
        }
    }

    @Override
    public void onCreate(World world, BlockPos placedPosition)
    {
        this.mainBlockPosition = placedPosition;
        this.markDirty();

        List<BlockPos> positions = new LinkedList<>();
        this.getPositions(placedPosition, positions);
        ((BlockMulti) GCBlocks.fakeBlock).makeFakeBlock(world, positions, placedPosition, this.getMultiType());
    }
    
    @Override
    public BlockMulti.EnumBlockMultiType getMultiType()
    {
        return EnumBlockMultiType.CRYO_CHAMBER;
    }

    @Override
    public void getPositions(BlockPos placedPosition, List<BlockPos> positions)
    {
        int buildHeight = this.world.getHeight() - 1;

        for (int y = 1; y < 3; y++)
        {
            if (placedPosition.getY() + y > buildHeight)
            {
                return;
            }
            positions.add(new BlockPos(placedPosition.getX(), placedPosition.getY() + y, placedPosition.getZ()));
        }
    }

    @Override
    public void onDestroy(TileEntity callingBlock)
    {
        final BlockPos thisBlock = getPos();
        List<BlockPos> positions = new LinkedList<>();
        this.getPositions(thisBlock, positions);

        for (BlockPos pos : positions)
        {
            BlockState stateAt = this.world.getBlockState(pos);

            if (stateAt.getBlock() == GCBlocks.fakeBlock && (EnumBlockMultiType) stateAt.getValue(BlockMulti.MULTI_TYPE) == EnumBlockMultiType.CRYO_CHAMBER)
            {
                if (this.world.isRemote && this.world.rand.nextDouble() < 0.1D)
                {
                    Minecraft.getInstance().effectRenderer.addBlockDestroyEffects(pos, this.world.getBlockState(pos));
                }
                this.world.destroyBlock(pos, false);
            }
        }
        this.world.destroyBlock(thisBlock, true);
    }

    @Override
    public void readFromNBT(CompoundNBT nbt)
    {
        super.readFromNBT(nbt);
        this.isOccupied = nbt.getBoolean("IsChamberOccupied");
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("IsChamberOccupied", this.isOccupied);
        return nbt;
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        return this.writeToNBT(new CompoundNBT());
    }
}
