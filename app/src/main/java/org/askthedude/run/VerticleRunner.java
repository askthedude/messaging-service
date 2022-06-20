package org.askthedude.run;

import io.vertx.config.ConfigChange;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TimerService;
import web.WebServerVerticle;
import web.controller.HealthCheckController;
import web.controller.TimerController;
import web.router.HealthcheckRouter;
import web.router.TimerRouter;

import static utils.Constants.DEFAULT_PORT;

public class VerticleRunner extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    /*
         Not initialised since, whenver it's used in undeploying and then redeploying
         It will already be set by vert.x;
     */
    private String webServerVerticleId;

    @Override
    public void start(Promise<Void> startPromise) {
        /*
            Let's try to pass config file as "vertx-config-path" system parameter while running with java jar.
         */
        var configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig(this::setupVerticlesWithConfiguration);
        configRetriever.listen(this::handleConfigurationChange);
        startPromise.complete();
    }

    private void handleConfigurationChange(ConfigChange change) {
        LOGGER.info("Configuration changes detected, trying to redeploy according verticles.");
        var oldConfig = change.getPreviousConfiguration();
        var newConfig = change.getNewConfiguration();
        var newPort = newConfig.getInteger("port");
        var oldPort = oldConfig.getInteger("port");
        if (!oldPort.equals(newPort)) {
            var undeploymentFuture = vertx.undeploy(webServerVerticleId);
            undeploymentFuture
                    .onSuccess(e -> startupWebVerticle(newPort))
                    .onFailure(LOGGER::warn);
        }
        LOGGER.info("Configuration change detected");
    }

    private void setupVerticlesWithConfiguration(AsyncResult<JsonObject> config) {
        if (config.succeeded()) {
            startUpVerticles(config.result());
        } else {
            LOGGER.warn("Couldn't retrieve json configuration");
        }
    }

    private void startUpVerticles(JsonObject configuration) {
        Integer port = configuration.getInteger("port");
        if(port == null){
            port = DEFAULT_PORT;
        }
        startupWebVerticle(port);
    }

    private void startupWebVerticle(int port) {
        var healthCheckRouter = new HealthcheckRouter(new HealthCheckController());
        var service = new TimerService();
        var timerController = new TimerController(service);
        var timerRouter = new TimerRouter(timerController);
        var webServerFuture = vertx.deployVerticle(new WebServerVerticle(port, healthCheckRouter, timerRouter));
        webServerFuture.onComplete(result -> {
            if (result.succeeded()) {
                webServerVerticleId = result.result();
                LOGGER.info("Deployed web server on port: {}", port);
            } else {
                LOGGER.warn("Couldn't start web server on port: {}", port);
            }
        });
    }
}
