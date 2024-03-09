package stefanolocati.text;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;
import stefanolocati.util.Debug;

public class DataFileReader {
   private LineNumberReader inFile;
   private String inputLine;
   private Vector lineInfo;
   private static final char COMMENT = ';';
   private static final char DELIMITER = '"';
   private static final char SEPARATOR = ',';
   private static final char ESCAPE = '\\';
   private static final char TAB = '\t';
   private static final char SPC = ' ';
   private static final char[] IGNORED = new char[]{'\t', ' '};
   private static final String LINESEPARATOR = System.getProperty("line.separator");

   public DataFileReader(String var1) throws FileNotFoundException {
      int var2 = 99000;
      this.lineInfo = new Vector();
      this.inFile = new LineNumberReader(new FileReader(var1));

      try {
         this.inFile.mark(99000);
      } catch (IOException var4) {
         Debug.msg(var4.getMessage());
      }

      this.readNextLine();
   }

   public final void close() {
      if (this.inFile != null) {
         try {
            this.inFile.close();
         } catch (IOException var2) {
            Debug.msg("DataFileReader.close(): " + var2.getMessage());
         }

         this.inFile = null;
      }

   }

   public final String getField(int var1) {
      return var1 >= 0 && var1 < this.lineInfo.size() ? (String)this.lineInfo.elementAt(var1) : null;
   }

   public final int getLineNumber() {
      return this.inFile.getLineNumber() - 1;
   }

   public final int getNumFields() {
      return this.lineInfo.size();
   }

   private static final boolean isIgnored(char var0) {
      for(int var1 = 0; var1 < IGNORED.length; ++var1) {
         if (IGNORED[var1] == var0) {
            return true;
         }
      }

      return false;
   }

   public static void main(String[] var0) throws FileNotFoundException {
      DataFileReader var1 = new DataFileReader("data.txt");

      do {
         for(int var2 = 0; var1.getField(var2) != null; ++var2) {
            System.out.print(var1.getField(var2) + "|");
         }

         System.out.println("Fields: " + var1.getNumFields() + " Line: " + var1.getLineNumber());
      } while(var1.readNextLine());

      var1.close();
   }

   private final boolean processLine(String var1) {
      boolean var3 = false;
      int var4 = 0;
      byte var2 = 65;
      this.lineInfo.removeAllElements();
      int var7 = var1.length();

      for(StringBuffer var6 = new StringBuffer(var7); var4 < var7; ++var4) {
         char var5 = var1.charAt(var4);
         switch (var2) {
            case 65:
               if (!isIgnored(var5)) {
                  if (var5 == '"') {
                     var2 = 66;
                  } else if (var5 == ';') {
                     var2 = 69;
                  } else {
                     var2 = 87;
                  }
               }
               break;
            case 66:
               if (var5 == '\\') {
                  var2 = 67;
               } else if (var5 == '"') {
                  var2 = 68;
                  this.lineInfo.addElement(var6.toString());
                  var6.setLength(0);
               } else {
                  var6.append(var5);
               }
               break;
            case 67:
               if (var5 == 'n') {
                  var6.append(LINESEPARATOR);
               } else {
                  var6.append(var5);
               }

               var2 = 66;
               break;
            case 68:
               if (!isIgnored(var5)) {
                  if (var5 == ',') {
                     var2 = 65;
                  } else {
                     var2 = 87;
                  }
               }
            case 69:
            case 87:
         }
      }

      if (var2 != 65 && var2 != 68 && var2 != 69) {
         return false;
      } else {
         return true;
      }
   }

   public final boolean readNextLine() {
      try {
         this.inputLine = this.inFile.readLine();
      } catch (IOException var1) {
         return false;
      }

      if (this.inputLine != null) {
         this.processLine(this.inputLine);
         return this.lineInfo.size() == 0 ? this.readNextLine() : true;
      } else {
         return false;
      }
   }

   public final void rewind() {
      try {
         this.inFile.reset();
      } catch (IOException var2) {
         Debug.msg("DataFile.rewind() " + var2.getMessage());
         System.exit(1);
      }

      this.readNextLine();
   }

   public final boolean setLineNumber(int var1) {
      this.rewind();

      boolean var3;
      for(int var2 = 0; (var3 = this.readNextLine()) && var2 < var1 - 1; ++var2) {
      }

      return var3;
   }
}
