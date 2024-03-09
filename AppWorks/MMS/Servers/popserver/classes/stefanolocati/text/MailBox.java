package stefanolocati.text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Vector;
import stefanolocati.util.Convert;
import stefanolocati.util.Debug;

public class MailBox {
   int dbgCount = 0;
   private String userName;
   private File mailDir;
   private String mailRoot;
   private StringBuffer mailDirName;
   private Vector fileList = new Vector();
   private Vector deleList = new Vector();
   private int nMessages;
   private StringBuffer strbuf = new StringBuffer(100);

   public MailBox(String var1) {
      this.mailRoot = var1 + File.separator + "mail" + File.separator;
   }

   public final void close() {
   }

   public final void commitDelete() {
      for(int var3 = 0; var3 < this.deleList.size(); ++var3) {
         int var2 = (Integer)this.deleList.elementAt(var3);
         File var1 = new File(this.getAnyMessage(var2));
         if (var1.isFile()) {
            var1.delete();
         } else {
            Debug.msg("Mailfile.commitDelete(): not a file");
            System.exit(1);
         }
      }

   }

   public final void deleteMessage(int var1) {
      if (this.messageExist(var1)) {
         Integer var2 = new Integer(var1);
         this.deleList.addElement(var2);
      }

   }

   public static boolean expireMessage(long var0, int var2) {
      boolean var3 = false;
      long var4 = 86400000L;
      if (var2 == 0) {
         return true;
      } else {
         if (var2 > 0) {
            long var6 = (System.currentTimeMillis() - var0) / 86400000L;
            if (var6 >= (long)var2) {
               return true;
            }
         }

         return false;
      }
   }

