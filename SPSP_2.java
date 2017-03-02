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
    public double projectOverwork;
    public double tableET[][];
    public double maxOverwork = -1;
    public final double epsilon = 0.00001;
    public double f[];
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
        
     }
    
    
    /**
     * Atribui aleatoriamente valores a tabela de dedicação.
     */
    public void tableValues(){
        for(int i = 0; i < nEmployee; i++){
            for(int j = 0; j < nTasks; j++){
                tableET[i][j] = Math.random();
                //System.out.println(tableET[i][j] + " ");
            }
            //System.out.println("");
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
            /*System.out.println("Tarefa " + i.getId() + " tem duracao de " + i.getDuration() + 
                        " comeca em " + i.getStart() + " e termina em " + i.getEnd());*/
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
            f[0] += sum;
        }
    }
    
    /**
     * Calcula a duração do projeto, que é o mesmo que o final da ultima tarefa executada.
     */
    public void calculateProjectDuration(){
        for(Task_2 i : arrTasks){
            f[1] = Math.max(f[1], i.getEnd());
        }
        //System.out.println("projectDuration eh " + projectDuration);
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
        for(int stnt = 0; stnt <= (int) f[1]; stnt++){
            for(Employee_2 ep : arrEmployees){
                double sum = 0.0;
                for(Task_2 t : arrTasks){
                    if(t.getStart() <= stnt && stnt <= t.getEnd()){
                        sum += tableET[ep.getId()][t.getId()];
                    }
                }
                if(sum > ep.getMaxDedication()){
                    ep.addOverDedication(sum - ep.getMaxDedication());
                }
                //System.out.println("(INSTANTE " + stnt + ") Funcionario " + ep.getId() + " sum = " + sum);
                maxOverwork = Math.max(maxOverwork, sum);
            }
        }
    }
    
    /**
     * Calcula o overwork do projeto baseado no overwork dos Funcionários.
     */
    public void getProjectOverwork(){
        projectOverwork = 0.0;
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
        f[1] *= (epsilon + maxOverwork);
        for(int i = 0; i < nEmployee; i++) arrEmployees.get(i).setOverDedication(0.0);
        projectOverwork = 0.0;
        getEmployeeOverwork();
        getProjectOverwork();
    }

    @Override
    public void evaluate(Solution solution) throws JMException { //custo e duracao
        f = new double[numberOfObjectives_];
        
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
        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }
    
    @Override
    public void evaluateConstraints(Solution solution) throws JMException{ //overwork, hasTakUndone, requiredSkill
        //Usar o método setNumberOfViolatedConstraint()
        int violatedConstraints = 0;
        if(projectOverwork > 0.0) violatedConstraints++;
        if(!noTaskUnleft()) violatedConstraints++;
        if(!hasRequiredSkill()) violatedConstraints++;
        solution.setNumberOfViolatedConstraint(violatedConstraints);
        solution.setOverallConstraintViolation(violatedConstraints);
    }
    
    public void print(){
        System.out.println("projectCost" + f[0]);
        System.out.println("projectDuration" + f[1]);
        System.out.println("projectOverwork" + projectOverwork);
        System.out.println("noTaskUnleft" + !noTaskUnleft());
        System.out.println("hasRequiredSkill" + !hasRequiredSkill());
    }
    
    /*public static void main(String args[]) throws IOException{
        Reader_2 rd = new Reader_2(new File("/Users/jjaneto/Google Drive/Pesquisa Leila/INSTANCIAS/scalableSPS1.conf"));
        new SPSP_2(rd.arrEmployee.size(), rd.arrTask.size());
    }*/
}
