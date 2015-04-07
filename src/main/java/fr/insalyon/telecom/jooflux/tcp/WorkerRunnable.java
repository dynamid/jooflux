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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class WorkerRunnable implements Runnable {

    protected final static String END_OF_INSTRUCTION = "-QUIT-";

    protected SocketChannel socketChannel = null;

    private JooFluxManagement jooFluxManagement;

    public WorkerRunnable(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.jooFluxManagement = new JooFluxManagement();
    }

    public WorkerRunnable(SocketChannel socketChannel, JooFluxManagement jooFluxManagement) {
        this.socketChannel = socketChannel;
        this.jooFluxManagement = jooFluxManagement;
    }

    public void run() {
        readBufferFromSocketAndExecute();
    }

    private void readBufferFromSocketAndExecute() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(256);
            int bytesRead = socketChannel.read(buf);

            StringBuilder sb = new StringBuilder();
            while (bytesRead != -1) {

                readBuffer(buf, sb);

                if (containsAnInstruction(sb)) {
                    String instruction = extractAndExecute(sb);
                    execute(instruction);
                }

                bytesRead = socketChannel.read(buf);
            }

            socketChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readBuffer(ByteBuffer buf, StringBuilder sb) {
        buf.flip();

        while (buf.hasRemaining()) {
            sb.append((char) buf.get());
        }
        buf.clear();
    }

    private String extractAndExecute(StringBuilder sb) throws IOException {
        int indexOfQuit = sb.toString().indexOf(END_OF_INSTRUCTION);
        String instruction = sb.substring(0, indexOfQuit);
        sb.delete(0, indexOfQuit + END_OF_INSTRUCTION.length());
        return instruction;
    }

    private boolean containsAnInstruction(StringBuilder sb) {
        return sb.toString().contains(END_OF_INSTRUCTION);
    }

    private void execute(String instruction) throws IOException {
        String calledMethod;
        JSONObject objIn = (JSONObject) JSONValue.parse(instruction);
        try {
            calledMethod = (String) objIn.get("call");
            switch (calledMethod) {
                case "changeCallSiteTarget":
                    changeCallSiteTarget(objIn);
                    sendOk(calledMethod);
                    break;
                case "applyBeforeAspect":
                    applyBeforeAspect(objIn);
                    sendOk(calledMethod);
                    break;
                case "applyAfterAspect":
                    applyAfterAspect(objIn);
                    sendOk(calledMethod);
                    break;
                case "getCallSiteType":
                    String target = (String) objIn.get("target");
                    checkIAnyfNull(target);
                    try {
                        String result = jooFluxManagement.getCallSiteType(target);
                        sendResult(result, calledMethod);
                    }
                    catch (NullPointerException e) {
                        sendError("unknownTarget", "The target '" + target + "' is not registered.");
                    }
                    break;
                case "getNumberOfRegisteredCallSites":
                    int nbOfRegisteredCallSites = jooFluxManagement.getNumberOfRegisteredCallSites();
                    sendResult(nbOfRegisteredCallSites+"", calledMethod);
                    break;
                case "getRegisteredCallSiteKeys":
                    Set<String> listOfRegisteredCallSiteKeys = jooFluxManagement.getRegisteredCallSiteKeys();
                    sendResult(jsonifyList(listOfRegisteredCallSiteKeys), calledMethod);
                    break;
                default:
                    sendError("unknownMethod", "Method '" + calledMethod + "' doesn't exist.");
                    break;
            }
        } catch (NullPointerException | UnexpectedAttribut e) {
            sendError("unexpectedAttribut", instruction);
        }
    }

    private void applyAfterAspect(JSONObject objIn) throws UnexpectedAttribut {
        String callSitesKeyAfter = (String) objIn.get("callSitesKey");
        String aspectClassAfter = (String) objIn.get("aspectClass");
        String aspectMethodAfter = (String) objIn.get("aspectMethod");
        checkIAnyfNull(callSitesKeyAfter, aspectClassAfter, aspectMethodAfter);
        jooFluxManagement.applyAfterAspect(callSitesKeyAfter, aspectClassAfter, aspectMethodAfter);
    }

    private void applyBeforeAspect(JSONObject objIn) throws UnexpectedAttribut {
        String callSitesKeyBefore = (String) objIn.get("callSitesKey");
        String aspectClassBefore = (String) objIn.get("aspectClass");
        String aspectMethodBefore = (String) objIn.get("aspectMethod");
        checkIAnyfNull(callSitesKeyBefore, aspectClassBefore, aspectMethodBefore);
        jooFluxManagement.applyBeforeAspect(callSitesKeyBefore, aspectClassBefore, aspectMethodBefore);
    }

    private void changeCallSiteTarget(JSONObject objIn) throws UnexpectedAttribut {
        String methodType = (String) objIn.get("methodType");
        String oldTarget = (String) objIn.get("oldTarget");
        String newTarget = (String) objIn.get("newTarget");
        checkIAnyfNull(methodType, oldTarget, newTarget);
        jooFluxManagement.changeCallSiteTarget(methodType, oldTarget, newTarget);
    }

    private JSONArray jsonifyList(Set<String> listOfRegisteredCallSiteKeys) {
        JSONArray list = new JSONArray();

        for (String callSiteKey : listOfRegisteredCallSiteKeys) {
            list.add(callSiteKey);
        }

        return list;
    }

    private void checkIAnyfNull(String... parsedString) throws UnexpectedAttribut {
        for (String s : parsedString) {
            if ( s == null ) {
                throw new UnexpectedAttribut();
            }
        }
    }

    private void sendResult(Object result, String calledMethod) throws IOException {
        JSONObject objOut = new JSONObject();
        objOut.put("result", "ok");
        objOut.put("calledMethod", calledMethod);
        objOut.put("return", result);

        send(objOut.toJSONString());
    }

    private void sendOk(String calledMethod) throws IOException {
        JSONObject objOut = new JSONObject();
        objOut.put("result", "ok");
        objOut.put("calledMethod", calledMethod);

        send(objOut.toJSONString());
    }


    private void sendError(String error, String message) throws IOException {
        JSONObject objResponse = new JSONObject();
        objResponse.put("result", "ko");
        objResponse.put("error", error);
        objResponse.put("message", message);

        send(objResponse.toJSONString());
    }

    protected void send(String message) throws IOException {
        String answer = message + END_OF_INSTRUCTION;

        ByteBuffer buf = writeBuffer(answer);

        buf.flip();

        while (buf.hasRemaining()) {
            socketChannel.write(buf);
        }
    }

    private ByteBuffer writeBuffer(String answer) {
        ByteBuffer buf = ByteBuffer.allocate(256);
        buf.clear();
        buf.put(answer.getBytes());
        return buf;
    }
}
