package web.controller.response;

public enum HealthStatus {
    GREEN("GREEN. Everything is healthy."),
    YELLOW("YELLOW. Service is on but storage or downstream service looks suspicious."),
    RED("RED. One of downstream services or storage is down");

    private final String message;

    HealthStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
