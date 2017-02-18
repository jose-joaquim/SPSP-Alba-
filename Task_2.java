package jmetal.problems.SPSP_2;

import jmetal.problems.SPSP.*;
import java.util.ArrayList;

/**
 *
 * @author joaqu
 */
public class Task_2 {

    private int start;
    private int end;
    private int id;
    private int duration;
    private double effort;
    private ArrayList<Integer> requiredSkills;
    private ArrayList<Integer> antecessores;
    
    public Task_2(int id) {
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public ArrayList<Integer> getAntecessores() {
        return antecessores;
    }

    public void setAntecessores(ArrayList<Integer> antecessores) {
        this.antecessores = antecessores;
    }
    
    
    
    
}