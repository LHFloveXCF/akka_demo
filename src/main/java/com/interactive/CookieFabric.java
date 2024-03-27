package com.interactive;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

/**
 * 请求响应模式
 */
public class CookieFabric {
    public static class Request {
        public final String query;
        public final ActorRef<Response> replyTo;

        public Request(String query, ActorRef<Response> replyTo) {
            this.query = query;
            this.replyTo = replyTo;
        }
    }

    public static class Response {
        public final String result;

        public Response(String result) {
            this.result = result;
        }
    }

    // actor behavior
    public static Behavior<Request> create() {
        return Behaviors.receive(Request.class)
                .onMessage(Request.class, CookieFabric::onRequest)
                .build();
    }

    private static Behavior<Request> onRequest(Request request) {
        // ... process request ...
        System.out.println("Here are the cookies for " + request.query);
        request.replyTo.tell(new Response("Here are the cookies for " + request.query));
        return Behaviors.same();
    }

    public static Behavior<Response> create2() {
        return Behaviors.receive(Response.class)
                .onMessage(Response.class, CookieFabric::onResponse)
                .build();
    }

    private static Behavior<Response> onResponse(Response request) {
        System.out.println("Here are the cookies for " + request.result);
        return Behaviors.same();
    }

    public static void main(String[] args) {
        final ActorSystem<Request> system = ActorSystem.create(CookieFabric.create(), "cookie-fabric-sample-system");
        final ActorSystem<Response> replyTo = ActorSystem.create(CookieFabric.create2(), "cookie-fabric-sample-system2");
        system.tell(new Request("give me cookies", replyTo));
    }
}
