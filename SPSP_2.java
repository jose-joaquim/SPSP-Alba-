package jmetal.problems.SPSP_2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.util.JMException;

/**
 * Cada instancia recebe uma cópia do ArrayList de Reader_2.java,
 * visto que em cada instância alterações particulares ocorrem nas tarefas,
 * dada a tabela de verifição. O resto dos atributos, como o Grafo,
 * são estáticos e podem ser compartilhados por todas as instâncias da solução.
 * @author jjaneto
 */
public class SPSP_2 extends Problem{

    public int L;
    public int nEmployee;
    public int nTasks;
    public int undt; //numero de tarefas sem funcionario associado
    public double projectCost, projectDuration, projectOverwork;
    public double tableET[][];
    public double maxOverwork = -1;
    public final double epsilon = 0.00001;
    
    public ArrayList<Task_2> arrTasks;
    public ArrayList<Employee_2> arrEmployees;    
    
    /**
     * Os objetivos são: menor custo e menor tempo. As restriçōes são: cada tarefa tem que 
     * ter ao menos um funcionário alocado, um funcionário deve possuir todas as habilidades
     * necessárias para completar a tarefa designada, e nenhum funcionário deve exceder seu 
     * limite de trabalho. A quantidade de variáveis é Funcionários * Tarefas.
     * @param nEmployee
     * @param nTasks 
     */
    public SPSP_2(int nEmployee, int nTasks) throws IOException{
        this.nEmployee = nEmployee;
        this.nTasks = nTasks;
        this.tableET = new double[nEmployee][nTasks];
        this.projectDuration = -1;
        arrTasks = new ArrayList<>(Reader_2.arrTask);
        arrEmployees = new ArrayList<>(Reader_2.arrEmployee);
        
        numberOfObjectives_ = 2;                       
        numberOfConstraints_ = 3;                       
        numberOfVariables_ = L = nEmployee * nTasks;
        
        lowerLimit_ = new double[numberOfVariables_];
        upperLimit_ = new double[numberOfVariables_];        
        for (int var = 0; var < L; var++){
          lowerLimit_[var] = 0.0;
          upperLimit_[var] = 1.0;
        }
        
        solutionType_ = new ArrayRealSolutionType(this);
        tableValues();
        
        calculateDurationTasks();
        calculateStartEndTasks();
        calculateProjectDuration();
        calculateProjectCost();
        getEmployeeOverwork();
        getProjectOverwork();
        
        if(projectOverwork > 0.0){
            repairOperator();
        }
        
     }
    
    
    /**
     * Atribui aleatoriamente valores a tabela de dedicação.
     */
    public void tableValues(){
        for(int i = 0; i < nEmployee; i++){
            for(int j = 0; j < nTasks; j++){
                tableET[i][j] = Math.random();
            }
        }
    }
    
    /**
     * Dada a duração de cada tarefa e o TPG, 
     * é possível calcular o início e o fim de cada tarefa.
     */
    public void calculateStartEndTasks(){
        ArrayList<Integer> pfEntrada = Graph_2.getPfEntrada();
        for(Task_2 i : arrTasks){
            if(pfEntrada.get(i.getId()) == 0){//se ele nao tem nenhum pre-requisito
                i.setStart(0);
                i.setEnd(i.getStart() + i.getDuration());
            }else{//se nao, pegue o ultimo pre requisito a acabar (o que tem maior taskEnd)
                double start = -1;
                ArrayList<ArrayList<Integer> > antList = Reader_2.graph.getAntecessor();
                ArrayList<Integer> vet = antList.get(i.getId());
                for(int j = 0; j < vet.size(); j++){
                    int antecessor = vet.get(j);
                    start = Math.max(start, arrTasks.get(antecessor).getEnd());
                }
                i.setStart(start);
                i.setEnd(i.getStart() + i.getDuration());
            }
        }
    }
    
    /**
     * Calcula a duração de cada tarefa.
     */
    public void calculateDurationTasks() {
        for(Task_2 t : arrTasks){
            double sum = 0;
            for(Employee_2 e : arrEmployees){
                sum += tableET[e.getId()][t.getId()];
            }
            t.setDuration((t.getEffort()/sum));
       }
    }
    
