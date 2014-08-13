// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Input extends LogicConstants
{

    public Input(Core core, String s)
    {
        varName = s.substring(s.indexOf(' ')).trim();
    }

    public String getVarName()
    {
        return varName;
    }

    public String toString()
    {
        return "<INPUT_ASIGNMENT>" + getVarName();
    }

    public int getType()
    {
        return 7;
    }

    private String varName;
}
