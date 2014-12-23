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

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TestableClient.class)
public class TcpClientTest {

    private final static String END_OF_INSTRUCTION = "-QUIT-";

    @Test
    public void testChangeCallSiteTarget() throws Exception {
        final String expected_answer = "{\"result\":\"ok\",\"calledMethod\":\"changeCallSiteTarget\"}" + END_OF_INSTRUCTION;

        final String sent_call = "changeCallSiteTarget";
        final String sent_methodType = "virtual";
        final String sent_oldTarget = "HelloWorld.tick:(HelloWorld)void";
        final String sent_newTarget = "HelloWorld.tack:()V";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        boolean ok = theClient.changeCallSiteTarget(sent_methodType, sent_oldTarget, sent_newTarget);
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(sent_methodType, objIn.get("methodType"));
        assertEquals(sent_oldTarget, objIn.get("oldTarget"));
        assertEquals(sent_newTarget, objIn.get("newTarget"));
        assertTrue(ok);
    }

    @Test
    public void testApplyBeforeAspect() throws Exception {
        final String expected_answer = "{\"result\":\"ok\",\"calledMethod\":\"applyBeforeAspect\"}" + END_OF_INSTRUCTION;

        final String sent_call = "applyBeforeAspect";
        final String sent_callSitesKey = "HelloWorld.tick:(HelloWorld)void";
        final String sent_aspectClass = "HelloWorld";
        final String sent_aspectMethod = "onCall";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        boolean ok = theClient.applyBeforeAspect(sent_callSitesKey, sent_aspectClass, sent_aspectMethod);
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(sent_callSitesKey, objIn.get("callSitesKey"));
        assertEquals(sent_aspectClass, objIn.get("aspectClass"));
        assertEquals(sent_aspectMethod, objIn.get("aspectMethod"));
        assertTrue(ok);
    }

    @Test
    public void testApplyAfterAspect() throws Exception {
        final String expected_answer = "{\"result\":\"ok\",\"calledMethod\":\"applyAfterAspect\"}" + END_OF_INSTRUCTION;

        final String sent_call = "applyAfterAspect";
        final String sent_callSitesKey = "HelloWorld.tick:(HelloWorld)void";
        final String sent_aspectClass = "HelloWorld";
        final String sent_aspectMethod = "onReturn";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        boolean ok = theClient.applyAfterAspect(sent_callSitesKey, sent_aspectClass, sent_aspectMethod);
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(sent_callSitesKey, objIn.get("callSitesKey"));
        assertEquals(sent_aspectClass, objIn.get("aspectClass"));
        assertEquals(sent_aspectMethod, objIn.get("aspectMethod"));
        assertTrue(ok);
    }

    @Test
    public void testGetCallSiteTypeOk() throws Exception {
        final String expected_answer = "{\"result\":\"ok\",\"return\":\"virtual\",\"calledMethod\":\"getCallSiteType\"}" + END_OF_INSTRUCTION;
        final String expectedResult = "virtual";

        final String sent_call = "getCallSiteType";
        final String sent_target = "HelloWorld.tick:(HelloWorld)void";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        String result = theClient.getCallSiteType(sent_target);
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(sent_target, objIn.get("target"));
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetCallSiteTypeKo() throws Exception {
        final String expected_answer = "{\"result\":\"ko\",\"error\":\"unknownTarget\",\"message\":\"The target 'HelloWorld.tick:(HelloWorld)vd' is not registered.\"}" + END_OF_INSTRUCTION;
        final String expectedResult = null;

        final String sent_call = "getCallSiteType";
        final String sent_target = "HelloWorld.notamethod";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        String result = theClient.getCallSiteType(sent_target);
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(sent_target, objIn.get("target"));
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetNumberOfRegisteredCallSites() throws Exception {
        final String expected_answer = "{\"result\":\"ok\",\"return\":\"1\",\"calledMethod\":\"getNumberOfRegisteredCallSites\"}" + END_OF_INSTRUCTION;
        final String expectedResult = "1";

        final String sent_call = "getNumberOfRegisteredCallSites";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        String result = theClient.getNumberOfRegisteredCallSites();
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetRegisteredCallSiteKeys() throws Exception {
        final String expected_answer = "{\"result\":\"ok\",\"calledMethod\":\"getRegisteredCallSiteKeys\",\"return\":[\"HelloWorld.tick:(HelloWorld)void\"]}" + END_OF_INSTRUCTION;
        final List<String> expectedResult = new ArrayList<String>() {{ add("HelloWorld.tick:(HelloWorld)void"); }};

        final String sent_call = "getRegisteredCallSiteKeys";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        List<String> result = theClient.getRegisteredCallSiteKeys();
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(expectedResult.get(0), result.get(0));
    }

    @Test
    public void testUnknownMethod() throws Exception {
        final String expected_answer = "{\"result\":\"ko\",\"error\":\"unknownMethod\",\"message\":\"Method 'makeCoffee' doesn't exist.\"}" + END_OF_INSTRUCTION;

        final String sent_call = "changeCallSiteTarget";
        final String sent_methodType = "virtual";
        final String sent_oldTarget = "HelloWorld.tick:(HelloWorld)void";
        final String sent_newTarget = "HelloWorld.tack:()V";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        boolean ok = theClient.changeCallSiteTarget(sent_methodType, sent_oldTarget, sent_newTarget);
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(sent_methodType, objIn.get("methodType"));
        assertEquals(sent_oldTarget, objIn.get("oldTarget"));
        assertEquals(sent_newTarget, objIn.get("newTarget"));
        assertFalse(ok);
    }

    @Test
    public void testServerUnknownAnswer() throws Exception {
        final String expected_answer = "{\"result\":\"foo\"}" + END_OF_INSTRUCTION;

        final String sent_call = "changeCallSiteTarget";
        final String sent_methodType = "virtual";
        final String sent_oldTarget = "HelloWorld.tick:(HelloWorld)void";
        final String sent_newTarget = "HelloWorld.tack:()V";

        SocketChannel socket = prepareRequestInjection(expected_answer);

        ArgumentCaptor<ByteBuffer> argument = forClass(ByteBuffer.class);

        TestableClient theClient = new TestableClient(socket);
        boolean ok = theClient.changeCallSiteTarget(sent_methodType, sent_oldTarget, sent_newTarget);
        theClient.disconnect();

        verify(socket).write(argument.capture());
        String sent_message = getAnswerFromBuffer(argument.getValue());

        JSONObject objIn = (JSONObject) JSONValue.parse(sent_message);

        assertEquals(sent_call, objIn.get("call"));
        assertEquals(sent_methodType, objIn.get("methodType"));
        assertEquals(sent_oldTarget, objIn.get("oldTarget"));
        assertEquals(sent_newTarget, objIn.get("newTarget"));
        assertFalse(ok);
    }

    private SocketChannel prepareRequestInjection(String request) throws IOException {
        ByteBuffer requestBuffer = ByteBuffer.allocate(256);
        requestBuffer.clear();
        requestBuffer.put(request.getBytes());

        SocketChannel socket = mock(SocketChannel.class);
        when(socket.read(requestBuffer)).thenReturn(request.length(), -1);

        mockStatic(ByteBuffer.class);
        when(ByteBuffer.allocate(anyInt())).thenCallRealMethod().thenReturn(requestBuffer);
        return socket;
    }

    private String getAnswerFromBuffer(ByteBuffer value) {
        String stringifiedAnswer = readBuffer(value);

        return removeEndOfInstruction(stringifiedAnswer);
    }

    private String removeEndOfInstruction(String stringifiedAnswer) {
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