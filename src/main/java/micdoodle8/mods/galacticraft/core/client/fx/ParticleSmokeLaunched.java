package micdoodle8.mods.galacticraft.core.client.fx;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleSmokeLaunched extends ParticleLaunchSmoke
{
    public ParticleSmokeLaunched(World world, double posX, double posY, double posZ, double motX, double motY, double motZ, float size, IAnimatedSprite sprite)
    {
        super(world, posX, posY, posZ, motX, motY, motZ, size, true, sprite);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new ParticleSmokeLaunched(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 1.0F, this.spriteSet);
        }
    }
}