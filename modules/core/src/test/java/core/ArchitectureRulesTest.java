package core;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureRulesTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("core");

    @Test
    void coreMustNotDependOnPluginImplementations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("core..")
                .should().dependOnClassesThat().resideInAPackage("plugins..")
                .as("Architecture rule: core must access plugins only via api, never via plugin implementations.");
        rule.check(classes);
    }

    @Test
    void coreMustNotDependOnUserModule() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("core..")
                .should().dependOnClassesThat().resideInAPackage("user..")
                .as("Architecture rule: core must not depend on user module.");
        rule.check(classes);
    }

    @Test
    void indexingMustBeIsolatedFromFileAndRepository() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("core.indexing..")
                .should().dependOnClassesThat().resideInAnyPackage("core.file..", "core.repository..")
                .as("Architecture rule: core.indexing must not depend on core.file or core.repository.");
        rule.check(classes);
    }
}
