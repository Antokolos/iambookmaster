// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class InvKill extends LogicConstants
{

    public InvKill(Core core, String s)
    {
        item = s.substring(7).trim();
    }

    public String toString()
    {
        return "<INVKILL>" + getItem();
    }

    public String getItem()
    {
        return item;
    }

    public int getType()
    {
        return 9;
    }

    String item;
}
