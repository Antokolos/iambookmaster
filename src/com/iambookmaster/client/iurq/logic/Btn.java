// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Btn extends LogicConstants
{

    public Btn(Core core1, String s)
    {
        core = core1;
        s = s.substring(4);
        int i = s.indexOf(',');
        if(i == -1)
        {
            System.out.println(s);
            core.exit();
        }
        location = s.substring(0, i).trim().toLowerCase();
        name = s.substring(i + 1).trim();
    }

    public String toString()
    {
        return "<BTN>" + getLocation() + "," + getName() + " PhantomState=" + isPhantom();
    }

    public boolean isPhantom()
    {
        return core.getLocation(getLocation()) == null;
    }

    public int getType()
    {
        return 4;
    }

    public String getLocation()
    {
        return core.getString(location);
    }

    public String getName()
    {
        if(!isPhantom())
            return core.getString(name);
        else
            return core.getString(name) + "// Phantom";
    }

    private Core core;
    private String name;
    private String location;
}
