/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.server;

/**
 *
 * @author lung
 */
public class Headers {

    public String URL;
    public String requestType;
    public int contentLength;

    public Headers(String URL, String requestType, int contentLength) {
        this.URL = URL;
        this.requestType = requestType;
        this.contentLength = contentLength;
    }

}
