package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Aggregator extends AbstractActor {
    static public Props props() {
        return Props.create(Aggregator.class, () -> new Aggregator());
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return waitingForReplies(0);
    }

    public Receive waitingForReplies(final int wordsCount) {
        return receiveBuilder().match(FileParser.StartOfFileEvent.class, event -> {
            log.info("filePath=" + event.filePath + ", StartOfFileEvent");
        }).match(FileParser.LineEvent.class, lineEvent -> {
            // log.info(lineEvent.line);
            int newWordsCount = wordsCount;
            newWordsCount += lineEvent.line.split(" ").length;
            getContext().become(waitingForReplies(newWordsCount));
        }).match(FileParser.EndOfFileEvent.class, event -> {
            log.info("filePath=" + event.filePath + ", EndOfFileEvent, wordsCount=" + wordsCount);
        }).build();
    }
}
