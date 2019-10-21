package http.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class WebPing {

    public static void main(String[] args) {
        
        String httpServerHost = "localhost";
        int httpServerPort = Integer.parseInt("3000");
        httpServerHost = "localhost";
        httpServerPort = Integer.parseInt("3000");

        try {
            InetAddress addr;
            Socket sock = new Socket(httpServerHost, httpServerPort);
            addr = sock.getInetAddress();
            System.out.println("Connected to " + addr);
            PrintStream socOut = new PrintStream(sock.getOutputStream());
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (true) {
               line=stdIn.readLine();
               if (line==".") break;
               socOut.println(line);
            }
            sock.close();
        } catch (java.io.IOException e) {
            System.out.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
            System.out.println(e);
        }
    }
}
