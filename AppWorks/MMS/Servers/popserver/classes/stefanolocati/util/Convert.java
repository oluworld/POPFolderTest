package stefanolocati.util;

import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class Convert {
   private Convert() {
   }

   public static final File changeExtension(File var0, String var1) {
      return changeExtension(var0.getName(), var1);
   }

   public static final File changeExtension(String var0, String var1) {
      int var2 = var0.lastIndexOf(46);
      if (var2 == -1) {
         return new File(var0 + "." + var1);
      } else {
         ++var2;
         return new File(var0.substring(0, var2) + var1);
      }
   }

   public static final String extension(String var0) {
      int var1 = var0.lastIndexOf(46);
      if (var1 == -1) {
         return null;
      } else {
         ++var1;
         return var0.substring(var1);
      }
   }

   public static final String insertStringAt(String var0, String var1, int var2) {
      return var0.substring(0, var2) + var1 + var0.substring(var2, var0.length());
   }

   public static void main(String[] var0) {
      Frame var1 = new Frame("stefanolocati.util.Convert test");
      var1.setSize(640, 320);
      var1.setLocation(100, 100);
      var1.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            System.exit(0);
         }
      });
      TextArea var2 = new TextArea();
      var2.setFont(new Font("Courier", 0, 14));
      var2.setEditable(false);
      var1.add(var2);
      var1.setVisible(true);
      var2.setText("toInt(\"124\")=" + toInt("124") + "\n");
      var2.append("toInt(\"abc\")=" + toInt("abc") + "\n");
      long var3 = 343324324L;
      var2.append("long l=" + var3 + "\n");
      var2.append("toString(l)=" + toString(var3) + "\n");
      var3 = toLong("243322");
      var2.append("toLong(\"243322\")=" + var3 + "\n");
      String var5 = "0123456789ABCDEFG";
      var2.append("t=" + var5 + "|\n");
      var2.append("removeSubstring(t, 6)=" + removeSubstring(var5, 6) + "\n");
      var2.append("removeSubstring(t, 6, 9)=" + removeSubstring(var5, 6, 9) + "\n");
      var2.append("insertStringAt(t, \"ciao\", 2)=" + insertStringAt(var5, "ciao", 2) + "\n");
      var5 = "I love Susan forever. Susan is the only one.";
      var2.append("t=" + var5 + "|\n");
      var2.append("replace(t, \"Susan\", \"Annie\")=" + replace(var5, "Susan", "Annie") + "\n");
      var5 = "muñeca, fünf, dreißig, garçon, città";
      var2.append("t=" + var5 + "|\n");
      var2.append("simpleUpperCase(t)=" + simpleUpperCase(var5) + "|");
   }

   public static final String removeChar(String var0, char var1) {
      while(var0.indexOf(var1) != -1) {
         var0 = removeCharAt(var0, var0.indexOf(var1));
      }

      return var0;
   }

   public static final String removeCharAt(String var0, int var1) {
      return removeSubstring(var0, var1, var1 + 1);
   }

   public static final String removeSubstring(String var0, int var1) {
      return removeSubstring(var0, var1, var0.length());
   }

   public static final String removeSubstring(String var0, int var1, int var2) {
      return var0.substring(0, var1) + var0.substring(var2, var0.length());
   }

   public static final String replace(String var0, String var1, String var2) {
      int var3;
      for(int var4 = 0; (var3 = var0.indexOf(var1, var4)) != -1; var4 = var3 + var2.length()) {
         var0 = removeSubstring(var0, var3, var3 + var1.length());
         var0 = insertStringAt(var0, var2, var3);
      }

      return var0;
   }

   public static final String simpleUpperCase(String var0) {
      String var1 = "ÑÄËÏÖÜÇÀÈÌÒÙÁÉÍÓÚ";
      String var2 = "NAEIOUCAEIOUAEIOU";
      String var3 = "()[]{}/|,.;:?!¡¿-_= ";
      var0 = var0.toUpperCase();

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         var0 = var0.replace(var1.charAt(var4), var2.charAt(var4));
      }

      for(int var5 = 0; var5 < var3.length(); ++var5) {
         var0 = removeChar(var0, var3.charAt(var5));
      }

      return var0;
   }

   public static final File tmpFile() {
      return new File("appo");
   }

   public static final int toInt(String var0) {
      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var1) {
         return 0;
      }
   }

   public static final long toLong(String var0) {
      try {
         return Long.parseLong(var0);
      } catch (NumberFormatException var1) {
         return 0L;
      }
   }

   public static final String toString(int var0) {
      return (new Integer(var0)).toString();
   }

   public static final String toString(long var0) {
      return (new Long(var0)).toString();
   }
}
