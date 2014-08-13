// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import com.iambookmaster.client.iurq.logic.Btn;
import com.iambookmaster.client.iurq.logic.GotoEnd;
import com.iambookmaster.client.iurq.logic.InvVar;
import com.iambookmaster.client.iurq.logic.InvVar.Action;
import com.iambookmaster.client.iurq.logic.LogicConstants;
import com.iambookmaster.client.iurq.logic.Operator;
import com.iambookmaster.client.iurq.logic.Pause;
import com.iambookmaster.client.iurq.logic.Play;
import com.iambookmaster.client.iurq.logic.URQImage;
import com.iambookmaster.client.iurq.logic.Variable;


// Referenced classes of package tge.core:
//            Property, Lang, FileFilterQSV, FileFilterQS1, 
//            FileFilterQST, URQParser

public class Core {

    private final String inventoryName;
	
    private int currInstr;
    private int prewInstr;
    public boolean debug;
    private boolean runned=true;
    private boolean end;
    private boolean interrupt;
    private URQUI gui;
    private Vector stack;
    private Vector retInstr;
    private Operator i_f;
    private InvVar inv;
//    public Thread coreThread;
    private String invSel;
//    private AudioClip bgMusicClip;
//    private AudioClip playClip;
    private int hide_phantoms;
    private int common;
    private String current_loc;
    private String previous_loc;
    public String last_btn_caption;
    public int style_dos_textcolor;
    public int style_dos_buttoncolor;
    public int style_dos_cursorcolor;
    private HashMap adresses;
    private HashMap variables;
    private HashMap inventory;
    
//    public String 
    
    public String getInventoryName() {
		return inventoryName;
	}

	public Core(String inventoryName)
    {
        prewInstr = -1;
        stack = new Vector();
        retInstr = new Vector();
        adresses = new HashMap();
        variables = new HashMap();
        inventory = new HashMap();
        this.inventoryName = inventoryName;
        Property.init();
//        Lang.init();
        sysVarsClear();
    }
    public void init(URQUI gui1)
    {
        gui = gui1;
        reset();
//        coreStart();
    }

//    public synchronized void coreStart()
//    {
//        if(!runned || coreThread == null)
//        {
//            if(debug)
//                System.out.println("CORE->>START");
//            runned = true;
//            coreThread = new Thread(this);
//            coreThread.start();
//        }
//    }

    public URQUI getGui()
    {
        return gui;
    }

    public boolean tick()
    {
        if (runned) { 
           return doInstruction();
        } else {
        	return false;
        }
        
//                Thread.sleep(25L);
//            }
//            catch(InterruptedException interruptedexception) { }
    }

    private synchronized boolean doInstruction()
    {
        if(end || currInstr < 0)
            return true;
        for(; currInstr < stack.size(); currInstr++)
        {
            if(!runned || interrupt) {
                return false;
            }
            if (doInstruction((Operator)stack.get(currInstr))==false) {
            	break;
            }
        }
        return true;

    }

