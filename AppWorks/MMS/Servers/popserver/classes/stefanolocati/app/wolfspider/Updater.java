package stefanolocati.app.wolfspider;

import stefanolocati.cmd.popserver.PopServer;

class Updater extends Thread {
   WolfSpider wolfSpider;
   Thread thread;
   String baseDir;

   Updater(String var1, WolfSpider var2, Thread var3) {
      this.thread = var3;
      this.wolfSpider = var2;
      this.baseDir = var1;
   }

   public void run() {
      while(true) {
         try {
            Thread.sleep(2000L);
         } catch (InterruptedException var5) {
         }

         boolean var1 = this.thread.isAlive();
         this.wolfSpider.setLight(var1);
         if (!var1) {
            PopServer var2 = new PopServer(this.baseDir);
            this.thread = new Thread(var2);
            this.thread.start();

            try {
               Thread.sleep(2000L);
            } catch (InterruptedException var4) {
            }

            this.wolfSpider.setLight(this.thread.isAlive());
         }

         try {
            Thread.sleep(10000L);
         } catch (InterruptedException var3) {
         }
      }
   }
}
