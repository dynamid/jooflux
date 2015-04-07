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

import fr.insalyon.telecom.jooflux.internal.jmx.JooFluxManagement;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TestableWorkerRunnable extends WorkerRunnable {

    public TestableWorkerRunnable(SocketChannel socketChannel, JooFluxManagement jooFluxManagement) {
        super(socketChannel, jooFluxManagement);
    }

    public TestableWorkerRunnable(SocketChannel socketChannel) {
        super(socketChannel);
    }

    @Override
    protected void send(String message) throws IOException {
        String answer = message + END_OF_INSTRUCTION;

        ByteBuffer buf = ByteBuffer.allocate(256);
        buf.clear();
        buf.put(answer.getBytes());
        buf.flip();

        socketChannel.write(buf);
    }
}
