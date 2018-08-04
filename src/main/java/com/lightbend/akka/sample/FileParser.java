package com.lightbend.akka.sample;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;

public class FileParser extends AbstractActor {
    static public Props props(ActorRef aggregator) {
        return Props.create(FileParser.class, () -> new FileParser(aggregator));
    }

    static public class LineEvent {
        public final String line;

        public LineEvent(String line) {
            this.line = line;
        }
    }

    static public class StartOfFileEvent {
        public final String filePath;

        public StartOfFileEvent(String filePath) {
            this.filePath = filePath;
        }
    }

    static public class EndOfFileEvent {
        public final String filePath;

        public EndOfFileEvent(String filePath) {
            this.filePath = filePath;
        }
    }

    private final ActorRef aggregator;

    // TODO: 應該是一個FileParser, 可以有多個Aggregator, 來提供併行讀取, 增快速度.
    public FileParser(ActorRef aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(FileScanner.ParseFileMessage.class, parseMessage -> {
            // 1. read file with lines
            // 2. and use aggregator actor to count words
            aggregator.tell(new StartOfFileEvent(parseMessage.filePath), getSelf());
            try (Stream<String> stream = Files.lines(Paths.get(parseMessage.filePath))) {
                stream.forEach(line -> aggregator.tell(new LineEvent(line), getSelf()));
            }
            aggregator.tell(new EndOfFileEvent(parseMessage.filePath), getSelf());
            getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
        }).build();
    }
}
