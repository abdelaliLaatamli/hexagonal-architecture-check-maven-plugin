package co.alaatamli.plugins.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class HexagonalStructureRule implements ArchitectureRule {

    @Override
    public String getName() {
        return "Hexagonal Core Layer Boundaries";
    }

    @Override
    public EvaluationResult check(JavaClasses classes, String basePackage) {

        ArchRule rule = classes()
                .that()
                .resideInAPackage(basePackage + "..")
                .should()
                .resideInAnyPackage(
                        basePackage + ".domain..",
                        basePackage + ".application..",
                        basePackage + ".infrastructure.."
                )
                .allowEmptyShould(true);

        rule.check(classes);

        return rule.evaluate(classes);


    }

}
