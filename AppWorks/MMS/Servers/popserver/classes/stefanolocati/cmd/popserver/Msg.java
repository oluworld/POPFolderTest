// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Msg.java

package stefanolocati.cmd.popserver;


interface Msg
{

    public static final String OK = "+OK ";
    public static final String NL = "\r\n";
    public static final String WELCOME = "+OK POPRunner server ready";
    public static final String WRONGARGS = "-ERR Wrong number of arguments";
    public static final String ENTERPASSWORD = "+OK enter password";
    public static final String NOAUTH = "-ERR wrong login or password";
    public static final String CANTLOCK = "-ERR mailbox is in use";
    public static final String ENTERCOMMAND = "+OK mailbox ready";
    public static final String UNKNOWN = "-ERR wrong or unimplemented command";
    public static final String UIDLOK = "+OK unique message IDs follows";
    public static final String RETROK = "+OK message follows";
    public static final String NOSUCHMSG = "-ERR no such message";
    public static final String TOPOK = "+OK top of message follows";
    public static final String DELEOK = "+OK message deleted";
    public static final String NOOPMSG = "+OK and looking at you";
    public static final String UNMARK = "+OK all messages undeleted";
    public static final String BYE = "+OK goodbye from POPRunner";
    public static final String NOTDELE = "-ERR some messages not deleted";
    public static final String NOPASSWORDS = "-ERR No password file found!";
}
