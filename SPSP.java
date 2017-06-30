package jmetal.problems.SPSP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 * Cada instancia recebe uma cópia do ArrayList de Reader.java,
 visto que em cada instância alterações particulares ocorrem nas tarefas,
 dada a tabela de verifição. O resto dos atributos, como o Grafo,
 * são estáticos e podem ser compartilhados por todas as instâncias da solução.
 * @author jjaneto
 */
public class SPSP extends Problem{

    public static int L;
    public int nEmployee;
    public int nTasks;
    public int undt; //numero de tarefas sem funcionario associado
    public double projectOverwork;
    public double tableET[][];
    public double maxOverwork = -1;
    public final double epsilon = 0.00001;
    public double f[];
    public ArrayList<Task> arrTasks;
    public ArrayList<Employee> arrEmployees;    
    
    /**
     * Os objetivos são: menor custo e menor tempo. As restriçōes são: cada tarefa tem que 
     * ter ao menos um funcionário alocado, um funcionário deve possuir todas as habilidades
     * necessárias para completar a tarefa designada, e nenhum funcionário deve exceder seu 
     * limite de trabalho. A quantidade de variáveis é Funcionários * Tarefas.
     * @param nEmployee
     * @param nTasks 
     */
    public SPSP(int nEmployee, int nTasks) throws IOException{
        this.nEmployee = nEmployee;
        this.nTasks = nTasks;
        this.tableET = new double[nEmployee][nTasks];
        arrTasks = new ArrayList<>(Reader.arrTask);
        arrEmployees = new ArrayList<>(Reader.arrEmployee);
        
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
      
     }
    
    
    /**
     * Atribui aleatoriamente valores a tabela de dedicação.
     * @param solution
     */
    public void tableValues(Solution solution){
        try{
            Double[] decisionVariables = ((ArrayReal) (solution.getDecisionVariables()[0])).array_;
            for(int i = 0; i < nEmployee; i++){
                for(int j = 0; j < nTasks; j++){
                    tableET[i][j] = decisionVariables[i*nTasks + j];
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * Dada a duração de cada tarefa e o TPG, 
     * é possível calcular o início e o fim de cada tarefa.
     */
    public void calculateStartEndTasks(){
        ArrayList<Integer> pfEntrada = Graph.getPfEntrada();
        for(Task task : arrTasks){
            if(pfEntrada.get(task.getId()) == 0){
                task.setStart(0);
                task.setEnd(task.getStart() + task.getDuration());
            }
            //System.out.println("Tarefa " + task.getId() + " começa em 0");
        }
        for(Task task : arrTasks){//para cada tarefa
            if(pfEntrada.get(task.getId()) != 0){
                double start = -1;
                ArrayList<ArrayList<Integer> > antList = Reader.graph.getAntecessor();
                ArrayList<Integer> vet = antList.get(task.getId());
                for(int j = 0; j < vet.size(); j++){
                    int antecessor = vet.get(j);
                    start = Math.max(start, arrTasks.get(antecessor).getEnd());
                }
                task.setStart(start);
                task.setEnd(task.getStart() + task.getDuration());
                //System.out.println("Tarefa " + task.getId() + " começa em " + task);
            }
        }
    }
    
    /**
     * Calcula a duração de cada tarefa.
     */
    public void calculateDurationTasks() {
        for(Task t : arrTasks){
            double sum = 0;
            for(Employee e : arrEmployees){
                sum += tableET[e.getId()][t.getId()];
            }
            t.setDuration((t.getEffort()/sum));
            //System.out.println("Tarefa " + t.getId() + " tem " + t.getDuration() + " de duração");
       }
    }
    
    /**
     * Calcula o custo do projeto.
     */
    public void calculateProjectCost(){
        for(Employee employee : arrEmployees){
            double sum = 0;
            for(Task task : arrTasks){
                sum += employee.getSalary() * tableET[employee.getId()][task.getId()] * task.getDuration();
            }
            f[0] += sum;
        }
    }
    
    /**
     * Calcula a duração do projeto, que é o mesmo que o final da ultima tarefa executada.
     */
    public void calculateProjectDuration(){
        for(Task i : arrTasks){
            f[1] = Math.max(f[1], i.getEnd());
        }
    }
    
    /**
     * Verifica se há alguma tarefa que está sem algum funcionário associado.
     *
     * @return true sse todas as tarefas tiverem algum funcionário associado.
     * Falso caso contrário.
     */
    public boolean noTaskUnleft() {
        for(Task t : arrTasks){
            int none = 0;
            for(Employee e : arrEmployees){
//                System.out.println("task id " + t.getId() + " emp id " + e.getId());
                if(tableET[e.getId()][t.getId()] == 0.0){
                    none++;
                }
            }
            if(none == nEmployee) undt++;
        }
//        for (int j = 0; j < nTasks; j++) {
//            int none = 0;
//            for (int i = 0; i < nEmployee; i++) {
//                //Se na tabela de dedicação tem alguma tarefa sem funcionário associado.
//                if (tableET[i][j] == 0.0) {
//                    none++;
//                }
//            }
//            if(none == nEmployee) undt++;
//        }
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
                        if (Collections.binarySearch(Reader.arrEmployee.get(i).getSkills(), k) < 0) {
//                            System.out.println("funcionario " + i + "nao possui skill " + k);
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
       for(double stnt = 0.0; stnt <= f[1]; stnt++){
            for(Employee ep : arrEmployees){
                double sum = 0.0;
                for(Task t : arrTasks){
                    if(t.getStart() <= stnt && stnt <= t.getEnd()){
                        sum += tableET[ep.getId()][t.getId()];
                    }
                }
                if(sum > ep.getMaxDedication()){
                    ep.addOverDedication(sum - ep.getMaxDedication());
                }
                maxOverwork = Math.max(maxOverwork, sum);
            }
        }
    }
    
    /**
     * Calcula o overwork do projeto baseado no overwork dos Funcionários.
     */
    public void getProjectOverwork(){
        projectOverwork = 0.0;
        for(Employee ep : arrEmployees){
            projectOverwork += ep.getOverDedication();
        }
    }  
    
    public void repairOperator(Solution solution) throws JMException{
        XReal arraySolution = new XReal(solution);
        for (int i = 0; i < nEmployee; i++) {
            for (int j = 0; j < nTasks; j++) {
                double value = epsilon + maxOverwork;
                tableET[i][j] = tableET[i][j] / value;
                arraySolution.setValue(i * nTasks + j, tableET[i][j]);
            }
        }
        
        f[1] *= (epsilon + maxOverwork);
        for (Employee ep : arrEmployees) {
            ep.setOverDedication(0.0);
        }
        projectOverwork = 0.0;
        maxOverwork = -1;
    }

    @Override
    public void evaluate(Solution solution) throws JMException { //custo e duracao
        f = new double[numberOfObjectives_];
        
        maxOverwork = 0;
        projectOverwork = 0;
        undt = 0;
        
        tableValues(solution);
        calculateDurationTasks();
        calculateStartEndTasks();
        calculateProjectDuration();
        calculateProjectCost();
        getEmployeeOverwork();
        getProjectOverwork();
        
        if(projectOverwork > 0.0){
            repairOperator(solution);
        }
        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }
    
    @Override
    public void evaluateConstraints(Solution solution) throws JMException{ //overwork, hasTakUndone, requiredSkill
        int violatedConstraints = 0;
        if(projectOverwork > 0.0) violatedConstraints++;
        if(!noTaskUnleft()) violatedConstraints++;
        if(!hasRequiredSkill()) violatedConstraints++;
        solution.setNumberOfViolatedConstraint(violatedConstraints);
        solution.setOverallConstraintViolation(violatedConstraints);
    }
    
}
