package com.bioxx.tfc.Entities.AI;

import com.bioxx.tfc.Core.Point3d;
import com.bioxx.tfc.api.TFCItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;

import com.bioxx.tfc.Core.TFC_Core;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.*;

public class EntityAILightPanic extends EntityAIBase
{
    private final EntityCreature theEntityCreature;
    private final double speed;
    private double randPosX;
    private double randPosY;
    private double randPosZ;
    private int criticalLightLevel = 10;
    private long scaredTime = 0;
    private Random random = new Random();

    /*
    private List<String> lightItems = Arrays.asList(
            new String[]{
                    //Item.getItemFromBlock(TFCBlocks.torch).getUnlocalizedName()
                    TFCItems.burningBranch.getUnlocalizedName()
            }
    );
    */

    public int randomBetween(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public EntityAILightPanic(EntityCreature par1EntityCreature, double par2)
    {
        this.theEntityCreature = par1EntityCreature;
        this.speed = par2;
        this.setMutexBits(1);
    }

    private boolean isNight(World world) {
        long time = world.getWorldTime();
        if (time > 12500) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute()
    {
        /*
        if ((isNight(this.theEntityCreature.worldObj) || this.theEntityCreature.posY < 140)
                && this.theEntityCreature.worldObj.getBlockLightValue((int) this.theEntityCreature.posX, (int) this.theEntityCreature.posY, (int) this.theEntityCreature.posZ) >= criticalLightLevel)
        {
            int radius = 32;
            int entity_x = (int) this.theEntityCreature.posX;
            int entity_y = (int) this.theEntityCreature.posY;
            int entity_z = (int) this.theEntityCreature.posZ;

            for (int counter = 0; counter < 100; counter++) {
                int x = randomBetween(entity_x - radius, entity_x + radius);
                int y = randomBetween(entity_y - 3, entity_y + 3);
                int z = randomBetween(entity_z - radius, entity_z + radius);

                if (this.theEntityCreature.worldObj.getBlockLightValue(x, y, z) < criticalLightLevel
                        //&& this.theEntityCreature.worldObj.getBlock(x, y - 1, z).isNormalCube()
                        //&& this.theEntityCreature.worldObj.getBlock(x, y, z) == Blocks.air
                        //&& this.theEntityCreature.worldObj.getBlock(x, y + 1, z) == Blocks.air
                        && Math.abs(this.theEntityCreature.posX - x) >= 8
                        && Math.abs(this.theEntityCreature.posZ - z) >= 8) {
                    this.randPosX = x;
                    this.randPosY = y;
                    this.randPosZ = z;
                    return true;
                } else {
                    //this.theEntityCreature.worldObj.setBlock(x, y, z, Blocks.wool);
                }
            }

            this.randPosX = 0;
            this.randPosY = 0;
            this.randPosZ = 0;
            System.out.println("[TerraFirmaCraft] LightPanic error!");
            return true;
        }
        else
        {
            return false;
        }
        */

        if ((isNight(this.theEntityCreature.worldObj) || this.theEntityCreature.posY < 140))
        {
            List<Point3d> lightPoints = new ArrayList<Point3d>();
            int radius = 6;

            int entity_x = (int) this.theEntityCreature.posX;
            int entity_y = (int) this.theEntityCreature.posY;
            int entity_z = (int) this.theEntityCreature.posZ;

            for (int x = entity_x - radius; x <= entity_x + radius; x++) {
                for (int y = entity_y - radius; y <= entity_y + radius; y++) {

                    for (int z = entity_z - radius; z <= entity_z + radius; z++) {
                        Block block = this.theEntityCreature.worldObj.getBlock(x, y, z);
                        if (block.getLightValue() > criticalLightLevel) {
                            Point3d lightPoint = new Point3d();
                            lightPoint.x = x;
                            lightPoint.y = y;
                            lightPoint.z = z;
                            lightPoints.add(lightPoint);
                        }
                    }
                }
            }

            List e = this.theEntityCreature.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(
                    this.theEntityCreature.posX-radius,
                    this.theEntityCreature.posY-radius,
                    this.theEntityCreature.posZ-radius,
                    (this.theEntityCreature.posX + radius),
                    (this.theEntityCreature.posY + radius),
                    (this.theEntityCreature.posZ + radius)
            ));

            if (e.size() > 0) {
                for (Iterator iterator = e.iterator(); iterator.hasNext();) {
                    EntityPlayer player = (EntityPlayer) iterator.next();

                    if (player.getHeldItem() != null
                            //&& lightItems.contains(player.getHeldItem().getItem().getUnlocalizedName())
                            && TFCItems.burningBranch == player.getHeldItem().getItem()
                        ) {
                        Point3d lightPoint = new Point3d();
                        lightPoint.x = player.posX;
                        lightPoint.y = player.posY;
                        lightPoint.z = player.posZ;
                        lightPoints.add(lightPoint);
                    }
                }
            }

            if (!lightPoints.isEmpty()) {
                Point3d panicLightPoint = null;
                if (lightPoints.size() == 1) {
                    panicLightPoint = lightPoints.get(0);
                } else {
                    int closestDist = 999;
                    for (Point3d lightPoint : lightPoints) {
                        int dist = (int) (Math.abs(this.theEntityCreature.posX - lightPoint.x) + Math.abs(this.theEntityCreature.posZ - lightPoint.z));
                        if (dist < closestDist) {
                            closestDist = dist;
                            panicLightPoint = lightPoint;
                        }
                    }
                }

                if (MinecraftServer.getSystemTimeMillis() - scaredTime > 5000) {
                    scaredTime = 0;
                }

                this.randPosX = this.theEntityCreature.posX - (panicLightPoint.x - this.theEntityCreature.posX) * randomBetween(2, 4);
                this.randPosY = this.theEntityCreature.posY;
                this.randPosZ = this.theEntityCreature.posZ - (panicLightPoint.z - this.theEntityCreature.posZ) * randomBetween(2, 4);
                return true;
            }
        }

        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        this.theEntityCreature.addPotionEffect(new PotionEffect(Potion.blindness.id, 20 * 5, 1));


        GameProfile gameProfile = new GameProfile(UUID.fromString("3186ad73-5b4b-4feb-9628-41f376264ef0"), "TerraFirmaCraft");
        EntityPlayer player = FakePlayerFactory.get((WorldServer) this.theEntityCreature.worldObj, gameProfile);

        this.theEntityCreature.setTarget(player);
        this.theEntityCreature.setRevengeTarget(player);
        this.theEntityCreature.setAttackTarget(player);

        /*
        for (int i = 0; i < 10; i++) {
            this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, speed);
            this.theEntityCreature.getMoveHelper().setMoveTo(this.randPosX, this.randPosY, this.randPosZ, speed);
        }
        */

        //this.theEntityCreature.getNavigator().clearPathEntity();
        this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, speed);

        if (scaredTime == 0) {
            scaredTime = MinecraftServer.getSystemTimeMillis();
            EntityPlayer closestPlayer = this.theEntityCreature.worldObj.getClosestPlayer(this.theEntityCreature.posX, this.theEntityCreature.posY, this.theEntityCreature.posZ, 8);
            if (closestPlayer != null) {
                this.theEntityCreature.worldObj.playSoundEffect(this.theEntityCreature.posX, this.theEntityCreature.posY, this.theEntityCreature.posZ, "fire.fire", 1F, 2F);
                TFC_Core.sendInfoMessage(closestPlayer, new ChatComponentTranslation("mob.panic.message", this.theEntityCreature.getCommandSenderName()));
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean continueExecuting()
    {
        //return !(this.theEntityCreature.getNavigator().noPath() && );\
        return !this.theEntityCreature.getNavigator().noPath();
    }
}
