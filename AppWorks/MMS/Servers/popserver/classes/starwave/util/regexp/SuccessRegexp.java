package starwave.util.regexp;

class SuccessRegexp extends Regexp {
   SuccessRegexp(Regexp var1) {
      super(var1);
   }

   protected boolean match(State var1) {
      return true;
   }
}
