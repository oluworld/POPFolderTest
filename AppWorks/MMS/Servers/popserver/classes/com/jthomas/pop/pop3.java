package com.jthomas.pop;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

public class pop3 {
   protected final int AUTHORIZATION = 1;
   protected final int TRANSACTION = 2;
   protected final int UPDATE = 3;
   protected int _TotalMsgs = 0;
   protected int _TotalSize = 0;
   protected boolean _StatusOK = false;
   protected int State = 0;
   protected String LastCmd;
   protected String Host = null;
   protected int Port = 110;
   protected String User = null;
   protected String Password = null;
   protected Socket server;
   protected BufferedReader serverInputStream;
   protected DataOutputStream serverOutputStream;
   private boolean debugOn = false;

   public pop3() {
   }

   public pop3(String var1, String var2, String var3) {
      this.Host = var1;
      this.User = var2;
      this.Password = var3;
   }

   String[] Parse(popStatus var1, int var2) {
      String[] var3 = null;
      var1._OK = false;
      String var4 = var1._Response;
      if (var4 != null) {
         int var5 = 0;
         if (var4.trim().startsWith("+OK")) {
            var1._OK = true;
         } else {
            this.debug(var4);
         }

         StringTokenizer var7 = new StringTokenizer(var4);
         int var6;
         if (var2 == -1) {
            var6 = var7.countTokens();
         } else {
            var6 = var2;
         }

         for(var3 = new String[var6 + 1]; var7.hasMoreTokens() && var5 < var6; ++var5) {
            var3[var5] = new String(var7.nextToken());
         }

         if (var7.hasMoreTokens()) {
            StringBuffer var8 = new StringBuffer(var7.nextToken());

            while(var7.hasMoreTokens()) {
               var8.append(" " + var7.nextToken());
            }

            var3[var6] = new String(var8);
         }
      }

      return var3;
   }

   public synchronized popStatus appendFile(String var1, int var2) {
      popStatus var3 = new popStatus();
      this.send("RETR " + var2);
      this.recvN(var3);
      this.Parse(var3, 2);
      if (var3._OK) {
         RandomAccessFile var6;
         try {
            var6 = new RandomAccessFile(var1, "rw");
         } catch (IOException var9) {
            var3._OK = false;
            var3._Response = "-ERR File open failed";
            return var3;
         }

         Date var7 = new Date();
         String[] var4 = var3.Responses();

         try {
            var6.seek(var6.length());
            var6.writeBytes("From - " + var7.toString() + "\r\n");

            for(int var8 = 0; var8 < var4.length; ++var8) {
               var6.writeBytes(var4[var8] + "\r\n");
            }

            var6.close();
         } catch (IOException var10) {
            var3._OK = false;
            var3._Response = "-ERR File write failed";
            return var3;
         }
      }

      var3._OK = true;
      return var3;
   }

   public synchronized void close() {
      this.debug("Closing socket");

      try {
         this.server.close();
         this.State = 0;
      } catch (IOException var1) {
         this.debug("Failure in server.close()");
      }

   }

   public synchronized popStatus connect() {
      popStatus var1 = new popStatus();
      this.debug("Connecting to " + this.Host + " at port " + this.Port);
      if (this.Host == null) {
         var1._Response = "-ERR Host not specified";
         var1._OK = false;
         return var1;
      } else {
         try {
            this.server = new Socket(this.Host, this.Port);
            if (this.server == null) {
               this.debug("-ERR Error while connecting to POP3 server");
               var1._OK = false;
               var1._Response = "-ERR Error while connecting to POP3 server";
            } else {
               this.debug("Connected");
               this.serverInputStream = new BufferedReader(new InputStreamReader(this.server.getInputStream()));
               if (this.serverInputStream == null) {
                  this.debug("Failed to setup an input stream.");
                  var1._OK = false;
                  var1._Response = "-ERR Error setting up input stream";
                  this.server = null;
               }

               this.serverOutputStream = new DataOutputStream(this.server.getOutputStream());
               if (this.serverOutputStream == null) {
                  this.debug("Failed to setup an output stream.");
                  var1._OK = false;
                  var1._Response = "-ERR Error setting up output stream";
                  this.server = null;
               }
            }
         } catch (Exception var4) {
            String var3 = "Exception! " + var4.toString();
            this.debug(var3);
            var1._OK = false;
            var1._Response = var3;
            this.server = null;
         }

         if (this.server != null) {
            var1._OK = true;
            this._StatusOK = true;
            var1._Response = this.recv();
            this.Parse(var1, 2);
            this.debug("Response=" + var1._Response);
         }

         if (var1._OK) {
            this.State = 1;
         }

         return var1;
      }
   }

