package starwave.util.regexp;

class Literal extends Regexp {
   static char[] caseMap = new char[128];
   char[] data = new char[0];
   int count;
   boolean mapCase = false;

   static {
      int var0;
      for(var0 = 0; var0 < 128; ++var0) {
         caseMap[var0] = (char)var0;
      }

      for(var0 = 97; var0 <= 122; ++var0) {
         caseMap[var0] = (char)(var0 + -32);
      }

   }

   Literal(Regexp var1, int var2, boolean var3) {
      super(var1);
      this.mapCase = var3;
      this.appendChar(var2);
   }

   Regexp advance(State var1) {
      int var2 = this.count;
      int var3 = var1.offset;
      if (var1.charsLeft() < var2) {
         return null;
      } else {
         int var4 = 0;
         if (this.mapCase) {
            while(true) {
               --var2;
               if (var2 < 0) {
                  break;
               }

               if (caseMap[this.data[var4++]] != caseMap[var1.getchar(var3++)]) {
                  return null;
               }
            }
         } else {
            while(true) {
               --var2;
               if (var2 < 0) {
                  break;
               }

               if (this.data[var4++] != var1.getchar(var3++)) {
                  return null;
               }
            }
         }

         var1.offset = var3;
         return super.next;
      }
   }

   void appendChar(int var1) {
      if (this.count >= this.data.length) {
         char[] var2 = new char[this.data.length + 16];
         System.arraycopy(this.data, 0, var2, 0, this.data.length);
         this.data = var2;
      }

      this.data[this.count++] = (char)var1;
   }

   int firstCharacter() {
      return this.mapCase ? -1 : this.data[0];
   }

   Regexp makeMulti(int var1) {
      if (this.count == 1) {
         return new Multi(super.prev, this, var1);
      } else {
         --this.count;
         return new Multi(this, new Literal((Regexp)null, this.data[this.count], this.mapCase), var1);
      }
   }

   public String toStringThis() {
      return new String(this.data, 0, this.count);
   }
}
