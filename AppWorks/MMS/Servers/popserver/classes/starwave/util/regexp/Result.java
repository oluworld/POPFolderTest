package starwave.util.regexp;

public class Result {
   State state;

   Result(State var1) {
      this.state = var1;
   }

   public String getMatch() {
      return this.getMatch(0);
   }

   public String getMatch(int var1) {
      return this.state.getGroupString(var1);
   }

   public int getMatchEnd() {
      return this.getMatchEnd(0);
   }

   public int getMatchEnd(int var1) {
      return this.state.getGroupEnd(var1);
   }

   public int getMatchStart() {
      return this.getMatchStart(0);
   }

   public int getMatchStart(int var1) {
      return this.state.getGroupStart(var1);
   }

   public String toString() {
      return this.getClass().getName() + "[" + this.getMatchStart(0) + ", " + this.getMatchEnd(0) + "]";
   }
}
