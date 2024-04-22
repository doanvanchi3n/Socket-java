package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandle implements Runnable{
    public static ArrayList<ClientHandle> clientHandles = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientHandle(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandles.add(this);
            boardcastMessage("SERVER: "+ clientUserName+ "has entered the chat!!");

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                boardcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }


    public void  boardcastMessage(String messageToSend) {
        for (ClientHandle clientHandle : clientHandles) {
            try {
                if (!clientHandle.clientUserName.equals(clientUserName)) {
                    clientHandle.bufferedWriter.write(messageToSend);
                    clientHandle.bufferedWriter.newLine();
                    clientHandle.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeClientHandle() {
        clientHandles.remove(this);
        boardcastMessage("SERVER: "+ clientUserName+ "has leave the chat!!");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandle();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter!= null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
