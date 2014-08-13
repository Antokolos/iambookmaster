// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Location extends LogicConstants
{

    public Location(Core core, String s)
    {
        name = s.toLowerCase().substring(1);
    }

    public String toString()
    {
        return "<LOCATION>" + getLocation();
    }

    public String getLocation()
    {
        return name;
    }

    public int getType()
    {
        return 2;
    }

    private String name;
}
