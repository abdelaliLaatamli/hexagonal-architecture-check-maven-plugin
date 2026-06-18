package co.alaatamli.plugins.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.EvaluationResult;

public interface ArchitectureRule {
    /**
     * Evaluates this specific rule against the imported classes.
     */
    EvaluationResult check(JavaClasses classes, String basePackage);

    /**
     * A user-friendly name for logging purposes.
     */
    String getName();
}
