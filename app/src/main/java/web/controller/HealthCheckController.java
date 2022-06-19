package web.controller;

import web.controller.response.HealthCheckResponse;
import web.controller.response.HealthStatus;

public class HealthCheckController {
    public HealthCheckResponse checkHealth(){
        return new HealthCheckResponse(HealthStatus.GREEN);
    }
}
