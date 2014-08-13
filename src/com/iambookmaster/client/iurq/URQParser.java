// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 30/11/2011 9:07:52 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import com.iambookmaster.client.iurq.logic.Btn;
import com.iambookmaster.client.iurq.logic.CLS;
import com.iambookmaster.client.iurq.logic.Comment;
import com.iambookmaster.client.iurq.logic.End;
import com.iambookmaster.client.iurq.logic.Goto;
import com.iambookmaster.client.iurq.logic.GotoEnd;
import com.iambookmaster.client.iurq.logic.If;
import com.iambookmaster.client.iurq.logic.Input;
import com.iambookmaster.client.iurq.logic.Instr;
import com.iambookmaster.client.iurq.logic.InvAdd;
import com.iambookmaster.client.iurq.logic.InvKill;
import com.iambookmaster.client.iurq.logic.InvSub;
import com.iambookmaster.client.iurq.logic.Location;
import com.iambookmaster.client.iurq.logic.Pause;
import com.iambookmaster.client.iurq.logic.PerKill;
import com.iambookmaster.client.iurq.logic.Play;
import com.iambookmaster.client.iurq.logic.Print;
import com.iambookmaster.client.iurq.logic.Proc;
import com.iambookmaster.client.iurq.logic.Quit;
import com.iambookmaster.client.iurq.logic.Save;
import com.iambookmaster.client.iurq.logic.URQAnykey;
import com.iambookmaster.client.iurq.logic.URQImage;
import com.iambookmaster.client.iurq.logic.Unknown;
import com.iambookmaster.client.iurq.logic.VarAction;


// Referenced classes of package tge.core:
//            Core, BugTrack, Property

public class URQParser
{

//    private static final String[] PATTERN = new String[0];

    public static final int STAGE_TOKENIZING = 0;

    public static final int STAGE_PARSING = 1;
    
    private static String charset = "\u0430\u0431\u0432\u0433\u0434\u0435\u0451\u0436\u0437\u0438\u0439\u043A\u043B\u043C\u043D\u043E\u043F\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044A\u044B\u044C\u044D\u044E\u044F" + "\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419\u041A\u041B\u041C\u041D\u041E\u041F\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042A\u042B\u042C\u042D\u042E\u042F" + "\u2116\246\247\251\254\266\u2122" + "\260\u0404\u045E\240\u0454\u0457";
    
    private static String encoding[] = {
        "cp1251", "cp866"
    };
    private List lines;

	private String[] questLines;

	private int lineCounter;

	private Core core;
    
//    public void loadFile(Core core, String s)
//    {
//        try
//        {
//            FileInputStream fileinputstream = new FileInputStream(s);
//            FileChannel filechannel = fileinputstream.getChannel();
//            MappedByteBuffer mappedbytebuffer = filechannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0L, filechannel.size());
//            byte abyte0[] = new byte[(int)filechannel.size()];
//            for(int i = 0; i < abyte0.length; i++)
//                abyte0[i] = mappedbytebuffer.get();
//
//            filechannel.close();
//            fileinputstream.close();
//            startParse(core.loadFileAsBytes(s), core);
//        }
//        catch(FileNotFoundException filenotfoundexception)
//        {
//            filenotfoundexception.printStackTrace();
//        }
//        catch(IOException ioexception)
//        {
//            ioexception.printStackTrace();
//        }
//    }

//    public void parseFile(Core core, String s)
//    {
//        try
//        {
//            boolean flag = false;
//            if(s.endsWith(".qs1"))
//                flag = true;
//            if(s.endsWith(".qs2"))
//                flag = true;
//            FileInputStream fileinputstream = new FileInputStream(s);
//            FileChannel filechannel = fileinputstream.getChannel();
//            MappedByteBuffer mappedbytebuffer = filechannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0L, filechannel.size());
//            byte abyte0[] = new byte[(int)filechannel.size()];
//            for(int i = 0; i < abyte0.length; i++)
//            {
//                abyte0[i] = mappedbytebuffer.get();
//                if(flag)
//                {
//                    int j = abyte0[i] & 0xff;
//                    if(j >= 32)
//                        abyte0[i] = (byte)(287 - j);
//                }
//            }
//
//            filechannel.close();
//            fileinputstream.close();
//            core.clear();
//            startParse(core.loadFileAsBytes(s), core);
//        }
//        catch(FileNotFoundException filenotfoundexception)
//        {
//            filenotfoundexception.printStackTrace();
//        }
//        catch(IOException ioexception)
//        {
//            ioexception.printStackTrace();
//        }
//    }

