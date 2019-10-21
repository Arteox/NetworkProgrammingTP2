///A Simple Web Server (WebServer.java)
package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 *
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

    /**
     * WebServer constructor.
     */
    private ArrayList<String> postedData = new ArrayList<String>();

    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 3000");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (;;) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.
                String str = ".";
                String requestType = "";
                String URL = "";
                while (!str.equals("")) {
                    str = in.readLine();
                    if (str.contains("HTTP")) {
                        requestType = str.split(" ")[0];
                        URL = str.split(" ")[1];
                    }
                    System.out.println("Html new line : " + str);
                }

                if ("GET".equals(requestType)) {
                    doGet(URL, out);
                }
                if ("POST".equals(requestType)) {
                    String body = "";
                    str = "dummy";
                    //réécrire le while et récupérer autant de charactères que das content length
                    while (!str.equals("")) {
                        str = in.readLine();
                        if (!str.equals("dummy") || !str.equals("")){
                            body += str+"\n";
                        }
                        System.out.println("post body new line : " + str);
                    }
                    doPost(URL, body);
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html");
                    out.println("Server: Bot");
                    out.println("");
                    out.println("status: 200");
                }
                if ("HEAD".equals(requestType)) ;
                if ("PUT".equals(requestType)) ;
                if ("DELETE".equals(requestType)) ;
                out.flush();
                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void doGet(String URL, PrintWriter out) throws IOException {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        out.println("");
        System.out.println(URL);
        if ("/adder.html".equals(URL)) {
            for (String line : Files.readAllLines(Paths.get("ress/Adder.html"), StandardCharsets.UTF_8)) {
                out.println(line);
                System.out.println(line);
            }
        } else if ("/data".equals(URL)) {
            for (String line : postedData) {
                out.println(line);
            }
        }
    }

    public void doPost(String URL, String body) {
        if (URL.equals("/data")){
            //break pour chaque \n
            postedData.add("<H1>" + body + "</H1>");
            System.out.println("data added :"+body);
        }
    }

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