    @SuppressWarnings("unchecked")
	private synchronized boolean doInstruction(Operator operator)
    {
        if(i_f != null && currInstr == i_f.getEndIf())
        {
            currInstr = i_f.getEnd() - 1;
            i_f = null;
            return true;
        }
        if(debug && prewInstr != currInstr)
        {
            System.out.println(System.currentTimeMillis());
            System.out.println("ID_\u2116: " + currInstr + "\t" + operator.toString());
        }
        prewInstr = currInstr;
        switch(operator.getType())
        {
        case -1: 
            gui.print(operator.getStrValue(), style_dos_textcolor);
            break;

        case 2: // '\002'
            break;

        case 3: // '\003'
            gui.print(operator.getStrValue(), style_dos_textcolor);
            break;

        case 4: // '\004'
            if(hide_phantoms == 0 || !operator.isPhantom())
                gui.addButton((Btn)operator);
            break;

        case 5: // '\005'
            setVariable(operator.getVarName(), operator.doAction());
            break;

        case 6: // '\006'
            setVariable(operator.getVarName(), operator.getStrValue());
            break;

        case 7: // '\007'
            gui.enableInput();
            if(gui.getInput() == null)
                return false;
            Variable variable = getVariable(operator.getVarName());
            String s = gui.getInput();
            gui.disableInput();
            if(variable.getType() == 3)
            {
                try
                {
                    variable.setFloat(Float.parseFloat(s));
                    break;
                }
                catch(NumberFormatException numberformatexception)
                {
                    gui.print("\u0432\u044B \u0432\u0432\u0435\u043B\u0438 \u043D\u0435 \u0447\u0438\u0441\u043B\u043E:" + s, 0);
                }
                gui.enableInput();
                return false;
            }
            variable.setString(s);
            break;

        case 8: // '\b'
            perKill();
            break;

        case 9: // '\t'
            invKill(operator.getItem());
            break;

        case 10: // '\n'
            Object obj = inventory.get(operator.getVarName());
            if(obj == null)
            {
                obj = new InvVar(3);
                inventory.put(operator.getVarName(), obj);
            }
            Variable variable1 = (Variable)obj;
            variable1.setFloat(variable1.getFloat() + operator.getFltValue());
            gui.invRefresh();
            break;

        case 11: // '\013'
            Object obj1 = inventory.get(operator.getVarName());
            if(obj1 == null)
            {
                obj1 = new InvVar(3);
                inventory.put(operator.getVarName(), obj1);
            }
            Variable variable2 = (Variable)obj1;
            variable2.setFloat(variable2.getFloat() - operator.getFltValue());
            if(variable2.getInt() <= 0)
                inventory.remove(operator.getVarName());
            gui.invRefresh();
            break;

        case 12: // '\f'
            i_f = operator;
            currInstr = operator.getGoto() - 1;
            break;

        case 13: // '\r'
            cls();
            break;

        case LogicConstants.PAUSE:
        	Pause pause = (Pause)operator; 
        	if (pause.getTime()>0) { 
	        	currInstr++;
	        	gui.pause(pause);
	            return false;
        	}
        	break;

        case 15: // '\017'
            gui.save(operator.getLocation());
            break;

        case LogicConstants.PLAY: // '\020'
        	gui.play((Play)operator);
            break;

        case 18: // '\022'
            doProc(operator.getLocation());
            break;

        case 19: // '\023'
            gotoLocation(operator.getLocation());
            break;

        case 21: // '\025'
            if(retInstr.size() > 0)
            {
                currInstr = ((Integer)retInstr.get(retInstr.size() - 1)).intValue();
                retInstr.remove(retInstr.size() - 1);
            } else
            {
                end = true;
                gui.end();
                return false;
            }
            break;

        case 22: // '\026'
            currInstr = stack.size();
            break;

        case 0: // '\0'
        case 1: // '\001'
        case 17: // '\021'
        case 20: // '\024'
        	break;
        	
        case LogicConstants.IMAGE:
        	gui.showImage((URQImage)operator);
            break;

        case LogicConstants.ANYKEY:
        	currInstr++;
        	gui.anykey();
            return false;
            
        case LogicConstants.GO_END:
        	currInstr = ((GotoEnd)operator).getEndIf();
            break;
            
            

        default:
//            System.out.println(operator);
            break;
        }
        return true;
    }

