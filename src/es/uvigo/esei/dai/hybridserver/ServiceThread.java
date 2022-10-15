package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.Socket;

public class ServiceThread implements Runnable{

    private final Socket socket;
   
    public ServiceThread(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket) {
            SocketIOManager ioManager = new SocketIOManager(this.socket);
            
            
        } catch (Exception e) {
        }
        
    }


}
