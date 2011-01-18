/*
 * Copyright (c)Crionics All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Crionics
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Crionics.
 *
 * Crionics MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. Crionics SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 */

package com.crionics.expr;

public final
class Log
{
    private static char SEP='|';

    public final static int INFO = 3;
    public final static int DEBUG = 2;
    public final static int WARNING = 1;
    public final static int ERROR = 0;

    private static int logLevel_ = WARNING;

    static public void setLogLevel(int _level) {
        logLevel_=_level;
    }
    
    static public void log(Object _o, int _level, String _err)
    {
        log(_o.getClass().getName(), _level, _err);
    }

    static public void log(String _class, int _level, String _err)
    {
        String s=getLogString(_class, _level, _err);
        if ( s!=null )
            System.out.println(s);
    }

    static public String getLogString(Object _o, int _level, String _err)
    {
        return (getLogString(_o.getClass().getName(), _level,_err));
    }

    static public String getLogString(String _class, int _level, String _err)
    {
        if ( _level<=logLevel_ )
        {
            StringBuffer sb=new StringBuffer(_class);
            sb.append(SEP).append(getLevel(_level)).append(SEP).append(_err);            
            return(sb.toString());
        }
        else
            return(null);
    }

    static private String getLevel(int _level)
    {
        switch ( _level )
        {
        case INFO:    return("INFO");
        case DEBUG:   return("DEBUG");
        case WARNING: return("WARNING");
        case ERROR:   return("ERROR");
        default:      return("UNKNOWN");
        }
    }

}