   public popStatus connect(String var1) {
      this.Host = var1;
      return this.connect();
   }

   public popStatus connect(String var1, int var2) {
      this.Host = var1;
      this.Port = var2;
      return this.connect();
   }

   public void debug(String var1) {
      if (this.debugOn) {
         System.err.println(var1);
      }

   }

   public synchronized popStatus dele(int var1) {
      popStatus var2 = new popStatus();
      this.send("DELE " + var1);
      var2._Response = this.recv();
      this.Parse(var2, 2);
      return var2;
   }

   public int get_TotalMsgs() {
      return this._TotalMsgs;
   }

   public int get_TotalSize() {
      return this._TotalSize;
   }

   public synchronized popStatus list() {
      popStatus var1 = new popStatus();
      this.send("LIST");
      this.recvN(var1);
      this.Parse(var1, 2);
      return var1;
   }

   public synchronized popStatus list(int var1) {
      popStatus var2 = new popStatus();
      boolean var3 = false;
      this.send("LIST " + var1);
      var2._Response = this.recv();
      this.Parse(var2, 2);
      return var2;
   }

   public synchronized popStatus login() {
      popStatus var1 = new popStatus();
      if (this.User != null && this.Password != null) {
         if (this.server != null) {
            this.send("USER " + this.User);
            var1._Response = this.recv();
            this.Parse(var1, 1);
            if (var1._OK) {
               this.send("PASS " + this.Password);
               var1._Response = this.recv();
               this.Parse(var1, 1);
               if (var1._OK) {
                  this.State = 2;
                  popStatus var2 = this.stat();
               }
            }
         }

         return var1;
      } else {
         var1._Response = "-ERR Userid or Password not specified";
         return var1;
      }
   }

   public popStatus login(String var1, String var2) {
      this.User = var1;
      this.Password = var2;
      return this.login();
   }

   public synchronized popStatus noop() {
      popStatus var1 = new popStatus();
      this.send("NOOP");
      var1._Response = this.recv();
      this.Parse(var1, 2);
      return var1;
   }

   public synchronized popStatus quit() {
      popStatus var1 = new popStatus();
      this.send("QUIT");
      this.State = 3;
      var1._Response = this.recv();
      this.Parse(var1, 2);
      this.close();
      return var1;
   }

   String recv() {
      String var1 = "";
      if (!this._StatusOK) {
         var1 = "-ERR Failed sending command to server";
         return var1;
      } else {
         try {
            var1 = this.serverInputStream.readLine();
            this.debug("<<" + var1);
         } catch (IOException var3) {
            System.err.println("Caught exception while reading");
            var1 = "-ERR Caught IOException while reading from server";
         } catch (Exception var4) {
            System.err.println("Unexpected exception: " + var4.toString());
            var1 = "-ERR Unexpected exception while reading from server";
         }

         if (var1 == null) {
            this.debug("Read a null line from server");
            var1 = "-ERR <NULL>";
         }

         if (var1.trim().startsWith("-ERR")) {
            this.debug("Result from server has error!");
            this.debug("Sent:     '" + this.LastCmd + "'");
            this.debug("Received: '" + var1 + "'");
            return var1;
         } else if (var1.trim().startsWith("+OK")) {
            return var1;
         } else {
            this.debug("Received strange response");
            this.debug("'" + var1 + "'");
            var1 = "-ERR Invalid response";
            return var1;
         }
      }
   }

