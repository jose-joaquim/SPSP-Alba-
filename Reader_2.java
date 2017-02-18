package jmetal.problems.SPSP_2;

import jmetal.problems.SPSP.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jjaneto
 */
public class Reader_2 {

    protected ArrayList<String> array;
    protected int numberTasks;
    protected String instanceName, fitnessFunction;
    //
    public static ArrayList<Employee_2> arrEmployee;
    public static Graph_2 graph;
    public static ArrayList<Task_2> arrTask;
    //
    public static double overloadWeight;
    public static double undoneTaskWeight;
    public static double costWeight;
    public static double timeWeight;
    public static double penalizeConstant; //?
    public static double needRscWeight; //acho que é needed resource (?)
    public static int points; //tentando descobrir o que significa
    
    public Reader_2(File file) {
        array = new ArrayList<>();
        try {
            readScalabe(file);
            makeModel();
            setArrayGraph(readInput("graph"));
            setArrayEmployee(readInput("employee"));
            setArrayTask(readInput("task"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<String> readInput(String look){
        ArrayList<String> newArray = new ArrayList<>();
        ArrayList<String> ret = new ArrayList<>();
        
        for(int i = 0; i < array.size(); i++){
            if(array.get(i).contains(look)){
                ret.add(array.get(i));
            }else{
                newArray.add(array.get(i));
            }
        }
        
        array = new ArrayList<>(newArray);

        return ret;
    }
    
    public void setArrayEmployee(ArrayList<String> aux){
        arrEmployee = new ArrayList<>();

        //
        String token[] = aux.get(aux.size() - 1).split("=");
        int size = Integer.parseInt(token[1]);
        for(int i = 0; i < size; i++) arrEmployee.add(new Employee_2(i));
        aux.remove(aux.size() - 1);
        //
        
        for(int i = 0; i < aux.size(); i++){
            String sp [] = aux.get(i).split("\\.");
            int id = Integer.parseInt(sp[1]);
            if(sp[2].contains("salary")){
                String w[] = aux.get(i).split("=");
                double salary = Double.parseDouble(w[1]);
                arrEmployee.get(id).setSalary(salary);
            }else if(!aux.get(i).contains("skill.number")){//skill
                String w[] = aux.get(i).split("=");
                int skill = Integer.parseInt(w[1]);
                arrEmployee.get(id).addSkill(skill);
            }
        }
        
        //return arrEmployee;
    }
    
    public void setArrayGraph(ArrayList<String> aux){
        graph = new Graph_2(numberTasks);
        
        //
        String pr[] = aux.get(aux.size() - 1).split("=");
        int arcs = Integer.parseInt(pr[1]);
        aux.remove(aux.size() - 1);
        //
        
        for(int i = 0; i < arcs; i++){
            String sd[] = aux.get(i).split("=");
            String w[] = sd[1].split(" ");
            int u = Integer.parseInt(w[0]), v = Integer.parseInt(w[1]);
            graph.addEdge(u, v);
        }
        
        //return graph;
    }
    
    private void setArrayTask(ArrayList<String> aux) {
        arrTask = new ArrayList<>();
        
        //
        String token[] = aux.get(aux.size() - 1).split("=");
        numberTasks = Integer.parseInt(token[1]);
        for(int i = 0; i < numberTasks; i++) arrTask.add(new Task_2(i));
        aux.remove(aux.size() - 1);
        //
        
        for(int i = 0; i < aux.size(); i++){
            //System.out.println("To olhando a linha " + aux.get(i));
            String sp[] = aux.get(i).split("\\.");
            int idTask = Integer.parseInt(sp[1]);
            if(sp[2].contains("cost")){
                String w[] = aux.get(i).split("=");
                double cost = Double.parseDouble(w[1]);
                arrTask.get(idTask).setEffort(cost);
            }else if(!aux.get(i).contains("skill.number")){
                String w[] = aux.get(i).split("=");
                int reqSkill = Integer.parseInt(w[1]);
                arrTask.get(idTask).addRequiredSkill(reqSkill);
            }
        }
        
        //return arrTask;
    }
    
    public void readScalabe(File file) throws FileNotFoundException, IOException{
        FileReader f = new FileReader(file);
        BufferedReader rd = new BufferedReader(f);
        
        String line; 
        
        while((line = rd.readLine()) != null){
            String token[] = line.split("=");
            switch(line){
                case "points": 
                    points = Integer.valueOf(token[1]);
                    break;
                case "needed-resrc-weight": 
                    needRscWeight = Double.valueOf(token[1]);
                    break;
                case "instance": 
                    instanceName = token[1];
                    break;
                case "fitness-function": 
                    fitnessFunction = token[1];
                    break;
                case "time-weight": 
                    timeWeight = Double.valueOf(token[1]);
                    break;
                case "penalize-constant": 
                    penalizeConstant = Double.valueOf(token[1]);
                    break;
                case "cost-weight": 
                    costWeight = Double.valueOf(token[1]);
                    break;
                case "overload-weight": 
                    overloadWeight = Double.valueOf(token[1]);
                    break;
                case "undone-task-weight": 
                    undoneTaskWeight = Double.valueOf(token[1]);
                    break;
                default:
                    if(!line.contains("employee-limit")){
                        System.err.println("Linha desconhecida!");
                    }
            }
        }
    }
    
    public void makeModel() throws FileNotFoundException, IOException{
        FileReader f = new FileReader(new File("/Users/jjaneto/Google Drive/Pesquisa Leila/INSTANCIAS/" + instanceName)); //vai usar o instanceName
        BufferedReader rd = new BufferedReader(f);
        
        String line;
        
        while((line = rd.readLine()) != null){
            array.add(line);
        }
        
        Collections.sort(array);
        
        array.remove(0);
        array.remove(0);
        
    }


    public static ArrayList<Employee_2> getArrEmployee() {
        return arrEmployee;
    }

    public static Graph_2 getGraph() {
        return graph;
    }

    public static ArrayList<Task_2> getArrTask() {
        return arrTask;
    }
    
    
    
}