    public boolean startParse(String questText, Core core) {
    	this.core = core;
        return lineTokenizer(questText);
    }

    private void lineLoad(String s, Core core) {
    	if (isSpaceChar(s.charAt(0))) {
    		int i = 1;
    		while (i<s.length() && isSpaceChar(s.charAt(i))) {
    			i++;
    		}
    		s = s.substring(i);
    	}
        String s1 = s.toLowerCase();
        if(s.startsWith(";"))
            core.add(new Comment(core, s));
        else
        if(s.startsWith("/*"))
            core.add(new Comment(core, s));
        else
        if(s.startsWith(":"))
            core.add(new Location(core, s.trim()));
        else
        if(s1.startsWith("println ") || s1.startsWith("pln "))
            core.add(new Print(core, s + "\n"));
        else
        if(s1.equals("println") || s1.equals("pln"))
            core.add(new Print(core, "\n"));
        else
        if(s1.startsWith("print ") || s1.startsWith("p "))
            core.add(new Print(core, s));
        else
        if(s1.startsWith("include ")) {
        	//TODO
//            loadFile(core, Property.lastDir + "/" + s.substring(8));
        } else if(s1.startsWith("proc "))
            core.add(new Proc(core, s));
        else
        if(s1.startsWith("goto "))
            core.add(new Goto(core, s));
        else
        if(s1.startsWith("quit"))
            core.add(new Quit());
        else
        if(s1.startsWith("end"))
            core.add(new End(core));
        else
        if(s1.startsWith("cls"))
            core.add(new CLS(core));
        else
        if(s1.startsWith("play "))
            core.add(new Play(core, s.substring(5).trim(),false));
        else
        if(s1.startsWith("music stop"))
            core.add(new Play(core, null,true));
        else
        if(s1.startsWith("image "))
            core.add(new URQImage(core, s.substring(6).trim()));
        else
        if(s1.startsWith("music "))
            core.add(new Play(core, s.substring(6).trim(), true));
        else
        if(s1.startsWith("pause"))
            core.add(new Pause(core, s));
        else
        if(s1.startsWith("save ") || s1.equals("save"))
            core.add(new Save(core, s));
        else
        if(s1.startsWith("perkill"))
            core.add(new PerKill(core));
        else
        if(s1.startsWith("invkill"))
            core.add(new InvKill(core, s1));
        else
        if(s1.startsWith("inv+"))
            core.add(new InvAdd(core, s1));
        else
        if(s1.startsWith("inv-"))
            core.add(new InvSub(core, s1));
        else
        if(s1.startsWith("btn "))
            core.add(new Btn(core, s));
        else
        if(s1.startsWith("instr "))
            core.add(new Instr(core, s));
        else
        if(s1.startsWith("inv_visible false"))
            core.add(new Instr(core, "instr inv_visible=0"));
        else
        if(s1.startsWith("inv_visible true"))
            core.add(new Instr(core, "instr inv_visible=1"));
        else
        if(s1.startsWith("input "))
            core.add(new Input(core, s));
        else
        if(s1.equals("anykey"))
            core.add(new URQAnykey(core));
        else
        if(s1.startsWith("if "))
            ifLoad(s, s1, core);
        else
        if(s1.indexOf("=") > 0)
            core.add(new VarAction(core, s));
        else
            core.add(new Unknown(core, s));
    }

    private boolean isSpaceChar(char c) {
	    switch(c) {
	    case ' ':
	    case '\t':
	    	return true;
		}
	    return false;
    }

