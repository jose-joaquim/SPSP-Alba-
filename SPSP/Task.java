package Prob.SPSP;

import java.util.ArrayList;

/**
 *
 * @author joaqu
 */
public class Task {

    private int duration;
    private int id;
    private double effort;
    private ArrayList<Integer> requiredSkills;
    
    public Task(int id) {
        requiredSkills = new ArrayList<>();
        this.id = id;
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

    public void addRequiredSkill(int skill){
        this.requiredSkills.add(skill);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
}