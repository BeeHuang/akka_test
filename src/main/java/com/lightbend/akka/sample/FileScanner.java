package com.lightbend.akka.sample;

import java.io.File;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class FileScanner extends AbstractActor {
    static public Props props(String preDefinedDirectory, ActorRef fileParser) {
        return Props.create(FileScanner.class, () -> new FileScanner(preDefinedDirectory, fileParser));
    }

    static public class ParseFileMessage {
        public final String filePath;

        public ParseFileMessage(String filePath) {
            this.filePath = filePath;
        }
    }

    static public class startMessage {
    }

    private final String preDefinedDirectory;
    private final ActorRef fileParser;

    public FileScanner(String preDefinedDirectory, ActorRef fileParser) {
        this.preDefinedDirectory = preDefinedDirectory;
        this.fileParser = fileParser;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(startMessage.class, start -> {
            // 1.scan floder and read all txt file
            // 2. tell Parser to parser log files
            // TODO: check log file type
            // TODO: can one file use one fileParser
            File actual = new File(this.preDefinedDirectory);
            for (File f : actual.listFiles()) {
                fileParser.tell(new ParseFileMessage(f.getPath()), getSelf());
            }
        }).build();
    }
}
