package starwave.util.regexp;

class Multi extends Regexp {
   Regexp child;
   int kind;

   Multi(Regexp var1, Regexp var2, int var3) {
      super(var1);
      this.child = var2;
      var2.next = Regexp.success;
      this.kind = var3;
   }

   Regexp advance(State var1) {
      int var2 = 0;
      int var3 = var1.offset;
      switch (this.kind) {
         case 42:
         case 43:
            while(this.child.match(var1)) {
               ++var2;
            }

            if (this.kind == 43 && var2 == 0) {
               return null;
            }
            break;
         case 63:
            this.child.match(var1);
      }

      while(var1.offset > var3) {
         if (super.next.match(var1)) {
            return Regexp.success;
         }

         this.child.backup(var1);
      }

      return this.kind != 43 ? super.next : null;
   }

   boolean canStar() {
      return false;
   }

   public String toStringThis() {
      return this.child.toString() + new Character((char)this.kind);
   }
}
