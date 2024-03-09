package stefanolocati.cmd.popclient;

import com.jthomas.pop.pop3;
import com.jthomas.pop.popStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import stefanolocati.app.wolfspider.WolfSpider;
import stefanolocati.resources.pop3.FileNames;
import stefanolocati.text.MailBox;
import stefanolocati.text.PasswordFileReader;
import stefanolocati.text.WordByWord;
import stefanolocati.ui.InfoBoard;
import stefanolocati.util.Convert;
import stefanolocati.util.Debug;

public class PopClient extends Thread {
   private static final int DBGDELAY = 0;
   private Vector msgList;
   private int numMessages;
   private int totSize;
   private String[] responses;
   private pop3 remoteMailBox;
   private MailBox localMailBox;
   private PasswordFileReader accounts;
   private String userName;
   private String mailServer;
   private String password;
   private String email;
   int expirePolicy;
   private String baseDir;
   private String accountsFile;
   private String logDir;
   private WolfSpider gui;
   ResourceBundle msg;
   private int newMails;
   private static InfoBoard infoBoard;

   public PopClient(String var1, String var2) {
      super("PopClient");
      this.numMessages = 0;
      this.totSize = 0;
      this.newMails = -1;
      this.baseDir = var1;
      this.accountsFile = this.baseDir + File.separator + "config" + File.separator + var2;
      this.logDir = this.baseDir + File.separator + FileNames.clientLogDir;
      this.msgList = new Vector();
      this.remoteMailBox = new pop3();
      this.localMailBox = new MailBox(this.baseDir);
      this.remoteMailBox.setDebugOn(true);
   }

   public PopClient(String var1, String var2, WolfSpider var3) {
      this(var1, var2);
      this.gui = var3;
      this.msg = this.gui.getResourceBundle();
   }

   private final void closeConnection() {
      popStatus var1 = this.remoteMailBox.quit();
      if (!var1.OK()) {
         Debug.msg("PopClient.closeConnection(): " + var1.Response(), this.gui == null, this.logDir);
      }

   }

   private final boolean connect() {
      Object[] var2 = new Object[]{this.email};
      this.guiMessage(MessageFormat.format(this.msg.getString("PopClient.connecting"), var2));
      popStatus var1 = this.remoteMailBox.connect(this.mailServer);
      if (!var1.OK()) {
         return false;
      } else {
         this.setProgress(0.02);
         return true;
      }
   }

   private final boolean downloadMessages() {
      this.msgList.removeAllElements();
      this.dumpMsgList();
      if (!this.processAccountsLine()) {
         return false;
      } else if (!this.connect()) {
         this.guiMessage(this.msg.getString("PopClient.cant_conn"));
         return false;
      } else if (!this.login()) {
         this.guiMessage(this.msg.getString("PopClient.bad_pwd"));
         return false;
      } else {
         this.setProgress(0.04);
         Object[] var1 = new Object[]{new Integer(this.numMessages), new Long((long)this.totSize)};
         this.guiMessage(MessageFormat.format(this.msg.getString("PopClient.numsize_tot"), var1));
         if (this.numMessages == 0) {
            this.setProgress(0.85);
            this.closeConnection();
            this.setProgress(1.0);

            try {
               Thread.sleep(300L);
            } catch (InterruptedException var5) {
            }

            return true;
         } else if (!this.list()) {
            this.closeConnection();
            this.guiMessage(this.msg.getString("PopClient.server_err"));
            return false;
         } else {
            this.setProgress(0.05);
            if (!this.uidl()) {
               this.closeConnection();
               this.guiMessage(this.msg.getString("PopClient.server_err"));
               return false;
            } else {
               long var2 = System.currentTimeMillis();
               Object[] var4 = new Object[]{new Integer(this.getNoOfNewMails()), new Long(this.getSizeOfNewMails())};
               System.out.println("getNoOfNewMails(): " + (System.currentTimeMillis() - var2));
               this.setProgress(0.06);
               this.guiMessage(MessageFormat.format(this.msg.getString("PopClient.numsize_new"), var4));
               if (!this.retr()) {
                  this.closeConnection();
                  this.guiMessage(this.msg.getString("PopClient.server_err"));
                  return false;
               } else {
                  this.setProgress(0.99);
                  this.closeConnection();
                  this.setProgress(1.0);

                  try {
                     Thread.sleep(300L);
                  } catch (InterruptedException var6) {
                  }

                  return true;
               }
            }
         }
      }
   }

   private void dumpMsgList() {
      String var1 = "size: " + this.msgList.size() + " - number:size:uidl:time";

      for(int var3 = 0; var3 < this.msgList.size(); ++var3) {
         Message var2 = (Message)this.msgList.elementAt(var3);
         var1 = var1 + "\n" + var2.number + ":" + var2.size + ":" + var2.uidl + ":" + var2.time;
      }

   }

   private final int findMessage(int var1) {
      if (((Message)this.msgList.elementAt(var1 - 1)).number != var1) {
         Debug.msg(this.msg.getString("PopClient.bad_enum"), this.gui == null, this.logDir);
         System.exit(1);
      }

      return var1 - 1;
   }

