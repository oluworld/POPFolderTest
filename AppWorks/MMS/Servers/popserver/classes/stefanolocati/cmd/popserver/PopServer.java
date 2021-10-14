// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PopServer.java

package stefanolocati.cmd.popserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import stefanolocati.resources.pop3.FileNames;
import stefanolocati.util.Debug;

// Referenced classes of package stefanolocati.cmd.popserver:
//            PopServerState

public class PopServer extends Thread
{

    public PopServer(String s)
    {
        baseDir = s;
        logDir = baseDir + File.separator + FileNames.serverLogDir;
        fileToList = new StringBuffer(70);
    }

    public static void main(String args[])
    {
        if(args.length != 1)
        {
            Debug.msg("Missing argument <basedir>, server aborting");
            System.exit(1);
        }
        PopServer popserver = new PopServer(args[0]);
        popserver.run();
    }

    private void mySleep()
    {
        try
        {
            Thread.sleep(0L);
        }
        catch(InterruptedException _ex) { }
    }

    public void run()
    {
        byte byte0 = 110;
        Object obj = null;
        Object obj1 = null;
        Object obj2 = null;
        Object obj3 = null;
        Debug.msg("--- Server starting up!", false, logDir);
        do
        {
            Debug.msg("--- Ready!", false, logDir);
            ServerSocket serversocket;
            try
            {
                serversocket = new ServerSocket(byte0);
            }
            catch(IOException ioexception)
            {
                Debug.msg("  PopServer 1: " + ioexception.getMessage(), false, logDir);
                return;
            }
            Socket socket;
            try
            {
                socket = serversocket.accept();
            }
            catch(IOException ioexception1)
            {
                Debug.msg("  PopServer 2: " + ioexception1.getMessage(), false, logDir);
                return;
            }
            mySleep();
            BufferedReader bufferedreader;
            PrintWriter printwriter;
            try
            {
                bufferedreader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printwriter = new PrintWriter(new BufferedOutputStream(socket.getOutputStream(), 1024), false);
            }
            catch(IOException ioexception2)
            {
                Debug.msg("  PopServer 3: " + ioexception2.getMessage(), false, logDir);
                return;
            }
            mySleep();
            PopServerState popserverstate = new PopServerState(baseDir);
            sentenceToSay = popserverstate.processInput(null, fileToList);
            Debug.msg("-> " + sentenceToSay, false, logDir);
            printwriter.print(sentenceToSay + "\r\n");
            printwriter.flush();
            mySleep();
            for(; !popserverstate.endOfDialog(); printwriter.flush())
            {
                try
                {
                    heardSentence = bufferedreader.readLine();
                }
                catch(IOException ioexception3)
                {
                    Debug.msg("  PopServer 4: " + ioexception3.getMessage(), false, logDir);
                    break;
                }
                if(heardSentence == null)
                    break;
                sentenceToSay = popserverstate.processInput(heardSentence, fileToList);
                Debug.msg(heardSentence + " -> " + sentenceToSay + " " + fileToList, false, logDir);
                mySleep();
                printwriter.print(sentenceToSay + "\r\n");
                if(fileToList.length() != 0)
                    try
                    {
                        BufferedReader bufferedreader1 = new BufferedReader(new FileReader(fileToList.toString()));
                        String s;
                        while((s = bufferedreader1.readLine()) != null) 
                        {
                            mySleep();
                            if(s.length() == 1 && s.charAt(0) == '.')
                                printwriter.print(". \r\n");
                            else
                                printwriter.print(s + "\r\n");
                            printwriter.flush();
                        }
                        printwriter.print(".\r\n");
                        printwriter.flush();
                        bufferedreader1.close();
                    }
                    catch(FileNotFoundException filenotfoundexception)
                    {
                        Debug.msg("  PopServer 6: " + filenotfoundexception.getMessage(), false, logDir);
                        return;
                    }
                    catch(IOException ioexception5)
                    {
                        Debug.msg("  PopServer 7: " + ioexception5.getMessage(), false, logDir);
                        return;
                    }
                mySleep();
            }

            mySleep();
            try
            {
                printwriter.close();
                bufferedreader.close();
                socket.close();
                serversocket.close();
            }
            catch(IOException ioexception4)
            {
                Debug.msg("  PopServer 7: " + ioexception4.getMessage(), false, logDir);
                return;
            }
        } while(true);
    }

    static final int DELAY = 0;
    static String heardSentence = null;
    static String sentenceToSay = null;
    String baseDir;
    String logDir;
    StringBuffer fileToList;

}
