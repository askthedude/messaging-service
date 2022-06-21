package web.router;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.controller.HealthCheckController;

public class HealthcheckRouter {
    private static final Logger LOGGER = LogManager.getLogger(HealthcheckRouter.class);
    private final HealthCheckController controller;

    public HealthcheckRouter(HealthCheckController controller) {
        this.controller = controller;
    }

    public Router router(Vertx vertx) {
        var router = Router.router(vertx);
        router.get("/healthcheck")
                .handler(this::handleHealthCheck);
        return router;
    }

    private void handleHealthCheck(RoutingContext ctx) {
        try {
            var jsonResponse = JsonObject.mapFrom(controller.checkHealth());
            ctx.end(jsonResponse.toBuffer());
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }
}
