package starwave.util.regexp;

class Dot extends Regexp {
   public Dot(Regexp var1) {
      super(var1);
   }

   Regexp advance(State var1) {
      if (var1.getchar() != 10) {
         ++var1.offset;
         return super.next;
      } else {
         return null;
      }
   }

   public String toStringThis() {
      return ".";
   }
}