    public String save(String restoreLocation) {
        StringBuffer stringbuffer = new StringBuffer(2000);
        stringbuffer.append("cls&invkill&");
        for(int j = 0; j < inventory.size(); j++)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)inventory.entrySet().toArray()[j];
            InvVar invvar = (InvVar)entry.getValue();
            if(invvar.getInt() == 1)
                stringbuffer.append("inv+ ").append(entry.getKey()).append('&');
            else
                stringbuffer.append("inv+ ").append(invvar.getInt()).append(",").append(entry.getKey()).append('&');
        }

        stringbuffer.append("perkill&");
        for(int k = 0; k < variables.size(); k++)
        {
            java.util.Map.Entry entry1 = (java.util.Map.Entry)variables.entrySet().toArray()[k];
            Variable variable = (Variable)entry1.getValue();
            if(variable.getType() == Variable.STRING) {
                stringbuffer.append("instr ").append(entry1.getKey()).append('=');
                stringbuffer.append(variable.getString()).append('&');
            } else {
                stringbuffer.append(entry1.getKey()).append('=');
                stringbuffer.append(variable.getFloat()).append('&');
            }
        }

        if(restoreLocation != null && restoreLocation.length() != 0) {
            stringbuffer.append("goto ").append(restoreLocation).append('&');
        }
        stringbuffer.append("end");
        return stringbuffer.toString();
    }

    public void coreStop()
    {
        if(debug)
            System.out.println("CORE->>STOP");
        runned = false;
    }

    public void load(URQParser parser,String loadData)
    {
	    int j = stack.size();
	    parser.startParse(loadData,this);
	    for(int k = j; k < stack.size(); stack.remove(k))
	    {
	        currInstr = k;
	        doInstruction((Operator)stack.get(currInstr));
	        if (currInstr != k) {
	        	//goto at the end
	        	break;
	        }
	    }
        interrupt = false;
    }

    public void add(Operator operator)
    {
        if(operator.getType() == 2 && adresses.get(operator.getLocation()) == null)
            adresses.put(operator.getLocation(), new Integer(stack.size()));
        stack.add(operator);
        boolean _tmp = debug;
    }

    public synchronized void reset()
    {
        cls();
        perKill();
        invKill("");
        invSel = "";
        inv = new InvVar(1);
        currInstr = 0;
        prewInstr = -1;
        retInstr = new Vector();
        end = false;
        interrupt = false;
        i_f = null;
        sysVarsClear();
    }

    private void sysVarsClear()
    {
        hide_phantoms = 0;
        common = 0;
        current_loc = "";
        previous_loc = "";
        last_btn_caption = "";
        style_dos_textcolor = 7;
        style_dos_buttoncolor = 15;
        style_dos_cursorcolor = 112;
        //TODO
//        if(bgMusicClip != null)
//            bgMusicClip.stop();
//        bgMusicClip = null;
//        if(playClip != null)
//            playClip.stop();
//        playClip = null;
    }

    public void clear()
    {
        reset();
        stack.clear();
        adresses.clear();
    }

    public String getString(String s)
    {
    	s = s.replaceAll("#/$", "\n");
        int i;
        int j;
        while((j = s.indexOf("$")) >= 0 && (i = s.lastIndexOf("#", j)) >= 0) 
            if(s.indexOf("#/$") == i)
                s = s.substring(0, i) + "\n" + s.substring(j + 1);
            else
            if(s.indexOf("#$") == i)
                s = s.substring(0, i) + " " + s.substring(j + 1);
            else
            if(s.indexOf("#%") == i)
                s = s.substring(0, i) + getVariable(s.substring(i + 2, j)) + s.substring(j + 1);
            else
            if(s.lastIndexOf("##", j) == i - 1 && i != 0)
            {
                byte byte0 = (byte)Integer.parseInt(s.substring(i + 1, j));
                s = s.substring(0, i - 1) + new String(new byte[] {
                    byte0
                }) + s.substring(j + 1);
            } else {
            	String s1 = s.substring(i + 1, j).trim();
            	
            	Variable variable = getVariable(s1,true);
            	if (variable==null) {
                    float f = doExpr(s1);
                    s = s.substring(0, i) + Math.round(f) + s.substring(j + 1);
            	} else if (variable.getType()==Variable.FLOAT){
                    s = s.substring(0, i) + Math.round(variable.getFloat()) + s.substring(j + 1);
            	} else {
                    s = s.substring(0, i) + variable.getString() + s.substring(j + 1);
            	}
            }
        return s;
    }

    public void setVariable(String s, float f)
    {
        s = s.toLowerCase();
        if(s.equals("music"))
        {
            playMusic(f);
        } else
        {
            if(s.equals("time"))
                return;
            Variable variable;
            if(s.equals("style_dos_textcolor"))
                style_dos_textcolor = Math.round(f);
            else
            if(s.equals("style_dos_buttoncolor"))
                style_dos_buttoncolor = Math.round(f);
            else
            if(s.equals("style_dos_cursorcolor"))
                style_dos_cursorcolor = Math.round(f);
            else
            if(s.equals("hide_phantoms"))
                hide_phantoms = Math.round(f);
            else
            if(s.equals("common"))
                common = Math.round(f);
            else
            if((variable = getVariable(s)) != null)
                variable.setFloat(f);
            else
                variables.put(s, new Variable(f));
        }
    }

    public void setVariable(String s, String s1)
    {
        s = s.toLowerCase();
        Variable variable;
        if(s.equals("current_loc"))
            current_loc = s1;
        else
        if(s.equals("previous_loc"))
            previous_loc = s1;
        else
        if(s.equals("last_btn_caption"))
            last_btn_caption = s1;
        else
        if((variable = getVariable(s)) != null)
            variable.setString(s1);
        else
            variables.put(s, new Variable(s1));
    }

    private void playMusic(float f)
    {
//        try
//        {
        		//TODO
//            bgMusicClip = Applet.newAudioClip(new URL("file:" + Property.lastDir + "/" + (int)f + ".mid"));
//            bgMusicClip.loop();
//        }
//        catch(MalformedURLException malformedurlexception)
//        {
//            malformedurlexception.printStackTrace();
//        }
    }

    public Variable getVariable(String s) {
    	return getVariable(s,false);
    	
    }
    
    public Variable getVariable(String s, boolean returnNull)
    {
        if(debug)
            System.out.println("_get_var_ " + s);
        s = s.toLowerCase();
        if(s.startsWith("rnd"))
        	if ("rnd".equals(s)) {
        		return new Variable((float)Math.random());
        	} else {
        		int i;
        		try {
					i = Integer.parseInt(s.substring(3));
				} catch (NumberFormatException e) {
					i=1;
				}
				if (i<=1) {
	        		return new Variable((float)Math.random());
				} else {
					i=i-1;
					int j = 1+(int)Math.round(Math.random()*i);
	        		return new Variable((float)j);
				}
        	}
        if(s.equals("time"))
        {
            Date date = new Date();
            int i = (date.getHours() * 60 + date.getMinutes()) * 60 + date.getSeconds();
            return new Variable(i);
        }
        if(s.equals("style_dos_textcolor"))
            return new Variable(style_dos_textcolor);
        if(s.equals("style_dos_buttoncolor"))
            return new Variable(style_dos_buttoncolor);
        if(s.equals("style_dos_cursorcolor"))
            return new Variable(style_dos_cursorcolor);
        if(s.equals("hide_phantoms"))
            return new Variable(hide_phantoms);
        if(s.equals("common"))
            return new Variable(common);
        if(s.equals("current_loc"))
            return new Variable(current_loc);
        if(s.equals("previous_loc"))
            return new Variable(previous_loc);
        if(s.equals("last_btn_caption"))
            return new Variable(last_btn_caption);
        if(s.startsWith("inv_"))
        {
            Object obj = getInvent().get(s.substring(4));
            if(obj != null)
                return (Variable)obj;
        }
        	
        Variable obj1 = (Variable)variables.get(s);
        if(obj1 == null) {
        	if (returnNull==false) {
	            obj1 = new Variable(0.0F);
	            variables.put(s, obj1);
        	}
        }
        if(debug)
            System.out.println(((Variable)obj1).getFloat());
        return obj1;
    }

    public Integer getLocation(String s)
    {
        return (Integer)adresses.get(s);
    }

    public void gotoLocation(String s)
    {
        Integer integer = getLocation(s);
        if(integer == null)
        {
            System.out.println(s + "->>Error not found");
            System.out.println(adresses);
            return;
        }
        Variable variable = getVariable("count_" + s);
        variable.setFloat(variable.getFloat() + 1.0F);
        if(debug)
            System.out.println("_Enter_location_->>" + s);
        end = false;
        currInstr = integer.intValue();
    }

    public void doCommon()
    {
        if(common == 0)
        {
            if(getLocation("common") != null)
                doProc("common");
        } else
        {
            String s = "common_" + common;
            if(getLocation(s) != null)
                doProc(s);
        }
    }

    public void doProc(String s)
    {
        retInstr.add(new Integer(currInstr));
        gotoLocation(s);
    }

    public void cls()
    {
        gui.clear();
    }

    private void perKill()
    {
        variables.clear();
    }

    private void invKill(String s)
    {
        if(s.length() == 0)
            inventory.clear();
        else
            inventory.remove(s);
        gui.invRefresh();
    }

    public Vector getStack()
    {
        return stack;
    }

    public HashMap getInvent()
    {
        return inventory;
    }

    public float doExpr(String s) {
        s = s.trim();
        try
        {
            return Float.parseFloat(s);
        }
        catch(Exception exception) { }
        int i;
        if((i = s.indexOf(')')) > 0)
        {
            int j = s.lastIndexOf('(', i);
            float f = doExpr(s.substring(j + 1, i));
            s = s.substring(0, j) + f + s.substring(i + 1);
            return doExpr(s);
        }
        if((i = s.indexOf('-')) > 0)
            return doExpr(s.substring(0, i)) - doExpr(s.substring(i + 1));
        if((i = s.indexOf('+')) > 0)
            return doExpr(s.substring(0, i)) + doExpr(s.substring(i + 1));
        if((i = s.indexOf('*')) > 0)
            return doExpr(s.substring(0, i)) * doExpr(s.substring(i + 1));
        if((i = s.indexOf('/')) > 0)
            return doExpr(s.substring(0, i)) / doExpr(s.substring(i + 1));
        else
            return getVariable(s).getFloat();
    }

    public Vector listOfInventActions(String s)
    {
        if(s == null) {
            return null;
        }
        invSel = s;
        InvVar invvar;
        int perfix;
        if(s.equals(inventoryName)) {
            if(inv.isTested()==false) {
            	inv.test(adresses, "use_inv");
            }
            perfix = 7;
            invvar = inv;
        } else {
            invvar = (InvVar)inventory.get(s);
            if(invvar == null) {
                return null;
            }
            if(invvar.isTested()==false) {
                invvar.test(adresses, "use_" + s);
            }
            perfix = 3;
        }
        StringBuffer buffer = new StringBuffer("hide_use");
        Vector actions = invvar.getActions();
        Vector result = actions;
        //inv_use
        for (int i = 0; i < actions.size(); i++) {
        	Action act = (Action)actions.get(i);
			String action = act.getLocName();
        	buffer.setLength(8);
        	//use_inv_
        	if (action.length()>perfix) {
        		buffer.append(action.substring(perfix));
        	}
        	Variable var = (Variable)variables.get(buffer.toString());
        	if (var==null || var.isEmpty()) {
        		if (actions != result) {
        			result.add(act);
        		}
        	} else if (actions == result){
        		//create filtered list
        		result = new Vector(actions);
        		result.setSize(i);
        	}
        }
		return result;
	}

	public void doAction(com.iambookmaster.client.iurq.logic.InvVar.Action action)
    {
        gotoLocation(action.getLocName());
    }

    public void switchLoc(String s)
    {
        previous_loc = current_loc;
        current_loc = s;
    }


//	public byte[] loadFileAsBytes(String s) {
//		return gui.loadFileAsBytes(s);
//	}

	public void exit() {
		gui.doExit();
	}

	public void doButton(Btn btn) {
        last_btn_caption = btn.getName();
        cls();
        switchLoc(btn.getLocation());
        gotoLocation(btn.getLocation());
        doCommon();
	}

	public void doInventory(String name, InvVar var) {
		// TODO Auto-generated method stub
		
	}
}
