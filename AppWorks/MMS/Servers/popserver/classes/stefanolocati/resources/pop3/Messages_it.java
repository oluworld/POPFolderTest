package stefanolocati.resources.pop3;

import java.util.ListResourceBundle;

public class Messages_it extends ListResourceBundle {
   static final Object[][] contents = new Object[][]{{"Interface.title", "Wolf Spider - preleva la posta da più caselle"}, {"Interface.get_mail", "Scarica Posta"}, {"Interface.about", "About"}, {"Interface.status_bar", "Wolf Spider 0.1                   Settembre 1999"}, {"Interface.no_basedir", "Manca il parametro obbligatorio BASEDIR"}, {"PopClient.bad_enum", "Il server remoto non numera i messaggi correttamente"}, {"PopClient.server_err", "Errore del server"}, {"PopClient.cant_conn", "Il server non risponde"}, {"PopClient.connecting", "Collegamento con {0}"}, {"PopClient.numsize_tot", "Stato casella: {0, choice, 0#nessun messaggio|1#un messaggio|1<{0,number,integer} messaggi}{0, choice, 0#|0< ({1,number,integer} bytes)}"}, {"PopClient.numsize_new", "{0, choice, 0#Nessun messaggio nuovo|1#Un messaggio nuovo|1<{0,number,integer} messaggi nuovi}{0, choice, 0#|0< ({1,number,integer} bytes)}"}, {"PopClient.downloading", "Scaricamento del messaggio {0} di {1} ({2} bytes)"}, {"PopClient.bad_pwd", "Login o password errati"}, {"PopClient.conn_end", "Fine del collegamento"}, {"PopClient.not_found", " non trovato"}, {"PopClient.press_enter", "Premere Enter per finire"}, {"PopClient.removing", "Rimozione di "}};

   public final Object[][] getContents() {
      return contents;
   }
}
