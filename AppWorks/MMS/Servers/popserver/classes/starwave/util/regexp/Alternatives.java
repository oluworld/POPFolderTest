package starwave.util.regexp;

import java.util.Vector;

class Alternatives extends Regexp {
   Vector alts = new Vector(2);

   Alternatives(Regexp var1) {
      super(var1);
   }

   void addAlt(Regexp var1) {
      this.alts.addElement(var1);
   }

   Regexp advance(State var1) {
      int var2 = var1.offset;
      int var3 = this.alts.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         Regexp var5 = (Regexp)this.alts.elementAt(var4);
         if (var5.match(var1)) {
            return Regexp.success;
         }

         var1.offset = var2;
      }

      return null;
   }

   public boolean canStar() {
      return false;
   }

   int firstCharacter() {
      int var1 = this.alts.size();
      int var2 = -1;

      for(int var3 = 0; var3 < var1; ++var3) {
         Regexp var4 = (Regexp)this.alts.elementAt(var3);
         int var5 = var4.firstCharacter();
         if (var2 == -1) {
            var2 = var5;
         } else if (var5 != var2) {
            return -1;
         }
      }

      return var2;
   }

   public String toStringThis() {
      StringBuffer var1 = new StringBuffer();
      int var2 = this.alts.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.append(this.alts.elementAt(var3).toString());
         if (var3 < var2 - 1) {
            var1.append("|");
         }
      }

      return var1.toString();
   }
}
