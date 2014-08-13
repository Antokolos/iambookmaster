// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import java.util.HashMap;
import java.util.Vector;


// Referenced classes of package tge.core.logic:
//            Variable

public class InvVar extends Variable
{
    public class Action
    {

        public String toString()
        {
            return action;
        }

        public String getLocName()
        {
            return locName;
        }

        private String action;
        private String locName;

        public Action(String s, String s1)
        {
            locName = s1;
            if(s.length() == 0)
                action = "Осмотреть";
            else
                action = s.substring(1);
        }
    }


    public InvVar(int i)
    {
        super(i);
        actions = new Vector();
    }

    public boolean isTested()
    {
        return tested;
    }

    public void test(HashMap hashmap, String s)
    {
        for(int i = 0; i < hashmap.size(); i++)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)hashmap.entrySet().toArray()[i];
            String s1 = (String)entry.getKey();
            if(s1.startsWith(s))
                actions.add(new Action(s1.substring(s.length()), s1));
        }

        tested = true;
    }

    public Vector getActions()
    {
        return actions;
    }

    private boolean tested;
    private Vector actions;
}
