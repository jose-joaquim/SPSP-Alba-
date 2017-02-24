package jmetal.operators.selection;

import java.util.HashMap;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 *
 * @author jjaneto
 */
public class UFSExtremeTournament extends Selection{

    private int a_[];
    
    private int index_;
    
    public UFSExtremeTournament(HashMap<String, Object> parameters) {
        super(parameters);
    }

    @Override
    public Object execute(Object object) throws JMException {
        SolutionSet population = (SolutionSet)object;
        if (index_ == 0) //Create the permutation
        {
          a_= (new jmetal.util.PermutationUtility()).intPermutation(population.size());
        }
        
        
        Solution solution1, solution2;
        solution1 = population.get(a_[index_]);
        solution2 = population.get(a_[index_+1]);

        index_ = (index_ + 2) % population.size();
        
        if(solution1.getNumberOfViolatedConstraint() < solution2.getNumberOfViolatedConstraint()){
            return solution1;
        }else if(solution2.getNumberOfViolatedConstraint() < solution1.getNumberOfViolatedConstraint()){
            return solution2;
        }else if(solution1.getCrowdingDistance() > solution2.getCrowdingDistance()){
            return solution1;
        }else if(solution2.getCrowdingDistance() > solution1.getCrowdingDistance()){
            return solution2;
        }else{
            if(PseudoRandom.randDouble() < 0.5){
                return solution1;
            }else return solution2;
        }
    }
    
}
