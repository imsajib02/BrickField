package com.b2gsoft.mrb.Utils;

import java.text.DecimalFormat;

public class RoundFormat {

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Double.valueOf(twoDForm.format(d));
    }
}
