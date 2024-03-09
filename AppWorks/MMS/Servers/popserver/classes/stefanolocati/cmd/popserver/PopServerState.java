package stefanolocati.cmd.popserver;

import java.io.File;
import java.io.FileNotFoundException;
import stefanolocati.resources.pop3.FileNames;
import stefanolocati.text.MailBox;
import stefanolocati.text.PasswordFileReader;
import stefanolocati.text.WordByWord;
import stefanolocati.util.Convert;
import stefanolocati.util.Debug;

class PopServerState {
   private int state = 0;
   private StringBuffer userName = new StringBuffer();
   private StringBuffer password = new StringBuffer();
   private StringBuffer reply = new StringBuffer();
   private MailBox mailbox;
   private String baseDir;
   private String localAccounts;

   final boolean endOfDialog() {
      return this.state == 4;
   }

   final String processInput(String var1, StringBuffer var2) {
      String var3 = null;
      WordByWord var4 = new WordByWord(var1);
      if (this.state != 0 && var4.getNumWords() == 0) {
         var3 = "-ERR wrong or unimplemented command";
         var2.setLength(0);
         return var3;
      } else {
         switch (this.state) {
            case 0:
               var3 = "+OK POPRunner server ready";
               var2.setLength(0);
               this.state = 1;
               break;
            case 1:
               var2.setLength(0);
               if (var4.getWord(0).equalsIgnoreCase("QUIT")) {
                  if (var4.getNumWords() == 1) {
                     var3 = "+OK goodbye from POPRunner";
                  } else {
                     var3 = "-ERR Wrong number of arguments";
                  }

                  this.state = 4;
               } else if (var4.getWord(0).equalsIgnoreCase("USER")) {
                  if (var4.getNumWords() == 2) {
                     this.userName.setLength(0);
                     this.userName.append(var4.getWord(1));
                     var3 = "+OK enter password";
                     this.state = 2;
                  } else {
                     var3 = "-ERR Wrong number of arguments";
                  }
               } else {
                  var3 = "-ERR wrong or unimplemented command";
               }
               break;
            case 2:
               var2.setLength(0);
               this.reply.setLength(0);
               if (var4.getWord(0).equalsIgnoreCase("QUIT")) {
                  if (var4.getNumWords() == 1) {
                     var3 = "+OK goodbye from POPRunner";
                  } else {
                     var3 = "-ERR Wrong number of arguments";
                  }

                  this.mailbox.close();
                  this.state = 4;
               } else if (var4.getWord(0).equalsIgnoreCase("PASS")) {
                  if (var4.getNumWords() == 2) {
                     this.password.setLength(0);
                     this.password.append(var4.getWord(1));

                     try {
                        PasswordFileReader var8 = new PasswordFileReader(this.localAccounts);
                        if (var8.validLogin(this.userName.toString(), this.password.toString())) {
                           if (!this.mailbox.open(this.userName.toString())) {
                              System.exit(1);
                           }

                           this.reply.append("+OK mailbox ready: " + this.mailbox);
                           var3 = this.reply.toString();
                           this.state = 3;
                        } else {
                           var3 = "-ERR wrong login or password";
                           this.state = 1;
                        }

                        var8.close();
                     } catch (FileNotFoundException var7) {
                        var3 = "-ERR No password file found! " + this.localAccounts;
                        this.state = 1;
                     }
                  } else {
                     var3 = "-ERR Wrong number of arguments";
                     this.state = 1;
                  }
               } else {
                  var3 = "-ERR wrong or unimplemented command";
                  this.state = 1;
               }
               break;
            case 3:
               var2.setLength(0);
               this.reply.setLength(0);
               if (var4.getWord(0).equalsIgnoreCase("QUIT")) {
                  if (var4.getNumWords() == 1) {
                     this.mailbox.commitDelete();
                     this.reply.append("+OK goodbye from POPRunner: " + this.mailbox);
                     var3 = this.reply.toString();
                  } else {
                     var3 = "-ERR Wrong number of arguments";
                  }

                  this.state = 4;
               } else {
                  int var5;
                  if (var4.getWord(0).equalsIgnoreCase("STAT")) {
                     if (var4.getNumWords() == 1) {
                        this.reply.append("+OK " + this.mailbox.getNumMessages() + " " + this.mailbox.size());
                        var3 = this.reply.toString();
                     } else if (var4.getNumWords() == 2) {
                        var5 = Convert.toInt(var4.getWord(1));
                        if (this.mailbox.messageExist(var5)) {
                           this.reply.append("+OK " + this.mailbox.size(var5));
                           var3 = this.reply.toString();
                        } else {
                           var3 = "-ERR no such message";
                        }
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else if (var4.getWord(0).equalsIgnoreCase("RETR")) {
                     if (var4.getNumWords() == 2) {
                        var5 = Convert.toInt(var4.getWord(1));
                        if (this.mailbox.messageExist(var5)) {
                           var2.append(this.mailbox.getMessage(var5));
                        }

                        if (var2.length() != 0) {
                           var3 = "+OK message follows";
                        } else {
                           var3 = "-ERR no such message";
                        }
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else if (var4.getWord(0).equalsIgnoreCase("LIST")) {
                     if (var4.getNumWords() == 1) {
                        this.reply.append("+OK " + this.mailbox);

                        for(var5 = 1; var5 <= this.mailbox.lastMessage(); ++var5) {
                           if (this.mailbox.messageExist(var5)) {
                              this.reply.append("\r\n" + var5 + " " + this.mailbox.size(var5));
                           }
                        }

                        this.reply.append("\r\n.");
                        var3 = this.reply.toString();
                     } else if (var4.getNumWords() == 2) {
                        var5 = Convert.toInt(var4.getWord(1));
                        if (this.mailbox.messageExist(var5)) {
                           this.reply.append("+OK " + var5 + " " + this.mailbox.size(var5));
                           var3 = this.reply.toString();
                        } else {
                           var3 = "-ERR no such message";
                        }
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else if (var4.getWord(0).equalsIgnoreCase("UIDL")) {
                     if (var4.getNumWords() == 1) {
                        this.reply.append("+OK unique message IDs follows");

                        for(var5 = 1; var5 <= this.mailbox.lastMessage(); ++var5) {
                           if (this.mailbox.messageExist(var5)) {
                              this.reply.append("\r\n" + var5 + " " + this.mailbox.getUidl(var5));
                           }
                        }

                        this.reply.append("\r\n.");
                        var3 = this.reply.toString();
                     } else if (var4.getNumWords() == 2) {
                        var5 = Convert.toInt(var4.getWord(1));
                        if (this.mailbox.messageExist(var5)) {
                           this.reply.append("+OK " + var5 + " " + this.mailbox.getUidl(var5));
                           var3 = this.reply.toString();
                        } else {
                           var3 = "-ERR no such message";
                        }
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else if (var4.getWord(0).equalsIgnoreCase("DELE")) {
                     if (var4.getNumWords() == 2) {
                        var5 = Convert.toInt(var4.getWord(1));
                        if (this.mailbox.messageExist(var5)) {
                           this.mailbox.deleteMessage(var5);
                           var3 = "+OK message deleted";
                        } else {
                           var3 = "-ERR no such message";
                        }
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else if (var4.getWord(0).equalsIgnoreCase("NOOP")) {
                     if (var4.getNumWords() == 1) {
                        var3 = "+OK and looking at you";
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else if (var4.getWord(0).equalsIgnoreCase("RSET")) {
                     if (var4.getNumWords() == 1) {
                        this.mailbox.undeleteAll();
                        var3 = "+OK all messages undeleted";
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else if (var4.getWord(0).equalsIgnoreCase("TOP")) {
                     if (var4.getNumWords() == 3) {
                        var5 = Convert.toInt(var4.getWord(1));
                        int var6 = Convert.toInt(var4.getWord(2));
                        if (this.mailbox.messageExist(var5)) {
                           var2.append(this.mailbox.getMessage(var5, var6));
                        }

                        if (var2.length() != 0) {
                           var3 = "+OK top of message follows";
                        } else {
                           var3 = "-ERR no such message";
                        }
                     } else {
                        var3 = "-ERR Wrong number of arguments";
                     }
                  } else {
                     var3 = "-ERR wrong or unimplemented command";
                  }
               }
               break;
            default:
               Debug.msg("PopServerState: case default");
               System.exit(1);
         }

         return var3;
      }
   }

   PopServerState(String var1) {
      this.baseDir = var1;
      this.mailbox = new MailBox(this.baseDir);
      this.localAccounts = this.baseDir + File.separator + FileNames.localAccounts;
   }
}
