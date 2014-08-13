// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Pause extends LogicConstants
{

    public Pause(Core core, String s)
    {
        time = Long.parseLong(s.substring(5).trim());
    }

    public String toString()
    {
        return "<PAUSE>" + getTime();
    }

    public int getType()
    {
        return 14;
    }

    public long getTime()
    {
        return time;
    }

    private long time;
}
