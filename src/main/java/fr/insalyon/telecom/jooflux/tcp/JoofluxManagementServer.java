/*
 * JooFlux
 *
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package fr.insalyon.telecom.jooflux.tcp;

import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class JoofluxManagementServer implements Runnable {

    private static final int PORT = 8080;
    private boolean isStopped = false;
    private Thread runningThread = null;

    ServerSocketChannel serverSocketChannel;

    public JoofluxManagementServer() throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
    }

    public JoofluxManagementServer(String hostname) throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(hostname, PORT));
    }

    public void run(){
        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }

        while(!isStopped()){
            SocketChannel clientSocket = null;
            try {
                clientSocket = this.serverSocketChannel.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            new Thread( new WorkerRunnable(clientSocket) ).start();
        }

        Logger.info("Server Stopped.");
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
}
