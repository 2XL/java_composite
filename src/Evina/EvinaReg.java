package Evina;

 
import java.io.*; 
import java.net.*;
import java.util.*;
 
class EvinaReg { 

	
	
	public static class MyTask implements Runnable {
		
		InetAddress IPAddress;
		int inPort;
		List<String> lista;

		public MyTask(InetAddress IPAddress, int inPort, List<String> lista){
			this.IPAddress = IPAddress;
			this.inPort = inPort;
			this.lista = lista;
		}

		public void run() 	// establir una conexió TCP omg
		{
			// CREAR un SocketTCP
		try {
				ServerSocket s = new ServerSocket(8888);	// per defecte
				Socket connectionSocket = s.accept();
				DataOutputStream  outToClient =  new DataOutputStream(connectionSocket.getOutputStream()); 
			
			for (String valor : lista)	// aixo hauria de ser un threat apart que enviés TCP...
			{
				outToClient.writeBytes(valor+"\n");
		  	    System.out.println("SENDING LIST..."+valor);
				//serverSocket.send(sendPacket); 
			}
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
    public static void main(String argv[]) throws Exception 
    { 
        byte[] receiveData = new byte[1024]; 	// buffer format de rebuda
        byte[] sendData  = new byte[1024]; 		// buffer format de sortida
 
      	// llista de tots els servidors registrats
      	List<String> lista = new ArrayList<String>();
 
     /*****************************************************************************************/     	
 
 // UDP socket
  	 System.out.println("UDP");   
  	    DatagramSocket serverSocket = new DatagramSocket(8888); // crear un servidor udp escoltant al port 8888
  	    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);	//crear un molla per paquets UDP
  	    /*
        for(String i : llista)
           	 outToClient.writeBytes(i+"\n");
        */      
     /*****************************************************************************************/
        // REBEDOR
  	    
  	    System.out.println("REBEDOR");
  	    int index = 0;
  	    boolean fin = false;      
  	    
do{        
	System.out.println("\nSERVER LISTENING: "+index);
  // UDP <<- Tasca 4
	 	byte[] buffer = new byte[1024]; 	// buffer format de rebuda
		receivePacket = new DatagramPacket(buffer, buffer.length);
  	    serverSocket.receive(receivePacket);
  	    
        String sentence = new String(receivePacket.getData()); 	
        String option, port;
        if(sentence.trim() != "fin")	
        {
        	index++;
        	// Get IP addr port #, of sender
        	InetAddress IPAddress = receivePacket.getAddress(); 
        	int inPort = receivePacket.getPort(); 

        	System.out.println("RECEIVING: \n\t\t"+sentence.trim());
        	
        	
 /*LIST*/  	if(sentence.trim().equals("LIST"))
        		{//retorna llista 
	 // crear un thread que retornará la llista dels servidors de jocs que tingui registrats
	 		
	 Thread t = new Thread(new MyTask(IPAddress, inPort, lista));
	 t.start();
	 System.out.println("enviant ips al client: ...\n");
	// while(t.isAlive()) ; //System.out.print(".");
     System.out.println("\n");   	
        		}
        	else
        		{
        			StringTokenizer st = new StringTokenizer(sentence, ":");
        			option = st.nextToken();
        			port = st.nextToken().trim();
        			
 /*REGISTER*/		if(option.equals("REGISTER"))
        				{
	 						StringTokenizer aux = new StringTokenizer(IPAddress.toString(),"/");
        					lista.add(aux.nextToken()+":"+port); // com saber l'ip? 
        					for(String in : lista)
        						System.out.println(in);
        					System.out.println("\n\t 200 OK :\n\tAdded: "+IPAddress+":"+inPort);
        				}
        				// enviar "200 OK"
 /*UNREGISTER*/		if(option.equals("UNREGISTER"))
        				{
							 StringTokenizer aux = new StringTokenizer(IPAddress.toString(),"/");
								lista.remove(aux.nextToken()+":"+port); // com saber l'ip? 
								for(String in : lista)
									System.out.println(in);
								System.out.println("\n\t 200 OK :\n\tRemoved: "+IPAddress+":"+inPort);
        				}	// enviar "200 OK"
        		
        			sendData = "200 OK".getBytes();
	        	
        			// Create datagram to send to client
		        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, inPort);
		        	serverSocket.send(sendPacket);
		        	
	        	}	
        }else	fin = true;
   	// TCP 		
}while(!fin);   
        
             
     	System.out.println("EvinaRegClose");
 
    } 
} 