   private final int getNoOfNewMails() {
      int var1 = 0;
      if (this.newMails >= 0) {
         return this.newMails;
      } else {
         for(int var2 = 0; var2 < this.msgList.size(); ++var2) {
            ((Message)this.msgList.elementAt(var2)).time = this.localMailBox.wasDownloaded(this.email, ((Message)this.msgList.elementAt(var2)).uidl);

            try {
               Thread.sleep(0L);
            } catch (InterruptedException var3) {
            }

            this.dumpMsgList();
            if (((Message)this.msgList.elementAt(var2)).time == 0L) {
               ++var1;
            }
         }

         this.newMails = var1;
         return var1;
      }
   }

   private final long getSizeOfNewMails() {
      int var2 = 0;

      for(int var3 = 0; var3 < this.msgList.size(); ++var3) {
         String var1 = ((Message)this.msgList.elementAt(var3)).uidl;
         if (((Message)this.msgList.elementAt(var3)).time == 0L) {
            var2 += ((Message)this.msgList.elementAt(var3)).size;
         }
      }

      return (long)var2;
   }

   private final void guiMessage(String var1) {
      if (this.gui != null) {
         this.gui.setMessage(var1);
      }

   }

   private final boolean list() {
      popStatus var2 = this.remoteMailBox.list();
      if (!var2.OK()) {
         Debug.msg("list: " + var2.Response(), this.gui == null, this.logDir);
         return false;
      } else {
         this.responses = var2.Responses();
         if (this.numMessages != this.responses.length) {
            Debug.msg("list: " + this.msg.getString("PopClient.server_err"), this.gui == null, this.logDir);
            return false;
         } else {
            for(int var4 = 0; var4 < this.numMessages; ++var4) {
               WordByWord var1 = new WordByWord(this.responses[var4]);
               Message var3 = new Message();
               var3.number = Convert.toInt(var1.getWord(0));
               var3.size = Convert.toInt(var1.getWord(1));
               this.msgList.addElement(var3);
               this.dumpMsgList();

               try {
                  Thread.sleep(0L);
               } catch (InterruptedException var5) {
               }
            }

            return true;
         }
      }
   }

   private final boolean login() {
      Debug.msg("login: " + this.email, this.gui == null, this.logDir);
      this.newMails = -1;
      popStatus var1 = this.remoteMailBox.login(this.userName, this.password);
      if (!var1.OK()) {
         this.closeConnection();
         return false;
      } else {
         this.numMessages = this.remoteMailBox.get_TotalMsgs();
         this.totSize = this.remoteMailBox.get_TotalSize();
         Object[] var2 = new Object[]{new Integer(this.numMessages), new Long((long)this.totSize)};
         Debug.msg("login: " + MessageFormat.format(this.msg.getString("PopClient.numsize_tot"), var2), this.gui == null, this.logDir);
         return true;
      }
   }

   public static void main(String[] var0) {
      if (var0.length != 2) {
         System.out.println("java PopClient baseDirectory accountsFile");
      } else {
         PopClient var1 = new PopClient(var0[0], var0[1]);
         Locale var2 = Locale.getDefault();
         var1.msg = ResourceBundle.getBundle("stefanolocati.resources.pop3.Messages", var2);
         var1.run();
         System.out.println(var1.msg.getString("PopClient.press_enter"));

         try {
            System.in.read();
         } catch (IOException var3) {
         }

         System.exit(0);
      }
   }

   private final boolean processAccountsLine() {
      this.userName = this.accounts.getField(0);
      this.password = this.accounts.getField(1);
      this.mailServer = this.accounts.getField(2);
      this.email = this.accounts.getField(3);
      this.expirePolicy = Convert.toInt(this.accounts.getField(4));
      if (this.userName != null && this.password != null && this.mailServer != null && this.email != null && this.accounts.getField(4) != null) {
         return true;
      } else {
         Debug.msg(this.email + " meno di 5 campi", this.gui == null, this.logDir);
         return false;
      }
   }

