package com.bioxx.tfc.Items;

import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;
import net.minecraft.item.ItemStack;

public class ItemBurningBranch extends ItemTerra {

    public ItemBurningBranch()
    {
        super();
        setMaxDamage(0);
        this.setCreativeTab(TFCTabs.TFC_WEAPONS);
    }

    @Override
    public EnumSize getSize(ItemStack is)
    {
        return EnumSize.TINY;
    }

    @Override
    public EnumWeight getWeight(ItemStack is)
    {
        return EnumWeight.LIGHT;
    }
}
