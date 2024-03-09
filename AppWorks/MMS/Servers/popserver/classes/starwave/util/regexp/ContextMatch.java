package starwave.util.regexp;

class ContextMatch extends Regexp {
   static CharClass word = CharClass.cloneCharClass((Regexp)null, 119);
   int kind;

   public ContextMatch(Regexp var1, int var2) {
      super(var1);
      this.kind = var2;
   }

   Regexp advance(State var1) {
      int var4 = var1.offset;
      switch (this.kind) {
         case 36:
            if (var1.charsLeft() > 0 && var1.getchar() != 10) {
               return null;
            }
            break;
         case 66:
         case 98:
            boolean var2 = var4 > 0 && var1.charsLeft() > 0 && word.charInClass(var1.getchar(var4 - 1));
            boolean var3 = var1.charsLeft() > 0 && word.charInClass(var1.getchar());
            if (this.kind == 66 != (var2 == var3)) {
               return null;
            }
            break;
         case 94:
            if (var4 > 0 && var1.getchar(var4 - 1) != 10) {
               return null;
            }
            break;
         default:
            return null;
      }

      return super.next;
   }

   void backup(State var1) {
   }

   boolean canStar() {
      return false;
   }

   int firstCharacter() {
      return this.kind == 94 ? super.next.firstCharacter() : -1;
   }

   public String toStringThis() {
      String var1 = String.valueOf(String.valueOf(new Character((char)this.kind)));
      return this.kind != 98 && this.kind != 66 ? var1 : "\\" + var1;
   }
}
