package com.bosictsolution.quickwaiter;

import com.andprn.jpos.request.RequestQueue;
import com.andprn.port.android.DeviceConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by User on 6/8/2017.
 */
public class PrinterWiFiPortConnection  implements DeviceConnection {

    private SocketChannel channel;
    private RequestQueue queue;
    private Thread requestHandler;

    protected PrinterWiFiPortConnection(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;
        if(this.channel != null && this.channel.isConnected()) {
            if(this.channel.isBlocking()) {
                this.channel.configureBlocking(false);
            }

            this.queue = new RequestQueue();
            this.requestHandler = new Thread(new PrinterWiFiPortConnection.SenderThread());
            this.requestHandler.start();
        }

    }

    public void close() throws InterruptedException, IOException {
        for(int count = 0; !this.queue.isEmpty() && count < 3; ++count) {
            Thread.sleep(1000L);
        }

        this.channel.close();
        if(this.requestHandler != null && this.requestHandler.isAlive()) {
            this.requestHandler.interrupt();
        }

        Thread.sleep(2000L);
    }

    public boolean isConnected() {
        return this.channel != null?this.channel.isConnected():false;
    }

    public RequestQueue getQueue() {
        return this.queue;
    }

    public int read(byte[] array) throws IOException, InterruptedException {
        boolean rin = false;
        int index = 0;
        ByteBuffer temp = ByteBuffer.allocate(128);
        byte[] buffer = new byte[512];
        Thread.sleep(500L);

        int rin1;
        while((rin1 = this.channel.read(temp)) > 0) {
            System.arraycopy(temp.array(), 0, buffer, index, rin1);
            temp.clear();
            index += rin1;
        }

        if(index <= 0) {
            return -1;
        } else if(index > array.length) {
            System.arraycopy(buffer, 0, array, 0, array.length);
            return array.length;
        } else {
            System.arraycopy(buffer, 0, array, 0, index);
            return index;
        }
    }

    public int readData(byte[] array) throws InterruptedException {
        PrinterWiFiPortConnection.ReadStatusThread statusThread = new PrinterWiFiPortConnection.ReadStatusThread();
        Thread t1 = new Thread(statusThread);
        t1.start();
        Thread.sleep(1000L);
        byte[] data = statusThread.getBuffer();
        if(data == null) {
            return -1;
        } else if(data.length > array.length) {
            System.arraycopy(data, 0, array, 0, array.length);
            return array.length;
        } else {
            System.arraycopy(data, 0, array, 0, data.length);
            return data.length;
        }
    }

    class ReadStatusThread implements Runnable {
        private static final String TAG = "WiFiPortConnection RThread";
        private byte[] readData;

        ReadStatusThread() {
        }

        public void run() {
            byte rin = 0;
            int index = 0;
            ByteBuffer temp = ByteBuffer.allocate(128);
            byte[] buffer = new byte[512];

            try {
                Thread.sleep(100L);

                int rin1;
                while((rin1 = PrinterWiFiPortConnection.this.channel.read(temp)) > 0) {
                    System.arraycopy(temp.array(), 0, buffer, index, rin1);
                    temp.clear();
                    index += rin1;
                }

                if(index > 0) {
                    this.setBuffer(buffer, 0, index);
                }
            } catch (IOException var6) {
                //Log.e("WiFiPortConnection RThread", var6.getMessage() + " ReadIn=" + rin);
            } catch (InterruptedException var7) {
                //Log.e("WiFiPortConnection RThread", var7.getMessage());
            }

        }

        public synchronized byte[] getBuffer() {
            return this.readData;
        }

        public synchronized void setBuffer(byte[] buffer, int offset, int length) {
            this.readData = new byte[length - offset];
            System.arraycopy(buffer, offset, this.readData, 0, length);
        }
    }

    class SenderThread implements Runnable {
        SenderThread() {
        }

        public void run() {
            while(true) {
                try {
                    if(!Thread.currentThread().isInterrupted()) {
                        byte[] data = PrinterWiFiPortConnection.this.queue.dequeue().getRequestData();
                        PrinterWiFiPortConnection.this.channel.write(ByteBuffer.wrap(data));
                        continue;
                    }
                } catch (Exception var3) {
                    PrinterWiFiPortConnection.this.queue.clearQueue();
                }

                return;
            }
        }
    }
}
