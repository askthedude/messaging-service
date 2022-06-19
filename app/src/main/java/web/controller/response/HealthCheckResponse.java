package web.controller.response;

public class HealthCheckResponse {
    private final HealthStatus status;

    public HealthCheckResponse(HealthStatus status) {
        this.status = status;
    }

    public HealthStatus getStatus() {
        return status;
    }
}
