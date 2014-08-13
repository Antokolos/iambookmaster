// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class VarAction extends LogicConstants
{

    public VarAction(Core core1, String s)
    {
        core = core1;
        int i = s.indexOf("=");
        varName = s.substring(0, i).trim();
        action = s.substring(i + 1).trim();
    }

    public float doAction()
    {
        return core.doExpr(action);
    }

    public String getVarName()
    {
        return varName;
    }

    public String toString()
    {
        return "<VAR_ACTION>" + action;
    }

    public int getType()
    {
        return 5;
    }

    private String action;
    private String varName;
    private Core core;
}
