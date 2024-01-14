package com.ukg.docgen.http.verticles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ukg.docgen.http.handlers.PostJobHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.cluster.infinispan.ClusterHealthCheck;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;

public class HttpVerticle extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(HttpVerticle.class);

  private static final int HTTP_PORT =
      Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "8080"));

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    setupRouter(router);

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(
            HTTP_PORT,
            http -> {
              if (http.succeeded()) {
                startPromise.complete();
                log.info("HTTP Server started on port {}", http.result().actualPort());
              } else {
                startPromise.fail(http.cause());
              }
            });
  }

  private void setupRouter(Router router) {
    router.route().handler(LoggerHandler.create(LoggerFormat.SHORT));
    router.route().handler(ResponseTimeHandler.create());
    router.post("/job").handler(createJobHandler());
    router.get("/job/:id").handler(createJobHandler());
    router.delete("/job/:id").handler(createJobHandler());
    router.get("/job/:id/output").handler(createJobHandler());
    router.delete("/job/:id/output").handler(createJobHandler());
    Handler<Promise<Status>> procedure = ClusterHealthCheck.createProcedure(vertx, false);
    HealthChecks checks = HealthChecks.create(vertx).register("cluster-health", procedure);
    router.get("/readiness").handler(HealthCheckHandler.createWithHealthChecks(checks));
  }

  private PostJobHandler createJobHandler() {
    return new PostJobHandler();
  }

  public static void main(String[] args) {
    Vertx.clusteredVertx(new VertxOptions())
        .compose(vertx -> vertx.deployVerticle(new HttpVerticle()))
        .onFailure(t -> t.printStackTrace());
  }
}
