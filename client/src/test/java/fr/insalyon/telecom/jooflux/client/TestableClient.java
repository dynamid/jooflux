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

package fr.insalyon.telecom.jooflux.client;

import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TestableClient extends TcpClient {
    public TestableClient(SocketChannel socketChannel) {
        super(socketChannel);
    }

    @Override
    protected String send(String message) throws IOException {
        String newData = message + END_OF_INSTRUCTION;
        Logger.debug(newData);

        ByteBuffer buf = ByteBuffer.allocate(256);
        buf.clear();
        buf.put(newData.getBytes());

        buf.flip();

        socketChannel.write(buf);

        return readAnswer();
    }
}
