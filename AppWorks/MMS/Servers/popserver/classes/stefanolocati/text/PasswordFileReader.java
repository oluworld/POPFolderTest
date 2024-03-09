package stefanolocati.text;

import java.io.FileNotFoundException;

public class PasswordFileReader extends DataFileReader {
   public PasswordFileReader() throws FileNotFoundException {
      this("passwd");
   }

   public PasswordFileReader(String var1) throws FileNotFoundException {
      super(var1);
   }

   public static void main(String[] var0) {
      try {
         PasswordFileReader var1 = new PasswordFileReader();
         if (var1.validLogin("giannibuozzi", "benjot3")) {
            System.out.println("Valid Login");
         } else {
            System.out.println("Invalid Login");
         }

         if (var1.validLogin("gionni", "jkjj")) {
            System.out.println("Valid Login");
         } else {
            System.out.println("Invalid Login");
         }
      } catch (FileNotFoundException var2) {
         System.out.println("Could not open the password file");
      }

      System.runFinalizersOnExit(true);
   }

   public final boolean validLogin(String var1, String var2) {
      this.rewind();

      do {
         if (this.getField(0).equalsIgnoreCase(var1) && this.getField(1).equals(var2)) {
            return true;
         }
      } while(this.readNextLine());

      return false;
   }
}
