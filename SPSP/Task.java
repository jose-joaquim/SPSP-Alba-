package Prob.SPSP;

import java.util.ArrayList;

/**
 *
 * @author joaqu
 */
public class Task {

    private int duration;
    private double effort;
    private ArrayList<Integer> requiredSkills;
    
    public Task() {
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<Integer> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(ArrayList<Integer> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public double getEffort() {
        return effort;
    }

    public void setEffort(double effort) {
        this.effort = effort;
    }
    
    
    
    
}
