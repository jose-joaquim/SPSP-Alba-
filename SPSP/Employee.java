package Prob.SPSP;

import java.util.ArrayList;

/**
 *
 * @author joaqu
 */
public class Employee implements Comparable<Employee>{

    private double salary;
    private int id;
    private ArrayList<Integer> skills;
    
    public Employee(int id) {
        skills = new ArrayList<>();
        this.id = id;
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

    @Override
    public int compareTo(Employee o) {
        return o.getId() - this.id;
    }
        
}