   private final boolean retr() {
      String var2 = "null";
      int var4 = this.getNoOfNewMails();
      int var5 = 0;
      double var6 = 0.06;

      for(int var13 = 0; var13 < this.msgList.size(); ++var13) {
         int var9 = ((Message)this.msgList.elementAt(var13)).number;
         String var8 = ((Message)this.msgList.elementAt(var13)).uidl;
         int var10 = ((Message)this.msgList.elementAt(var13)).size;
         long var11 = ((Message)this.msgList.elementAt(var13)).time;
         if (var11 != 0L) {
            if (MailBox.expireMessage(var11, this.expirePolicy)) {
               Debug.msg(this.msg.getString("PopClient.removing") + var8, this.gui == null, this.logDir);
               this.remoteMailBox.dele(var9);
            }
         } else {
            popStatus var3 = this.remoteMailBox.top(var9, 1);
            if (!var3.OK()) {
               return false;
            }

            this.responses = var3.Responses();
            var6 += 0.92 / (double)var4 * 0.05;
            this.setProgress(var6);
            String var1 = Rules.getLocalAccount(this.baseDir + File.separator + FileNames.rules, this.email, this.responses, (long)var10);
            if (var1.equals("null")) {
               Debug.msg("Messaggio ignorato " + var8, this.gui == null, this.logDir);
               ++var5;
               var6 += 0.92 / (double)var4 * 0.95;
               this.setProgress(var6);
               this.guiMessage("Messaggio " + var5 + " ignorato.");
               this.localMailBox.putUidl(this.email, var8, this.responses);
               var11 = System.currentTimeMillis();
               if (MailBox.expireMessage(var11, this.expirePolicy)) {
                  Debug.msg(this.msg.getString("PopClient.removing") + var8, this.gui == null, this.logDir);
                  this.remoteMailBox.dele(var9);
               }
            } else if (var1.length() == 0) {
               Debug.msg("Nessuna regola per " + this.email + " impossibile scaricare!", this.gui == null, this.logDir);
               this.guiMessage("Errore di configurazione: manca la regola di default!");
            } else {
               Debug.msg("Nuovo: (" + var10 + " bytes)  UIDL=" + var8, this.gui == null, this.logDir);
               ++var5;
               Object[] var14 = new Object[]{new Integer(var5), new Integer(var4), new Integer(var10)};
               this.guiMessage(MessageFormat.format(this.msg.getString("PopClient.downloading"), var14));
               var3 = this.remoteMailBox.retr(var9);
               if (!var3.OK()) {
                  return false;
               }

               var6 += 0.92 / (double)var4 * 0.95;
               this.setProgress(var6);
               this.responses = var3.Responses();
               this.localMailBox.open(var1);

               try {
                  this.localMailBox.putMessage(this.email, var8, this.responses);
               } catch (IOException var15) {
                  this.localMailBox.close();
                  Debug.msg("PopClient.retr(): IOException. uidl=" + var8, this.gui == null, this.logDir);
                  continue;
               }

               this.localMailBox.close();
               var11 = System.currentTimeMillis();
               if (MailBox.expireMessage(var11, this.expirePolicy)) {
                  Debug.msg(this.msg.getString("PopClient.removing") + var8, this.gui == null, this.logDir);
                  this.remoteMailBox.dele(var9);
               }
            }
         }
      }

      return true;
   }

   private final void retry(Vector var1) {
      int var2 = var1.size() * 3;

      while(!var1.isEmpty()) {
         try {
            Thread.sleep(3000L);
         } catch (InterruptedException var4) {
         }

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            this.accounts.setLineNumber((Integer)var1.elementAt(var3));
            if (this.downloadMessages()) {
               var1.removeElementAt(var3);
               --var2;
               break;
            }
         }

         var2 -= var1.size();
         if (var2 <= 0) {
            break;
         }
      }

   }

   public void run() {
      Vector var1 = new Vector();

      try {
         this.accounts = new PasswordFileReader(this.accountsFile);
      } catch (FileNotFoundException var2) {
         Debug.msg("PopClient.PopClient():" + this.accountsFile + this.msg.getString("PopClient.not_found"), this.gui == null, this.logDir);
         if (this.gui != null) {
            this.gui.endOfJob();
         }

         this.guiMessage(this.accountsFile + this.msg.getString("PopClient.not_found"));
         System.out.println(this.accountsFile + this.msg.getString("PopClient.not_found"));
         return;
      }

      do {
         this.setProgress(0.01);
         if (!this.downloadMessages()) {
            var1.addElement(new Integer(this.accounts.getLineNumber()));
         }
      } while(this.accounts.readNextLine());

      this.accounts.close();
      Debug.msg("---", this.gui == null, this.logDir);
      this.guiMessage(this.msg.getString("PopClient.conn_end"));
      if (this.gui != null) {
         this.gui.endOfJob();
      }

   }

   private final void setProgress(double var1) {
      if (this.gui != null) {
         this.gui.setProgress(var1);
      }

   }

   private final boolean uidl() {
      popStatus var2 = this.remoteMailBox.uidl();
      if (!var2.OK()) {
         Debug.msg("uidl: " + var2.Response(), this.gui == null, this.logDir);
         return false;
      } else {
         this.responses = var2.Responses();
         if (this.numMessages != this.responses.length) {
            Debug.msg("uidl: " + this.msg.getString("PopClient.server_err"), this.gui == null, this.logDir);
            return false;
         } else {
            for(int var4 = 0; var4 < this.numMessages; ++var4) {
               WordByWord var1 = new WordByWord(this.responses[var4]);
               int var3 = Convert.toInt(var1.getWord(0));
               Message var5 = (Message)this.msgList.elementAt(this.findMessage(var3));
               var5.uidl = var1.getWord(1);
               this.dumpMsgList();

               try {
                  Thread.sleep(0L);
               } catch (InterruptedException var6) {
               }
            }

            return true;
         }
      }
   }
}
