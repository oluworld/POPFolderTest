package stefanolocati.cmd.popserver;

interface Msg {
   String OK = "+OK ";
   String NL = "\r\n";
   String WELCOME = "+OK POPRunner server ready";
   String WRONGARGS = "-ERR Wrong number of arguments";
   String ENTERPASSWORD = "+OK enter password";
   String NOAUTH = "-ERR wrong login or password";
   String CANTLOCK = "-ERR mailbox is in use";
   String ENTERCOMMAND = "+OK mailbox ready";
   String UNKNOWN = "-ERR wrong or unimplemented command";
   String UIDLOK = "+OK unique message IDs follows";
   String RETROK = "+OK message follows";
   String NOSUCHMSG = "-ERR no such message";
   String TOPOK = "+OK top of message follows";
   String DELEOK = "+OK message deleted";
   String NOOPMSG = "+OK and looking at you";
   String UNMARK = "+OK all messages undeleted";
   String BYE = "+OK goodbye from POPRunner";
   String NOTDELE = "-ERR some messages not deleted";
   String NOPASSWORDS = "-ERR No password file found!";
}
