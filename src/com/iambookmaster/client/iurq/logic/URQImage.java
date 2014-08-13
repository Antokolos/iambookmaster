// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class URQImage extends LogicConstants
{

	private String filePath;

	public URQImage(Core core, String name)
    {
   		filePath = name;
    }

    public String getLocation()
    {
        return filePath;
    }

    public int getType()
    {
        return IMAGE;
    }

}
