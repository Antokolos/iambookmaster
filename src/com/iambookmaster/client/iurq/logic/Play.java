// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Play extends LogicConstants
{

    private boolean background;
    private String filePath;

	public Play(Core core, String name,boolean background)
    {
   		filePath = name;
   		this.background = background;
    }

    public String getLocation()
    {
        return filePath;
    }

    public int getType()
    {
        return PLAY;
    }

}
