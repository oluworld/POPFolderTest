package stefanolocati.ui;

import Acme.Widgets.ImageLabel;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OkDialog extends Dialog {
   Frame parent;

   public OkDialog(Frame var1, Image var2, String var3) {
      super(var1, true);
      this.parent = var1;
      this.build(var2, var3);
   }

   public OkDialog(Frame var1, String var2, Image var3, String var4) {
      super(var1, var2, true);
      this.parent = var1;
      this.build(var3, var4);
   }

   private void build(Image var1, String var2) {
      this.setSize(400, 100);
      this.setResizable(false);
      GridBagLayout var3 = new GridBagLayout();
      this.setLayout(var3);
      GridBagConstraints var4 = new GridBagConstraints();
      var4.insets = new Insets(5, 5, 5, 5);
      ImageLabel var5 = new ImageLabel(var1);
      var3.setConstraints(var5, var4);
      this.add(var5);
      MultiLineLabel var6 = new MultiLineLabel(var2);
      var4.gridwidth = 0;
      var3.setConstraints(var6, var4);
      this.add(var6);
      Button var7 = new Button("  Ok  ");
      var3.setConstraints(var7, var4);
      this.add(var7);
      var7.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            OkDialog.this.setVisible(false);
            OkDialog.this.dispose();
         }
      });
   }

   public void setVisible(boolean var1) {
      if (var1) {
         this.show();
      } else {
         super.setVisible(false);
      }

   }

   /** @deprecated */
   public void show() {
      this.pack();
      this.validate();
      Point var1 = this.parent.getLocation();
      Dimension var2 = this.parent.getSize();
      Dimension var3 = this.getSize();
      int var4 = var1.x + var2.width / 2 - var3.width / 2;
      int var5 = var1.y + var2.height / 2 - var3.height / 2;
      if (var4 < 0) {
         var4 = 0;
      }

      if (var5 < 0) {
         var5 = 0;
      }

      this.setLocation(var4, var5);
      super.show();
   }
}
