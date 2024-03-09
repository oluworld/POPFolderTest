package stefanolocati.text;

import java.util.Enumeration;
import java.util.Vector;

public class WordByWord {
   private char[] separator;
   private String sentence;
   final char TERMINATOR;
   private Vector word;

   WordByWord() {
      this.separator = new char[50];
      this.TERMINATOR = 0;
      this.separator[0] = ' ';
      this.separator[1] = '\b';
      this.separator[2] = '\r';
      this.separator[3] = '\n';
      this.separator[4] = 0;
      this.word = new Vector();
      this.sentence = "";
   }

   public WordByWord(String var1) {
      this();
      this.setSentence(var1);
   }

   public final int getNumWords() {
      return this.word.size();
   }

   public final String getWord(int var1) {
      return var1 >= 0 && var1 <= this.getNumWords() - 1 ? (String)this.word.elementAt(var1) : null;
   }

   private final boolean isSeparator(char var1) {
      for(int var2 = 0; this.separator[var2] != 0; ++var2) {
         if (var1 == this.separator[var2]) {
            return true;
         }
      }

      return false;
   }

   public static void main(String[] var0) {
      String var2 = "An apple a day,  keeps the doctor away!";
      WordByWord var1 = new WordByWord();
      var1.setSeparators(" ,;:.?!()[]{}");
      var1.setSentence(var2);
      System.out.println(">" + var2 + "<" + " has " + var1.getNumWords() + " words");
      System.out.println("These words are:");

      for(int var3 = 0; var3 < var1.getNumWords(); ++var3) {
         System.out.println(var1.getWord(var3));
      }

   }

   private final void processSentence() {
      int var2 = 0;
      boolean var3 = true;
      StringBuffer var1 = new StringBuffer();
      this.word.removeAllElements();
      if (this.sentence != "" && this.sentence != null) {
         for(; var2 < this.sentence.length(); ++var2) {
            if (var3) {
               if (!this.isSeparator(this.sentence.charAt(var2))) {
                  var1.append(this.sentence.charAt(var2));
                  var3 = false;
               }
            } else if (!this.isSeparator(this.sentence.charAt(var2))) {
               var1.append(this.sentence.charAt(var2));
            } else {
               this.word.addElement(var1.toString());
               var1.setLength(0);
               var3 = true;
            }
         }

         if (var1.length() > 0) {
            this.word.addElement(var1.toString());
         }

      }
   }

   public final void setSentence(String var1) {
      this.sentence = var1;
      this.processSentence();
   }

   public final void setSeparators(String var1) {
      for(int var2 = 0; var2 < var1.length(); ++var2) {
         this.separator[var2] = var1.charAt(var2);
      }

      this.separator[var1.length()] = 0;
      this.processSentence();
   }

   public final Enumeration words() {
      return this.word.elements();
   }
}
