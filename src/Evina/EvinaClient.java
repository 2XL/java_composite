package Evina;

import java.io.*; 
import java.net.*; 
import java.util.*;

  
class EvinaClient { 
	
	static boolean finParty = false;

	public static void main(String args[]) throws Exception, IOException 
    { 
		// Create input stream
    	BufferedReader teclat = new BufferedReader(new InputStreamReader(System.in));
        
  
 /***************************************************************************/ 
    // BUSCAR SEVIDORS DISPOSPONIBLES A LA LLISTA DEL META-SERVIDOR
    	
    	
      System.out.println("Indiqui el nom/ip de EvinaReg");
      String AddrReg = teclat.readLine();
    InetAddress IPAddress = InetAddress.getByName(AddrReg);			// indicar on es troba el metaservidor
      
    // Create client socket
      byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[1024]; 
      String sentence;
      String modifiedSentence;
      DatagramSocket clientSocket = new DatagramSocket(); 						// inicialitzar el datagram socket
      
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
      DatagramPacket sendPacket; 	// ip:port
      
      
  /**************************************************************************/
      // SELECCIONAR AMB QUIN SERVIDOR JUGAR
      System.out.println("Indiqui l'index del servidor que usarem #:  ");
      	// enviar la comanda LIST
     
      sendData = "LIST".getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8888);		// meta server always on 8888
      clientSocket.send(sendPacket);													// enviar datagrama
 
      
    // REBRE LA LLISTA DE SERVIDORS DISPONIBLES  
      List<String> serverList = new ArrayList<String>(); // Player llista de participants de la partida actual
      int i=0;  // index de nombre de servidors rebuts 
 
      Socket clientTCP = new Socket(AddrReg, 8888); 
      BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientTCP.getInputStream())); 
   
   
      do{ 
    	  sentence = inFromServer.readLine();
    	  System.out.println(i+" :"+sentence);
    	  serverList.add(sentence);
    	  i++;
      	} 
   		while ((inFromServer.ready()== true)&&(sentence!=null));

   
      clientTCP.close(); 
   
   // en cas de rebre només un no cal selecciónar
      int opcio = 0;
      if(i!=1)
      {   
    	  System.out.println("llista completa... Elecció:");
    	  opcio = Integer.parseInt(teclat.readLine());
      }
 
   /*	// omplir la llista per UDP	
   try{
   do
   	{ 
	  clientSocket.setSoTimeout(TTL);
	  byte[] buf = new byte[1024]; 
	  receivePacket = new DatagramPacket(buf, buf.length); 
      clientSocket.receive(receivePacket);	// rep missatge 
      modifiedSentence = new String(receivePacket.getData());	// obre missatge
      System.out.println(i+" :"+modifiedSentence);
      serverList.add(modifiedSentence);
     i++;
   	 }
   	while(i<10000);	// limit de ip's
   } 
   catch (SocketTimeoutException timeout)	// cal permetre la continuació d'execució
   {
 	//  timeout.printStackTrace();
	   System.out.println("...:");
   } 
    */   
   
      String addrIP, port;
      StringTokenizer aux = new StringTokenizer(serverList.get(opcio),":");
   
   
   
   
      addrIP = aux.nextToken();
      StringTokenizer stip = new StringTokenizer(addrIP,".");
    	
    
    
      byte[] ip = {
		   			(byte) Integer.parseInt(stip.nextToken()), 
		   			(byte) Integer.parseInt(stip.nextToken()),
		   			(byte) Integer.parseInt(stip.nextToken()),
		   			(byte) Integer.parseInt(stip.nextToken())
   				  };
  
   
      port = aux.nextToken().trim();
      System.out.println("\nip: "+ip[0]+"."+ip[1]+"."+ip[2]+"."+ip[3]);
      System.out.println("port: "+port);
      System.out.println("Entrant en Joc!:");
   
    

 
  /*************************************************************************/
      // JUGANT
      boolean encert = false;          
      do{   
	
    	  System.out.println("SendFormat: Nickname: ##"); 
    	 
    	  sentence = teclat.readLine()+" \n";
    	  sendData = sentence.getBytes();
    	  
 /***************************************************************************/
     // CREATE DATAGRAM & SEND THEN CREATE RECEIVE DATAGRAM & RECEIVE
  
      // Create datagram with data-to-send, length, IP addr, port
      
    	  sendPacket.setData(sendData,0,sendData.length);
    	  InetAddress iaddr = InetAddress.getByAddress(ip);
    	  sendPacket.setAddress(iaddr);
    	  sendPacket.setPort(Integer.parseInt(port));
      
      
    	  byte[] buf = new byte[1024];
    	  receivePacket = new DatagramPacket(buf, buf.length); 
     

    	  System.out.print("\n");
      	
    	  clientSocket.send(sendPacket);		 
          clientSocket.receive(receivePacket);
  
      
 /***************************************************************************/     
      // DECRYPT DATAGRAM
     
      modifiedSentence = new String(receivePacket.getData());  
   
      System.out.println(modifiedSentence);
      StringTokenizer s = new StringTokenizer(modifiedSentence, ": ");
		
      String name=null, result=null;
  	  name = s.nextToken();    
	  result = s.nextToken().trim();
	  if(s.hasMoreTokens())	// en cas de que hi hagi més...
	  {
		  System.out.println("and the Winner is...:"+s.nextToken());
		  break;
	  }
	  
	  if(result.equals("="))
	   	  {
		  System.out.println(name+" U got IT!");
		  encert = true;
	   	  }
  	}while(!encert);
  /***************************************************************************/     
      // FINAL DE PARTIDA
 
      clientSocket.close(); 
       } 
 } 