   private final String generateUidl() {
      String var3 = this.mailRoot + File.separator + this.userName + ".uid";

      long var1;
      try {
         BufferedReader var5 = new BufferedReader(new FileReader(var3));
         var1 = (long)Convert.toInt(var5.readLine());
         var5.close();
      } catch (FileNotFoundException var8) {
         var1 = 0L;
      } catch (IOException var9) {
         var1 = 0L;
      }

      File var4;
      do {
         ++var1;
         var4 = new File(this.mailDirName.toString() + File.separator + var1);
         if (var4.isDirectory()) {
            var4.delete();
         }
      } while(var4.isFile());

      try {
         PrintWriter var6 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(var3)));
         var6.println(Convert.toString(var1));
         var6.flush();
         var6.close();
      } catch (IOException var7) {
      }

      return Convert.toString(var1);
   }

   private final String getAnyMessage(int var1) {
      return var1 > 0 && var1 <= this.nMessages ? String.valueOf(this.mailDirName) + File.separator + this.fileList.elementAt(var1 - 1) : null;
   }

   public final String getMessage(int var1) {
      return !this.messageExist(var1) ? null : String.valueOf(this.mailDirName) + File.separator + this.fileList.elementAt(var1 - 1);
   }

   public final String getMessage(int var1, int var2) {
      BufferedReader var3 = null;
      PrintWriter var4 = null;
      String var5 = "TOP";
      if (!this.messageExist(var1)) {
         return null;
      } else {
         try {
            var3 = new BufferedReader(new FileReader(this.getMessage(var1)));
         } catch (FileNotFoundException var9) {
            Debug.msg("Mailfile: " + var9.getMessage());
            return null;
         } catch (IOException var10) {
            Debug.msg("Mailfile: " + var10.getMessage());
            return null;
         }

         try {
            var4 = new PrintWriter(new BufferedOutputStream(new FileOutputStream("TOP")));
         } catch (IOException var8) {
            Debug.msg("Mailfile: " + var8.getMessage());
            return null;
         }

         try {
            int var7 = 0;

            String var6;
            while((var6 = var3.readLine()) != null) {
               var4.print(var6 + "\r\n");
               if (var6.length() == 0 && var7 == 0) {
                  var7 = 1;
               } else if (var7 > 0) {
                  ++var7;
               }

               if (var2 < var7) {
                  break;
               }
            }

            if (var3 != null) {
               var3.close();
            }

            if (var4 != null) {
               var4.flush();
               var4.close();
            }

            return "TOP";
         } catch (IOException var11) {
            Debug.msg("Mailfile: " + var11.getMessage());
            return null;
         }
      }
   }

   public final int getNumMessages() {
      return this.nMessages - this.deleList.size();
   }

   public final String getUidl(int var1) {
      return !this.messageExist(var1) ? null : this.fileList.elementAt(var1 - 1).toString();
   }

   public final String[] getWholeMessage(int var1) {
      Vector var2 = new Vector(50, 100);

      BufferedReader var5;
      try {
         var5 = new BufferedReader(new FileReader(this.getMessage(var1)));
      } catch (FileNotFoundException var8) {
         Debug.msg(var8.getMessage());
         return null;
      }

      String var4;
      try {
         while((var4 = var5.readLine()) != null) {
            var2.addElement(var4);
         }
      } catch (IOException var9) {
         Debug.msg(var9.getMessage());
      }

      try {
         if (var5 != null) {
            var5.close();
         }
      } catch (IOException var7) {
         Debug.msg(var7.getMessage());
      }

      String[] var3 = new String[var2.size()];
      var2.copyInto(var3);
      return var3;
   }

   private final String idxFileName(String var1) {
      return this.mailRoot + var1 + ".idx";
   }

   public final int lastMessage() {
      return this.nMessages;
   }

   private final void list() {
      this.fileList.removeAllElements();
      String[] var1 = this.mailDir.list();
      this.nMessages = var1.length;

      for(int var2 = 0; var2 < this.nMessages; ++var2) {
         this.fileList.addElement(var1[var2]);
      }

   }

   public static void main(String[] var0) {
      MailBox var1 = new MailBox("c:\\documenti\\stefano\\ecommerce");
      if (!var1.open("giannibuozzi")) {
         System.out.println("Mailbox locked");
      }

      System.out.println(var1.getNumMessages() + " messages, mailbox size " + var1.size());

      for(int var2 = 0; var2 <= var1.lastMessage() + 1; ++var2) {
         System.out.println("message " + var2 + " exist=" + var1.messageExist(var2) + ", size " + var1.size(var2));
      }

      System.out.println();
      if (var1.messageExist(1)) {
         var1.deleteMessage(1);
      }

      if (var1.messageExist(3)) {
         var1.deleteMessage(3);
      }

      System.out.println(var1.getNumMessages() + " messages, mailbox size " + var1.size());

      for(int var3 = 0; var3 <= var1.lastMessage() + 1; ++var3) {
         System.out.println("message " + var3 + " exist=" + var1.messageExist(var3) + ", size " + var1.size(var3));
      }

      var1.commitDelete();
      var1.close();
   }

   public final boolean messageExist(int var1) {
      if (var1 > 0 && var1 <= this.nMessages) {
         for(int var3 = 0; var3 < this.deleList.size(); ++var3) {
            Integer var2 = (Integer)this.deleList.elementAt(var3);
            if (var2 == var1) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public final boolean open(String var1) {
      this.fileList.removeAllElements();
      this.deleList.removeAllElements();
      this.userName = var1.toLowerCase();
      this.mailDirName = new StringBuffer(this.mailRoot + this.userName);
      this.mailDir = new File(this.mailDirName.toString());
      if (this.mailDir.isFile()) {
         this.mailDir.delete();
      }

      if (!this.mailDir.isDirectory()) {
         this.mailDir.mkdir();
      }

      this.list();
      return true;
   }

   public final void putMessage(String var1, String var2, String[] var3) throws IOException {
      String var5 = String.valueOf(this.mailDirName) + File.separator + this.generateUidl();
      PrintWriter var4 = new PrintWriter(new BufferedOutputStream(new FileOutputStream(var5)));

      for(int var6 = 0; var6 < var3.length; ++var6) {
         var4.println(var3[var6]);
      }

      var4.flush();
      var4.close();
      this.putUidl(var1, var2, var3);
   }

   public final void putUidl(String var1, String var2, String[] var3) {
      try {
         DataFileWriter var4 = new DataFileWriter(this.idxFileName(var1));
         var4.putField(var2);
         var4.putField((new Date()).getTime());
         MailMessage var5 = new MailMessage(var3);
         var4.putField(var5.getHeader("from"));
         var5 = new MailMessage(var3);
         var4.putField(var5.getHeader("subject"));
         var4.writeLine();
         var4.close();
      } catch (IOException var6) {
      }

   }

   public final long size() {
      int var2 = 0;

      for(int var3 = 1; var3 <= this.nMessages; ++var3) {
         if (this.messageExist(var3)) {
            File var1 = new File(this.getMessage(var3));
            var2 = (int)((long)var2 + var1.length());
         }
      }

      return (long)var2;
   }

   public final long size(int var1) {
      if (!this.messageExist(var1)) {
         return 0L;
      } else {
         File var2 = new File(this.getMessage(var1));
         return var2.length();
      }
   }

   public final String toString() {
      this.strbuf.setLength(0);
      this.strbuf.append(this.getNumMessages());
      this.strbuf.append(" messages (");
      this.strbuf.append(this.size());
      this.strbuf.append(" bytes)");
      return this.strbuf.toString();
   }

   public final void undeleteAll() {
      this.deleList.removeAllElements();
   }

   public final long wasDownloaded(String var1, String var2) {
      System.out.println("wasDownloaded()" + ++this.dbgCount);

      DataFileReader var3;
      try {
         var3 = new DataFileReader(this.idxFileName(var1));
      } catch (FileNotFoundException var4) {
         return 0L;
      }

      while(!var2.equals(var3.getField(0))) {
         if (!var3.readNextLine()) {
            var3.close();
            return 0L;
         }
      }

      return Convert.toLong(var3.getField(1));
   }
}
