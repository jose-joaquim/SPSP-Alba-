package jmetal.problems.SPSP_2;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author joaqu
 */
public class Employee_2{

    private double salary;
    private double maxDedication;
    private double overDedication;
    private int id;
    private ArrayList<Integer> skills;
    private ArrayList<Double> workInstant;
    
    public Employee_2(int id) {
        skills = new ArrayList<>();
        workInstant = new ArrayList<>();
        this.id = id;
        this.maxDedication = 1.0;
        this.overDedication = 0.0;
    }

    public double getMaxDedication() {
        return maxDedication;
    }

    public void setMaxDedication(double maxDedication) {
        this.maxDedication = maxDedication;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public ArrayList<Integer> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Integer> skills) {
        this.skills = skills;
    }
    
    public void addSkill(int skill){
        this.skills.add(skill);
    }

    public double getOverDedication() {
        return overDedication;
    }

    public void setOverDedication(double overDedication) {
        this.overDedication = overDedication;
    }

    public ArrayList<Double> getWorkInstant() {
        return workInstant;
    }

    public void setWorkInstant(ArrayList<Double> workInstant) {
        this.workInstant = workInstant;
    }
    
    public void sortArraySkills(){
        Collections.sort(skills);
    }
    
    public void addOverDedication(double x){
        this.overDedication += x;
    }
    
}