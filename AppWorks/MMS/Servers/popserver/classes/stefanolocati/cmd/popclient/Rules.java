package stefanolocati.cmd.popclient;

import java.io.FileNotFoundException;
import starwave.util.regexp.Regexp;
import stefanolocati.text.DataFileReader;
import stefanolocati.text.MailMessage;
import stefanolocati.util.Convert;
import stefanolocati.util.Debug;

class Rules {
   static String getLocalAccount(String var0, String var1, String[] var2, long var3) {
      String var5 = "";
      DataFileReader var6 = null;
      boolean var8 = false;
      boolean var9 = true;
      boolean var10 = true;
      boolean var11 = true;
      MailMessage var7 = new MailMessage(var2);

      try {
         var6 = new DataFileReader(var0);
      } catch (FileNotFoundException var14) {
         Debug.msg("Rules.whichLocalAccount(): non trovo il file " + var0);
         return "null";
      }

      do {
         if (var1.equalsIgnoreCase(var6.getField(0))) {
            if (var6.getField(1).equalsIgnoreCase("default")) {
               var5 = var6.getField(3).toLowerCase();
               break;
            }

            if (var6.getField(1).equalsIgnoreCase("size")) {
               if (var3 / 1024L >= (long)Convert.toInt(var6.getField(2))) {
                  var5 = var6.getField(3).toLowerCase();
                  break;
               }
            } else {
               Regexp var12 = Regexp.compile(var6.getField(2).toLowerCase());
               String var13 = var7.getHeader(var6.getField(1)).toLowerCase();
               if (var12.match(var13, 0) != null) {
                  var5 = var6.getField(3).toLowerCase();
                  break;
               }
            }
         }
      } while(var6.readNextLine());

      var6.close();
      return var5;
   }

   public static void main(String[] var0) {
      String[] var1 = new String[9];
      String var2 = "c:\\documenti\\stefano\\ecommerce\\config\\rules";
      var1[0] = "\tby geocities.com (8.8.5/8.8.5) with ESMTP id AAA04880;";
      var1[1] = "\tTue, 10 Feb 1998 00:44:32 -0800 (PST)";
      var1[2] = "From: spammer@Somewhere.com";
      var1[3] = "Received: by jtsr-stock.com (8.8.8/8.8.5) with SMTP id OAA20078;";
      var1[4] = "\tSun, 1 Feb 1998 14:05:04 -0500 (EST)";
      var1[5] = "Date: Sun, 01 Feb 98 12:47:44 EST";
      var1[6] = "To: Friend@public.com";
      var1[7] = "Subject: A long vacation!!";
      var1[8] = "Message-ID: <>";
      System.out.println(getLocalAccount(var2, "slocati@geocities.com", var1, 2345L));
      System.out.println(getLocalAccount(var2, "stefano.locati@somewhere.net", var1, 3432L));
      var1[2] = "From: joe@poorfellow.org";
      System.out.println(getLocalAccount(var2, "slocati@geocities.com", var1, 3432L));
      System.out.println(getLocalAccount(var2, "slocati@geocities.com", var1, 51200L));
      var1[7] = "Subject: Web Order Form: A33 toaster";
      System.out.println(getLocalAccount(var2, "stefano.locati@somewhere.net", var1, 3432L));
   }
}
