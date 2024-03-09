package stefanolocati.ui;

import java.awt.Dialog;
import java.awt.Frame;

public class InfoBoard extends Dialog {
   protected MultiLineLabel board;
   protected String message;

   public InfoBoard(Frame var1) {
      super(var1, "InfoBoard", false);
      this.setSize(350, 200);
      this.setLocation(var1.getLocation());
      this.message = "";
      this.board = new MultiLineLabel(this.message);
      this.add(this.board);
   }

   public void appendMessage(String var1) {
      this.message = this.message + var1;
      this.board.setLabel(this.message);
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
      this.board.setLabel(var1);
   }
}
