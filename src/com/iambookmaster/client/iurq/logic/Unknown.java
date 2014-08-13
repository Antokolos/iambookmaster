// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Unknown extends LogicConstants
{

    public Unknown(Core core1, String s)
    {
        core = core1;
        string = s;
    }

    public String toString()
    {
        return "<UNKNOWN>" + core.getString(string);
    }

    public int getType()
    {
        return -1;
    }

    public String getStrValue()
    {
        return string;
    }

    private Core core;
    private String string;
}
