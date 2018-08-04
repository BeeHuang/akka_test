package com.lightbend.akka.sample;

import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class AkkaQuickstart {

    private static final String preDefinedDirectory = "tmp";

    public static void main(String[] args) {

        final ActorSystem system = ActorSystem.create("fileScanner");
        try {
            // TODO: add test case
            final ActorRef fileScanner = system.actorOf(FileScanner.props(preDefinedDirectory), "fileScanner");
            fileScanner.tell(new FileScanner.startMessage(), ActorRef.noSender());
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}
