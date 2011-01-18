package com.crionics.expr;

import java.io.*;

class Test
{

    static private String scanf(String str)
    throws IOException 
    {
        byte[] buf = new byte[128];

        System.out.println(str);
        int len=System.in.read(buf);
        return(new String(buf,0, len-2));
    } 

    public static void main(String args[])
    {

        //Log.setLogLevel(4);
        while ( true )
        {
            try
            {
                String s=scanf("Give me an expression:");

                MyTestObject mo=new MyTestObject(17,32);

                Expression e=new Expression(s);
                double v=e.evaluate(mo);
                System.out.println("------>"+s+"="+v);
            }
            catch ( Exception e )
            {
                System.out.println(e);
            }
        }
    }
}
