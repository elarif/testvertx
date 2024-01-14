package com.ukg.docgen.http.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class DeleteJobHandler implements Handler<RoutingContext> {

  private String message = "pong";

  public void handle(RoutingContext context) {
    context.response().setStatusCode(200).end(message);
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
