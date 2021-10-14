// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   State.java

package stefanolocati.cmd.popserver;


interface State
{

    public static final int DISCONNECTED = 0;
    public static final int WAITUSER = 1;
    public static final int WAITPASSWORD = 2;
    public static final int TRANSACTION = 3;
    public static final int QUIT = 4;
}
