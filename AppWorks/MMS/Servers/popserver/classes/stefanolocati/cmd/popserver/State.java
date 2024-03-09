package stefanolocati.cmd.popserver;

interface State {
   int DISCONNECTED = 0;
   int WAITUSER = 1;
   int WAITPASSWORD = 2;
   int TRANSACTION = 3;
   int QUIT = 4;
}