	private void ifLoad(String s, String s1, Core core) {
        int thenPosition = findToken(s1,"then",0);
        if(thenPosition > 0) {
            If if1 = new If(core);
            core.add(if1);
            int elsePosition = findToken(s1,"else",thenPosition);
            if1.setExp(s1.substring(3, thenPosition).trim());
	        if1.setGoIf(core.getStack().size());
	    	Vector lns = new Vector();
	    	String afterThen = s.substring(thenPosition + 5); 
	    	if(elsePosition < 0) {
	        	//only if
	        	parseLine(afterThen, lns);
	        	for (int k = 0; k < lns.size(); k++) {
	        		lineLoad((String)lns.get(k), core);
				}
	            if1.setGoElse(core.getStack().size());
	        } else {
	        	//if ... else ...
	            int subIfPosition = findToken(s1,"if",thenPosition);
	            int subThenPosition = subIfPosition >0 ? findToken(s1,"then",subIfPosition) : -1;
	            if (subThenPosition<0) {
	            	//no inner if
	            	parseLine(s.substring(thenPosition + 5, elsePosition), lns);
	            	for (int k = 0; k < lns.size(); k++) {
	            		lineLoad((String)lns.get(k), core);
	    			}
	            	lns.clear();
	                if1.setGoElse(core.getStack().size());
	                parseLine(s.substring(elsePosition + 5), lns);
	            	for (int k = 0; k < lns.size(); k++) {
	            		lineLoad((String)lns.get(k), core);
	    			}
	            } else if (elsePosition<subIfPosition) {
	            	//else before second if
	            	parseLine(s.substring(thenPosition + 5, elsePosition), lns);
	            	for (int k = 0; k < lns.size(); k++) {
	            		lineLoad((String)lns.get(k), core);
	    			}
	            	lns.clear();
	                if1.setGoElse(core.getStack().size());
	                parseLine(s.substring(elsePosition + 5,subIfPosition), lns);
	            	for (int k = 0; k < lns.size(); k++) {
	            		lineLoad((String)lns.get(k), core);
	    			}
	            	//load inline if
	            	String subIf = s.substring(subIfPosition);
	            	ifLoad(subIf,subIf.toLowerCase(),core);
	            } else {
	            	//inner if
	            	int i = subIfPosition;
	            	int j = elsePosition;
	            	while (i>0 && j>0 && i<j) {
	            		i = findToken(s1,"if",i+1);
	            		j = findToken(s1,"else",j+1);
	            	}
	            	parseLine(s.substring(thenPosition + 5, subIfPosition), lns);
	            	for (int k = 0; k < lns.size(); k++) {
	            		lineLoad((String)lns.get(k), core);
	    			}
	            	lns.clear();
	            	if (j>0) {
	            		//use the last else as our
		            	String subIf = s.substring(subIfPosition,j); 
		            	ifLoad(subIf,subIf.toLowerCase(),core);
		            	//add go-to our end before our else
		            	GotoEnd gotoEnd = new GotoEnd();
		            	core.add(gotoEnd);
		            	//process ELSE
		                if1.setGoElse(core.getStack().size());
		                subIf = s.substring(j+5);
		            	parseLine(subIf, lns);
		            	for (int k = 0; k < lns.size(); k++) {
		            		lineLoad((String)lns.get(k), core);
		    			}
		            	//set position of the go_end
		            	gotoEnd.setEndIf(core.getStack().size());
	            	} else {
	            		//no our else
		            	String subIf = s.substring(subIfPosition); 
		            	ifLoad(subIf,subIf.toLowerCase(),core);
		                if1.setGoElse(core.getStack().size());
	            	}
	            } 
	        }
            if1.setGoEnd(core.getStack().size());
        }
    }

    private int findToken(String s1, String pattern,int from) {
        int i=s1.indexOf(pattern,from);
        int good = i;
        while (i>0) {
        	if (Character.isLetterOrDigit(s1.charAt(i-1)) || Character.isLetterOrDigit(s1.charAt(i+pattern.length()))) {
        		//go next 
        		i =  s1.indexOf(pattern,i+1);
        	} else {
        		good = i;
    			//first occurrence
    			break;
        	}
        }
        return good;
	}

