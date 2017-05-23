package com.mutant.godutch.utils;

import java.util.regex.Pattern;

/**
 * Created by evanfang102 on 2017/5/23.
 */

public class Patterns {

    public final static Pattern PASSWORD = Pattern.compile("[a-zA-Z\\d]{6,20}");

}
