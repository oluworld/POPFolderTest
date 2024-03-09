package stefanolocati.resources.pop3;

import java.util.ListResourceBundle;

public class Messages extends ListResourceBundle {
   static final Object[][] contents = new Object[][]{{"Interface.title", "Wolf Spider - fetch mail from multiples mailboxes"}, {"Interface.get_mail", "Get Mail"}, {"Interface.about", "About"}, {"Interface.status_bar", "Wolf Spider 0.1                   September 1999"}, {"Interface.no_basedir", "Compulsory parameter BASEDIR missing"}, {"PopClient.bad_enum", "The remote server doesn't enumerate messages in the right way."}, {"PopClient.server_err", "Server error"}, {"PopClient.cant_conn", "Unreachable server"}, {"PopClient.connecting", "Connection with {0}"}, {"PopClient.numsize_tot", "Mailbox status: {0, choice, 0#no messages|1#One message|1<{0,number,integer} messages}{0, choice, 0#|0< ({1,number,integer} bytes)}"}, {"PopClient.numsize_new", "{0, choice, 0#No new messages|1#One new message|1<{0,number,integer} new messages}{0, choice, 0#|0< ({1,number,integer} bytes)}"}, {"PopClient.downloading", "Downloading message {0} of {1} ({2} bytes)"}, {"PopClient.bad_pwd", "Wrong login or password"}, {"PopClient.conn_end", "End of connection"}, {"PopClient.not_found", " not found"}, {"PopClient.press_enter", "Press Enter to exit"}, {"PopClient.removing", "Removing "}};

   public final Object[][] getContents() {
      return contents;
   }
}
