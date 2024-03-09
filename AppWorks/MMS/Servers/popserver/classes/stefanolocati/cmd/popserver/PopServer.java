package stefanolocati.cmd.popserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import stefanolocati.resources.pop3.FileNames;
import stefanolocati.util.Debug;

public class PopServer extends Thread {
   static final int DELAY = 0;
   static String heardSentence = null;
   static String sentenceToSay = null;
   String baseDir;
   String logDir;
   StringBuffer fileToList;

   public static void main(String[] var0) {
      if (var0.length != 1) {
         Debug.msg("Missing argument <basedir>, server aborting");
         System.exit(1);
      }

      PopServer var1 = new PopServer(var0[0]);
      var1.run();
   }

   private void mySleep() {
      try {
         Thread.sleep(0L);
      } catch (InterruptedException var2) {
      }

   }

   public void run() {
      byte var1 = 110;
      Object var2 = null;
      Object var3 = null;
      Object var4 = null;
      Object var5 = null;
      Debug.msg("--- Server starting up!", false, this.logDir);

      while(true) {
         Debug.msg("--- Ready!", false, this.logDir);

         ServerSocket var6;
         try {
            var6 = new ServerSocket(var1);
         } catch (IOException var16) {
            Debug.msg("  PopServer 1: " + var16.getMessage(), false, this.logDir);
            return;
         }

         Socket var7;
         try {
            var7 = var6.accept();
         } catch (IOException var15) {
            Debug.msg("  PopServer 2: " + var15.getMessage(), false, this.logDir);
            return;
         }

         this.mySleep();

         BufferedReader var8;
         PrintWriter var9;
         try {
            var8 = new BufferedReader(new InputStreamReader(var7.getInputStream()));
            var9 = new PrintWriter(new BufferedOutputStream(var7.getOutputStream(), 1024), false);
         } catch (IOException var14) {
            Debug.msg("  PopServer 3: " + var14.getMessage(), false, this.logDir);
            return;
         }

         this.mySleep();
         PopServerState var10 = new PopServerState(this.baseDir);
         sentenceToSay = var10.processInput((String)null, this.fileToList);
         Debug.msg("-> " + sentenceToSay, false, this.logDir);
         var9.print(sentenceToSay + "\r\n");
         var9.flush();
         this.mySleep();

         while(!var10.endOfDialog()) {
            try {
               heardSentence = var8.readLine();
            } catch (IOException var17) {
               Debug.msg("  PopServer 4: " + var17.getMessage(), false, this.logDir);
               break;
            }

            if (heardSentence == null) {
               break;
            }

            sentenceToSay = var10.processInput(heardSentence, this.fileToList);
            Debug.msg(heardSentence + " -> " + sentenceToSay + " " + this.fileToList, false, this.logDir);
            this.mySleep();
            var9.print(sentenceToSay + "\r\n");
            if (this.fileToList.length() != 0) {
               try {
                  BufferedReader var11 = new BufferedReader(new FileReader(this.fileToList.toString()));

                  while(true) {
                     String var12;
                     if ((var12 = var11.readLine()) == null) {
                        var9.print(".\r\n");
                        var9.flush();
                        var11.close();
                        break;
                     }

                     this.mySleep();
                     if (var12.length() == 1 && var12.charAt(0) == '.') {
                        var9.print(". \r\n");
                     } else {
                        var9.print(var12 + "\r\n");
                     }

                     var9.flush();
                  }
               } catch (FileNotFoundException var18) {
                  Debug.msg("  PopServer 6: " + var18.getMessage(), false, this.logDir);
                  return;
               } catch (IOException var19) {
                  Debug.msg("  PopServer 7: " + var19.getMessage(), false, this.logDir);
                  return;
               }
            }

            this.mySleep();
            var9.flush();
         }

         this.mySleep();

         try {
            var9.close();
            var8.close();
            var7.close();
            var6.close();
         } catch (IOException var13) {
            Debug.msg("  PopServer 7: " + var13.getMessage(), false, this.logDir);
            return;
         }
      }
   }

   public PopServer(String var1) {
      this.baseDir = var1;
      this.logDir = this.baseDir + File.separator + FileNames.serverLogDir;
      this.fileToList = new StringBuffer(70);
   }
}
