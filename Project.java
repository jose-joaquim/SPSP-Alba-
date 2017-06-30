package jmetal.problems.SPSP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jmetal.core.Solution;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 *
 * @author jjaneto
 */
public class Project {

    private ArrayList<Employee> employees;
    private ArrayList<Task> tasks;
    private Graph graph;
    private double dedicationMatrix[][];
    private double f[];
    private double overwork;
    private double maxOverwork;
    private double projectOverwork;
    private int nEmployees;
    private int nTasks;
    
    private final double epsilon = 0.00001;

    public Project(File file, int nEmployees, int nTasks, int objectives_) {
        this.nTasks = nTasks;
        this.nEmployees = nEmployees;
        this.f = new double[objectives_];
        new Loader(file);
    }

    public void alterDedicationMatrix(Solution solution) {
        Double[] decisionVariables = ((ArrayReal) (solution.getDecisionVariables()[0])).array_;
        for (int i = 0; i < nEmployees; i++) {
            for (int j = 0; j < nTasks; j++) {
                dedicationMatrix[i][j] = decisionVariables[i * nTasks + j];
            }
        }
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public double[][] getDedicationMatrix() {
        return dedicationMatrix;
    }

    public int getnEmployees() {
        return nEmployees;
    }

    public int getnTasks() {
        return nTasks;
    }
    
    public double getOverwork(){
        return overwork;
    }
    
    public void calculateProjectDuration(){
        for(Task i : tasks){
            f[1] = Math.max(f[1], i.getEnd());
        }
    }
    
    public void calculateProjectCost(){
        for(Employee employee : employees){
            double sum = 0;
            for(Task task : tasks){
                sum += employee.getSalary() * dedicationMatrix[employee.getId()][task.getId()] * task.getDuration();
            }
            f[0] += sum;
        }
    }
    
    public double getProjectOverwork(){
        projectOverwork = 0.0;
        for(Employee ep : employees){
            projectOverwork += ep.getOverDedication();
        }
        return projectOverwork;
    }
    
    public void calculateStartEndTasks(){
        ArrayList<Integer> pfEntrada = Graph.getPfEntrada();
        for(Task task : tasks){
            if(pfEntrada.get(task.getId()) == 0){
                task.setStart(0);
                task.setEnd(task.getStart() + task.getDuration());
            }
        }
        for(Task task : tasks){//para cada tarefa
            if(pfEntrada.get(task.getId()) != 0){
                double start = -1;
                ArrayList<ArrayList<Integer> > antList = Reader.graph.getAntecessor();
                ArrayList<Integer> vet = antList.get(task.getId());
                for(int j = 0; j < vet.size(); j++){
                    int antecessor = vet.get(j);
                    start = Math.max(start, tasks.get(antecessor).getEnd());
                }
                task.setStart(start);
                task.setEnd(task.getStart() + task.getDuration());
            }
        }
    }
    
    public void calculateDurationTasks() {
        for(Task t : tasks){
            double sum = 0;
            for(Employee e : employees){
                sum += dedicationMatrix[e.getId()][t.getId()];
            }
            t.setDuration((t.getEffort()/sum));
            //System.out.println("Tarefa " + t.getId() + " tem " + t.getDuration() + " de duração");
       }
    }
    
    public void getEmployeeOverwork(){
       for(double stnt = 0.0; stnt <= f[1]; stnt++){
            for(Employee ep : employees){
                double sum = 0.0;
                for(Task t : tasks){
                    if(t.getStart() <= stnt && stnt <= t.getEnd()){
                        sum += dedicationMatrix[ep.getId()][t.getId()];
                    }
                }
                if(sum > ep.getMaxDedication()){
                    ep.addOverDedication(sum - ep.getMaxDedication());
                }
                maxOverwork = Math.max(maxOverwork, sum);
            }
        }
    }
    
    public void repairOperator(Solution solution) throws JMException{
        XReal arraySolution = new XReal(solution);
        for (int i = 0; i < nEmployees; i++) {
            for (int j = 0; j < nTasks; j++) {
                double value = epsilon + maxOverwork;
                dedicationMatrix[i][j] = dedicationMatrix[i][j] / value;
                arraySolution.setValue(i * nTasks + j, dedicationMatrix[i][j]);
            }
        }
        
        f[1] *= (epsilon + maxOverwork);
        for (Employee ep : employees) {
            ep.setOverDedication(0.0);
        }
        projectOverwork = 0.0;
        maxOverwork = -1;
    }
    
    public double getObjective(int index){
        return f[index];
    }

    private class Loader {

        private ArrayList<String> entries;
        private BufferedReader reader;

        public Loader(File file) {
            try {
                reader = new BufferedReader(new FileReader(file));
                entries = new ArrayList<>();
                
                String line;
                while ((line = reader.readLine()) != null) {
                    entries.add(line);
                }

                loadGraph();
                loadTasks();
                loadEmployee();
                
                dedicationMatrix = new double[nEmployees][nTasks];

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void loadGraph() {
            graph = new Graph(nTasks);
//            System.out.println("task eh " + nTasks);
            String pattern = "^(graph)(\\.)(arc)(\\.)(\\d+)(=)(\\d+)( )(\\d+)$";

            for (String e : entries) {
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(e);
                if (m.find()) {
                    int u = Integer.parseInt(m.group(7));
                    int v = Integer.parseInt(m.group(9));
//                    System.out.println("Adicionei aresta " + u + " " + v);
                    graph.addEdge(u, v);
                }
            }
            System.out.println("");
        }

        public void loadTasks() {
            tasks = new ArrayList<>();
            for (int i = 0; i < nTasks; i++) {
                Task t = new Task(i);
                tasks.add(t);
            }

            String pTaskSkill = "^(task)(\\.)(\\d+)(\\.)(skill)(\\.)(\\d+)(=)(\\d+)$";
            String pTaskCost = "^(task)(\\.)(\\d+)(\\.)(cost)(=)(\\d+\\.\\d+)$";

            for (String e : entries) {
                Pattern r = Pattern.compile(pTaskSkill);
                Matcher m = r.matcher(e);
                if (m.find()) {
                    int id = Integer.parseInt(m.group(3));
                    int skill = Integer.parseInt(m.group(9));
                    tasks.get(id).addRequiredSkill(skill);
                }
            }

            for (String e : entries) {
                Pattern r = Pattern.compile(pTaskCost);
                Matcher m = r.matcher(e);
                if (m.find()) {
                    int id = Integer.parseInt(m.group(3));
                    double valor = Double.parseDouble(m.group(7));
                    tasks.get(id).setEffort(valor);
                }
            }
            
            for(Task t : tasks){
                t.sortRequiredSkills();
            }
            
            for(Task t : tasks){
                System.out.println("Task: " + t.getId());
                System.out.println("Cost: " + t.getEffort());
                System.out.print("Skills:");
                for(Integer x : t.getRequiredSkills()) System.out.print(" " + x);
                System.out.println("");
            }
            
        }

        public void loadEmployee() {
            employees = new ArrayList<>();
            for (int i = 0; i < nEmployees; i++) {
                Employee e = new Employee(i, 0);
                employees.add(e);
            }

            String pEmployeeSalary = "^(employee)(\\.)(\\d+)(\\.)(salary)(=)(\\d+\\.\\d+)$";
            String pEmployeeSkill = "^(employee)(\\.)(\\d+)(\\.)(skill)(\\.)(\\d+)(=)(\\d+)$";

            for (String e : entries) {
                Pattern r = Pattern.compile(pEmployeeSalary);
                Matcher m = r.matcher(e);
//                System.out.println("pau");
                if (m.find()) {
                    int id = Integer.parseInt(m.group(3));
                    double value = Double.parseDouble(m.group(7));
                    employees.get(id).setSalary(value);
//                    System.out.println("Setei salario " + id + " para " + value);                           
                }
            }

            for (String e : entries) {
                Pattern r = Pattern.compile(pEmployeeSkill);
                Matcher m = r.matcher(e);
                if (m.find()) {
                    int id = Integer.parseInt(m.group(3));
                    int skill = Integer.parseInt(m.group(9));
                    employees.get(id).addSkill(skill);
                }
            }

            for (Employee e : employees) {
                e.sortArraySkills();
            }
            
            for(Employee e : employees){
                System.out.println("Employee: " + e.getId());
                System.out.println("Salary: " + e.getSalary());
                System.out.print("Skills:");
                for(Integer x : e.getSkills()) System.out.print(" " + x);
                System.out.println("");
            }
        }

    }
    
//    public static void main(String args[]){
//        new Project(new File("/Users/jjaneto/Google Drive/Pesquisa Leila/INSTANCIAS/inst-16-8.conf"), 8, 16, 2);
//    }
}
