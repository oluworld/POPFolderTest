package starwave.util.regexp;

class StringState extends State {
   String s;

   StringState(String var1, int var2) {
      super(var2, var1.length());
      this.s = var1;
   }

   public String getGroupString(int var1) {
      this.validateGroup(var1);
      return this.s.substring(super.pstart[var1] - 1, super.pend[var1] - 1);
   }

   int getchar() {
      return this.s.charAt(super.offset);
   }

   int getchar(int var1) {
      return this.s.charAt(var1);
   }

   int indexOf(int var1, int var2) {
      return this.s.indexOf(var1, var2);
   }

   int lastIndexOf(int var1, int var2) {
      return this.s.lastIndexOf(var1, var2);
   }
}
