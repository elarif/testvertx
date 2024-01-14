package com.ukg.docgen.http.handlers;

import java.util.concurrent.TimeUnit;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class PostJobHandler implements Handler<RoutingContext> {

  public void handle(RoutingContext context) {
    try {
      TimeUnit.MILLISECONDS.sleep(100);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    context.response().setStatusCode(200).end();
  }
}
