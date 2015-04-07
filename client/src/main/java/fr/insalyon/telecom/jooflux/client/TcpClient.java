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
import org.json.simple.parser.ParseException;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TcpClient {

    protected final static String END_OF_INSTRUCTION = "-QUIT-";
    protected SocketChannel socketChannel;

    public TcpClient(String hostname, int port) throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(hostname, port));
    }

    public TcpClient() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(8080));
    }

    public TcpClient(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void disconnect() throws IOException {
        socketChannel.close();
        Logger.debug("Disconnected !");
    }

    public List<String> getRegisteredCallSiteKeys() throws IOException, ParseException {
        Map obj = new LinkedHashMap();
        obj.put("call", "getRegisteredCallSiteKeys");

        String stringResult = send(JSONValue.toJSONString(obj));
        List<String> res = (List<String>) getReturn(stringResult);

        return res;
    }

    public String getNumberOfRegisteredCallSites() throws IOException {
        Map obj = new LinkedHashMap();
        obj.put("call", "getNumberOfRegisteredCallSites");

        String stringResult = send(JSONValue.toJSONString(obj));
        return (String) getReturn(stringResult);
    }

    public String getCallSiteType(String target) throws IOException {
        Map obj = new LinkedHashMap();
        obj.put("call", "getCallSiteType");
        obj.put("target", target);

        String stringResult = send(JSONValue.toJSONString(obj));
        return (String) getReturn(stringResult);
    }

    public boolean changeCallSiteTarget(String methodType, String oldTarget, String newTarget) throws IOException {
        Map obj = new LinkedHashMap();
        obj.put("call", "changeCallSiteTarget");
        obj.put("methodType", methodType);
        obj.put("oldTarget", oldTarget);
        obj.put("newTarget", newTarget);

        String stringResult = send(JSONValue.toJSONString(obj));
        JSONObject jsonResult = (JSONObject) JSONValue.parse(stringResult);

        return isThatOk(jsonResult);
    }

    public boolean applyBeforeAspect(String callSitesKey, String aspectClass, String aspectMethod) throws IOException {
        Map obj = new LinkedHashMap();
        obj.put("call", "applyBeforeAspect");
        obj.put("callSitesKey", callSitesKey);
        obj.put("aspectClass", aspectClass);
        obj.put("aspectMethod", aspectMethod);

        String stringResult = send(JSONValue.toJSONString(obj));
        JSONObject jsonResult = (JSONObject) JSONValue.parse(stringResult);

        return isThatOk(jsonResult);
    }

    public boolean applyAfterAspect(String callSitesKey, String aspectClass, String aspectMethod) throws IOException {
        Map obj = new LinkedHashMap();
        obj.put("call", "applyAfterAspect");
        obj.put("callSitesKey", callSitesKey);
        obj.put("aspectClass", aspectClass);
        obj.put("aspectMethod", aspectMethod);

        String stringResult = send(JSONValue.toJSONString(obj));
        JSONObject jsonResult = (JSONObject) JSONValue.parse(stringResult);

        return isThatOk(jsonResult);
    }

    protected String send(String message) throws IOException {
        String newData = message + END_OF_INSTRUCTION;
        Logger.debug("Sending : " + newData);

        ByteBuffer buf = writeBuffer(newData);

        while (buf.hasRemaining()) {
            socketChannel.write(buf);
        }

        Logger.debug("Sent");
        return readAnswer();
    }

    private ByteBuffer writeBuffer(String newData) {
        ByteBuffer buf = ByteBuffer.allocate(256);
        buf.clear();
        buf.put(newData.getBytes());

        buf.flip();
        return buf;
    }

    protected String readAnswer() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(256);
        int bytesRead = socketChannel.read(buf);

        StringBuilder sb = new StringBuilder();
        while (bytesRead != -1) {

            readBuffer(buf, sb);

            if (sb.toString().contains(END_OF_INSTRUCTION)) {
                String instruction = extractInstruction(sb);

                Logger.debug("Received instruction : " + instruction);
                return instruction;
            }

            bytesRead = socketChannel.read(buf);
        }

        return null;
    }

    private void readBuffer(ByteBuffer buf, StringBuilder sb) {
        buf.flip();

        while(buf.hasRemaining()){
            sb.append((char) buf.get());
        }

        buf.clear();
    }

    private String extractInstruction(StringBuilder sb) {
        int indexOfQuit = sb.toString().indexOf(END_OF_INSTRUCTION);
        String instruction = sb.substring(0, indexOfQuit);
        sb.delete(0, indexOfQuit + END_OF_INSTRUCTION.length());
        return instruction;
    }

    private boolean isThatOk(JSONObject obj) {
        String result = (String) obj.get("result");

        switch (result) {
            case "ok":
                String calledMethod = (String) obj.get("calledMethod");
                Logger.debug("Method '" + calledMethod + "' successfully called");
                return true;
            case "ko":
                String error = (String) obj.get("error");
                String message = (String) obj.get("message");
                Logger.debug("Server returned error '" + error + "' with message : " + message);
                return false;
            default:
                return false;
        }
    }

    private Object getReturn(String result) throws IOException {
        JSONObject jsonResult = (JSONObject) JSONValue.parse(result);

        if (isThatOk(jsonResult)) {
            return jsonResult.get("return");
        }
        else {
            return null;
        }
    }
}
