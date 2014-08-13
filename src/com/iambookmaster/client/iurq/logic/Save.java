// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Save extends LogicConstants
{

    public Save(Core core1, String s)
    {
        core = core1;
        location = s.substring(4).trim().toLowerCase();
    }

    public String toString()
    {
        return "<SAVE>" + location;
    }

    public int getType()
    {
        return 15;
    }

    public String getLocation()
    {
        return core.getString(location);
    }

    private String location;
    private Core core;
}
