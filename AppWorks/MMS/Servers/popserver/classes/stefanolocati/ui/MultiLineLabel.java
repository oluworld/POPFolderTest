package stefanolocati.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.StringTokenizer;

public class MultiLineLabel extends Canvas {
   public static final int LEFT = 0;
   public static final int CENTER = 1;
   public static final int RIGHT = 2;
   protected String[] lines;
   protected int num_lines;
   protected int margin_width;
   protected int margin_height;
   protected int line_height;
   protected int line_ascent;
   protected int[] line_widths;
   protected int max_width;
   protected int alignment;

   public MultiLineLabel(String var1) {
      this(var1, 10, 10, 0);
   }

   public MultiLineLabel(String var1, int var2) {
      this(var1, 10, 10, var2);
   }

   public MultiLineLabel(String var1, int var2, int var3) {
      this(var1, var2, var3, 0);
   }

   public MultiLineLabel(String var1, int var2, int var3, int var4) {
      this.alignment = 0;
      this.newLabel(var1);
      this.margin_width = var2;
      this.margin_height = var3;
      this.alignment = var4;
   }

   public void addNotify() {
      super.addNotify();
      this.measure();
   }

   public int getAlignment() {
      return this.alignment;
   }

   public int getMarginHeight() {
      return this.margin_height;
   }

   public int getMarginWidth() {
      return this.margin_width;
   }

   public Dimension getMinimumSize() {
      return new Dimension(this.max_width, this.num_lines * this.line_height);
   }

   public Dimension getPreferredSize() {
      return new Dimension(this.max_width + 2 * this.margin_width, this.num_lines * this.line_height + 2 * this.margin_height);
   }

   protected void measure() {
      FontMetrics var1 = this.getFontMetrics(this.getFont());
      if (var1 != null) {
         this.line_height = var1.getHeight();
         this.line_ascent = var1.getAscent();
         this.max_width = 0;

         for(int var2 = 0; var2 < this.num_lines; ++var2) {
            this.line_widths[var2] = var1.stringWidth(this.lines[var2]);
            if (this.line_widths[var2] > this.max_width) {
               this.max_width = this.line_widths[var2];
            }
         }

      }
   }

   protected void newLabel(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, "\n");
      this.num_lines = var2.countTokens();
      this.lines = new String[this.num_lines];
      this.line_widths = new int[this.num_lines];

      for(int var3 = 0; var3 < this.num_lines; ++var3) {
         this.lines[var3] = var2.nextToken();
      }

   }

   public void paint(Graphics var1) {
      Dimension var4 = this.getSize();
      int var3 = this.line_ascent + (var4.height - this.num_lines * this.line_height) / 2;

      for(int var5 = 0; var5 < this.num_lines; var3 += this.line_height) {
         int var2;
         switch (this.alignment) {
            case 0:
               var2 = this.margin_width;
               break;
            case 1:
            default:
               var2 = (var4.width - this.line_widths[var5]) / 2;
               break;
            case 2:
               var2 = var4.width - this.margin_width - this.line_widths[var5];
         }

         var1.drawString(this.lines[var5], var2, var3);
         ++var5;
      }

   }

   public void setAlignment(int var1) {
      this.alignment = var1;
      this.repaint();
   }

   public void setFont(Font var1) {
      super.setFont(var1);
      this.measure();
      this.repaint();
   }

   public void setForeground(Color var1) {
      super.setForeground(var1);
      this.repaint();
   }

   public void setLabel(String var1) {
      this.newLabel(var1);
      this.measure();
      this.repaint();
   }

   public void setMarginHeight(int var1) {
      this.margin_height = var1;
      this.repaint();
   }

   public void setMarginWidth(int var1) {
      this.margin_width = var1;
      this.repaint();
   }
}
