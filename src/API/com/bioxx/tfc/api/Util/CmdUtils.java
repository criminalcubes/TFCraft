package com.bioxx.tfc.api.Util;

import net.minecraft.command.PlayerNotFoundException;

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
}
