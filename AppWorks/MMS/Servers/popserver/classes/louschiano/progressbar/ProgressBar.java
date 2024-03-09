package louschiano.progressbar;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

public class ProgressBar extends Canvas {
   private int progressWidth;
   private int progressHeight;
   private float percentage;
   private Image offscreenImg;
   private Graphics offscreenG;
   private Color progressColor;
   private Color progressBackground;

   public ProgressBar(int var1, int var2) {
      this.progressColor = Color.red;
      this.progressBackground = Color.white;
      Font var3 = new Font("Dialog", 1, 15);
      this.setFont(var3);
      this.progressWidth = var1;
      this.progressHeight = var2;
      this.resize(var1, var2);
   }

   public ProgressBar(int var1, int var2, Color var3, Color var4, Color var5) {
      this.progressColor = Color.red;
      this.progressBackground = Color.white;
      Font var6 = new Font("Dialog", 1, 15);
      this.setFont(var6);
      this.progressWidth = var1;
      this.progressHeight = var2;
      this.progressColor = var4;
      this.progressBackground = var5;
      this.resize(var1, var2);
      this.setBackground(var3);
   }

   public void paint(Graphics var1) {
      boolean var2 = false;
      boolean var3 = false;
      byte var4 = 4;
      this.offscreenImg = this.createImage(this.progressWidth - var4, this.progressHeight - var4);
      this.offscreenG = this.offscreenImg.getGraphics();
      int var5 = this.offscreenImg.getWidth(this);
      int var6 = this.offscreenImg.getHeight(this);
      this.offscreenG.setColor(this.progressBackground);
      this.offscreenG.fillRect(0, 0, var5, var6);
      this.offscreenG.setColor(this.progressColor);
      this.offscreenG.fillRect(0, 0, (int)((float)var5 * this.percentage), var6);
      this.offscreenG.drawString(Integer.toString((int)(this.percentage * 100.0F)) + "%", var5 / 2 - 8, var6 / 2 + 5);
      this.offscreenG.clipRect(0, 0, (int)((float)var5 * this.percentage), var6);
      this.offscreenG.setColor(this.progressBackground);
      this.offscreenG.drawString(Integer.toString((int)(this.percentage * 100.0F)) + "%", var5 / 2 - 8, var6 / 2 + 5);
      var1.setColor(this.progressBackground);
      var1.draw3DRect(this.size().width / 2 - this.progressWidth / 2, 0, this.progressWidth - 1, this.progressHeight - 1, false);
      var1.drawImage(this.offscreenImg, var4 / 2, var4 / 2, this);
   }

   public void setBackGroundColor(Color var1) {
      this.progressBackground = var1;
   }

   public void setCanvasColor(Color var1) {
      this.setBackground(var1);
   }

   public void setProgressColor(Color var1) {
      this.progressColor = var1;
   }

   public void update(Graphics var1) {
      this.paint(var1);
   }

   public void updateBar(float var1) {
      this.percentage = var1;
      this.repaint();
   }
}
