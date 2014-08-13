// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;


public class Variable
{

    public Variable(String s)
    {
        strValue = "";
        fValue = 0.0F;
        strValue = s;
        type = 2;
    }

    public Variable(int i)
    {
        strValue = "";
        fValue = 0.0F;
        type = i;
    }

    public Variable(float f)
    {
        strValue = "";
//        fValue = 0.0F;
        fValue = f;
        type = 3;
    }

    public Variable()
    {
        strValue = "";
        fValue = 0.0F;
        type = 1;
    }

    public int getType()
    {
        return type;
    }

    public float getFloat()
    {
        return fValue;
    }

    public int getInt()
    {
        return Math.round(fValue);
    }

    public String getString()
    {
        return strValue;
    }

    public void setType(int i)
    {
        type = i;
    }

    public void setFloat(float f)
    {
        type = 3;
        fValue = f;
    }

    public String toString()
    {
        switch(type)
        {
        case FLOAT: // '\003'
            return String.valueOf(getInt());

        case STRING: // '\002'
            return strValue;
        }
        return super.toString();
    }

    public void setString(String s)
    {
        type = 2;
        strValue = s;
    }

    public static final int UNDEFINED = 1;
    public static final int STRING = 2;
    public static final int FLOAT = 3;
    protected int type;
    protected String strValue;
    protected float fValue;
	public boolean isEmpty() {
		if (type==FLOAT) {
			return fValue==0;
		} else {
			return strValue==null || strValue.length()==0; 
		}
	}
}
