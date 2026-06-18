package co.alaatamli.plugins.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.EvaluationResult;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class DomainIsolationRule implements ArchitectureRule{

    @Override
    public String getName() {
        return "Domain Layer Independence (No Infrastructure leaks)";
    }

    @Override
    public EvaluationResult check(JavaClasses classes, String basePackage) {
        // The domain layer must not depend on application or infrastructure layers
        return noClasses()
                .that()
                .resideInAPackage(basePackage + ".domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(basePackage + ".infrastructure..", basePackage + ".application..")
                .allowEmptyShould(true)
                .evaluate(classes);
    }

}
