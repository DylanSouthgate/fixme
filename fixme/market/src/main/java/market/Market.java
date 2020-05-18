package market;

import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

import java.util.concurrent.*;
public class Market 
{
	String[] instruments = {"Copper", "Aluminium", "Nickel", "Zinc", "Lead", "Tin"};
    public static final int[] inventory = {30, 60, 90, 120,150, 180};
	public static Socket s;
	public static Scanner myObj;
	public static void main(String[] args) throws IOException 
	{ 
		try
		{ 
			myObj = new Scanner(System.in);
			Scanner scn = new Scanner(System.in); 
			InetAddress ip = InetAddress.getByName("localhost"); 
			s = new Socket(ip, 5000); 
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

    	Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                    	PrintWriter pr = new PrintWriter(s.getOutputStream());
                    	String userName = myObj.nextLine();
                    	pr.println(userName);
                    	pr.flush();
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
                    while (true) {
                    	InputStreamReader in = new InputStreamReader(s.getInputStream());
                    	BufferedReader bf = new BufferedReader(in);
                        String str = bf.readLine();
                        System.out.println("Broker :"+str+"");
                        calculate(str);
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
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}
    public static void calculate (String str)
    {
        int buyorsell = 0;
        int quantity = 0;
        int price = 0;
        int index = 0;
        int i = 0;
        String[] arr = str.split("\\|");
        for (int a = 0; a < arr.length; a++)
        {
            if (a == 7)
            {
                buyorsell = Integer.parseInt(arr[a].split("=")[1]);
            }
            if (a == 9)
            {
                quantity = Integer.parseInt(arr[a].split("=")[1]);
            }
            if (a == 11)
            {
                
                price = Integer.parseInt(arr[a].split("=")[1]);
            }
            if (a == 12)
            {
                
                index = Integer.parseInt(arr[a].split("=")[1]);
            }
        }
        if (buyorsell == 1)
        {
            if (quantity <= inventory[index])
            {
                inventory[index] -= quantity;
                try {

                PrintWriter pr = new PrintWriter(s.getOutputStream());
                String userName = "Type=Exeuted|"+str+"";
                pr.println(userName);
                pr.flush();
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
                System.out.println(inventory[index]);
            }
            else
            {
                try {

                PrintWriter pr = new PrintWriter(s.getOutputStream());
                String userName = "Type=Rejected|"+str+"";
                pr.println(userName);
                pr.flush();
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        }
        else if (buyorsell == 2)
        {
            inventory[index] += quantity;
            try {

                PrintWriter pr = new PrintWriter(s.getOutputStream());
                String userName = "Type=Exeuted|"+str+"";
                pr.println(userName);
                pr.flush();
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            System.out.println(inventory[index]);
        }
        
    }
}
