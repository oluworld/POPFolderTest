package stefanolocati.ui;

import Acme.Widgets.BorderPanel;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.applet.AppletAudioClip;

public class MainFrame extends Frame implements Runnable, AppletStub, AppletContext {
   private String[] args = null;
   private static int instances = 0;
   private String name;
   private boolean barebones = false;
   private Applet applet;
   private Label label = null;
   private Dimension appletSize;
   private static final String PARAM_PROP_PREFIX = "parameter.";

   public MainFrame(Applet var1, int var2, int var3) {
      this.build(var1, (String[])null, var2, var3);
   }

   public MainFrame(Applet var1, String[] var2) {
      this.build(var1, var2, -1, -1);
   }

   public MainFrame(Applet var1, String[] var2, int var3, int var4) {
      this.build(var1, var2, var3, var4);
   }

   public void appletResize(int var1, int var2) {
      Dimension var3 = this.getSize();
      var3.width += var1 - this.appletSize.width;
      var3.height += var2 - this.appletSize.height;
      this.setSize(var3);
      this.appletSize = this.applet.getSize();
   }

   private void build(Applet var1, String[] var2, int var3, int var4) {
      ++instances;
      this.applet = var1;
      this.args = var2;
      var1.setStub(this);
      this.name = var1.getClass().getName();
      this.setTitle(this.name);
      Properties var5 = System.getProperties();
      var5.put("browser", "Acme.MainFrame");
      var5.put("browser.version", "18jul98");
      var5.put("browser.vendor", "Acme Laboratories - modified by Stefano Locati");
      var5.put("browser.vendor.url", "http://www.acme.com/ - http://www.geocities.com/SunsetStrip/Studio/4994/java.html");
      if (var2 != null) {
         parseArgs(var2, var5);
      }

      String var6 = this.getParameter("width");
      if (var6 != null) {
         var3 = Integer.parseInt(var6);
      }

      String var7 = this.getParameter("height");
      if (var7 != null) {
         var4 = Integer.parseInt(var7);
      }

      if (var3 != -1 && var4 != -1) {
         String var8 = this.getParameter("barebones");
         if (var8 != null && var8.equals("true")) {
            this.barebones = true;
         }

         this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
               System.exit(0);
            }
         });
         if (!this.barebones) {
            MenuBar var9 = new MenuBar();
            Menu var10 = new Menu("Applet");
            MenuItem var11 = new MenuItem("Restart");
            var10.add(var11);
            var11.addActionListener(new Restart());
            var11 = new MenuItem("Clone");
            var10.add(var11);
            var11.addActionListener(new Clone());
            var11 = new MenuItem("Close");
            var10.add(var11);
            var11.addActionListener(new Close());
            var11 = new MenuItem("Quit");
            var10.add(var11);
            var11.addActionListener(new Quit());
            var9.add(var10);
            this.setMenuBar(var9);
         }

         this.setLayout(new BorderLayout());
         this.add("Center", var1);
         if (!this.barebones) {
            BorderPanel var12 = new BorderPanel(1);
            var12.setLayout(new BorderLayout());
            this.label = new Label("");
            var12.add("Center", this.label);
            this.add("South", var12);
         }

         this.pack();
         this.validate();
         this.appletSize = var1.getSize();
         var1.resize(var3, var4);
         this.show();
         this.run();
      } else {
         System.err.println("Width and height must be specified.");
      }
   }

   public Applet getApplet(String var1) {
      return var1.equals(this.name) ? this.applet : null;
   }

   public AppletContext getAppletContext() {
      return this;
   }

   public Enumeration getApplets() {
      Vector var1 = new Vector();
      var1.addElement(this.applet);
      return var1.elements();
   }

   public AudioClip getAudioClip(URL var1) {
      return new AppletAudioClip(var1);
   }

   public URL getCodeBase() {
      String var1 = System.getProperty("java.class.path");
      StringTokenizer var2 = new StringTokenizer(var1, ":");

      while(var2.hasMoreElements()) {
         String var3 = (String)var2.nextElement();
         String var4 = var3 + File.separatorChar + this.name + ".class";
         File var5 = new File(var4);
         if (var5.exists()) {
            String var6 = var3.replace(File.separatorChar, '/');

            try {
               return new URL("file:" + var6 + "/");
            } catch (MalformedURLException var7) {
               return null;
            }
         }
      }

      return null;
   }

   public URL getDocumentBase() {
      String var1 = System.getProperty("user.dir");
      String var2 = var1.replace(File.separatorChar, '/');

      try {
         return new URL("file:" + var2 + "/");
      } catch (MalformedURLException var3) {
         return null;
      }
   }

   public Image getImage(URL var1) {
      Toolkit var2 = Toolkit.getDefaultToolkit();

      try {
         ImageProducer var3 = (ImageProducer)var1.getContent();
         return var2.createImage(var3);
      } catch (IOException var4) {
         return null;
      }
   }

   public String getParameter(String var1) {
      return System.getProperty("parameter." + var1.toLowerCase());
   }

   public boolean isActive() {
      return true;
   }

   private static void parseArgs(String[] var0, Properties var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         String var3 = var0[var2];
         int var4 = var3.indexOf(61);
         if (var4 == -1) {
            var1.put("parameter." + var3.toLowerCase(), "");
         } else {
            var1.put("parameter." + var3.substring(0, var4).toLowerCase(), var3.substring(var4 + 1));
         }
      }

   }

   public void run() {
      this.showStatus(this.name + " initializing...");
      this.applet.init();
      this.validate();
      this.showStatus(this.name + " starting...");
      this.applet.start();
      this.validate();
      this.showStatus(this.name + " running...");
   }

   public void showDocument(URL var1) {
   }

   public void showDocument(URL var1, String var2) {
   }

   public void showStatus(String var1) {
      if (this.label != null) {
         this.label.setText(var1);
      }

   }

   class Restart implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         MainFrame.this.applet.stop();
         MainFrame.this.applet.destroy();
         Thread var2 = new Thread(MainFrame.this);
         var2.start();
      }
   }

   class Clone implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         try {
            new MainFrame((Applet)MainFrame.this.applet.getClass().newInstance(), MainFrame.this.args, MainFrame.this.appletSize.width, MainFrame.this.appletSize.height);
         } catch (IllegalAccessException var3) {
            MainFrame.this.showStatus(var3.getMessage());
         } catch (InstantiationException var4) {
            MainFrame.this.showStatus(var4.getMessage());
         }

      }
   }

   class Close implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         MainFrame.this.setVisible(false);
         MainFrame.this.remove(MainFrame.this.applet);
         MainFrame.this.applet.stop();
         MainFrame.this.applet.destroy();
         if (MainFrame.this.label != null) {
            MainFrame.this.remove(MainFrame.this.label);
         }

         MainFrame.this.dispose();
         MainFrame.instances = MainFrame.instances - 1;
         if (MainFrame.instances == 0) {
            System.exit(0);
         }

      }
   }

   class Quit implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         System.exit(0);
      }
   }
}
