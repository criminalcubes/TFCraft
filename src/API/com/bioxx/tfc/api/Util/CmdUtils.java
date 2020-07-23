package com.bioxx.tfc.api.Util;

import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 *
 * @author Swarg
 */
public class CmdUtils
{
    public static double parseDouble(String[] params, int index, int minLimit, int maxLimit) {
        double value = 0;
        if ( params == null || index < 0 || index > params.length) 
            return 0;
        try {
            value = Double.parseDouble(params[index]);
        } catch (NumberFormatException e) {
            throw new PlayerNotFoundException("Invalid");
        }
        if (value < minLimit || value > maxLimit) {
            throw new PlayerNotFoundException(new StringBuilder("OutOfBounds ").append(minLimit).append('-').append(maxLimit).toString());
        }
        return value;
    }
    
    public static long getRangeForPlayer(EntityPlayerMP player) {
        if (player == null) return -1;
        int highY = (int) player.posY;
        int lowY = (int) player.posY;
        if (player.onGround) {
            lowY -=10;
            highY +=32;
        } else {
            lowY-=32;
            highY += 10;
        }
        if (highY > 255) highY = 255;
        if (lowY < 4) highY = 4;
        return packIntsToLong(lowY, highY);
    }
    
    public static long packIntsToLong(int a, int b) {
        return (long) a & 0xFFFFFFFFL | ((long) b & 0xFFFFFFFFL) << 32 ;
    }
    public static int getA(long range) {
        return (int)range;
    }    
    public static int getB(long range) {
        return (int)(range >> 32);
    }
}
