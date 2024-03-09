package stefanolocati.resources.pop3;

import java.io.File;

public interface FileNames {
   String configDir = "config";
   String logDir = "logs";
   String clientLogDir = "logs" + File.separator + "client";
   String serverLogDir = "logs" + File.separator + "server";
   String formsLogDir = "logs" + File.separator + "server";
   String decodedFormsDir = "forms";
   String remoteAccounts = "remote_accounts";
   String localAccounts = "config" + File.separator + "local_accounts";
   String rules = "config" + File.separator + "rules";
   String imagesDir = "images" + File.separator;
   String logo = imagesDir + "logo.jpg";
   String green = imagesDir + "green.gif";
   String gray = imagesDir + "gray.gif";
   String wolfSpider = imagesDir + "wolfspider.gif";
}
