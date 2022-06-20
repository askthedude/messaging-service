package web;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.router.HealthcheckRouter;
import web.router.TimerRouter;

public class WebServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger(WebServerVerticle.class);


    private final int port;
    private final HealthcheckRouter healthCheckRouter;
    private final TimerRouter timerRouter;


    public WebServerVerticle(int port, HealthcheckRouter healthCheckRouter, TimerRouter timerRouter) {
        this.port = port;
        this.healthCheckRouter = healthCheckRouter;
        this.timerRouter = timerRouter;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        var server = vertx.createHttpServer();
        var mainRouter = setUpRoutersHierarchy();
        server.requestHandler(mainRouter).listen(port);
        startPromise.complete();
    }

    private Router setUpRoutersHierarchy(){
        var baseRouter = Router.router(vertx);
        baseRouter.route("/*")
                        .handler(this::handleLogging);
        baseRouter.route("/*")
                        .failureHandler(this::handleFailure);
        baseRouter.route("/api/*")
                        .subRouter(healthCheckRouter.router(vertx));
        baseRouter.route("/api/*")
                .subRouter(timerRouter.router(vertx));

        return baseRouter;
    }

    /*
        Later we'll add here different scenarios for different error codes returned
     */
    private void handleFailure(RoutingContext e) {
        if (e.failed()) {
            e.response()
                    .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                    .end(new JsonObject().put("message", "Some problem on server occured").toBuffer());
        }
    }

    private void handleLogging(RoutingContext ctx){
        var request = ctx.request();
        var URI = request.uri();
        var method = request.method().toString();
        LOGGER.info("Got HTTP {} request on the URI: {}", method, URI);
        ctx.next();
    }
}