    /**
     * Calcula o custo do projeto.
     */
    public void calculateProjectCost(){
        for(Employee_2 i : arrEmployees){
            double sum = 0;
            for(Task_2 j : arrTasks){
                sum += i.getSalary() * tableET[i.getId()][j.getId()] * j.getDuration();
            }
            projectCost += sum;
        }
    }
    
    /**
     * Calcula a duração do projeto, que é o mesmo que o final da ultima tarefa executada.
     */
    public void calculateProjectDuration(){
        for(Task_2 i : arrTasks){
            projectDuration = Math.max(projectDuration, i.getEnd());
        }
    }
    
    /**
     * Verifica se há alguma tarefa que está sem algum funcionário associado.
     *
     * @return true sse todas as tarefas tiverem algum funcionário associado.
     * Falso caso contrário.
     */
    public boolean noTaskUnleft() {
        for (int j = 0; j < nTasks; j++) {
            int none = 0;
            for (int i = 0; i < nEmployee; i++) {
                //Se na tabela de dedicação tem alguma tarefa sem funcionário associado.
                if (tableET[i][j] == 0.0) {
                    none++;
                }
            }
            if(none == nEmployee) undt++;
        }
        return (undt == 0);
    }
    
    /**
     * Para cada candidato, irá verificar se ele possui todas as habilidades de
     * tal tarefa.
     *
     * @param employee
     * @param task
     * @return true sse o employee possui todas as habilidades. Falso caso
     * contrário.
     */
    public boolean hasRequiredSkill() {
        for (int i = 0; i < nEmployee; i++) {
            for (int j = 0; j < nTasks; j++) {
                if (tableET[i][j] > 0) {
                    for (Integer k : arrTasks.get(j).getRequiredSkills()) {
                        if (Collections.binarySearch(Reader_2.arrEmployee.get(i).getSkills(), k) < 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Calcula o overwork de cada funcionário.
     * Lembrar de verifica se é assim mesmo, devido a complexidade alta.
     * O(QuantidadeFuncionarios * QuantidadeTarefas * ProjetoDuracao)
     */
    public void getEmployeeOverwork(){
        for(Employee_2 ep : arrEmployees){
            ArrayList<Double> overwork = new ArrayList<>();
            double overDedication = 0.0;
            for(int instante = 0; instante <= (int) projectDuration; instante++){
                double sum = 0.0;
                for(Task_2 task : arrTasks){
                    if(task.getStart() <= instante && instante <= task.getEnd()){
                        
                        sum += tableET[ep.getId()][task.getId()];
                    }
                }
                if(sum > ep.getMaxDedication()){
                    overwork.add(sum - ep.getMaxDedication());
                }else overwork.add(0.0);
                overDedication += overwork.get(overwork.size() - 1);
                maxOverwork = Math.max(maxOverwork, overwork.get(overwork.size() - 1));
            }
            ep.setWorkInstant(overwork);
            ep.setOverDedication(overDedication);
        }
    }
    
    /**
     * Calcula o overwork do projeto baseado no overwork dos Funcionários.
     */
    public void getProjectOverwork(){
        for(Employee_2 ep : arrEmployees){
            projectOverwork += ep.getOverDedication();
        }
        //System.out.println("projectOverwork eh " + projectOverwork);
    }  
    
    public void repairOperator(){
        for(int i = 0; i < nEmployee; i++){
            for(int j = 0; j < nTasks; j++){
                double value = epsilon + maxOverwork;
                tableET[i][j] = tableET[i][j] / value;
            }
        }
        projectDuration *= (epsilon + maxOverwork);
    }

    @Override
    public void evaluate(Solution solution) throws JMException { //custo e duracao
        solution.setObjective(0, projectCost);
        solution.setObjective(1, projectDuration);
    }
    
    @Override
    public void evaluateConstraints(Solution solution) throws JMException{ //overwork, hasTakUndone, requiredSkill
        //Usar o método setNumberOfViolatedConstraint()
        int violatedConstraints = 0;
        if(projectOverwork > 0.0) violatedConstraints++;
        if(!noTaskUnleft()) violatedConstraints++;
        if(!hasRequiredSkill()) violatedConstraints++;
        solution.setNumberOfViolatedConstraint(violatedConstraints);
    }
}
