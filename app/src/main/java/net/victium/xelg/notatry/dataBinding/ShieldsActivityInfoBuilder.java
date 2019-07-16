package net.victium.xelg.notatry.dataBinding;

import android.content.Context;

import net.victium.xelg.notatry.utilities.PreferenceUtilities;

public class ShieldsActivityInfoBuilder {

    public static ShieldsActivityInfo createShieldsActivityInfo(Context context) {

        ShieldsActivityInfo info = new ShieldsActivityInfo();

        int currentMagicPower = PreferenceUtilities.getCurrentMagicPower(context);
        int magicPowerLimit = PreferenceUtilities.getMagicPowerLimit(context);
        info.magicPower = String.format("Резерв силы: %s/%s", currentMagicPower, magicPowerLimit);

        info.battleForm = PreferenceUtilities.getBattleForm(context);

        int naturalDefence = PreferenceUtilities.getNaturalDefence(context);
        info.naturalDefence = String.format("Естественная защита: %s", naturalDefence);

        return info;
    }
}
