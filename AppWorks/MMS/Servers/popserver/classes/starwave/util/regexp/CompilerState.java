package starwave.util.regexp;

class CompilerState {
   String input;
   int offset;
   int groupCount = 0;
   int limit;
   boolean mapCase;
   boolean eof = false;

   CompilerState(String var1, boolean var2) {
      this.input = var1;
      this.mapCase = var2;
      this.limit = var1.length();
   }

   final boolean atEop() {
      return this.offset == this.limit;
   }

   final int currentChar() {
      return this.eof ? -1 : this.input.charAt(this.offset - 1);
   }

   final int nextChar() {
      if (this.offset < this.limit) {
         return this.input.charAt(this.offset++);
      } else {
         this.eof = true;
         return -1;
      }
   }

   final int nextGroup() {
      return this.groupCount++;
   }

   final String substring(int var1) {
      return this.input.substring(var1, this.offset);
   }

   public String toString() {
      return this.eof ? "EOF" : this.input.substring(this.offset);
   }

   final void ungetc() {
      this.eof = false;
      --this.offset;
   }
}
