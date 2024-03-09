package starwave.util.regexp;

class Group extends Regexp {
   int kind;
   int n;

   Group(Regexp var1, int var2, int var3) {
      super(var1);
      this.kind = var2;
      this.n = var3;
   }

   Regexp advance(State var1) {
      if (this.kind == 40) {
         var1.startGroup(this.n);
      } else {
         var1.endGroup(this.n);
      }

      return super.next;
   }

   void backup(State var1) {
      try {
         var1.clearGroup(this.n);
      } catch (NoSuchMatchException var2) {
      }

   }

   boolean canStar() {
      return false;
   }

   int firstCharacter() {
      return super.next != null ? super.next.firstCharacter() : -1;
   }

   public String toStringThis() {
      return String.valueOf((char)this.kind);
   }
}
