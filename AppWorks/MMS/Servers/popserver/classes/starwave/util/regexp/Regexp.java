package starwave.util.regexp;

import java.io.FileInputStream;

public class Regexp {
   static final boolean debug = false;
   static Regexp success = new SuccessRegexp((Regexp)null);
   Regexp next;
   Regexp prev;

   Regexp(Regexp var1) {
      this.prev = var1;
      if (var1 != null) {
         var1.next = this;
      }

   }

   Regexp advance(State var1) {
      return this.next;
   }

   void backup(State var1) {
      --var1.offset;
   }

   boolean canStar() {
      return true;
   }

   public static Regexp compile(String var0) {
      return compile(var0, false);
   }

   public static Regexp compile(String var0, boolean var1) {
      return RegexpCompiler.compile(var0, var1);
   }

   int firstCharacter() {
      return -1;
   }

   public static void main(String[] var0) throws Exception {
      FileInputStream var1 = new FileInputStream(var0[0]);
      byte[] var2 = new byte[var1.available()];
      if (var1.read(var2) != var2.length) {
         System.out.println("Huh?");
      }

      var1.close();
      String var3 = new String(var2);
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;

      int var7;
      for(var7 = 1; var0[var7].charAt(0) == '-'; ++var7) {
         if (var0[var7].equals("-time")) {
            var5 = true;
         } else if (var0[var7].equals("-reverse")) {
            var4 = true;
         } else if (var0[var7].equals("-ignore")) {
            var6 = true;
         } else {
            System.out.println("Unknown option: " + var0[var7]);
         }
      }

      Regexp var8 = compile(var0[var7], var6);
      System.out.println("Processing " + var0[var7] + " = " + var8.toString());
      ++var7;
      if (var8.firstCharacter() != -1) {
         System.out.println("First Character: " + new Character((char)var8.firstCharacter()));
      }

      int var9;
      if (var5) {
         var9 = 50;
         long var10 = System.currentTimeMillis();

         for(int var12 = 0; var12 < var9 && var8.searchForward(var3, 0) != null; ++var12) {
         }

         System.out.println((float)(1000.0 * (double)var9 / (double)(System.currentTimeMillis() - var10)) + " per second.");
      } else {
         var9 = Integer.parseInt(var0[var7]);
         int var11 = 0;

         Result var13;
         while((var13 = var8.searchForward(var3, var11)) != null) {
            var11 = var13.getMatchEnd(0);
            System.out.println("Match = " + var13 + " match " + var9 + " = " + var13.getMatch(var9));
         }
      }

   }

   Regexp makeMulti(int var1) {
      return new Multi(this.prev, this, var1);
   }

   public Result match(String var1, int var2) {
      StringState var3 = new StringState(var1, var2);
      return this.match(var3) ? new Result(var3) : null;
   }

   protected boolean match(State var1) {
      Regexp var2 = this;
      int var4 = var1.offset;

      Regexp var3;
      try {
         while((var3 = var2.advance(var1)) != null) {
            if (var3 == success) {
               return true;
            }

            var2 = var3;
         }
      } catch (StringIndexOutOfBoundsException var5) {
      } catch (ArrayIndexOutOfBoundsException var6) {
      }

      var1.offset = var4;
      return false;
   }

   public Result match(char[] var1, int var2, int var3) {
      CharArrayState var4 = new CharArrayState(var1, var2, var3);
      return this.match(var4) ? new Result(var4) : null;
   }

   public Result searchBackward(String var1, int var2) {
      StringState var3 = new StringState(var1, var2);
      return this.searchBackward(var3);
   }

   private final Result searchBackward(State var1) {
      int var2 = this.firstCharacter();
      int var3;
      if (var2 != -1) {
         while(--var1.offset >= 0 && (var3 = var1.lastIndexOf(var2, var1.offset)) != -1) {
            var1.offset = var3;
            if (this.match(var1)) {
               return new Result(var1);
            }
         }
      } else {
         var3 = var1.getLimit();

         while(--var1.offset >= 0) {
            if (this.match(var1)) {
               return new Result(var1);
            }
         }
      }

      return null;
   }

   public Result searchBackward(char[] var1, int var2, int var3) {
      CharArrayState var4 = new CharArrayState(var1, var2, var3);
      return this.searchBackward(var4);
   }

   public final Result searchForward(String var1, int var2) {
      StringState var3 = new StringState(var1, var2);
      return this.searchForward(var3);
   }

   private final Result searchForward(State var1) {
      int var2 = this.firstCharacter();
      int var3;
      if (var2 != -1) {
         while((var3 = var1.indexOf(var2, var1.offset)) != -1) {
            var1.offset = var3;
            if (this.match(var1)) {
               return new Result(var1);
            }

            var1.offset = var3 + 1;
         }
      } else {
         for(var3 = var1.getLimit(); var1.offset < var3; ++var1.offset) {
            if (this.match(var1)) {
               return new Result(var1);
            }
         }
      }

      return null;
   }

   public final Result searchForward(char[] var1, int var2, int var3) {
      CharArrayState var4 = new CharArrayState(var1, var2, var3);
      return this.searchForward(var4);
   }

   public final String toString() {
      return this.next != null && this.next != success ? this.toStringThis() + this.next.toString() : this.toStringThis();
   }

   public String toStringThis() {
      return this == success ? "<!>" : "";
   }
}
