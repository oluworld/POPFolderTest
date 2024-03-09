package starwave.util.regexp;

abstract class State {
   int offset;
   int limit;
   int[] pstart;
   int[] pend;

   State(int var1, int var2) {
      this.offset = var1;
      this.limit = var2;
   }

   int charsLeft() {
      return this.limit - this.offset;
   }

   final void clearGroup(int var1) {
      this.validateGroup(var1);
      this.pstart[var1] = this.pend[var1] = 0;
   }

   final void endGroup(int var1) {
      this.ensureGroup(var1);
      this.pend[var1] = this.offset + 1;
   }

   final void ensureGroup(int var1) {
      if (this.pstart == null || var1 >= this.pstart.length) {
         int[] var2 = new int[var1 + 4];
         if (this.pstart != null) {
            System.arraycopy(this.pstart, 0, var2, 0, this.pstart.length);
         }

         this.pstart = var2;
         var2 = new int[var1 + 4];
         if (this.pend != null) {
            System.arraycopy(this.pend, 0, var2, 0, this.pend.length);
         }

         this.pend = var2;
      }

   }

   final int getGroupEnd(int var1) {
      this.validateGroup(var1);
      return this.pend[var1] - 1;
   }

   final int getGroupLength(int var1) {
      this.validateGroup(var1);
      return this.pend[var1] - this.pstart[var1];
   }

   final int getGroupStart(int var1) {
      this.validateGroup(var1);
      return this.pstart[var1] - 1;
   }

   abstract String getGroupString(int var1);

   final int getLimit() {
      return this.limit;
   }

   abstract int getchar();

   abstract int getchar(int var1);

   abstract int indexOf(int var1, int var2);

   abstract int lastIndexOf(int var1, int var2);

   final void startGroup(int var1) {
      this.ensureGroup(var1);
      this.pstart[var1] = this.offset + 1;
   }

   public String toString() {
      return "offset = " + this.offset + ", limit = " + this.limit;
   }

   final void validateGroup(int var1) {
      if (this.pstart == null || var1 >= this.pstart.length || this.pstart[var1] == 0 || this.pend[var1] == 0) {
         throw new NoSuchMatchException(": " + var1);
      }
   }
}
