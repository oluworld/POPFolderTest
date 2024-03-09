package starwave.util.regexp;

class RegexpCompiler {
   static Regexp compile(String var0, boolean var1) {
      CompilerState var2 = new CompilerState(var0, var1);
      Regexp var3 = new Regexp((Regexp)null);
      Regexp var4 = compileAlternatives(var3, var2, -1);
      var4.next = Regexp.success;
      return var3.next;
   }

   static Regexp compileAlternative(CompilerState var0, Regexp var1) {
      Object var3;
      Object var2 = var3 = new Regexp((Regexp)null);

      try {
         label62:
         while(true) {
            int var4;
            label58:
            switch (var4 = var0.nextChar()) {
               case -1:
               case 41:
               case 124:
                  ((Regexp)var3).next = var1;
                  return ((Regexp)var2).next;
               case 36:
                  if (var0.atEop()) {
                     var3 = new ContextMatch((Regexp)var3, var4);
                     continue;
                  }
                  break;
               case 40:
                  var3 = compileAlternatives((Regexp)var3, var0, 41);
                  continue;
               case 42:
               case 43:
               case 63:
                  if (((Regexp)var3).prev != null) {
                     if (!((Regexp)var3).canStar()) {
                        throw new MalformedRegexpException("cannot " + (char)var4 + " " + var3 + "'s");
                     }

                     var3 = ((Regexp)var3).makeMulti(var4);
                     continue;
                  }
                  break;
               case 46:
                  var3 = new Dot((Regexp)var3);
                  continue;
               case 91:
                  int var5 = var0.offset - 1;

                  while(true) {
                     switch (var0.nextChar()) {
                        case -1:
                           throw new MalformedRegexpException("Missing ]");
                        case 92:
                           var0.nextChar();
                           break;
                        case 93:
                           var3 = new CharClass((Regexp)var3, var0.substring(var5));
                           continue label62;
                     }
                  }
               case 92:
                  switch (var4 = var0.nextChar()) {
                     case 48:
                     case 49:
                     case 50:
                     case 51:
                     case 52:
                     case 53:
                     case 54:
                     case 55:
                     case 56:
                     case 57:
                        if (var0.groupCount < var4 - 48) {
                           throw new MalformedRegexpException("illegal forward reference: \\" + new Character((char)var4));
                        }

                        var3 = new GroupReference((Regexp)var3, var4 - 48);
                        continue;
                     case 66:
                     case 98:
                        var3 = new ContextMatch((Regexp)var3, var4);
                        continue;
                     case 68:
                     case 83:
                     case 87:
                     case 100:
                     case 115:
                     case 119:
                        var3 = CharClass.cloneCharClass((Regexp)var3, var4);
                        continue;
                     case 102:
                        var4 = 12;
                        break label58;
                     case 110:
                        var4 = 10;
                        break label58;
                     case 114:
                        var4 = 13;
                        break label58;
                     case 116:
                        var4 = 9;
                     default:
                        break label58;
                  }
               case 94:
                  if (var0.offset == 1) {
                     var3 = new ContextMatch((Regexp)var3, var4);
                     continue;
                  }
            }

            if (var3 instanceof Literal) {
               ((Literal)var3).appendChar(var4);
            } else {
               var3 = new Literal((Regexp)var3, var4, var0.mapCase);
            }
         }
      } catch (MalformedRegexpException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new MalformedRegexpException("near " + var0.substring(var0.offset - 1));
      }
   }

   static Regexp compileAlternatives(Regexp var0, CompilerState var1, int var2) {
      int var3 = var1.nextGroup();
      Group var8 = new Group(var0, 40, var3);
      Alternatives var4 = new Alternatives(var8);
      Group var6 = new Group((Regexp)null, 41, var3);

      int var7;
      do {
         Regexp var5 = compileAlternative(var1, var6);
         var7 = var1.currentChar();
         if (var7 != var2 && var7 != 124) {
            throw new MalformedRegexpException((char)var7 + " unexpected");
         }

         if (var5 == null) {
            break;
         }

         var4.addAlt(var5);
      } while(var7 != var2);

      return var6;
   }
}
