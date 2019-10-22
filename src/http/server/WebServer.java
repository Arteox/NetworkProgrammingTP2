///A Simple Web Server (WebServer.java)
package http.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;

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
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }
        System.out.println("Waiting for connection");
        for (;;) {
            try {
                Socket remote = s.accept();

                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());

                Headers headers = readHeaders(in);

                executeRequest(headers, out, in);

                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public Headers readHeaders(BufferedReader in) throws IOException {
        String str = ".";
        String requestType = "";
        String URL = "";
        int contentLength = 0;
        while (!str.equals("")) {
            str = in.readLine();
            if (str.contains("HTTP")) {
                requestType = str.split(" ")[0];
                URL = str.split(" ")[1];
            } else if (str.contains("Content-Length")) {
                contentLength = Integer.parseInt(str.substring(16));
            }
            System.out.println("Html new line : " + str);
        }
        return new Headers(URL, requestType, contentLength);
    }

    public void executeRequest(Headers headers, PrintWriter out, BufferedReader in) throws IOException {
        if ("GET".equals(headers.requestType)) {
            doGet(headers.URL, out);
        } else if ("POST".equals(headers.requestType)) {
            String body = readBody(in, headers.contentLength);
            doPost(headers.URL, body, out);
        } else if ("HEAD".equals(headers.requestType)) {
            doHead(headers.URL, out);
        } else if ("PUT".equals(headers.requestType)) {
            String body = readBody(in, headers.contentLength);
            doPut(headers.URL, body, out);
        }
        if ("DELETE".equals(headers.requestType)) {
            doDelete(headers.URL, out);
        }
        out.flush();
    }

    public void doGet(String URL, PrintWriter out) throws IOException {
        System.out.println(URL);
        ArrayList<String> headers = createGetHeaders(URL);
        for (String header : headers) {
            out.println(header);
        }
        if ("/adder.html".equals(URL)) {
            for (String line : Files.readAllLines(Paths.get("doc/Adder.html"), StandardCharsets.UTF_8)) {
                out.println(line);
                System.out.println(line);
            }
        } else if ("/data".equals(URL)) {
            for (String line : postedData) {
                out.println(line);
            }
            if (postedData.isEmpty()) {
                out.println("data empty");
            }
        } else if ("/image".equals(URL)) {
            File file = new File("doc/image.jpg");
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String newImage = "data:image/jpeg;base64,";
            newImage += Base64.getEncoder().encodeToString(fileContent);
            out.println("<img src=" + newImage + " />");
        }
    }

    public void doPost(String URL, String body, PrintWriter out) {
        if (URL.equals("/data")) {
            postedData.add("<H1>" + body + "</H1>");
            System.out.println("data added :" + body);
        }
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        out.println("");
        out.println("status: 200");
    }

    public void doPut(String URL, String body, PrintWriter out) {
        if (URL.equals("/data")) {
            postedData.clear();
            postedData.add("<H1>" + body + "</H1>");
            System.out.println("data replaced :" + body);
        }
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        out.println("");
        out.println("status: 200");
    }

    public void doDelete(String URL, PrintWriter out) {
        if (URL.equals("/data")) {
            postedData.clear();
        }
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        out.println("");
        out.println("status: 200");
    }

    public void doHead(String URL, PrintWriter out) {
        ArrayList<String> headers = createGetHeaders(URL);
        for (String header : headers) {
            out.println(header);
        }
    }

    private ArrayList<String> createGetHeaders(String URL) {
        ArrayList<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 200 OK");
        headers.add("Content-Type: text/html");
        headers.add("Server: Bot");
        headers.add("");
        return headers;
    }

    private String readBody(BufferedReader in, int bodyLength) throws IOException {
        String body = "";
        int characterAdded = 0;
        while (characterAdded < bodyLength) {
            char c = (char) in.read();
            if (c == '\r' || c == '\n') {
                body += "<br>";
            } else {
                body += c;
            }
            characterAdded++;
        }
        return body;
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
