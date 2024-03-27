package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

class StartStopActor1 extends AbstractBehavior<String> {

    static Behavior<String> create() {
        return Behaviors.setup(StartStopActor1::new);
    }

    public StartStopActor1(ActorContext<String> context) {
        super(context);
        System.out.println("first start");

        context.spawn(StartStopActor2.create(), "second");
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessageEquals("stop", Behaviors::stopped).onSignal(PostStop.class, e -> onPostStop()).build();
    }

    private Behavior<String> onPostStop() {
        System.out.println("first stop");
        return this;
    }
}

class StartStopActor2 extends AbstractBehavior<String> {

    static Behavior<String> create() {
        return Behaviors.setup(StartStopActor2::new);
    }

    public StartStopActor2(ActorContext<String> context) {
        super(context);
        System.out.println("second start");
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, e -> onPostStop()).build();
    }

    private Behavior<String> onPostStop() {
        System.out.println("second stop");
        return this;
    }
}

public class StartStopDemo {
    public static void main(String[] args) {
        ActorRef<String> testSystem = ActorSystem.create(StartStopActor1.create(), "testSystem");
        testSystem.tell("stop");
    }
}
