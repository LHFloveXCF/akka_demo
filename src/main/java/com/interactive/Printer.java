package com.interactive;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

/**
 * 即发即忘的模式
 * 1. 不确定消息送达了
 * 2、 消息可能丢失
 */
public class Printer {
    public static class PrintMe {
        public final String message;

        public PrintMe(String message) {
            this.message = message;
        }
    }

    public static Behavior<PrintMe> create() {
        return Behaviors.setup(
                context ->
                        Behaviors.receive(PrintMe.class)
                                .onMessage(
                                        PrintMe.class,
                                        printMe -> {
                                            context.getLog().info(printMe.message);
                                            return Behaviors.same();
                                        })
                                .build());
    }

    public static void main(String[] args) {
        final ActorSystem<PrintMe> system = ActorSystem.create(Printer.create(), "printer-sample-system");

// note that system is also the ActorRef to the guardian actor
        final ActorRef<PrintMe> ref = system;

// these are all fire and forget
        ref.tell(new Printer.PrintMe("message 1"));
        ref.tell(new Printer.PrintMe("message 2"));
    }
}