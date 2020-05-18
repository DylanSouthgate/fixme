package router;
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*;
import java.util.concurrent.*;

public class Router 
{
	public ServerSocket marketServerSocket;
    public Socket marketClientSocket;
    public ServerSocket brokerServerSocket;
    public Socket brokerClientSocket;
    public PrintWriter pr;
    public int brokerid = 100000 + (int)(Math.random() * ((499999 - 100000) + 1));
    public int marketid = 500000 + (int)(Math.random() * ((999999 - 500000) + 1));
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        new Router().startServer();
    }
    public void startServer() {

        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    marketServerSocket = new ServerSocket(5000);
                    System.out.println("Waiting for Broker to connect... 5000");
                    while (true) {
                        marketClientSocket = marketServerSocket.accept();
                        clientProcessingPool.submit(new ClientTask(marketClientSocket , brokerClientSocket));

                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };

        Runnable serverTask1 = new Runnable() {
            @Override
            public void run() {
                try {
                    brokerServerSocket = new ServerSocket(5001);
                    System.out.println("Waiting for Market to connect... on 5001");
                    while (true) {
                        brokerClientSocket = brokerServerSocket.accept();
                        clientProcessingPool.submit(new ClientTask1(marketClientSocket , brokerClientSocket));

                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();
        Thread serverThread1 = new Thread(serverTask1);
        serverThread1.start();
    }

    private class ClientTask implements Runnable {

        private ClientTask(Socket test, Socket test2) {
        }

        @Override
        public void run() {
            System.out.println("Got a market !");
            idgen(marketid, marketClientSocket);
            try {
                while(true)
                {
                        InputStreamReader in = new InputStreamReader(marketClientSocket.getInputStream());
                        BufferedReader bf = new BufferedReader(in);
                        String str = bf.readLine();
                        System.out.println("Market["+marketid+"] :" + str);
                        pr = new PrintWriter(brokerClientSocket.getOutputStream());
                        pr.println(str);
                        pr.flush();
                 }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            try {
                marketClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ClientTask1 implements Runnable {

        private ClientTask1(Socket test, Socket test2) {
        }

        @Override
        public void run() {
            System.out.println("Got a broker !");
            idgen(brokerid, brokerClientSocket);
            try {
                while(true)
                {
                        InputStreamReader in = new InputStreamReader(brokerClientSocket.getInputStream());
                        BufferedReader bf = new BufferedReader(in);
                        String str = bf.readLine();
                        System.out.println("Broker["+brokerid+"] :" + str);
                        pr = new PrintWriter(marketClientSocket.getOutputStream());
                        pr.println(str);
                        pr.flush();
                 }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            try {
                brokerClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void idgen (int str, Socket test){
        try {
        pr = new PrintWriter(test.getOutputStream());
            pr.println("MarketID="+marketid+"|BrokerID="+brokerid+"");
            pr.flush();
            } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
    }
}
