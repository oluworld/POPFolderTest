package com.jthomas.pop;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class apop extends pop3 {
   String ConnectTimestamp = null;

   public apop() {
   }

   public apop(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public boolean apopSupport() {
      return this.ConnectTimestamp != null;
   }

   public synchronized popStatus connect() {
      this.ConnectTimestamp = null;
      popStatus var1 = super.connect();
      if (var1._OK) {
         String[] var2 = this.Parse(var1, -1);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3] != null && var2[var3].charAt(0) == '<' && var2[var3].charAt(var2[var3].length() - 1) == '>') {
               this.ConnectTimestamp = var2[var3];
               this.debug("APOP timestamp='" + this.ConnectTimestamp + "'");
            }
         }
      }

      return var1;
   }

   public synchronized popStatus login() {
      popStatus var1 = new popStatus();
      if (super.User != null && super.Password != null) {
         if (super.server == null) {
            var1._Response = "-ERR Not connected";
         } else {
            var1._OK = true;
         }
      } else {
         var1._Response = "-ERR User or Password not specified";
      }

      if (this.ConnectTimestamp == null) {
         if (var1._OK) {
            var1 = super.login();
         }

         return var1;
      } else {
         if (var1._OK) {
            String var2 = this.ConnectTimestamp + super.Password;
            this.debug("MD5 input='" + var2 + "'");
            String var4 = null;

            try {
               MessageDigest var3 = MessageDigest.getInstance("MD5");
               var4 = Convert.toHexString(var3.digest(var2.getBytes()));
            } catch (NoSuchAlgorithmException var6) {
            }

            this.debug("MD5 output='" + var4 + "'");
            this.send("APOP " + super.User + " " + var4);
            var1._Response = this.recv();
            this.Parse(var1, 1);
            if (var1._OK) {
               super.State = 2;
               popStatus var5 = this.stat();
            } else {
               var1 = super.login();
            }
         }

         return var1;
      }
   }

   public popStatus login(String var1, String var2) {
      super.User = var1;
      super.Password = var2;
      return this.login();
   }
}
