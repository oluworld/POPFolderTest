package stefanolocati.text;

import java.util.Enumeration;

public class Justify {
   private String sentence;
   private int lineLength = 78;

   public Justify() {
      this.sentence = null;
   }

   public Justify(String var1) {
      this.setSentence(var1);
   }

   public String getJustified() {
      String var2 = "";
      String var3 = "";
      WordByWord var1 = new WordByWord(this.sentence);
      var1.setSeparators(" ");
      Enumeration var5 = var1.words();

      while(var5.hasMoreElements()) {
         String var4 = (String)var5.nextElement();
         if (var3.length() == 0) {
            var3 = var3 + var4;
         } else if (var3.length() + var4.length() + 1 <= this.lineLength) {
            var3 = var3 + " " + var4;
         } else {
            var2 = var2 + var3 + "\n";
            var3 = var4;
         }
      }

      var2 = var2 + var3;
      return var2;
   }

   public int getLineLength() {
      return this.lineLength;
   }

   public String getSentence() {
      return this.sentence;
   }

   public static void main(String[] var0) {
      String var1 = "Questo è un esempio di testo da giustificare!";
      Justify var2 = new Justify();
      var2.setSentence(var1);
      var2.setLineLength(13);
      System.out.println(var1);
      System.out.println("\n" + var2.getJustified());
   }

   public void setLineLength(int var1) {
      this.lineLength = var1;
   }

   public void setSentence(String var1) {
      this.sentence = var1;
   }
}
