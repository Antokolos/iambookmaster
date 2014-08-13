// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class InvAdd extends LogicConstants
{

    public InvAdd(Core core1, String s)
    {
        core = core1;
        s = s.substring(4).trim();
        int i;
        if((i = s.indexOf(',')) > 0)
        {
            var = s.substring(i + 1).trim();
            count = s.substring(0, i).trim();
        } else
        {
            var = s;
            count = "1";
        }
    }

    public String toString()
    {
        return "<INV_ADD>" + getVarName() + "," + getFltValue();
    }

    public int getType()
    {
        return 10;
    }

    public String getVarName()
    {
        return var;
    }

    public float getFltValue()
    {
        return core.doExpr(count);
    }

    private String var;
    private String count;
    private Core core;
}
