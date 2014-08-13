// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;


// Referenced classes of package tge.core.logic:
//            LogicConstants

public class GotoEnd extends LogicConstants
{

    private int endIf;

	public String toString()
    {
        return "<GOTO POSITION>" + getEndIf();
    }

    public int getType()
    {
        return LogicConstants.GO_END;
    }

    public void setEndIf(int endif) {
    	this.endIf = endif;
    }
    
    public int getEndIf()
    {
    	return endIf;
    }
    
}
