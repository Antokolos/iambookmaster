// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Instr extends LogicConstants
{

    public Instr(Core core, String s)
    {
        value = "";
        s = s.substring(s.indexOf(' ')).trim();
        String as[] = s.split("=");
        var = as[0].trim();
        if(as.length > 1)
            value = as[1].trim();
    }

    public String getStrValue()
    {
        return value;
    }

    public String getVarName()
    {
        return var;
    }

    public String toString()
    {
        return "<STRING_ASIGNMENT>" + getVarName() + "=" + getStrValue();
    }

    public int getType()
    {
        return 6;
    }

    private String var;
    private String value;
}
