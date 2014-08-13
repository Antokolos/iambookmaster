// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;


public abstract class Operator
{

    public Operator()
    {
    }

    public abstract int getType();

    public String getLocation()
    {
        return null;
    }

    public float doAction()
    {
        return 0.0F;
    }

    public String getStrValue()
    {
        return null;
    }

    public float getFltValue()
    {
        return 0.0F;
    }

    public String getVarName()
    {
        return null;
    }

    public String getItem()
    {
        return null;
    }

    public int getGoto()
    {
        return 0;
    }

    public int getEndIf()
    {
        return 0;
    }

    public int getEnd()
    {
        return 0;
    }

    public boolean isPhantom()
    {
        return false;
    }
}
