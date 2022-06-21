package web.router;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.controller.TimerController;

import java.util.UUID;

import static service.TimerService.CONTINUE_FLAG;
import static utils.Constants.NO_TIMER_RESULT;

public class TimerRouter {
    private static final Logger LOGGER = LogManager.getLogger(TimerRouter.class);

    private Vertx vertx;
    private final TimerController timerController;

    public TimerRouter(TimerController timerController) {
        this.timerController = timerController;
    }

    public Router router(Vertx vertx) {
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

    private void handleTimerPost(RoutingContext routingContext) {
        var body = routingContext.body().asJsonObject();
        var delay = body.getLong("delay");
        var delta = body.getLong("delta");
        var initValue = body.getInteger("initValue");
        if (delay == null || delta == null || initValue == null) {
            routingContext.response()
                    .setStatusCode(400)
                    .end(new JsonObject()
                            .put("message", "invalid input, please specify: delay, initValue and delta")
                            .toBuffer());
            return;
        }
        String uniqueId = UUID.randomUUID().toString();
        timerController.addNewInitializedTimer(uniqueId, initValue);
        long id = vertx.setPeriodic(delay, e -> {
            System.out.println(Thread.currentThread().getName());
            var timerId = timerController.changeTimerValueForId(uniqueId, -1 * delta);
            if (timerId != CONTINUE_FLAG) {
                boolean removedTimer = vertx.cancelTimer(timerId);
                timerController.removeTimerWithId(timerId);
                if (removedTimer) {
                    LOGGER.info("Removed timer with id: {}", timerId);
                }
            }
        });
        System.out.println(Thread.currentThread().getName());
        timerController.updateTimerIdForUniqueId(uniqueId, id);
        routingContext.response()
                .setStatusCode(200)
                .end(new JsonObject()
                        .put("uniqueId", uniqueId)
                        .toBuffer());
    }

    private void handleTimerGet(RoutingContext routingContext) {
        var uniqueId = routingContext.pathParam("timerId");
        var value = timerController.getResultForTimerId(uniqueId);
        if (value == NO_TIMER_RESULT) {
            routingContext.response()
                    .setStatusCode(404)
                    .end();
        } else {
            routingContext.response()
                    .setStatusCode(200)
                    .end(new JsonObject()
                            .put("value", value).toBuffer());
        }
    }

    private void handleTimerDelete(RoutingContext routingContext) {
        var timerId = routingContext.pathParam("timerId");
        timerController.removeTimerUniqueId(timerId);
        routingContext.response()
                .setStatusCode(200)
                .end();
    }
}
