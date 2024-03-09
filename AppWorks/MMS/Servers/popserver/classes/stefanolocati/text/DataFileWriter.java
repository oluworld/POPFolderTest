package stefanolocati.text;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class DataFileWriter {
   private RandomAccessFile outFile;
   private Vector field = new Vector();

   public DataFileWriter(String var1) throws IOException {
      this.outFile = new RandomAccessFile(var1, "rw");
      this.outFile.seek(this.outFile.length());
   }

   public final void close() {
      if (this.outFile != null) {
         try {
            this.outFile.close();
         } catch (IOException var1) {
         }

         this.outFile = null;
      }

   }

   public final int getNumFields() {
      return this.field.size();
   }

   public static void main(String[] var0) throws IOException {
      DataFileWriter var1 = new DataFileWriter("gen-datafile");
      var1.putField(4);
      var1.putField("ciao");
      var1.writeLine();
      var1.writeLine();
      String var2 = "\" \"\"\n";
      var1.putField(var2);
      var2 = "\\some \\ talk\" \ra";
      var1.putField(var2);
      var1.writeLine();
      var1.close();
      var1 = new DataFileWriter("gen-datafile");
      var1.putField("PS");
      var1.putField("my last word");
      var1.writeLine();
      var1.close();
   }

   public final void putField(int var1) {
      this.putField(new Integer(var1));
   }

   public final void putField(long var1) {
      this.putField(new Long(var1));
   }

   public final void putField(Object var1) {
      if (var1 instanceof String) {
         byte var2 = 0;
         String var4 = "";

         for(int var5 = 0; var5 < ((String)var1).length(); ++var5) {
            char var3 = ((String)var1).charAt(var5);
            switch (var2) {
               case 0:
                  if (var3 == '\\') {
                     var4 = var4 + "\\" + var3;
                  } else if (var3 == '"') {
                     var4 = var4 + "\\" + var3;
                  } else if (var3 == '\r') {
                     var2 = 1;
                  } else if (var3 == '\n') {
                     var2 = 2;
                  } else {
                     var4 = var4 + var3;
                  }
                  break;
               case 1:
                  if (var3 == '\r') {
                     var4 = var4 + "\\n";
                  } else if (var3 == '\n') {
                     var4 = var4 + "\\n";
                     var2 = 0;
                  } else if (var3 == '\\') {
                     var4 = var4 + "\\n\\" + var3;
                     var2 = 0;
                  } else if (var3 == '"') {
                     var4 = var4 + "\\n\\" + var3;
                     var2 = 0;
                  } else {
                     var4 = var4 + "\\n" + var3;
                     var2 = 0;
                  }
                  break;
               case 2:
                  if (var3 == '\n') {
                     var4 = var4 + "\\n";
                  } else if (var3 == '\r') {
                     var4 = var4 + "\\n";
                     var2 = 0;
                  } else if (var3 == '\\') {
                     var4 = var4 + "\\n\\" + var3;
                     var2 = 0;
                  } else if (var3 == '"') {
                     var4 = var4 + "\\n\\" + var3;
                     var2 = 0;
                  } else {
                     var4 = var4 + "\\n" + var3;
                     var2 = 0;
                  }
            }
         }

         if (var2 != 0) {
            var4 = var4 + "\\n";
         }

         var1 = var4;
      }

      this.field.addElement(var1);
   }

   public final void writeLine() {
      boolean var1 = true;
      if (this.getNumFields() != 0) {
         try {
            for(int var2 = 0; var2 < this.getNumFields(); ++var2) {
               this.outFile.writeBytes('"' + this.field.elementAt(var2).toString() + '"');
               if (var2 < this.getNumFields() - 1) {
                  this.outFile.writeBytes(", ");
               }
            }

            this.outFile.writeBytes(System.getProperty("line.separator"));
         } catch (IOException var3) {
         }

         this.field.removeAllElements();
      }
   }
}
