package es.uvigo.esei.dai.hybridserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketIOManager implements IOManager{

    private final Socket socket;
    private final DataInputStream reader;
    private final DataOutputStream writer;

    public SocketIOManager(Socket socket) throws IOException{
        this.socket = socket;
        this.reader = new DataInputStream(socket.getInputStream()); //Para recibir el nombre del fichero
        this.writer = new DataOutputStream(socket.getOutputStream()); //Para enviarle el fichero de vuelta
    }
    public Socket getSocket(){
        return this.socket;
    }
    
    
}
