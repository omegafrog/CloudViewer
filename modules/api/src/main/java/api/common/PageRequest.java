package api.common;

public record PageRequest(String cursor, int limit) {
    public PageRequest {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be >= 0");
        }
    }
}
