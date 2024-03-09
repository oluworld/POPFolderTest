package Acme.Widgets;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;

public class BorderPanel extends Panel {
   public static final int SOLID = 0;
   public static final int RAISED = 1;
   public static final int LOWERED = 2;
   public static final int IN = 3;
   public static final int OUT = 4;
   private int type;
   private int thickness;
   private Panel innerPanel = null;

   public BorderPanel(int var1) {
      this.type = var1;
      switch (var1) {
         case 0:
            this.thickness = 2;
            break;
         case 1:
            this.thickness = 2;
            break;
         case 2:
            this.thickness = 2;
            break;
         case 3:
            this.thickness = 2;
            break;
         case 4:
            this.thickness = 2;
      }

      this.build();
   }

   public BorderPanel(int var1, int var2) {
      this.type = var1;
      this.thickness = var2;
      this.build();
   }

   public Component add(Component var1) {
      return this.innerPanel == null ? super.add(var1) : this.innerPanel.add(var1);
   }

   public Component add(Component var1, int var2) {
      return this.innerPanel == null ? super.add(var1, var2) : this.innerPanel.add(var1, var2);
   }

   public Component add(String var1, Component var2) {
      return this.innerPanel == null ? super.add(var1, var2) : this.innerPanel.add(var1, var2);
   }

   private void build() {
      Panel var1 = new Panel();
      GridBagLayout var2 = new GridBagLayout();
      this.setLayout(var2);
      GridBagConstraints var3 = new GridBagConstraints();
      var3.fill = 1;
      var3.weightx = var3.weighty = 1.0;
      var3.insets = new Insets(this.thickness, this.thickness, this.thickness, this.thickness);
      var2.setConstraints(var1, var3);
      this.add(var1, -1);
      this.innerPanel = var1;
   }

   public int countComponents() {
      return this.innerPanel == null ? super.countComponents() : this.innerPanel.countComponents();
   }

   public Component getComponent(int var1) {
      return this.innerPanel == null ? super.getComponent(var1) : this.innerPanel.getComponent(var1);
   }

   public LayoutManager getLayout() {
      return this.innerPanel == null ? super.getLayout() : this.innerPanel.getLayout();
   }

   public Insets insets(int var1) {
      return this.innerPanel == null ? super.insets() : this.innerPanel.insets();
   }

   public void paint(Graphics var1) {
      Dimension var2 = this.size();
      var1.setColor(this.getBackground());
      int var3;
      switch (this.type) {
         case 0:
            var1.setColor(this.getForeground());

            for(var3 = 0; var3 < this.thickness; ++var3) {
               var1.drawRect(var3, var3, var2.width - var3 * 2 - 1, var2.height - var3 * 2 - 1);
            }

            return;
         case 1:
            for(var3 = 0; var3 < this.thickness; ++var3) {
               var1.draw3DRect(var3, var3, var2.width - var3 * 2 - 1, var2.height - var3 * 2 - 1, true);
            }

            return;
         case 2:
            for(var3 = 0; var3 < this.thickness; ++var3) {
               var1.draw3DRect(var3, var3, var2.width - var3 * 2 - 1, var2.height - var3 * 2 - 1, false);
            }

            return;
         case 3:
            var1.draw3DRect(0, 0, var2.width - 1, var2.height - 1, false);
            var1.draw3DRect(this.thickness - 1, this.thickness - 1, var2.width - this.thickness * 2 + 1, var2.height - this.thickness * 2 + 1, true);
            break;
         case 4:
            var1.draw3DRect(0, 0, var2.width - 1, var2.height - 1, true);
            var1.draw3DRect(this.thickness - 1, this.thickness - 1, var2.width - this.thickness * 2 + 1, var2.height - this.thickness * 2 + 1, false);
      }

   }

   public void remove(Component var1) {
      if (this.innerPanel == null) {
         super.remove(var1);
      } else {
         this.innerPanel.remove(var1);
      }

   }

   public void removeAll() {
      if (this.innerPanel == null) {
         super.removeAll();
      } else {
         this.innerPanel.removeAll();
      }

   }

   public void setLayout(LayoutManager var1) {
      if (this.innerPanel == null) {
         super.setLayout(var1);
      } else {
         this.innerPanel.setLayout(var1);
      }

   }
}
