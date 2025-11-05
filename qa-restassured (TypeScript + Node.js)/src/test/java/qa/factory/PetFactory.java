package qa.factory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PetFactory {
    public static long randomId() {
        return ThreadLocalRandom.current().nextLong(100_000, 9_999_999);
    }
    public static Map<String, Object> petBody(long id, String name, String status) {
        return Map.of(
                "id", id,
                "category", Map.of("id", 1, "name", "dog"),
                "name", name,
                "photoUrls", List.of("https://example.com/dog.jpg"),
                "tags", List.of(Map.of("id", 1, "name", "friendly")),
                "status", status
        );
    }
}
