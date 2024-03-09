package com.jthomas.pop;

public class Convert {
   private Convert() {
   }

   public static final String toHexString(byte var0) {
      return var0 >= 0 && var0 <= 15 ? "0" + Integer.toHexString(var0 & 255) : Integer.toHexString(var0 & 255);
   }

   public static final String toHexString(byte[] var0) {
      StringBuffer var1 = new StringBuffer(2 * var0.length);

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.append(toHexString(var0[var2]));
      }

      return var1.toString();
   }

   public static final int toInt(String var0) {
      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var1) {
         return 0;
      }
   }
}