   void recvN(popStatus var1) {
      this.debug("entered recvN");
      Vector var2 = new Vector(100, 100);
      String var3 = "";
      Object var4 = null;

      try {
         boolean var5 = false;
         int var6 = 0;

         while(!var5) {
            var3 = this.serverInputStream.readLine();
            ++var6;
            this.debug("<<" + var3.length() + " '" + var3 + "'");
            if (var6 == 1) {
               if (var3.trim().startsWith("-ERR ")) {
                  this.debug("Result from server has error!");
                  this.debug("Sent:     '" + this.LastCmd + "'");
                  this.debug("Received: '" + var3 + "'");
                  var5 = true;
                  var1._Response = var3;
               } else if (var3.trim().startsWith("+OK")) {
                  var1._Response = var3;
               } else {
                  this.debug("Received strange response");
                  this.debug("'" + var3 + "'");
                  var5 = true;
                  var1._Response = "-ERR Invalid response";
               }
            } else if (var3.startsWith(".")) {
               if (var3.length() == 1) {
                  var5 = true;
               } else {
                  var2.addElement(var3.substring(1));
               }
            } else {
               var2.addElement(var3);
            }
         }
      } catch (IOException var7) {
         System.err.println("Caught exception while reading");
         var1._Response = "-ERR Caught IOException while reading from server";
      } catch (Exception var8) {
         System.err.println("Unexpected exception: " + var8.toString());
         var1._Response = "-ERR Unexpected exception while reading from server";
      }

      var1._Responses = new String[var2.size()];
      var2.copyInto(var1._Responses);
   }

   public synchronized popStatus retr(int var1) {
      popStatus var2 = new popStatus();
      this.send("RETR " + var1);
      this.recvN(var2);
      this.Parse(var2, 2);
      return var2;
   }

   public synchronized popStatus rset() {
      popStatus var1 = new popStatus();
      this.send("RSET");
      var1._Response = this.recv();
      this.Parse(var1, 2);
      return var1;
   }

   void send(String var1) {
      this.debug(">> " + var1);
      this.LastCmd = var1;

      try {
         this.serverOutputStream.writeBytes(var1 + "\r\n");
         this._StatusOK = true;
      } catch (IOException var3) {
         System.err.println("Caught exception while sending command to server");
         this._StatusOK = false;
      } catch (Exception var4) {
         System.err.println("Send: Unexpected exception: " + var4.toString());
         this._StatusOK = false;
      }

   }

   public void setDebugOn(boolean var1) {
      this.debugOn = var1;
   }

   public synchronized popStatus stat() {
      popStatus var1 = new popStatus();
      if (this.State != 2) {
         var1._Response = "-ERR Server not in transaction mode";
         return var1;
      } else {
         this.send("STAT");
         var1._Response = this.recv();
         String[] var2 = this.Parse(var1, 4);
         if (var1._OK) {
            this._TotalMsgs = Convert.toInt(var2[1]);
            this._TotalSize = Convert.toInt(var2[2]);
         }

         return var1;
      }
   }

   public synchronized popStatus top(int var1, int var2) {
      popStatus var3 = new popStatus();
      this.send("TOP " + var1 + " " + var2);
      this.recvN(var3);
      this.Parse(var3, 2);
      return var3;
   }

   public synchronized popStatus uidl() {
      popStatus var1 = new popStatus();
      this.send("UIDL");
      this.recvN(var1);
      this.Parse(var1, 2);
      return var1;
   }

   public synchronized popStatus uidl(int var1) {
      popStatus var2 = new popStatus();
      this.send("UIDL " + var1);
      var2._Response = this.recv();
      this.Parse(var2, 2);
      return var2;
   }
}
