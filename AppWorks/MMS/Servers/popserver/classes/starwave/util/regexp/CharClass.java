package starwave.util.regexp;

class CharClass extends Regexp {
   static final int NCHARS = 256;
   static final int SHIFT = 5;
   static final int MASK = 31;
   static CharClass sClass = new CharClass((Regexp)null, "[ \t\n\r]");
   static CharClass wClass = new CharClass((Regexp)null, "[a-zA-Z0-9_]");
   static CharClass dClass = new CharClass((Regexp)null, "[0-9]");
   int[] bits;
   boolean in = true;

   CharClass(CharClass var1) {
      super((Regexp)null);
      this.bits = var1.bits;
      this.in = var1.in;
   }

   CharClass(Regexp var1, String var2) {
      super(var1);
      this.bits = new int[8];
      this.process(var2);
   }

   final void addChar(int var1) {
      int[] var10000 = this.bits;
      var10000[var1 >> 5] |= 1 << (var1 & 31);
   }

   final void addChars(int var1, int var2) {
      if (var1 > var2) {
         int var3 = var1;
         var1 = var2;
         var2 = var3;
      }

      while(var1 <= var2) {
         this.addChar(var1++);
      }

   }

   Regexp advance(State var1) {
      int var2 = var1.getchar();
      if (this.charInClass(var2) == this.in) {
         ++var1.offset;
         return super.next;
      } else {
         return null;
      }
   }

   final boolean charInClass(int var1) {
      return (this.bits[var1 >> 5] & 1 << (var1 & 31)) != 0;
   }

   static CharClass cloneCharClass(Regexp var0, int var1) {
      CharClass var2;
      switch (var1) {
         case 68:
         case 100:
            var2 = dClass;
            break;
         case 83:
         case 115:
            var2 = sClass;
            break;
         case 87:
         case 119:
            var2 = wClass;
            break;
         default:
            throw new MalformedRegexpException("Internal exception");
      }

      var2 = new CharClass(var2);
      if (Character.isUpperCase((char)var1)) {
         var2.in = false;
      }

      var2.prev = var0;
      if (var0 != null) {
         var0.next = var2;
      }

      return var2;
   }

   final void merge(CharClass var1, boolean var2) {
      for(int var3 = 0; var3 < this.bits.length; ++var3) {
         int var4 = var1.bits[var3];
         if (var2) {
            var4 = ~var4;
         }

         int[] var10000 = this.bits;
         var10000[var3] |= var4;
      }

   }

   final String ppChar(int var1) {
      String var2;
      switch (var1) {
         case 9:
            var2 = "\\t";
            break;
         case 10:
            var2 = "\\n";
            break;
         case 11:
         case 12:
         default:
            if (var1 < 32) {
               var2 = "^" + new Character((char)(var1 + 64));
            } else {
               var2 = String.valueOf(new Character((char)var1));
            }
            break;
         case 13:
            var2 = "\\r";
      }

      return var2;
   }

   void process(String var1) {
      int var2 = 1;
      int var3 = var1.length() - 1;
      if (var1.charAt(var2) == '^') {
         ++var2;
         this.in = false;
      }

      while(true) {
         while(var2 < var3) {
            char var4;
            switch (var4 = var1.charAt(var2++)) {
               case '-':
                  if (var2 < var3) {
                     this.addChars(var1.charAt(var2 - 2), var1.charAt(var2++));
                  } else {
                     this.addChar(45);
                  }
                  break;
               case '\\':
                  switch (var4 = var1.charAt(var2++)) {
                     case 'D':
                     case 'd':
                        this.merge(dClass, var4 == 'D');
                        continue;
                     case 'S':
                     case 's':
                        this.merge(sClass, var4 == 'S');
                        continue;
                     case 'W':
                     case 'w':
                        this.merge(wClass, var4 == 'W');
                        continue;
                     case 'b':
                        var4 = '\b';
                        break;
                     case 'f':
                        var4 = '\f';
                        break;
                     case 'n':
                        var4 = '\n';
                        break;
                     case 'r':
                        var4 = '\r';
                        break;
                     case 't':
                        var4 = '\t';
                  }
               default:
                  this.addChar(var4);
            }
         }

         return;
      }
   }

   public String toStringThis() {
      StringBuffer var1 = new StringBuffer("[");
      if (!this.in) {
         var1.append("^");
      }

      for(int var2 = 0; var2 < 255; ++var2) {
         if (this.charInClass(var2)) {
            int var3;
            for(var3 = var2 + 1; var3 < 255 && this.charInClass(var3); ++var3) {
            }

            int var4 = var3 - var2;
            var1.append(this.ppChar(var2));
            switch (var4) {
               case 3:
               default:
                  var1.append('-');
               case 2:
                  var1.append(this.ppChar(var3 - 1));
               case 1:
                  var2 = var3 - 1;
            }
         }
      }

      var1.append(']');
      return var1.toString();
   }
}
