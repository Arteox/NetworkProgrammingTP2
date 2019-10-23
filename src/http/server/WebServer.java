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
            //System.out.println("Html new line : " + str);
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

    public void doGet(String URL, PrintWriter out) {
        if ("/adder.html".equals(URL)) {
            try {
                sendHeaders(URL, out, "200");
                for (String line : Files.readAllLines(Paths.get("doc/Adder.html"), StandardCharsets.UTF_8)) {
                    out.println(line);
                }
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if (URL.equals("/multiplier.html")) {
            try {
                sendHeaders(URL, out, "200");
                for (String line : Files.readAllLines(Paths.get("doc/Multiplier.html"), StandardCharsets.UTF_8)) {
                    out.println(line);
                }
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if (URL.equals("/shrug.html")) {
            try {
                sendHeaders(URL, out, "200");
                for (String line : Files.readAllLines(Paths.get("doc/shrug.html"), StandardCharsets.UTF_8)) {
                    out.println(line);
                }
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if ("/data".equals(URL)) {
            try {
                sendHeaders(URL, out, "200");
                for (String line : postedData) {
                    out.println(line);
                }
                if (postedData.isEmpty()) {
                    out.println("data empty");
                }
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if ("/image".equals(URL)) {
            try {
                File file = new File("doc/image.jpg");
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String newImage = "data:image/jpeg;base64,";
                newImage += Base64.getEncoder().encodeToString(fileContent);

                sendHeaders(URL, out, "200");
                out.println("<img src=" + newImage + " />");
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if ("/video".equals(URL)) {
            try {
                File file = new File("doc/video.mp4");
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String newVideo = "data:video/mp4;base64,";
                newVideo += Base64.getEncoder().encodeToString(fileContent);
                sendHeaders(URL, out, "200");
                out.println("<video controls>\n"
                        + "	<source type=\"video/mp4\" src=" + newVideo + ">\n"
                        + "</video>");
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if ("/shortvideo".equals(URL)) {
            try {
                File file = new File("doc/shortvideo.mp4");
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String newVideo = "data:video/mp4;base64,";
                newVideo += Base64.getEncoder().encodeToString(fileContent);
                sendHeaders(URL, out, "200");
                out.println("<video controls>\n"
                        + "	<source type=\"video/mp4\" src=" + newVideo + ">\n"
                        + "</video>");
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if ("/audio".equals(URL)) {
            try {
                File file = new File("doc/audio.mp3");
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String newAudio = "data:audio/mpeg;base64,";
                newAudio += Base64.getEncoder().encodeToString(fileContent);
                sendHeaders(URL, out, "200");
                out.println("<audio controls>\n"
                        + "	<source type=\"audio/mpeg\" src=" + newAudio + ">\n"
                        + "</audio>");
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if ("/ushio".equals(URL)) {
            try {
                File file = new File("doc/ushio.mp3");
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String newAudio = "data:audio/mpeg;base64,";
                newAudio += Base64.getEncoder().encodeToString(fileContent);
                sendHeaders(URL, out, "200");
                out.println("<audio controls>\n"
                        + "	<source type=\"audio/mpeg\" src=" + newAudio + ">\n"
                        + "</audio>");
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else if ("/dango".equals(URL)) {
            try {
                File file = new File("doc/dango.mp3");
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String newAudio = "data:audio/mpeg;base64,";
                newAudio += Base64.getEncoder().encodeToString(fileContent);
                sendHeaders(URL, out, "200");
                out.println("<audio controls>\n"
                        + "	<source type=\"audio/mpeg\" src=" + newAudio + ">\n"
                        + "</audio>");
            } catch (Exception e) {
                sendHeaders(URL, out, "500");
                System.err.println("Error: " + e);
            }
        } else {
            sendHeaders(URL, out, "404");
            out.println("<H1>404 Sorry, this page doesn't exist</H1>");
        }
    }

    public void doPost(String URL, String body, PrintWriter out) {
        try {
            if (URL.equals("/data")) {
                postedData.add("<H1>" + body + "</H1>");
                System.out.println("data added :" + body);
                sendHeaders(URL, out, "200");
            } else {
                sendHeaders(URL, out, "501");
                out.println("<H1>501 Service not implemented</H1>");
            }

        } catch (Exception e) {
            sendHeaders(URL, out, "500");
            System.err.println("Error: " + e);
        }

    }

    public void doPut(String URL, String body, PrintWriter out) {
        try {
            if (URL.equals("/data")) {
                postedData.clear();
                postedData.add("<H1>" + body + "</H1>");
                sendHeaders(URL, out, "200");
            } else {
                sendHeaders(URL, out, "501");
                out.println("<H1>501 Service not implemented</H1>");
            }
        } catch (Exception e) {
            sendHeaders(URL, out, "500");
            System.err.println("Error: " + e);
        }
    }

    public void doDelete(String URL, PrintWriter out) {
        try {
            if (URL.equals("/data")) {
                postedData.clear();
                sendHeaders(URL, out, "200");
            } else {
                sendHeaders(URL, out, "501");
                out.println("<H1>501 Service not implemented</H1>");
            }
        } catch (Exception e) {
            sendHeaders(URL, out, "500");
            System.err.println("Error: " + e);
        }
    }

    public void doHead(String URL, PrintWriter out) {
        try {
            sendHeaders(URL, out, "200");
        } catch (Exception e) {
            sendHeaders(URL, out, "500");
            System.err.println("Error: " + e);
        }
    }

    private ArrayList<String> createGetHeaders(String URL, String statusCode) {
        ArrayList<String> headers = new ArrayList<>();
        String statusMessage = createStatusMsg(statusCode);
        headers.add("HTTP/1.1 " + statusCode + " " + statusMessage);
        headers.add("Content-Type: text/html");
        headers.add("Server: Bot");
        headers.add("");
        return headers;
    }

    private String createStatusMsg(String statusCode) {
        String statusMessage = "";
        if (statusCode.equals("200")) {
            statusMessage = "OK";
        } else if (statusCode.equals("404")) {
            statusMessage = "Not Found";
        } else if (statusCode.equals("500")) {
            statusMessage = "Internal Server Error";
        }
        else if (statusCode.equals("501")) {
            statusMessage = "Service not available";
        }
        return statusMessage;
    }

    private void sendHeaders(String URL, PrintWriter out, String statusCode) {
        ArrayList<String> headers = createGetHeaders(URL, statusCode);
        for (String header : headers) {
            out.println(header);
        }
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
