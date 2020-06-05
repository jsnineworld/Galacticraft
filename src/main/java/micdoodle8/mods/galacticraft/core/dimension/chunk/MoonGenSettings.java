package micdoodle8.mods.galacticraft.core.dimension.chunk;

import net.minecraft.world.gen.GenerationSettings;

public class MoonGenSettings extends GenerationSettings {
    public int getBiomeSize() {
        return 4;
    }

    public int getRiverSize() {
        return 4;
    }

    public int getBiomeId() {
        return -1;
    }

    public int getBedrockFloorHeight() {
        return 0;
    }

    public int getHomeTreeDistance() {
        return 20;
    }

    public int getHomeTreeSeparation() {
        return 4;
    }
}