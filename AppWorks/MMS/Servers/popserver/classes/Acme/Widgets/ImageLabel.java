package Acme.Widgets;

import Acme.GuiUtils;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class ImageLabel extends Canvas {
   private Image image;
   private int width = -1;
   private int height = -1;

   public ImageLabel(Image var1) {
      this.setImage(var1);
   }

   private void changeSize(int var1, int var2) {
      if (var1 != -1 && var2 != -1) {
         this.width = var1;
         this.height = var2;
         this.resize(this.width, this.height);
         GuiUtils.packWindow(this);
      }
   }

   public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      boolean var7 = (var2 & 64) != 0;
      boolean var8 = (var2 & 128) != 0;
      boolean var9 = (var2 & 1) != 0;
      boolean var10 = (var2 & 2) != 0;
      boolean var11 = (var2 & 4) != 0;
      boolean var12 = (var2 & 8) != 0;
      boolean var13 = (var2 & 16) != 0;
      boolean var14 = (var2 & 32) != 0;
      if (!var7 && !var8) {
         if (var9 || var10 || var13 || var14) {
            this.changeSize(var5, var6);
         }

         if (!var13 && !var14) {
            if (var12) {
               this.repaint(100L);
            }

            return true;
         } else {
            this.repaint();
            return false;
         }
      } else {
         this.setImage(GuiUtils.brokenIcon(this));
         return false;
      }
   }

   public Dimension minimumSize() {
      return new Dimension(this.width, this.height);
   }

   public void paint(Graphics var1) {
      var1.drawImage(this.image, 0, 0, this);
   }

   public Dimension preferredSize() {
      return this.minimumSize();
   }

   public void setImage(Image var1) {
      this.invalidate();
      this.image = var1;
      this.width = this.height = -1;
      this.changeSize(var1.getWidth(this), var1.getHeight(this));
      this.repaint();
   }
}
