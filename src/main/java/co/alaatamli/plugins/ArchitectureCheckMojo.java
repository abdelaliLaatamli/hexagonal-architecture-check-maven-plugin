package co.alaatamli.plugins;

import co.alaatamli.plugins.rules.ArchitectureRule;
import co.alaatamli.plugins.rules.DomainIsolationRule;
import co.alaatamli.plugins.rules.HexagonalStructureRule;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.EvaluationResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mojo(
    name = "check",
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class ArchitectureCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * The package to scan.
     * 🚀 MAGIC HAPPENS HERE: If not explicitly configured, Maven automatically
     * extracts and injects the target project's <groupId> at runtime.
     */
    @Parameter(property = "basePackage", defaultValue = "${project.groupId}")
    private String basePackage;

    //  All rules are registered here
    private final List<ArchitectureRule> rules = List.of(
            new HexagonalStructureRule(),
            new DomainIsolationRule());

    @Override
    public void execute() throws MojoFailureException {

        var shouldContinue = this.checkAllowedPackaging();

        if(!shouldContinue)
            return;


        String outputDirectory = project.getBuild().getOutputDirectory();
        File classesDir = new File(outputDirectory);

        if (!classesDir.exists()) {
            getLog().warn("No compiled classes found. Skipping architecture check.");
            return;
        }

        long startTime = System.currentTimeMillis();
        int totalTests = rules.size();
        int failures = 0;
        int errors = 0;
        List<String> failureDetails = new ArrayList<>();

        // 1. Header block
        getLog().info("");
        getLog().info("-------------------------------------------------------");
        getLog().info(" T E S T S   A R C H I T E C T U R E ");
        getLog().info("-------------------------------------------------------");
        getLog().info("Running ArchitectureEnforcementTest against package: " + basePackage);

        // ✅ Import from target/classes of the target project, not the plugin classpath
        JavaClasses importedClasses = new ClassFileImporter()
                .importPath(Paths.get(outputDirectory));

        // 2. Execute rules
        for (ArchitectureRule rule : rules) {
            try {
                EvaluationResult result = rule.check(importedClasses, basePackage);

                if (result.hasViolation()) {
                    failures++;
                    failureDetails.add(rule.getClass().getSimpleName() + " : " + result.getFailureReport());
                }
            } catch (AssertionError e) {
                // ✅ rule.check() throws AssertionError with the full ArchUnit message
                errors++;
                failureDetails.add(rule.getClass().getSimpleName() + " : " + e.getMessage());
            } catch (Throwable t) {
                errors++;
                failureDetails.add(rule.getClass().getSimpleName() + " : " + t.getMessage());
            }
        }

        double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

        // 3. Summary Block
        getLog().info("");
        getLog().info("Results:");
        getLog().info("");

        String summaryLine = String.format("Tests run: %d, Failures: %d, Errors: %d, Skipped: 0, Time elapsed: %.2f s",
                totalTests, failures, errors, elapsedTime);

        if (failures > 0 || errors > 0) {
            getLog().info(summaryLine + " <<< FAILURE! -- in ArchitectureEnforcementTest");
            getLog().info("");

            for (String detail : failureDetails) {
                getLog().error(detail);
            }
            getLog().info("");
            throw new MojoFailureException("Architecture check failed. Review the custom test report above.");
        } else {
            getLog().info(summaryLine + " <<< SUCCESS! -- in ArchitectureEnforcementTest");
            getLog().info("All architecture rules passed successfully!");
            getLog().info("");
        }
    }

    private boolean checkAllowedPackaging(){

        // Read the packaging type of the target project (e.g., "jar", "war", "pom")
        String packaging = project.getPackaging();

        List<String> allowedPackaging = List.of( "jar" , "war");

        if (packaging == null || !allowedPackaging.contains(packaging.toLowerCase())){
            getLog().info("Skipping architecture check: packaging '" + packaging + "' is not supported.");
            return false;
        }

        return true;
    }


}