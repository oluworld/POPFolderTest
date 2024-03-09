package stefanolocati.app.wolfspider;

import Acme.Widgets.ImageLabel;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.Image;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import louschiano.progressbar.ProgressBar;
import stefanolocati.cmd.popclient.PopClient;
import stefanolocati.cmd.popserver.PopServer;
import stefanolocati.resources.pop3.FileNames;
import stefanolocati.ui.MainFrame;

public class WolfSpider extends Applet {
   private Button getMailBT;
   private Button aboutBT;
   private TextArea logTA;
   private ProgressBar mboxPB;
   private ImageLabel serverGreenIL;
   private ImageLabel serverGrayIL;
   private boolean lightStatus = true;
   private String baseDir;
   private String mailToGet;
   private PopClient client;
   private Thread clientThread;
   private MainFrame mf;
   private ResourceBundle msg;

   public boolean action(Event var1, Object var2) {
      if (var1.target.equals(this.getMailBT)) {
         this.getMailBT.setEnabled(false);
         this.logTA.setText("");
         this.logTA.setBackground(Color.white);
         this.mboxPB.updateBar(0.0F);
         this.mboxPB.setVisible(true);
         this.client = new PopClient(this.baseDir, this.mailToGet, this);
         this.clientThread = new Thread(this.client);
         this.clientThread.start();
      } else {
         if (!var1.target.equals(this.aboutBT)) {
            return super.action(var1, var2);
         }

         Thread var3 = new Thread(new About(this.mf, this.baseDir));
         var3.start();
      }

      return true;
   }

   public void endOfJob() {
      this.getMailBT.setEnabled(true);
      this.logTA.setBackground(Color.lightGray);
      this.mboxPB.setVisible(false);
   }

   public ResourceBundle getResourceBundle() {
      return this.msg;
   }

   public void init() {
      if (this.msg == null) {
         Locale var1 = Locale.getDefault();
         this.msg = ResourceBundle.getBundle("stefanolocati.resources.pop3.Messages", var1);
      }

      this.baseDir = this.getParameter("BASEDIR");
      this.setLayout((LayoutManager)null);
      this.setBackground(Color.black);
      this.getMailBT = new Button(this.msg.getString("Interface.get_mail"));
      this.getMailBT.setBounds(30, 30, 100, 30);
      this.getMailBT.setBackground(Color.orange);
      this.add(this.getMailBT);
      this.aboutBT = new Button(this.msg.getString("Interface.about"));
      this.aboutBT.setBounds(30, 70, 100, 30);
      this.aboutBT.setBackground(Color.orange);
      this.add(this.aboutBT);
      this.logTA = new TextArea("", 6, 40, 1);
      this.logTA.setBounds(160, 80, 330, 150);
      this.logTA.setEditable(false);
      this.logTA.setBackground(Color.white);
      this.add(this.logTA);
      this.mboxPB = new ProgressBar(200, 20, Color.lightGray, Color.blue, Color.white);
      this.mboxPB.setBounds(220, 45, 200, 20);
      this.mboxPB.setVisible(false);
      this.add(this.mboxPB);
      Image var10 = Toolkit.getDefaultToolkit().getImage(this.baseDir + File.separator + FileNames.logo);
      ImageLabel var2 = new ImageLabel(var10);
      var2.setBounds(30, 120, 100, 92);
      this.add(var2);
      Image var3 = Toolkit.getDefaultToolkit().getImage(this.baseDir + File.separator + FileNames.green);
      this.serverGreenIL = new ImageLabel(var3);
      this.serverGreenIL.setBounds(475, 240, 15, 15);
      this.add(this.serverGreenIL);
      Image var4 = Toolkit.getDefaultToolkit().getImage(this.baseDir + File.separator + FileNames.gray);
      this.serverGrayIL = new ImageLabel(var4);
      this.serverGrayIL.setBounds(475, 240, 15, 15);
      this.serverGrayIL.setVisible(false);
      this.add(this.serverGrayIL);
      Label var5 = new Label(this.msg.getString("Interface.status_bar"));
      var5.setBounds(10, 232, 460, 30);
      var5.setForeground(Color.yellow);
      this.add(var5);
      if (this.baseDir == null) {
         this.logTA.append(this.msg.getString("Interface.no_basedir"));
         this.getMailBT.setEnabled(false);
      }

      this.mailToGet = this.getParameter("GETMAIL");
      if (this.mailToGet == null) {
         this.mailToGet = "remote_accounts";
      }

      PopServer var6 = new PopServer(this.baseDir);
      Thread var7 = new Thread(var6);
      var7.start();
      Updater var8 = new Updater(this.baseDir, this, var7);
      Thread var9 = new Thread(var8);
      var9.start();
   }

   public static void main(String[] var0) {
      WolfSpider var1 = new WolfSpider();
      Locale var2 = Locale.getDefault();
      var1.msg = ResourceBundle.getBundle("stefanolocati.resources.pop3.Messages", var2);
      var1.mf = new MainFrame(var1, var0, 500, 260);
      var1.mf.setTitle(var1.msg.getString("Interface.title"));
   }

   public void setLight(boolean var1) {
      if (var1) {
         this.serverGrayIL.setVisible(false);
         this.serverGreenIL.setVisible(true);
      } else {
         this.serverGreenIL.setVisible(false);
         this.serverGrayIL.setVisible(true);
      }

   }

   public void setMessage(String var1) {
      this.logTA.append(var1 + System.getProperty("line.separator"));
   }

   public void setProgress(double var1) {
      this.mboxPB.updateBar((float)var1);
   }
}
