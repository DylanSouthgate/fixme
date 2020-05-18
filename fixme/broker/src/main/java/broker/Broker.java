package broker;

import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.*;
public class Broker 
{
    public static String[] instruments = {"Copper", "Aluminium", "Nickel", "Zinc", "Lead", "Tin"};
	public static final int[] inventory = {1, 1, 1, 1,1, 1};
	public static int buyorsell;
	public static String instru;
	public static int index;
	public static int price;
	public static int quantity;
	public static int checksum;
	public static String assignid;
      public static String marketid;
      public static String brokerid;
	public static Socket s;
	public static Scanner myObj;
	public static int bodylength;
      public static String type;
      public static ZonedDateTime time;
	public static void main(String[] args) throws IOException 
	{ 
		time= ZonedDateTime.now(ZoneOffset.UTC);
		try
		{
			myObj = new Scanner(System.in);
			Scanner scn = new Scanner(System.in); 
			InetAddress ip = InetAddress.getByName("localhost"); 
			s = new Socket(ip, 5001);
			InputStreamReader in = new InputStreamReader(s.getInputStream());
			BufferedReader bf = new BufferedReader(in);
			assignid = bf.readLine();
                  String[] arr = assignid.split("\\|");
                  brokerid = arr[1].split("=")[1];
                  marketid = arr[0].split("=")[1];
			System.out.println("BROKER ID "+arr[1].split("=")[1]+"");
                  boom();
		}
		catch(Exception e){ 
			e.printStackTrace(); 
		} 

	}
      public static void buyorsell()
      {
            System.out.println("BUY[1] OR SELL[2]");
            while(true)
            {
                  buyorsell = myObj.nextInt();
                  if (buyorsell == 1)
                        break;
                  else if (buyorsell == 2)
                        break;
                  else
                        System.out.println("BUY[1] OR SELL[2]");
            }
            System.out.println(buyorsell);
      }
      public static void Instrument()
      {
            System.out.println("Select Instrument");
            while(true)
            {
                  index = myObj.nextInt();
                  if (index <= 5 && index >= 0)
                        break;
                  else
                        System.out.println("Select Instrument");
            }
            instru = instruments[index];
            System.out.println(instru);
      }
      public static void Quantity()
      {
            if (buyorsell == 2)
                  System.out.println("Select Quantity To Sell [ 1 - "+inventory[index]+"]");
            else
                  System.out.println("Select Quantity To Buy");
            while(true)
            {
                  quantity = myObj.nextInt();
                  if (buyorsell == 2)
                  {
                        if (quantity > 0 && quantity <= inventory[index])
                              break;
                        else
                              System.out.println("Select Quantity To Sell [ 1 - "+inventory[index]+" ]");
                  }
                  else
                  {
                        if (quantity > 0 && quantity <= 10000)
                              break;
                        else
                              System.out.println("Select Quantity To Buy");
                  }
            }
      }
      public static void Price()
      {
            System.out.println("Select Price");
            while(true)
            {
                  price = myObj.nextInt();
                  if (price > 0 && price <= 900)
                        break;
                  else
                        System.out.println("Select Price");
            }
      }
      public static void SendMessage()
      {
            String message = "35=D|49="+brokerid+"|56="+marketid+"|52="+time+"|55=D|54="+buyorsell+"|60=1|38="+quantity+"|40=1|44="+price+"|64="+index+"|39=1";
            bodylength = message.length();
            message = "8=FIX.4|9="+bodylength+"|"+message+"";
            int ascii;
            int checksum = 0;
            for (int i = 0; i < message.length(); i++)
            {
                  if (message.charAt(i) != '|')
                        ascii = (int)message.charAt(i);
                  else
                        ascii = 1;
                  checksum += ascii;
            }
            try
            {
                  message = ""+message+"|"+checksum+"";
                  System.out.println(message);
                  PrintWriter pr = new PrintWriter(s.getOutputStream());
                  pr.println(message);
                  pr.flush();
            }catch (IOException e)
            {
                  System.err.println("Unable to process client request");
                  e.printStackTrace();
            }
      }
      public static int stringCompare(String str1, String str2) 
      { 
            int l1 = str1.length(); 
            int l2 = str2.length(); 
            int lmin = Math.min(l1, l2); 
            for (int i = 0; i < lmin; i++)
            { 
                  int str1_ch = (int)str1.charAt(i); 
                  int str2_ch = (int)str2.charAt(i); 
                  if (str1_ch != str2_ch) { 
                        return str1_ch - str2_ch; 
                  }  
                  if (l1 != l2) { 
                        return l1 - l2; 
                  } 
                  else
                  { 
                        return 0; 
                  } 
            }
            return 0;
      }
      public static void recievemessage(String str)
      {
            String[] arr = str.split("\\|");
            for (int a = 0; a < arr.length; a++)
            {
                  if (a == 0)
                        type = arr[a].split("=")[1];
                  if (a == 8)
                        buyorsell = Integer.parseInt(arr[a].split("=")[1]);
                  if (a == 10)
                        quantity = Integer.parseInt(arr[a].split("=")[1]);
                  if (a == 12)
                        price = Integer.parseInt(arr[a].split("=")[1]);
                  if (a == 13)
                        index = Integer.parseInt(arr[a].split("=")[1]);
            }
            if (stringCompare(type, "Exeuted") == 0)
            {
                  if (buyorsell == 1)
                  {
                        inventory[index] += quantity;
                  }
                  else if (buyorsell == 2)
                  {
                        inventory[index] -= quantity;
                  }
            }

      }
      public static void boom() throws IOException 
      { 
            try
            { 
                  final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
                  Runnable survey = new Runnable() {
                        @Override
                        public void run() {
                              try {
                                    while (true) {
                                          buyorsell();
                                          Instrument();
                                          Quantity();
                                          Price();
                                          SendMessage();
                                          InputStreamReader in = new InputStreamReader(s.getInputStream());
                                          BufferedReader bf = new BufferedReader(in);
                                          String str = bf.readLine();
                                          System.out.println("Market["+marketid+"] :"+str+"");
                                          recievemessage(str);
                                    }
                              } catch (IOException e) {
                                    System.err.println("Unable to process client request");
                                   e.printStackTrace();
                              }
                        }
                  };
      Thread surveyThread = new Thread(survey);
      surveyThread.start();
            }catch(Exception e){ 
                  e.printStackTrace(); 
            } 
      }
}
