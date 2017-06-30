package jmetal.problems.SPSP;

import java.io.File;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import static jmetal.problems.SPSP.SPSP.L;
import jmetal.util.JMException;

/**
 *
 * @author jjaneto
 */
public class SPSP_2 extends Problem {

    private Project project;
    private Constraints constraints;
    private final int L;

    public SPSP_2(File file, int employees_, int tasks_) {
        numberOfVariables_ = L = employees_ * tasks_;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 3;
        solutionType_ = new ArrayRealSolutionType(this);

        lowerLimit_ = new double[numberOfVariables_];
        upperLimit_ = new double[numberOfVariables_];
        for (int var = 0; var < L; var++) {
            lowerLimit_[var] = 0.0;
            upperLimit_[var] = 1.0;
        }

        project = new Project(file, employees_, tasks_, numberOfObjectives_);
        constraints = new Constraints(project);
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
//        maxOverwork = 0;
//        projectOverwork = 0;
//        undt = 0;

        project.alterDedicationMatrix(solution);
        project.calculateDurationTasks();
        project.calculateStartEndTasks();
        project.calculateProjectDuration();
        project.calculateProjectCost();
        project.getEmployeeOverwork();
        project.getProjectOverwork();

        if(project.getProjectOverwork() > 0.0){
            project.repairOperator(solution);
        }
        
//        if (projectOverwork > 0.0) {
//            repairOperator(solution);
//        }
        solution.setObjective(0, project.getObjective(0));
        solution.setObjective(1, project.getObjective(1));
    }

    @Override
    public void evaluateConstraints(Solution solution) throws JMException {
        int violatedConstraints = 0;
        if(constraints.isOverworked()) violatedConstraints++;
        if(!constraints.noTaskUnleft()) violatedConstraints++;
        if(!constraints.hasRequiredSkill()) violatedConstraints++;
        solution.setNumberOfViolatedConstraint(violatedConstraints);
        solution.setOverallConstraintViolation(violatedConstraints);
//        int violatedConstraints = 0;
//        if(projectOverwork > 0.0) violatedConstraints++;
//        if(!noTaskUnleft()) violatedConstraints++;
//        if(!hasRequiredSkill()) violatedConstraints++;
//        solution.setNumberOfViolatedConstraint(violatedConstraints);
//        solution.setOverallConstraintViolation(violatedConstraints);

    }

}
