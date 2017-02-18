package jmetal.problems.SPSP_2;

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
    public SPSP_2(int nEmployee, int nTasks) {
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
                int start = 1000000000;
                for(int j = 0; j < i.getAntecessores().size(); j++){
                    start = Math.min(start, pfEntrada.get(i.getAntecessores().get(j)));
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
        for (int j = 0; j < nTasks; j++) {
            double sum = 0;
            for (int i = 0; i < nEmployee; i++) {
                sum += tableET[i][j];
            }
            arrTasks.get(j).setDuration((int) (arrTasks.get(j).getEffort() / sum));
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
    public void calculateDuration(){
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
            for (int i = 0; i < nEmployee; i++) {
                //Se na tabela de dedicação tem alguma tarefa sem funcionário associado.
                if (tableET[i][j] == 0.0) {
                    undt++;
                }
            }
        }
        return undt > 0;
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
            for(int instante = 1; instante <= projectDuration; instante++){
                double sum = 0.0;
                for(Task_2 task : arrTasks){
                    if(task.getStart() <= instante && instante <= task.getEnd()){
                        sum += tableET[ep.getId()][task.getId()];
                    }
                }
                overwork.add(sum);
            }
            ep.setWorkInstant(overwork);
        }
    }
    
    /**
     * Calcula o overwork do projeto baseado no overwork dos Funcionários.
     */
    public void getProjectOverowork(){
        for(Employee_2 ep : arrEmployees){
            projectOverwork += ep.getOverDedication();
        }
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
    
        
        
        for(int j = 0; j < numberOfObjectives_; j++){
            solution.setObjective(j, j);
        }
    }
    
}
