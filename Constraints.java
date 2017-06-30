package jmetal.problems.SPSP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author jjaneto
 */
public class Constraints {

    private final Project project;
    
    public Constraints(Project project) {
        this.project = project;
    }
    
    public boolean noTaskUnleft(/*Project project*/){
        int undt = 0;
        for(int i = 0; i < project.getnEmployees(); i++){
            int none = 0;
            for(int j = 0; j < project.getnTasks(); j++){
                none += (project.getDedicationMatrix()[i][j] != 0.0) ? 1 : 0;
            }
            if(none == project.getnEmployees()) undt++;
        }
        return (undt == 0);
    }
    
    
    public boolean hasRequiredSkill(/*Project project*/){
        for(int i = 0; i < project.getnEmployees(); i++){
            for(int j = 0; j < project.getnTasks(); j++){
                if(project.getDedicationMatrix()[i][j] > 0.0){
                    for(Integer skill : project.getTasks().get(i).getRequiredSkills()){
                        if(!hasSkill(project.getTasks().get(i).getRequiredSkills(), skill)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public boolean hasSkill(ArrayList<Integer> skills, int skill){
        return Collections.binarySearch(skills, skill) >= 0;
    }
    
    public boolean isOverworked(/*Project project*/){
        return project.getOverwork() > 0.0;
    }
}
