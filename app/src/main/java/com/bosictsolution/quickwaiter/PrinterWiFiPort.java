package com.bosictsolution.quickwaiter;

import com.andprn.jpos.request.RequestQueue;
import com.andprn.port.PortMediator;
import com.andprn.port.android.PortInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * Created by User on 6/8/2017.
 */
public class PrinterWiFiPort implements PortInterface {

    private static int defaultPort = 9100;
    private Socket socket;
    private static PrinterWiFiPort wifiPort;
    private OutputStream os;
    private InputStream is;
    private int timeout = 3000;

    /** @deprecated */
    public PrinterWiFiPort() {
    }

    public static PrinterWiFiPort getInstance() {
        if(wifiPort == null) {
            wifiPort = new PrinterWiFiPort();
        }

        return wifiPort;
    }

    public PrinterWiFiPortConnection open(String address) throws IOException {
        return this.open(address, defaultPort);
    }

    public PrinterWiFiPortConnection open(String address, int port) throws IOException {
        InetSocketAddress socketAdress = new InetSocketAddress(InetAddress.getByName(address), port);
        SocketChannel channel = SocketChannel.open(socketAdress);
        return new PrinterWiFiPortConnection(channel);
    }

    public void connect(final String address, int port) throws IOException {
        try {
            Thread.sleep(1000);
            PortMediator pm = PortMediator.getInstance();
            InetSocketAddress socketAdress = new InetSocketAddress(InetAddress.getByName(address), port);
            this.socket = new Socket();
            this.socket.connect(socketAdress);
            this.os = this.socket.getOutputStream();
            this.is = this.socket.getInputStream();
            pm.setIs(this.is);
            pm.setOs(this.os);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connect(String address) throws IOException {
        this.connect(address, defaultPort);
    }

    /** @deprecated */
    public InputStream getInputStream() throws IOException {
        if(this.socket != null) {
            this.is = this.socket.getInputStream();
        }

        return this.is;
    }

    /** @deprecated */
    public OutputStream getOutputStream() throws IOException {
        if(this.socket != null) {
            this.os = this.socket.getOutputStream();
        }

        return this.os;
    }

    public void disconnect() throws IOException, InterruptedException {
        int count = 0;

        for(RequestQueue rq = RequestQueue.getInstance(); !rq.isEmpty() && count < 4; ++count) {
            Thread.sleep(1000);
        }

        if(this.os != null) {
            this.os.close();
            this.os = null;
        }

        if(this.is != null) {
            this.is.close();
            this.is = null;
        }

        if(this.socket != null) {
            this.socket.close();
            this.socket = null;
        }

    }

    public boolean isConnected() {
        return this.socket != null?this.socket.isConnected():false;
    }
}
