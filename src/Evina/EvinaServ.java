package Evina;

import java.io.*; 
import java.net.*; 
import java.util.*; 


class EvinaServ {
	private static int socketPort;
  public static void main(String args[]) throws Exception 
    { 
	  
	  // GENERAR ATZAR
	  Random rnd = new Random();
	  System.out.println("RANDOM:\t"+rnd);
	  
/*********************************************************************************************/
	  //	DECLARACIÓ DE VARIABLES
	  
	  BufferedReader teclat = new BufferedReader(new InputStreamReader(System.in));

	  List<String> playerList = new ArrayList<String>(); // Player llista de participants de la partida actual
	  String winnerNick = null;	// nickname of the winner
	  
	  System.out.println("Indiqui el nom de EvinaReg");
	  String RegAddr = teclat.readLine();
	  InetAddress IPAddress = InetAddress.getByName(RegAddr);			// demanar el nom de la maquina EvinaReg.java

	  socketPort = rnd.nextInt(9000)+1;
	  DatagramSocket serverSocket = new DatagramSocket(socketPort); 	// servidor escolta c: 8888	// aquest port hauria de ser algo diferent
	   
	  serverSocket.getInetAddress();	// obtenir l'adreça de la maquina actual 
 
	  int secret = rnd.nextInt(100)+1;					//--> numero secret a desxifrar
	  System.out.println("\tSECRET:\t"+secret);
  
      byte[] receiveData = new byte[1024]; 	
      byte[] sendData  = new byte[1024]; 
      String resposta = null;

/*********************************************************************************************/
	  // CREAR EL MISSATGE DE REGISTRE	      
      
      resposta =  "REGISTER:"+String.valueOf(socketPort)+ "\n";	
      System.out.println("enviant datagram regisre... "+resposta);	
      sendData =resposta.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8888);	// crear datagrama per registrar el servidor al EvinaReg.java <Meta-Server>
	  serverSocket.send(sendPacket);
	  
	  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);	
	  serverSocket.receive(receivePacket);	// esperar postals en la bustia, aqui se queda aturat fins rebre un postal
	  String sentence = new String(receivePacket.getData()); // obrir i extreure el postal en el cas de que hi hagi
	  System.out.println("resposta de META Server: "+sentence.trim());	 
	  
	  
/*********************************************************************************************/
	  // ENTRAR EN EL BUCLE DEL JOC
	  String name=null, bet;
	  int number = 0;
      boolean fin = false;
      while(!fin) 
        { 
    	  System.out.println("\n******************* Game lap: "+number+" ***************************");
    	  // Receive datagram
          serverSocket.receive(receivePacket);						// ficarme a esperar fins en rebre algun postal
          sentence = new String(receivePacket.getData()); 			// tractar la dada rebuda, el format de la dada resbuda tindra el forma NICK: ##

          StringTokenizer st = new StringTokenizer(sentence, ": ");	// fer un tokenizer per tractarlo
          
			name = st.nextToken();		   		// nick del usuari 
		    bet = st.nextToken().trim();		// aposta

		    System.out.print("\tb: "+bet);
		    System.out.print(" VS s: "+secret);
		    System.out.println(" -> "+bet.compareTo(Integer.toString(secret)));
		    
		   if((bet.compareTo(Integer.toString(secret)) != 0))
		    	if((bet.compareTo(Integer.toString(secret)) < 0))
		    		resposta = "+";
		    	else
		    		resposta = "-";
		   else
		   		{
			   	resposta = "=";
			   	fin = true;
			   	winnerNick =name;
		   		}
		   
		   System.out.println("\tresult is: "+resposta);
		   resposta = name +": "+ resposta +"\n";

		   // Get IP addr port #, of sender
		   		IPAddress = receivePacket.getAddress(); 
		   		int port = receivePacket.getPort(); 
		   		// format -> IPAddress:port:name
		   		StringTokenizer aux = new StringTokenizer (IPAddress.toString(),"/");
		   		String auxIP = aux.nextToken();
		   			if(!playerList.contains	(auxIP+":"+port+ ":"+ name+"\n"))
		   				{playerList.add		(auxIP+":"+port+ ":"+ name+"\n");
		   				System.out.println("New Nick Found: "+name+"!");
		   				}
	
          sendData = resposta.getBytes();			
         
          sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);		 // Create datagram to send to client
          serverSocket.send(sendPacket); 	 // Write out datagram to socket	
          // en un ultim cas enviaria un = i sortiriem del bucle
          
          number++;
        } // fin while
      
      
/******************************************************************************/     
      //	END SYSTEM & 
      
      resposta = "UNREGISTER:"+String.valueOf(socketPort)+ "\n";			// crear missatge de finalització --> especificant: "UNREGISTER:port"
      sendData = resposta.getBytes();
      sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8888);
      serverSocket.send(sendPacket);
      serverSocket.receive(receivePacket);				// rebem un "200 OK"

/******************************************************************************/     
      // LISTING WINNER!	// por TCP
       
      
      for(String s: playerList)	// enviarà tants datagrams com de lista de jugadors i l'aplicació morira...
      {
    	// s(format) -> IPAddress:port:name
    	   
    	  StringTokenizer st = new StringTokenizer(s, ":");
    	  StringTokenizer stip = new StringTokenizer(st.nextToken(),".");
    	  byte [] ipMasc =  {
    		  				(byte)Integer.parseInt(stip.nextToken()), 
    		  				(byte)Integer.parseInt(stip.nextToken()), 
    		  				(byte)Integer.parseInt(stip.nextToken()), 
    		  				(byte)Integer.parseInt(stip.nextToken()) 
    		  				};
    	  InetAddress IP = InetAddress.getByAddress(ipMasc);
       
    	  sendData = (Integer.toString(secret)+": =: "+winnerNick).getBytes();
    	  
    	  sendPacket = new DatagramPacket(sendData, sendData.length, IP , Integer.parseInt(st.nextToken()));
    	  serverSocket.send(sendPacket);
      }
      
      
      
      
      
      
      

      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
    } 
}  


