package com.lightbend.akka.sample;

import java.io.File;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class FileScanner extends AbstractActor {
    static public Props props(String preDefinedDirectory) {
        return Props.create(FileScanner.class, () -> new FileScanner(preDefinedDirectory));
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

    public FileScanner(String preDefinedDirectory) {
        this.preDefinedDirectory = preDefinedDirectory;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(startMessage.class, start -> {
            // 1.scan floder and read all txt file
            // 2. tell Parser to parser log files
            // TODO: check log file type
            // TOD
            File actual = new File(this.preDefinedDirectory);
            for (File f : actual.listFiles()) {
                final ActorRef aggregator = getContext().actorOf(Aggregator.props(), "aggregator_" + f.getName());
                final ActorRef fileParser = getContext().actorOf(FileParser.props(aggregator),
                        "fileParser_" + f.getName());
                fileParser.tell(new ParseFileMessage(f.getPath()), getSelf());
            }
        }).build();
    }
}
