package stefanolocati.app.wolfspider;

import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import stefanolocati.resources.pop3.FileNames;
import stefanolocati.text.Justify;
import stefanolocati.ui.OkDialog;

class About extends Thread {
   Frame parent;
   String baseDir;

   About(Frame var1, String var2) {
      this.parent = var1;
      this.baseDir = var2;
   }

   public void run() {
      Image var1 = Toolkit.getDefaultToolkit().getImage(this.baseDir + File.separator + FileNames.wolfSpider);
      Justify var2 = new Justify("Copyright © Apr 1998  Stefano Locati slocati@geocities.com http://www.geocities.com/SunsetStrip/Studio/4994/ \n \nThe magnificent nebula 30 Doradus, called also Wolf Spider. A huge matrix of stars a thousand light years wide.");
      var2.setLineLength(49);
      OkDialog var3 = new OkDialog(this.parent, "About Wolf Spider", var1, var2.getJustified());
      var3.setVisible(true);
   }
}
