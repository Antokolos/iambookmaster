// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Print extends LogicConstants
{

    public Print(Core core1, String s)
    {
        core = core1;
        int i;
        if((i = s.indexOf(' ')) > 0)
            s = s.substring(i + 1);
        string = s;
    }

    public String getStrValue()
    {
        return core.getString(string);
    }

    public String toString()
    {
        return "<PRINT>" + getStrValue();
    }

    public int getType()
    {
        return 3;
    }

    private Core core;
    private String string;
}
