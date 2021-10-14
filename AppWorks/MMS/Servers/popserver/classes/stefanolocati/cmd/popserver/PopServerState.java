// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PopServerState.java

package stefanolocati.cmd.popserver;

import java.io.File;
import java.io.FileNotFoundException;
import stefanolocati.resources.pop3.FileNames;
import stefanolocati.text.*;
import stefanolocati.util.Convert;
import stefanolocati.util.Debug;

// Referenced classes of package stefanolocati.cmd.popserver:
//            Msg, State

class PopServerState
{

    PopServerState(String s)
    {
        userName = new StringBuffer();
        password = new StringBuffer();
        reply = new StringBuffer();
        state = 0;
        baseDir = s;
        mailbox = new MailBox(baseDir);
        localAccounts = baseDir + File.separator + FileNames.localAccounts;
    }

    final boolean endOfDialog()
    {
        return state == 4;
    }

    final String processInput(String s, StringBuffer stringbuffer)
    {
        String s1 = null;
        WordByWord wordbyword = new WordByWord(s);
        if(state != 0 && wordbyword.getNumWords() == 0)
        {
            s1 = "-ERR wrong or unimplemented command";
            stringbuffer.setLength(0);
            return s1;
        }
        switch(state)
        {
        case 0: // '\0'
            s1 = "+OK POPRunner server ready";
            stringbuffer.setLength(0);
            state = 1;
            break;

        case 1: // '\001'
            stringbuffer.setLength(0);
            if(wordbyword.getWord(0).equalsIgnoreCase("QUIT"))
            {
                if(wordbyword.getNumWords() == 1)
                    s1 = "+OK goodbye from POPRunner";
                else
                    s1 = "-ERR Wrong number of arguments";
                state = 4;
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("USER"))
            {
                if(wordbyword.getNumWords() == 2)
                {
                    userName.setLength(0);
                    userName.append(wordbyword.getWord(1));
                    s1 = "+OK enter password";
                    state = 2;
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
            } else
            {
                s1 = "-ERR wrong or unimplemented command";
            }
            break;

        case 2: // '\002'
            stringbuffer.setLength(0);
            reply.setLength(0);
            if(wordbyword.getWord(0).equalsIgnoreCase("QUIT"))
            {
                if(wordbyword.getNumWords() == 1)
                    s1 = "+OK goodbye from POPRunner";
                else
                    s1 = "-ERR Wrong number of arguments";
                mailbox.close();
                state = 4;
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("PASS"))
            {
                if(wordbyword.getNumWords() == 2)
                {
                    password.setLength(0);
                    password.append(wordbyword.getWord(1));
                    try
                    {
                        PasswordFileReader passwordfilereader = new PasswordFileReader(localAccounts);
                        if(passwordfilereader.validLogin(userName.toString(), password.toString()))
                        {
                            if(!mailbox.open(userName.toString()))
                                System.exit(1);
                            reply.append("+OK mailbox ready: " + mailbox);
                            s1 = reply.toString();
                            state = 3;
                        } else
                        {
                            s1 = "-ERR wrong login or password";
                            state = 1;
                        }
                        passwordfilereader.close();
                        break;
                    }
                    catch(FileNotFoundException _ex)
                    {
                        s1 = "-ERR No password file found! " + localAccounts;
                    }
                    state = 1;
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                    state = 1;
                }
            } else
            {
                s1 = "-ERR wrong or unimplemented command";
                state = 1;
            }
            break;

        case 3: // '\003'
            stringbuffer.setLength(0);
            reply.setLength(0);
            if(wordbyword.getWord(0).equalsIgnoreCase("QUIT"))
            {
                if(wordbyword.getNumWords() == 1)
                {
                    mailbox.commitDelete();
                    reply.append("+OK goodbye from POPRunner: " + mailbox);
                    s1 = reply.toString();
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
                state = 4;
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("STAT"))
            {
                if(wordbyword.getNumWords() == 1)
                {
                    reply.append("+OK " + mailbox.getNumMessages() + " " + mailbox.size());
                    s1 = reply.toString();
                    break;
                }
                if(wordbyword.getNumWords() == 2)
                {
                    int i = Convert.toInt(wordbyword.getWord(1));
                    if(mailbox.messageExist(i))
                    {
                        reply.append("+OK " + mailbox.size(i));
                        s1 = reply.toString();
                    } else
                    {
                        s1 = "-ERR no such message";
                    }
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("RETR"))
            {
                if(wordbyword.getNumWords() == 2)
                {
                    int j = Convert.toInt(wordbyword.getWord(1));
                    if(mailbox.messageExist(j))
                        stringbuffer.append(mailbox.getMessage(j));
                    if(stringbuffer.length() != 0)
                        s1 = "+OK message follows";
                    else
                        s1 = "-ERR no such message";
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("LIST"))
            {
                if(wordbyword.getNumWords() == 1)
                {
                    reply.append("+OK " + mailbox);
                    for(int k = 1; k <= mailbox.lastMessage(); k++)
                        if(mailbox.messageExist(k))
                            reply.append("\r\n" + k + " " + mailbox.size(k));

                    reply.append("\r\n.");
                    s1 = reply.toString();
                    break;
                }
                if(wordbyword.getNumWords() == 2)
                {
                    int l = Convert.toInt(wordbyword.getWord(1));
                    if(mailbox.messageExist(l))
                    {
                        reply.append("+OK " + l + " " + mailbox.size(l));
                        s1 = reply.toString();
                    } else
                    {
                        s1 = "-ERR no such message";
                    }
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("UIDL"))
            {
                if(wordbyword.getNumWords() == 1)
                {
                    reply.append("+OK unique message IDs follows");
                    for(int i1 = 1; i1 <= mailbox.lastMessage(); i1++)
                        if(mailbox.messageExist(i1))
                            reply.append("\r\n" + i1 + " " + mailbox.getUidl(i1));

                    reply.append("\r\n.");
                    s1 = reply.toString();
                    break;
                }
                if(wordbyword.getNumWords() == 2)
                {
                    int j1 = Convert.toInt(wordbyword.getWord(1));
                    if(mailbox.messageExist(j1))
                    {
                        reply.append("+OK " + j1 + " " + mailbox.getUidl(j1));
                        s1 = reply.toString();
                    } else
                    {
                        s1 = "-ERR no such message";
                    }
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("DELE"))
            {
                if(wordbyword.getNumWords() == 2)
                {
                    int k1 = Convert.toInt(wordbyword.getWord(1));
                    if(mailbox.messageExist(k1))
                    {
                        mailbox.deleteMessage(k1);
                        s1 = "+OK message deleted";
                    } else
                    {
                        s1 = "-ERR no such message";
                    }
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("NOOP"))
            {
                if(wordbyword.getNumWords() == 1)
                    s1 = "+OK and looking at you";
                else
                    s1 = "-ERR Wrong number of arguments";
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("RSET"))
            {
                if(wordbyword.getNumWords() == 1)
                {
                    mailbox.undeleteAll();
                    s1 = "+OK all messages undeleted";
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
                break;
            }
            if(wordbyword.getWord(0).equalsIgnoreCase("TOP"))
            {
                if(wordbyword.getNumWords() == 3)
                {
                    int l1 = Convert.toInt(wordbyword.getWord(1));
                    int i2 = Convert.toInt(wordbyword.getWord(2));
                    if(mailbox.messageExist(l1))
                        stringbuffer.append(mailbox.getMessage(l1, i2));
                    if(stringbuffer.length() != 0)
                        s1 = "+OK top of message follows";
                    else
                        s1 = "-ERR no such message";
                } else
                {
                    s1 = "-ERR Wrong number of arguments";
                }
            } else
            {
                s1 = "-ERR wrong or unimplemented command";
            }
            break;

        default:
            Debug.msg("PopServerState: case default");
            System.exit(1);
            break;
        }
        return s1;
    }

    private int state;
    private StringBuffer userName;
    private StringBuffer password;
    private StringBuffer reply;
    private MailBox mailbox;
    private String baseDir;
    private String localAccounts;
}
