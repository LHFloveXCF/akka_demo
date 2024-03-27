package com.routee;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;

class Proxy {

  public final ServiceKey<Message> registeringKey =
      ServiceKey.create(Message.class, "aggregator-key");

  public String mapping(Message message) {
    return message.getId();
  }

  static class Message {

    public Message(String id, String content) {
      this.id = id;
      this.content = content;
    }

    private String content;
    private String id;

    public final String getContent() {
      return content;
    }

    public final String getId() {
      return id;
    }
  }

  static Behavior<Message> create(ActorRef<String> monitor) {
    return Behaviors.receive(Message.class)
        .onMessage(Message.class, in -> onMyMessage(monitor, in))
        .build();
  }

  private static Behavior<Message> onMyMessage(ActorRef<String> monitor, Message message) {
    monitor.tell(message.getId());
    return Behaviors.same();
  }
}