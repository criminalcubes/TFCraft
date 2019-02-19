package com.bioxx.tfc.Entities.Mobs;

import java.util.Calendar;

import com.bioxx.tfc.Core.TFC_MobData;
import com.bioxx.tfc.api.Enums.EnumDamageType;
import com.bioxx.tfc.api.Interfaces.ICausesDamage;
import com.bioxx.tfc.api.Interfaces.IInnateArmor;
import com.bioxx.tfc.api.TFCItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityBandit extends EntityMob implements ICausesDamage, IInnateArmor {

    private boolean field_146076_bu = false;
    private final EntityAIBreakDoor field_146075_bs = new EntityAIBreakDoor(this);
    private int textureVariant;
    private EnumDamageType damageType = EnumDamageType.CRUSHING;

    private Item[] gems = new Item[]{
            TFCItems.gemRuby,
            TFCItems.gemSapphire,
            TFCItems.gemEmerald,
            TFCItems.gemTopaz,
            TFCItems.gemGarnet,
            TFCItems.gemOpal,
            TFCItems.gemAmethyst,
            TFCItems.gemJasper,
            TFCItems.gemBeryl,
            TFCItems.gemTourmaline,
            TFCItems.gemJade,
            TFCItems.gemAgate,
            TFCItems.gemDiamond
    };

    private Item[] metals = new Item[]{
            TFCItems.bismuthIngot,
            TFCItems.bismuthBronzeIngot,
            TFCItems.blackBronzeIngot,
            TFCItems.blackSteelIngot,
            TFCItems.highCarbonBlackSteelIngot,
            TFCItems.blueSteelIngot,
            TFCItems.weakBlueSteelIngot,
            TFCItems.highCarbonBlueSteelIngot,
            TFCItems.brassIngot,
            TFCItems.bronzeIngot,
            TFCItems.copperIngot,
            TFCItems.goldIngot,
            TFCItems.wroughtIronIngot,
            TFCItems.leadIngot,
            TFCItems.nickelIngot,
            TFCItems.pigIronIngot,
            TFCItems.platinumIngot,
            TFCItems.redSteelIngot,
            TFCItems.weakRedSteelIngot,
            TFCItems.highCarbonRedSteelIngot,
            TFCItems.roseGoldIngot,
            TFCItems.silverIngot,
            TFCItems.steelIngot,
            TFCItems.weakSteelIngot,
            TFCItems.highCarbonSteelIngot,
            TFCItems.sterlingSilverIngot,
            TFCItems.tinIngot,
            TFCItems.zincIngot
    };

    public EntityBandit(World par1World) {
        super(par1World);
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
        this.setSize(0.6F, 1.8F);
        this.experienceValue = 10;
        this.textureVariant = rand.nextInt(2) + 1;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(TFC_MobData.BANDIT_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(TFC_MobData.BANDIT_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(TFC_MobData.BANDIT_SPEED);
    }

    public int getTotalArmorValue() {
        int i = super.getTotalArmorValue() + 2;
        if (i > 20) {
            i = 20;
        }

        return i;
    }

    protected boolean isAIEnabled() {
        return true;
    }

    protected int getExperiencePoints(EntityPlayer p_70693_1_) {
        if (this.isChild()) {
            this.experienceValue = (int)((float)this.experienceValue * 7.5F);
        }

        return super.getExperiencePoints(p_70693_1_);
    }

    public void addRandomEquipment() {
        this.addRandomArmor();
    }

    protected void addRandomArmor() {
        int i = this.rand.nextInt(2);
        float f = this.worldObj.difficultySetting == EnumDifficulty.HARD ? 0.1F : 0.25F;
        if (this.rand.nextFloat() < 0.095F) {
            ++i;
        }

        if (this.rand.nextFloat() < 0.095F) {
            ++i;
        }

        if (this.rand.nextFloat() < 0.095F) {
            ++i;
        }

        for(int j = 3; j >= 0; --j) {
            ItemStack itemstack = this.func_130225_q(j);
            if (j < 3 && this.rand.nextFloat() < f) {
                break;
            }

            if (itemstack == null) {
                Item item = getArmor(j + 1, i);
                if (item != null) {
                    this.setCurrentItemOrArmor(j + 1, new ItemStack(item));
                }
            }
        }

        i = this.rand.nextInt(4);
        switch (i) {
            case 0:
                this.setCurrentItemOrArmor(0, new ItemStack(TFCItems.bronzeMace));
                this.damageType = EnumDamageType.CRUSHING;
                break;
            case 1:
                this.setCurrentItemOrArmor(0, new ItemStack(TFCItems.bronzeAxe));
                this.damageType = EnumDamageType.SLASHING;
                break;
            case 2:
                this.setCurrentItemOrArmor(0, new ItemStack(TFCItems.bronzeSword));
                this.damageType = EnumDamageType.PIERCING;
                break;
            case 3:
                this.setCurrentItemOrArmor(0, new ItemStack(TFCItems.bronzeHammer));
                this.damageType = EnumDamageType.CRUSHING;
                break;
            case 4:
                this.setCurrentItemOrArmor(0, new ItemStack(TFCItems.bronzeJavelin));
                this.damageType = EnumDamageType.PIERCING;
                break;
    }
}

    private Item selectArmor(int level, int type) {
        double percent = (level + 1) * 0.2;
        if (percent > 1) {
            percent = 1;
        }
        if (Math.random() <= percent) {
            if (Math.random() <= 0.5) {
                switch (type) {
                    case 4:
                        return TFCItems.leatherHelmet;
                    case 2:
                        return TFCItems.leatherChestplate;
                    case 1:
                        return TFCItems.leatherLeggings;
                    case 0:
                        return TFCItems.leatherBoots;
                }
            } else {
                switch (type) {
                    case 4:
                        return TFCItems.bronzeHelmet;
                    case 2:
                        return TFCItems.bronzeChestplate;
                    case 1:
                        return TFCItems.bronzeGreaves;
                    case 0:
                        return TFCItems.bronzeBoots;
                }
            }
        }
        return null;
    }

    private Item getArmor(int p_82161_0_, int p_82161_1_) {
        return selectArmor(p_82161_1_, p_82161_0_);
    }

    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setBoolean("CanBreakDoors", this.func_146072_bX());
    }

    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        this.func_146070_a(p_70037_1_.getBoolean("CanBreakDoors"));
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData p_110161_1_) {
        Object p_110161_1_1 = super.onSpawnWithEgg(p_110161_1_);
        float f = this.worldObj.func_147462_b(this.posX, this.posY, this.posZ);
        this.setCanPickUpLoot(false);
        this.func_146070_a(this.rand.nextFloat() < f * 0.1F);
        this.addRandomArmor();
        if (this.getEquipmentInSlot(4) == null) {
            Calendar calendar = this.worldObj.getCurrentDate();
            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
                this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                this.equipmentDropChances[4] = 0.0F;
            }
        }

        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05000000074505806D, 0));
        double d0 = this.rand.nextDouble() * 1.5D * (double)this.worldObj.func_147462_b(this.posX, this.posY, this.posZ);
        return (IEntityLivingData)p_110161_1_1;
    }

    public boolean func_146072_bX() {
        return this.field_146076_bu;
    }

    public void func_146070_a(boolean p_146070_1_) {
        if (this.field_146076_bu != p_146070_1_) {
            this.field_146076_bu = p_146070_1_;
            if (p_146070_1_) {
                this.tasks.addTask(1, this.field_146075_bs);
            } else {
                this.tasks.removeTask(this.field_146075_bs);
            }
        }

    }

    public boolean isEntityUndead() {
        return false;
    }

    public boolean isChild() {
        return false;
    }

    public String getLivingSound() {
        return null;
    }

    public String getHurtSound() {
        return "game.neutral.hurt";
    }

    public String getDeathSound() {
        return "game.neutral.die";
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
        this.playSound("step.wood", 0.15F, 1.0F);
    }

    public boolean attackEntityFrom(DamageSource source, float damage) {
        super.attackEntityFrom(source, damage);
        return true;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = super.attackEntityAsMob(entity);
        if (flag) {
            int i = this.worldObj.difficultySetting.getDifficultyId();
            if (this.getHeldItem() == null && this.isBurning() && this.rand.nextFloat() < (float)i * 0.3F) {
                entity.setFire(2 * i);
            }
        }

        return flag;
    }

    protected Item getDropItem() {
        return null;
    }

    public void dropFewItems(boolean hitRecently, int looting) {
        this.dropItem(TFCItems.nametag, this.rand.nextInt(1));
        this.dropItem(TFCItems.arrow, this.rand.nextInt(3));
        this.dropItem(TFCItems.rope, this.rand.nextInt(1));

        int count = this.rand.nextInt(3) + 1;
        for (int i = 0; i < count; i++) {
            try {
                this.dropItem(gems[this.rand.nextInt(gems.length)], 1);
            } catch (Exception e) {
                System.out.println("Error on dropping gem from bandit!");
            }
        }
    }

    public void dropRareDrop(int looting) {
        try {
            this.dropItem(metals[this.rand.nextInt(metals.length)], 1);
        } catch (Exception e) {
            System.out.println("Error on dropping metal from bandit!");
        }
    }

    public boolean canDespawn() {
        return true;
    }

    public int getTextureVariant() {
        return textureVariant;
    }

    public void setTextureVariant(int textureVariant) {
        this.textureVariant = textureVariant;
    }

    @Override
    public EnumDamageType getDamageType() {
        return damageType;
    }

    @Override
    public int getCrushArmor()
    {
        return 100;
    }

    @Override
    public int getSlashArmor()
    {
        return -100;
    }

    @Override
    public int getPierceArmor()
    {
        return -100;
    }
}
