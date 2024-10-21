package io.tmgg.logview;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TailFile extends Thread {

    public static final int BUFFER_SIZE = 100;

    private final int sleepTime;
    private long lastFilePosition = 0;
    private boolean shouldIRun = true;
    private final File crunchifyFile;

    Consumer<List<String>> onMessage;

    private List<String> buffer = new ArrayList<>(BUFFER_SIZE);

    public TailFile(File myFile, int myInterval, Consumer<List<String>> onMessage) {
        crunchifyFile = myFile;
        this.sleepTime = myInterval;
        this.onMessage = onMessage;
    }

    private void onReadLine(String message) {
        message = new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        buffer.add(message);
        if (buffer.size() > BUFFER_SIZE) {
            triggerMessageList();
        }
    }

    private synchronized void triggerMessageList() {
        if (!buffer.isEmpty()) {
            onMessage.accept(Collections.unmodifiableList(buffer));
            buffer.clear();
        }
    }

    public void stopRunning() {
        shouldIRun = false;
    }

    public void run() {
        try {
            while (shouldIRun) {
                triggerMessageList();

                Thread.sleep(sleepTime);
                long fileLength = crunchifyFile.length();
                if (fileLength > lastFilePosition) {
                    // Reading and writing file
                    RandomAccessFile accessFile = new RandomAccessFile(crunchifyFile, "r");
                    accessFile.seek(lastFilePosition);
                    String crunchifyLine = null;
                    while ((crunchifyLine = accessFile.readLine()) != null) {
                        this.onReadLine(crunchifyLine);
                    }
                    lastFilePosition = accessFile.getFilePointer();
                    accessFile.close();
                }
            }
        } catch (Exception e) {
            stopRunning();
        }

    }


}
