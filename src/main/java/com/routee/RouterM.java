package com.routee;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.PoolRouter;
import akka.actor.typed.javadsl.Routers;

public class RouterM {

    private final ActorContext<NotUsed> context;

    public RouterM(ActorContext<NotUsed> context) {
        this.context = context;
    }

    public static void main(String[] args) {
        ActorSystem<NotUsed> system = ActorSystem.create(RouterM.create(), "router-");
    }

    private static Behavior<NotUsed> create() {
//        Behaviors.setup(context -> new RouterM(context).behavior());
        Behaviors.setup(
                context -> {
                    int poolSize = 4;
                    PoolRouter<Worker.Command> pool =
                            Routers.pool(
                                    poolSize,
                                    // make sure the workers are restarted if they fail
                                    Behaviors.supervise(Worker.create()).onFailure(SupervisorStrategy.restart()));
                    ActorRef<Worker.Command> router = context.spawn(pool, "worker-pool");

                    for (int i = 0; i < 10; i++) {
                        router.tell(new Worker.DoLog("msg " + i));
                    }
                    return null;
                });
        return Behaviors.empty();
    }
}
