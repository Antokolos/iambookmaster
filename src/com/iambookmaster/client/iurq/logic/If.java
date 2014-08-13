// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq.logic;

import com.iambookmaster.client.iurq.Core;

// Referenced classes of package tge.core.logic:
//            LogicConstants, Variable

public class If extends LogicConstants
{

    public If(Core core1)
    {
        core = core1;
    }

    public int getType()
    {
        return 12;
    }

    public void setExp(String s)
    {
        expr = s;
    }

    public void setGoElse(int i)
    {
        elseInstr = i;
    }

    public void setGoIf(int i)
    {
        ifInstr = i;
    }

    public int getGoto()
    {
//    	if (expr.equals("not хомяк")) {
//    		expr = expr.trim();
//    	}
        if(checkExpr(expr))
        {
            ifEnd = elseInstr;
            return ifInstr;
        } else
        {
            ifEnd = end;
            return elseInstr;
        }
    }

    public String toString()
    {
        return "<IF>->" + expr + "<- true goto " + ifInstr + ", false goto " + elseInstr + ", end if " + end;
    }

    private boolean checkExpr(String s)
    {
        s = s.trim();
        int i = s.length();
        if (i>2) {
        	if (s.charAt(0)=='(' && s.charAt(i-1)==')') {
        		//useless brackets, remove them
        		s = s.substring(1,i-1);
        	}
        }
        if((i = hasExpression(s,"or")) > 0) {
            return checkExpr(s.substring(0, i)) || checkExpr(s.substring(i + 2));
        }
        if((i = hasExpression(s,"and")) > 0) {
            return checkExpr(s.substring(0, i)) && checkExpr(s.substring(i + 3));
        }
        if((i = hasExpression(s,"not")) >= 0) {
            if(i == 0) {
                return !checkExpr(s.substring(i + 3));
            }
        } else {
            if((i = s.indexOf("<=")) >= 0)
            {
                float f = core.doExpr(s.substring(0, i));
                float f6 = core.doExpr(s.substring(i + 2));
                return Math.round(f) <= Math.round(f6);
            }
            if((i = s.indexOf("<>")) >= 0)
            {
                float f1 = core.doExpr(s.substring(0, i));
                float f7 = core.doExpr(s.substring(i + 2));
                return Math.round(f1) != Math.round(f7);
            }
            if((i = s.indexOf(">=")) >= 0)
            {
                float f2 = core.doExpr(s.substring(0, i));
                float f8 = core.doExpr(s.substring(i + 2));
                return Math.round(f2) >= Math.round(f8);
            }
            if((i = s.indexOf("<")) >= 0)
            {
                float f3 = core.doExpr(s.substring(0, i));
                float f9 = core.doExpr(s.substring(i + 1));
                return Math.round(f3) < Math.round(f9);
            }
            if((i = s.indexOf(">")) >= 0)
            {
                float f4 = core.doExpr(s.substring(0, i));
                float f10 = core.doExpr(s.substring(i + 1));
                return Math.round(f4) > Math.round(f10);
            }
            if((i = s.indexOf("=")) >= 0)
            {
                float f5 = core.doExpr(s.substring(0, i));
                float f11 = core.doExpr(s.substring(i + 1));
                return Math.round(f5) == Math.round(f11);
            }
        }
        if(core.getInvent().get(s) != null) {
            return true;
        }
        i = s.indexOf(',');
        if (i<0) {
            i = s.indexOf(' ');
        }
        if(i>0) {
            String s1 = s.substring(0, i).trim();
            String s2 = s.substring(i + 1).trim();
            if(core.getInvent().get(s2) != null)
            {
                Variable variable = (Variable)core.getInvent().get(s2);
                try {
					if(variable.getInt() >= Integer.parseInt(s1))
					    return true;
				} catch (NumberFormatException e) {
					//TODO error
				    return false;
				}
            }
        } else {
        	Variable variable = core.getVariable(s);
        	return variable.isEmpty()==false; 
        }
        
        return false;
    }

    private int hasExpression(String s, String expr	) {
    	int i = s.indexOf(expr);
    	if (i<0) {
    		//not exist
    		return -1;
    	} else if (i==0) {
    		if (Character.isLetterOrDigit(s.charAt(i+expr.length()))) {
    		 	return -1;
    		}
    	} else if (Character.isLetterOrDigit(s.charAt(i-1)) || Character.isLetterOrDigit(s.charAt(i+expr.length()))){
    		//it is a part of an identificator
    		return -1;
    	}
		return i;
	}

	public void setGoEnd(int i)
    {
        end = i;
    }

    public int getEndIf()
    {
        return ifEnd;
    }

    public int getEnd()
    {
        return end;
    }

    private String expr;
    private int ifInstr;
    private int elseInstr;
    private int ifEnd;
    private int end;
    private Core core;
}
