package starwave.util.regexp;

class CharArrayState extends State {
   char[] data;

   CharArrayState(char[] var1, int var2, int var3) {
      super(var2, var3);
      this.data = var1;
   }

   public String getGroupString(int var1) {
      this.validateGroup(var1);
      return String.copyValueOf(this.data, super.pstart[var1] - 1, super.pend[var1] - super.pstart[var1]);
   }

   int getchar() {
      if (super.offset >= super.limit) {
         throw new ArrayIndexOutOfBoundsException(super.offset + " >= " + super.limit);
      } else {
         return this.data[super.offset];
      }
   }

   int getchar(int var1) {
      if (var1 >= super.limit) {
         throw new ArrayIndexOutOfBoundsException(var1 + " >= " + super.limit);
      } else {
         return this.data[var1];
      }
   }

   int indexOf(int var1, int var2) {
      char[] var3 = this.data;

      for(int var4 = super.limit; var2 < var4; ++var2) {
         if (var3[var2] == var1) {
            return var2;
         }
      }

      return -1;
   }

   int lastIndexOf(int var1, int var2) {
      for(char[] var3 = this.data; var2 >= 0; --var2) {
         if (var3[var2] == var1) {
            return var2;
         }
      }

      return -1;
   }
}
