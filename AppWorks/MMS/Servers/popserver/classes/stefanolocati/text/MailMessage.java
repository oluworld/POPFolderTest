package stefanolocati.text;

public class MailMessage {
   String[] mailMsg;

   public MailMessage(String[] var1) {
      this.mailMsg = var1;
   }

   public String[] getBody() {
      int var2;
      for(var2 = 0; var2 < this.mailMsg.length && this.mailMsg[var2].length() != 0; ++var2) {
      }

      ++var2;
      String[] var1 = new String[this.mailMsg.length - var2];

      for(int var3 = var2; var3 < this.mailMsg.length; ++var3) {
         var1[var3 - var2] = this.mailMsg[var3];
      }

      return var1;
   }

   public String getFirstBodyLine() {
      int var1;
      for(var1 = 0; var1 < this.mailMsg.length && this.mailMsg[var1].length() != 0; ++var1) {
      }

      int var2;
      for(var2 = var1; var2 < this.mailMsg.length && this.mailMsg[var2].length() == 0; ++var2) {
      }

      return this.mailMsg.length == var2 ? "" : this.mailMsg[var2];
   }

   public String getHeader(String var1) {
      var1 = var1.toLowerCase();

      for(int var2 = 0; var2 < this.mailMsg.length && this.mailMsg[var2].length() != 0; ++var2) {
         if (this.mailMsg[var2].toLowerCase().startsWith(var1)) {
            if (this.mailMsg[var2].length() < var1.length() + 2) {
               break;
            }

            if (this.mailMsg[var2].charAt(var1.length()) == ':') {
               return this.mailMsg[var2].substring(var1.length() + 2);
            }
         }
      }

      return "";
   }

   public long getSize() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.mailMsg.length; ++var2) {
         var1 += this.mailMsg.length + 2;
      }

      return (long)var1;
   }

   public static void main(String[] var0) {
      String[] var1 = new String[]{"\tby geocities.com (8.8.5/8.8.5) with ESMTP id AAA04880;", "\tTue, 10 Feb 1998 00:44:32 -0800 (PST)", "From: etmp@etmp.com", "Received: by jtsr-stock.com (8.8.8/8.8.5) with SMTP id OAA20078;", "\tSun, 1 Feb 1998 14:05:04 -0500 (EST)", "Date: Sun, 01 Feb 98 12:47:44 EST", "To: Friend@public.com", "Subject: Huge Orders Coming In!!", "Message-ID: <>", "", "You have been carefully selected to receive our", "gift based upon your previous internet postings."};
      MailMessage var2 = new MailMessage(var1);
      System.out.println("Message size: " + var2.getSize());
      System.out.println("From: " + var2.getHeader("fRoM") + "-");
      System.out.println("Subject: " + var2.getHeader("subject") + "-");
      System.out.println("Date: " + var2.getHeader("date") + "-");
      System.out.println("-- BODY --");
      String[] var3 = var2.getBody();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         System.out.println("->" + var3[var4] + "<-");
      }

      System.out.println("--- First body line ---");
      System.out.println(var2.getFirstBodyLine());
   }
}
