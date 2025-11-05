package qa.runner;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Runner único para toda la suite de tests.
 * - Busca tests dentro del/los paquete(s) indicados.
 * - Incluye clases cuyo nombre termine en *Spec o *Test.
 */
@Suite
@SelectPackages({
        "qa.specs"      // <-- aquí van tus clases como PetStoreSpec, etc.
})
@IncludeClassNamePatterns({".*Spec$", ".*Test$"})
public class AllTestsRunner {
    // No necesita código: las anotaciones hacen el trabajo.
}
