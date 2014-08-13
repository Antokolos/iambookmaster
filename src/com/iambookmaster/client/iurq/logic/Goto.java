// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Goto extends LogicConstants
{

    public Goto(Core core1, String s)
    {
        core = core1;
        string = s.substring(5).trim();
    }

    public String toString()
    {
        return "<GOTO>" + getLocation();
    }

    public int getType()
    {
        return 19;
    }

    public String getLocation()
    {
        return core.getString(string).toLowerCase();
    }

    private Core core;
    private String string;
}
