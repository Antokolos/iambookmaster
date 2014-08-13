// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants

public class Comment extends LogicConstants
{

    public Comment(Core core, String s)
    {
        comment = s;
    }

    public String toString()
    {
        return "<COMMENT>" + comment;
    }

    public String getName()
    {
        return comment;
    }

    public int getType()
    {
        return 1;
    }

    private String comment;
}
