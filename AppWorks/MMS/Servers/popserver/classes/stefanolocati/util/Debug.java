package stefanolocati.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import stefanolocati.text.DataFileWriter;

public class Debug {
   public static void msg(String var0) {
      msg(var0, false, (String)null);
   }

   public static void msg(String var0, String var1) {
      msg(var0, false, var1);
   }

   public static void msg(String var0, boolean var1) {
      msg(var0, var1, (String)null);
   }

   public static void msg(String var0, boolean var1, String var2) {
      String var4;
      if (var2 != null) {
         var4 = var2 + File.separator + "debug.txt";
      } else {
         var4 = "debug.txt";
      }

      DataFileWriter var3;
      try {
         var3 = new DataFileWriter(var4);
      } catch (IOException var5) {
         return;
      }

      var3.putField(DateFormat.getDateTimeInstance(3, 2).format(new Date()));
      var3.putField(var0);
      var3.writeLine();
      var3.close();
      if (var1) {
         System.out.println(var0);
      }

   }
}
