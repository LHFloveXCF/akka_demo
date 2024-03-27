package com.example;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

class SupervisingActor extends AbstractBehavior<String> {

    static Behavior<String> create() {
        return Behaviors.setup(SupervisingActor::new);
    }

    private final ActorRef<String> child;

    public SupervisingActor(ActorContext<String> context) {
        super(context);

        System.out.println("supervising start");

        child = context.spawn(
                        Behaviors.supervise(SupervisedActor.create()).onFailure(SupervisorStrategy.restart()),
                        "supervising-actor");

    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessageEquals("failChild", this::onFailChild).build();
    }

    private Behavior<String> onFailChild() {
        child.tell("fail");
        return this;
    }
}

class SupervisedActor extends AbstractBehavior<String> {

    static Behavior<String> create() {
        return Behaviors.setup(SupervisedActor::new);
    }

    public SupervisedActor(ActorContext<String> context) {
        super(context);
        System.out.println("SupervisedActor start");
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessageEquals("fail", this::fail).onSignal(PreRestart.class, e -> preStart()).onSignal(PostStop.class, e-> postStop()).build();
    }

    private Behavior<String> postStop() {
        System.out.println("SupervisedActor stop");
        return this;
    }

    private Behavior<String> preStart() {
        System.out.println("SupervisedActor restart");
        return this;
    }

    private Behavior<String> fail() {
        System.out.println("SupervisedActor fail");
        throw new RuntimeException("SupervisedActor fail");
    }
}

public class SupervisingDemo {
    public static void main(String[] args) {
        ActorRef<String> testSystem = ActorSystem.create(SupervisingActor.create(), "testSystem");
        testSystem.tell("failChild");
    }
}
