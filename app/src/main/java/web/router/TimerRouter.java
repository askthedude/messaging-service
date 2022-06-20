package web.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.controller.TimerController;

import java.util.UUID;

public class TimerRouter {
    private Vertx vertx;
    private final TimerController timerController;

    public TimerRouter(TimerController timerController) {
        this.timerController = timerController;
    }

    public Router router(Vertx vertx){
        this.vertx = vertx;
        var router = Router.router(vertx);
        router.post("/timer")
                .handler(this::handleTimerPost);

        router.get("/timer/:timerId")
                .handler(this::handleTimerGet);

        router.delete("/timer/:timerId")
                .handler(this::handleTimerDelete);
        return router;
    }

    private void handleTimerDelete(RoutingContext routingContext) {
        var timerId = Long.parseLong(routingContext.pathParam("timerId"));
        timerController.removeTimerWithId(timerId);
    }

    private void handleTimerPost(RoutingContext routingContext) {
        var body = routingContext.body().asJsonObject();
        var delay = body.getLong("delay");
        var delta = body.getLong("delta");
        var initValue = body.getInteger("init");
        String uniqueId = UUID.randomUUID().toString();
        timerController.addNewInitializedTimer(uniqueId, initValue);
        long id = vertx.setPeriodic(delay, e -> {
            timerController.changeTimerValueForId(uniqueId, delta);
        });

        timerController.updateTimerIdForUniqueId(uniqueId, id);
    }

    private void handleTimerGet(RoutingContext routingContext) {
    }
}
