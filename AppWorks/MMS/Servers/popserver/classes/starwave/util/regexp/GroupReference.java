package starwave.util.regexp;

class GroupReference extends Regexp {
   int n;

   GroupReference(Regexp var1, int var2) {
      super(var1);
      this.n = var2;
   }

   Regexp advance(State var1) {
      String var2;
      try {
         var2 = var1.getGroupString(this.n);
      } catch (NoSuchMatchException var6) {
         return null;
      }

      int var3 = var2.length();
      if (var1.charsLeft() < var3) {
         return null;
      } else {
         int var4 = var1.offset;
         int var5 = 0;

         do {
            --var3;
            if (var3 < 0) {
               var1.offset = var4;
               return super.next;
            }
         } while(var2.charAt(var5++) == var1.getchar(var4++));

         return null;
      }
   }

   void backup(State var1) {
      try {
         int var2 = var1.getGroupLength(this.n);
         var1.offset -= var2;
      } catch (NoSuchMatchException var3) {
      }

   }

   public String toStringThis() {
      return "\\" + new Character((char)(48 + this.n));
   }
}