	private boolean lineTokenizer(String questText) {
        if ((int)questText.charAt(0)==65279) {
        	//BOM character in UTF
        	questText=questText.substring(1);
        }
        if (questText.indexOf("\r\n")<0) {
        	questLines = questText.split("\n");
        } else {
        	questLines = questText.split("\r\n");
        }
        questText = null;//release memory
        lines = new Vector(questLines.length);
        //start processing lines
        lineCounter = 0;
        return countinueLineTokinizing();
    }

    protected boolean countinueLineTokinizing() {
    	if (questLines==null) {
    		//stage 2
            for(lineCounter = 0; lineCounter < lines.size(); lineCounter++) {
            	if (canContinue(lineCounter,lines.size(),STAGE_PARSING)==false) {
            		return false;
            	}
                lineLoad((String)lines.get(lineCounter), core);
            }
            done();
            return true;
    	} else {
    		//stage 1
            for(; lineCounter < questLines.length; lineCounter++) {
            	if (canContinue(lineCounter,questLines.length,STAGE_TOKENIZING)==false) {
            		return false;
            	}
                String s1 = questLines[lineCounter];
                int i;
                if((i = s1.indexOf("/*")) >= 0)
                {
                    int k = -1;
                    StringBuffer stringbuffer = (new StringBuffer()).append(s1);
                    while(lineCounter < questLines.length && (k = s1.indexOf("*/", i)) < i) 
                    {
                        lineCounter++;
                        stringbuffer.append("\n").append(questLines[lineCounter].trim());
                        s1 = stringbuffer.toString();
                    }
                    if(k > i)
                    {
                        parseLine(s1.substring(0, i),lines);
                        parseLine(s1.substring(i, k + 2),lines);
                        parseLine(s1.substring(k + 2),lines);
                    } else
                    {
                        parseLine(s1.substring(0, i),lines);
                        parseLine(s1.substring(i),lines);
                    }
                } else
                {
                    parseLine(s1,lines);
                }
            }
            questLines = null;
            lineCounter = 0;
            return countinueLineTokinizing();
    	}
	}

    protected void done() {
	}

	/**
     * For showing progress of processing
     * @param counter
     * @param max
     * @param stage
     * @return
     */
    protected boolean canContinue(int counter, int max, int stage) {
		return true;
	}

	private static void parseLine(String s,List lines)
    {
        int i;
        if((i = s.indexOf(";")) >= 0)
        {
            parseLine(s.substring(0, i).trim(),lines);
            lines.add(s.substring(i).trim());
        } else
        if((i = s.indexOf("/* ")) == 0)
            lines.add(s.substring(i).trim());
        else
        if((i = s.indexOf("if ")) >= 0)
        {
            parseLine(s.substring(0, i).trim(),lines);
            lines.add(s.substring(i).trim());
        } else
        if((i = s.indexOf("&")) > 0)
        {
            lines.add(s.substring(0, i).trim());
            parseLine(s.substring(i + 1).trim(),lines);
        } else
        if(s.length() != 0)
            lines.add(s);
    }

    private static String getDecoded(byte abyte0[])
    {
label0:
        for(int i = 0; i < encoding.length; i++)
            try
            {
                String s = new String(abyte0, encoding[i]);
                s = s.replace('\u2013', '-').replace('\u2014', '-').replace('\u2019', '\'').replace('\273', '"').replace('\u201C', '"').replace('\u201D', '"').replace('\253', '"').replaceAll("\u2026", "...");
                for(int j = 0; j < s.length(); j++)
                {
                    char c = s.charAt(j);
                    if(c < '\200' || charset.indexOf(c) >= 0)
                        continue;
                    System.out.println(c + "->>" + (int)c + " _-_ " + j);
                    continue label0;
                }

                return s;
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                return unsupportedencodingexception.getMessage();
            }

        return "null";
    }


}
