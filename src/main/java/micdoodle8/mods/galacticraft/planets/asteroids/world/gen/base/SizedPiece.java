package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public abstract class SizedPiece extends Piece
{
    protected Direction direction;
    protected int sizeX;
    protected int sizeY;
    protected int sizeZ;

    public SizedPiece()
    {
    }

    public SizedPiece(BaseConfiguration configuration, int sizeX, int sizeY, int sizeZ, Direction direction)
    {
        super(configuration);
        this.direction = direction;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public Direction getDirection()
    {
        return direction;
    }

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }

    @Override
    protected void writeStructureToNBT(CompoundNBT tagCompound)
    {
        super.writeStructureToNBT(tagCompound);

        tagCompound.setInteger("dir", this.direction.ordinal());
        tagCompound.setInteger("sX", this.sizeX);
        tagCompound.setInteger("sY", this.sizeY);
        tagCompound.setInteger("sZ", this.sizeZ);
    }

    @Override
    protected void readStructureFromNBT(CompoundNBT tagCompound, TemplateManager manager)
    {
        super.readStructureFromNBT(tagCompound, manager);

        this.sizeX = tagCompound.getInteger("sX");
        this.sizeY = tagCompound.getInteger("sY");
        this.sizeZ = tagCompound.getInteger("sZ");

        if (tagCompound.hasKey("dir"))
        {
            this.direction = Direction.byIndex(tagCompound.getInteger("dir"));
        }
        else
        {
            this.direction = Direction.NORTH;
        }
    }

    public int getSizeX()
    {
        return sizeX;
    }

    public int getSizeY()
    {
        return sizeY;
    }

    public int getSizeZ()
    {
        return sizeZ;
    }

    @Override
    protected int getXWithOffset(int x, int z)
    {
        if (this.getCoordBaseMode() == null)
        {
            return x;
        }
        else
        {
            switch (this.getCoordBaseMode())
            {
                case NORTH:
                    return this.boundingBox.minX + x;
                case SOUTH:
                    return this.boundingBox.maxX - x;
                case WEST:
                    return this.boundingBox.maxX - z;
                case EAST:
                    return this.boundingBox.minX + z;
                default:
                    return x;
            }
        }
    }

    @Override
    protected int getZWithOffset(int x, int z)
    {
        if (this.getCoordBaseMode() == null)
        {
            return z;
        }
        else
        {
            switch (this.getCoordBaseMode())
            {
                case NORTH:
                    return this.boundingBox.minZ + z;
                case SOUTH:
                    return this.boundingBox.maxZ - z;
                case WEST:
                    return this.boundingBox.minZ + x;
                case EAST:
                    return this.boundingBox.maxZ - x;
                default:
                    return z;
            }
        }
    }

    //Unused currently
    public Piece getDoorway(Random rand, BaseStart startPiece, int maxAttempts, boolean small)
    {
        Direction randomDir;
        int blockX;
        int blockZ;
        int sizeX;
        int sizeZ;
        boolean valid;
        int attempts = maxAttempts;
        do
        {
            int randDir = rand.nextInt(4);
            randomDir = Direction.getHorizontal((randDir == getDirection().getOpposite().getHorizontalIndex() ? randDir + 1 : randDir) % 4);
            MutableBoundingBox extension = getExtension(randomDir, 1, 3);
            blockX = extension.minX;
            blockZ = extension.minZ;
            sizeX = extension.maxX - extension.minX;
            sizeZ = extension.maxZ - extension.minZ;
            valid = true;
            attempts--;
        }
        while (!valid && attempts > 0);

        if (!valid)
        {
            return null;
        }

        return new BaseLinking(this.configuration, rand, blockX, this.boundingBox.minY, blockZ, sizeX, 3, sizeZ, randomDir);
    }
}
