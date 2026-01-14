package core.repository;

import api.common.RepositoryDescriptor;
import api.common.RepositoryRegistration;
import api.common.UserRepositoryRef;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryCatalogTest {

    @Test
    void registerStoresDescriptorPerUser() {
        RepositoryCatalog catalog = new RepositoryCatalog();
        RepositoryRegistration registration = new RepositoryRegistration(
                "user-1",
                "repo-1",
                "TEST",
                Map.of("rootPath", "/tmp")
        );

        RepositoryDescriptor descriptor = catalog.register(registration);

        RepositoryDescriptor found = catalog.getOrThrow(new UserRepositoryRef("user-1", "repo-1"));
        assertEquals(descriptor, found);
    }

    @Test
    void getOrThrowFailsForUnknownUser() {
        RepositoryCatalog catalog = new RepositoryCatalog();
        RepositoryRegistration registration = new RepositoryRegistration(
                "user-1",
                "repo-1",
                "TEST",
                Map.of()
        );
        catalog.register(registration);

        assertThrows(IllegalStateException.class,
                () -> catalog.getOrThrow(new UserRepositoryRef("user-2", "repo-1")));
    }
}
