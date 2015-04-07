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
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TestableWorkerRunnable.class)
public class TcpServerTest {
    private final static String END_OF_INSTRUCTION = "-QUIT-";

    @Test
    public void testChangeCallSiteTarget() throws Exception {
        final String request = "{\"call\":\"changeCallSiteTarget\",\"methodType\":\"virtual\",\"oldTarget\":\"HelloWorld.tick:(HelloWorld)void\",\"newTarget\":\"HelloWorld.tack:()V\"}" + END_OF_INSTRUCTION;
        final String answer_result = "ok";
        final String answer_calledMethod= "changeCallSiteTarget";

        SocketChannel socket = prepareRequestInjection(request);

        JooFluxManagement jooFluxManagement = mock(JooFluxManagement.class);
        doNothing().when(jooFluxManagement).changeCallSiteTarget("virtual", "HelloWorld.tick:(HelloWorld)void", "HelloWorld.tack:()V");

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket, jooFluxManagement);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_calledMethod, objIn.get("calledMethod"));
    }


    @Test
    public void testApplyBeforeAspect() throws Exception {
        final String request = "{\"call\":\"applyBeforeAspect\",\"callSitesKey\":\"HelloWorld.tick:(HelloWorld)void\",\"aspectClass\":\"HelloWorld\",\"aspectMethod\":\"onCall\"}" + END_OF_INSTRUCTION;
        final String answer_result = "ok";
        final String answer_calledMethod = "applyBeforeAspect";

        SocketChannel socket = prepareRequestInjection(request);

        JooFluxManagement jooFluxManagement = mock(JooFluxManagement.class);
        doNothing().when(jooFluxManagement).applyBeforeAspect("HelloWorld.tick:(HelloWorld)void", "HelloWorld", "onCall");

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket, jooFluxManagement);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_calledMethod, objIn.get("calledMethod"));
    }

    @Test
    public void testApplyAfterAspect() throws Exception {
        final String request = "{\"call\":\"applyAfterAspect\",\"callSitesKey\":\"HelloWorld.tick:(HelloWorld)void\",\"aspectClass\":\"HelloWorld\",\"aspectMethod\":\"onReturn\"}" + END_OF_INSTRUCTION;
        final String answer_result = "ok";
        final String answer_calledMethod = "applyAfterAspect";

        SocketChannel socket = prepareRequestInjection(request);

        JooFluxManagement jooFluxManagement = mock(JooFluxManagement.class);
        doNothing().when(jooFluxManagement).applyAfterAspect("HelloWorld.tick:(HelloWorld)void", "HelloWorld", "onReturn");

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket, jooFluxManagement);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_calledMethod, objIn.get("calledMethod"));
    }

    @Test
    public void testGetCallSiteTypeOk() throws Exception {
        final String request = "{\"call\":\"getCallSiteType\",\"target\":\"HelloWorld.tick:(HelloWorld)void\"}" + END_OF_INSTRUCTION;

        final String answer_result = "ok";
        final String answer_return = "virtual";
        final String answer_calledMethod = "getCallSiteType";

        SocketChannel socket = prepareRequestInjection(request);

        JooFluxManagement jooFluxManagement = mock(JooFluxManagement.class);
        when(jooFluxManagement.getCallSiteType("HelloWorld.tick:(HelloWorld)void")).thenReturn("virtual");

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket, jooFluxManagement);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_return, objIn.get("return"));
        assertEquals(answer_calledMethod, objIn.get("calledMethod"));
    }

    @Test
    public void testGetCallSiteTypeKo() throws Exception {
        final String request = "{\"call\":\"getCallSiteType\",\"target\":\"HelloWorld.notamethod\"}" + END_OF_INSTRUCTION;

        final String answer_result = "ko";
        final String answer_error = "unknownTarget";
        final String answer_message = "The target 'HelloWorld.notamethod' is not registered.";

        SocketChannel socket = prepareRequestInjection(request);

        JooFluxManagement jooFluxManagement = mock(JooFluxManagement.class);
        when(jooFluxManagement.getCallSiteType("HelloWorld.notamethod")).thenThrow(NullPointerException.class);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket, jooFluxManagement);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_error, objIn.get("error"));
        assertEquals(answer_message, objIn.get("message"));
    }

    @Test
    public void testGetNumberOfRegisteredCallSites() throws Exception {
        final String request = "{\"call\":\"getNumberOfRegisteredCallSites\"}" + END_OF_INSTRUCTION;

        final String answer_result = "ok";
        final String answer_return = "1";
        final String answer_calledMethod = "getNumberOfRegisteredCallSites";

        SocketChannel socket = prepareRequestInjection(request);

        JooFluxManagement jooFluxManagement = mock(JooFluxManagement.class);
        when(jooFluxManagement.getNumberOfRegisteredCallSites()).thenReturn(1);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket, jooFluxManagement);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_return, objIn.get("return"));
        assertEquals(answer_calledMethod, objIn.get("calledMethod"));
    }

    @Test
    public void testGetRegisteredCallSiteKeys() throws Exception {
        final String request = "{\"call\":\"getRegisteredCallSiteKeys\"}" + END_OF_INSTRUCTION;

        final String answer_result = "ok";
        final List<String> answer_return = new ArrayList<String>() {{ add("HelloWorld.tick:(HelloWorld)void"); }};
        final String answer_calledMethod = "getRegisteredCallSiteKeys";

        final Set<String> registredCallSites = new HashSet<String>() {{ add("HelloWorld.tick:(HelloWorld)void"); }};

        SocketChannel socket = prepareRequestInjection(request);

        JooFluxManagement jooFluxManagement = mock(JooFluxManagement.class);
        when(jooFluxManagement.getRegisteredCallSiteKeys()).thenReturn(registredCallSites);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket, jooFluxManagement);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_return.get(0), ((List<String>) objIn.get("return")).get(0));
        assertEquals(answer_calledMethod, objIn.get("calledMethod"));
    }

    @Test
    public void testUnknownMethod() throws Exception {
        final String request = "{\"call\":\"makeCoffee\"}" + END_OF_INSTRUCTION;
        final String answer_result = "ko";
        final String answer_error = "unknownMethod";
        final String answer_message = "Method 'makeCoffee' doesn't exist.";

        SocketChannel socket = prepareRequestInjection(request);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_error, objIn.get("error"));
        assertEquals(answer_message, objIn.get("message"));
    }

    @Test
    public void testNullPointerException() throws Exception {
        final String request = "{\"callMe\":\"blondie\"}" + END_OF_INSTRUCTION;
        final String answer_result = "ko";
        final String answer_error = "unexpectedAttribut";
        final String answer_message = "{\"callMe\":\"blondie\"}";

        SocketChannel socket = prepareRequestInjection(request);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_error, objIn.get("error"));
        assertEquals(answer_message, objIn.get("message"));
    }

    @Test
    public void testUnexpectedAttributException() throws Exception {
        final String request = "{\"call\":\"applyAfterAspect\"}" + END_OF_INSTRUCTION;
        final String answer_result = "ko";
        final String answer_error = "unexpectedAttribut";
        final String answer_message = "{\"call\":\"applyAfterAspect\"}";

        SocketChannel socket = prepareRequestInjection(request);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket);
        workerRunnable.run();

        verify(socket).write(argument.capture());

        String answer = getAnswerFromBuffer(argument.getValue());
        JSONObject objIn = (JSONObject) JSONValue.parse(answer);

        assertEquals(answer_result, objIn.get("result"));
        assertEquals(answer_error, objIn.get("error"));
        assertEquals(answer_message, objIn.get("message"));
    }

    @Test(expected = RuntimeException.class)
    public void testIOException() throws Exception {
        SocketChannel socket = mock(SocketChannel.class);
        when(socket.read(Matchers.<ByteBuffer>any())).thenThrow(IOException.class);

        TestableWorkerRunnable workerRunnable = new TestableWorkerRunnable(socket);
        workerRunnable.run();
    }

    private SocketChannel prepareRequestInjection(String request) throws IOException {
        ByteBuffer requestBuffer = ByteBuffer.allocate(256);
        requestBuffer.clear();
        requestBuffer.put(request.getBytes());

        SocketChannel socket = mock(SocketChannel.class);
        when(socket.read(requestBuffer)).thenReturn(request.length(), -1);

        mockStatic(ByteBuffer.class);
        when(ByteBuffer.allocate(anyInt())).thenReturn(requestBuffer);
        return socket;
    }

    private String getAnswerFromBuffer(ByteBuffer value) {
        String stringifiedAnswer = readBuffer(value);
        return stringifiedAnswer.substring(0, stringifiedAnswer.length() - END_OF_INSTRUCTION.length());
    }

    private String readBuffer(ByteBuffer buf) {
        StringBuilder sb = new StringBuilder();

        while (buf.hasRemaining()) {
            sb.append((char) buf.get());
        }

        buf.clear();
        return sb.toString();
    }
}
