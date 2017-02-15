package Prob.SPSP;

import NCRO.Mol;
import NCRO.ObjectiveFunction;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Implementação do SPSP segundo Alba e Chicaco (2007).
 * @author joaqu
 */
public class SPSP {

    // parametres of the Container
    private static final int popSize = 64;
    private static final double enBuff = 0;
    private static final double iniKE = 10000;
    private static final double collR = 0.7;
    private static final double lossR = 0.4;
    private static final double step = 0.12;
    private static final double synThres = 10;
    private static final int decThres = 10;
    private static final int FELimit = 5000;
    //
    
    private static final double wcost   = 1e-6;
    private static final double wdur    = 0.1;
    private static final double wpenal  = 100.0;
    private static final double wundt   = 10;
    private static final double wreqsk  = 10;
    private static final double wover   = 0.1;
    
    private static ArrayList<Task> tasks;
    private static ArrayList<Employee> employees;
    private static int numberEmployees;
    private static int numberTasks;
    private static int undt;
    private static int reqsk;
    
    private static double tableEX[][];
    
    public SPSP() {
        numberEmployees = 0;
        numberTasks = 0;
        undt = 0;
        reqsk = 0;
    }
    
    public static class F implements ObjectiveFunction{

        public F() {
            calculateDurationTasks();
        }
        
        /**
         * Calcula a duração de cada tarefa.
         */
        public void calculateDurationTasks(){
            for(int j = 0; j < numberTasks; j++){
                double sum = 0;
                for(int i = 0; i < numberEmployees; i++){
                    sum += tableEX[i][j];
                }
                tasks.get(j).setEffort(tasks.get(j).getEffort() / sum);
            }
        }
        
        /**
         *  Para cada candidato, irá verificar se ele possui todas as habilidades de tal tarefa.
         * @param employee
         * @param task
         * @return true sse o employee possui todas as habilidades. Falso caso contrário.
         */
        public boolean hasRequiredSkill(){
            for(int i = 0; i < numberEmployees; i++){
                for(int j = 0; j < numberTasks; j++){
                    if(tableEX[i][j] > 0){
                        for(Integer k : tasks.get(j).getRequiredSkills()){
                            if(Collections.binarySearch(employees.get(i).getSkills(), k) < 0){
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }

        /**
         * Verifica se há alguma tarefa que está sem algum funcionário associado.
         * @return true sse todas as tarefas tiverem algum funcionário associado. Falso caso contrário. 
         */
        public boolean noTaskUnleft(){           
            boolean hasSomeone = false;
            for(int j = 0; j < numberTasks; j++){
                for(int i = 0; i < numberEmployees; i++){
                    //Se na tabela de dedicação tem alguma tarefa sem funcionário associado.
                    if(tableEX[i][j] != 0.0){
                        undt++;
                    }
                }
            }
            return true;
        }
        
        /**
         * Calcula o overwork total da solução.
         * @return Total Overwork
         */
        public double getTotalOverwork(){
            
            return 0;
        }
        
        /**
         * Calcula o custo do projeto.
         * @return Custo do Projeto.
         */
        public double getSolutionValue(){
            double ret = 0;
            for(int i = 0; i < numberEmployees; i++){
                double sum = 0;
                for(int j = 0; j < numberTasks; j++){
                    sum += employees.get(i).getSalary()* tableEX[i][j] * tasks.get(j).getDuration();
                }
                ret += sum;
            }
            return ret;
        }
        
        /**
         * Calcula o quão boa é uma solução.
         * @param molecule
         * @return 
         */
        @Override
        public double evaluate(Mol molecule) {
            double q = getSolutionValue();
            
            if(noTaskUnleft() && hasRequiredSkill()){//solução viável
                return 1/q;
            }else{//solução inviável
                double p = wpenal + wundt*undt + wreqsk*reqsk + wover*getTotalOverwork();
                return 1 / (q + p);
            }
        }
    }
    
    public static void main(String args[]){
        
    }
}