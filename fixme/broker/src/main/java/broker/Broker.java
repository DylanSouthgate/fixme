package broker;
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.*;
// Client class 
public class Broker 
{ 
	public static String[] instruments = {"Copper", "Aluminium", "Nickel", "Zinc", "Lead", "Tin"};
	public static final int[] inventory = {0, 0, 0, 0,0, 0};
	public static int buyorsell;
	public static String instru;
	public static int index;
	public static int price;
	public static int quantity;
	public static int checksum;
	public static String assignid;
	public static Socket s;
	public static Scanner myObj;
	public static int bodylength;
	public static void main(String[] args) throws IOException 
	{ 
		ZonedDateTime time= ZonedDateTime.now(ZoneOffset.UTC);
		try
		{
			myObj = new Scanner(System.in);
			Scanner scn = new Scanner(System.in); 
			InetAddress ip = InetAddress.getByName("localhost"); 
			s = new Socket(ip, 5001);
			InputStreamReader in = new InputStreamReader(s.getInputStream());
			BufferedReader bf = new BufferedReader(in);
			assignid = bf.readLine();
			System.out.println(assignid);

			do {
            		System.out.println("BUY[1] OR SELL[2]");
            		while(true)
            		{
            			buyorsell = myObj.nextInt();
            			if (buyorsell == 1)
            			{
            				break;
            			}
            			else if (buyorsell == 2)
            			{
            				break;
            			}
            			else
            			{
            				System.out.println("BUY[1] OR SELL[2]");
            			}
            		}
            		System.out.println(buyorsell);
            		System.out.println("Select Instrument");
            		while(true)
            		{
            			index = myObj.nextInt();
            			if (index <= 5 && index >= 0)
            			{
//            				System.out.println(instruments[index]);
            				break;
            			}
            			else
            			{
            				System.out.println("Select Instrument");
            			}
            		}
            		instru = instruments[index];
            		System.out.println(instru);
            		System.out.println("Select Quantity");
            		while(true)
            		{
            			quantity = myObj.nextInt();
            			if (quantity > 0 && quantity <= 1000)
            			{
            				break;
            			}
            			else
            			{
            				System.out.println("Select Quantity");
            			}
            		}
            		System.out.println("Select Price");
            		while(true)
            		{
            			price = myObj.nextInt();
            			if (price > 0 && price <= 1000)
            			{
            				break;
            			}
            			else
            			{
            				System.out.println("Select Price");
            			}
            		}
            		String message = "35=D|49="+assignid+"|56=100001|52="+time+"|55=D|54="+buyorsell+"|60=1|38="+quantity+"|40=1|44="+price+"|39=1";
            		bodylength = message.length();
            		message = "8=FIX.4|9="+bodylength+"|"+message+"";
            		int ascii;
            		int checksum = 0;
            		for (int i = 0; i < message.length(); i++)
            		{
            			if (message.charAt(i) != '|')
            			{
            				ascii = (int)message.charAt(i);
            			}
            			else
            			{
            				ascii = 1;
            			}
            			checksum += ascii;
            		}
            		message = ""+message+"|"+checksum+"";
            		System.out.println(message);
            		PrintWriter pr = new PrintWriter(s.getOutputStream());
            		pr.println(message);
            		pr.flush();
            	}while(true);
		}
		catch(Exception e){ 
			e.printStackTrace(); 
		} 

	}
}