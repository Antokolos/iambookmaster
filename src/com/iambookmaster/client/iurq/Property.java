// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.iambookmaster.client.iurq;


public class Property
{

    public Property()
    {
    }

    public static void init()
    {
    }

    public static void setLastDir(String s)
    {
    	//TODO
//        File file = new File(s);
//        if(file.exists())
//            if(file.isFile())
//                lastDir = file.getParent();
//            else
//                lastDir = file.getPath();
    }

    public static int getColor(int i)
    {
        switch(i)
        {
        case 0: // '\0'
            return 0;

        case 1: // '\001'
            return 128;

        case 2: // '\002'
            return 32768;

        case 3: // '\003'
            return 32896;

        case 4: // '\004'
            return 0x800000;

        case 5: // '\005'
            return 0x800080;

        case 6: // '\006'
            return 0x808000;

        case 7: // '\007'
            return 0xc0c0c0;

        case 8: // '\b'
            return 0x808080;

        case 9: // '\t'
            return 255;

        case 10: // '\n'
            return 65280;

        case 11: // '\013'
            return 65535;

        case 12: // '\f'
            return 0xff0000;

        case 13: // '\r'
            return 0xff00ff;

        case 14: // '\016'
            return 0xffff00;

        case 15: // '\017'
            return 0xffffff;
        }
        return 0x888888;
    }

    public static String lang = "ru-RU";
    public static String version = "0.6.2b";
    public static String lastDir = ".";

}
