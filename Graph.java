package jmetal.problems.SPSP;

import jmetal.problems.SPSP.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author jjaneto
 */
public class Graph {

    public static final int DFS_WHITE = -1; // normal DFS
    public static final int DFS_BLACK = 1;
    public static int numComp;
  
    public static Vector<Vector<Integer> > adjList;
    public static Vector<Integer> dfs_num, topologicalSort;
    public static ArrayList<Integer> pfEntrada;
    public static ArrayList<ArrayList<Integer> > antecessor;
    
    public Graph(int qtdTasks) {
        dfs_num = new Vector<>();
        adjList = new Vector<>();
        pfEntrada = new ArrayList<>();
        antecessor = new ArrayList<>();
        for(int i = 0; i < qtdTasks + 1; i++){
            adjList.add(new Vector<>());
            antecessor.add(new ArrayList<>());
            pfEntrada.add(0);
        }
    }

    public static ArrayList<Integer> getPfEntrada() {
        return pfEntrada;
    }
    
    public static void initDFS(int V) { // used in normal DFS
        dfs_num = new Vector < Integer > ();
        dfs_num.addAll(Collections.nCopies(V, DFS_WHITE));
        numComp = 0;
    }
    
    public static void initTopologicalSort(int V) {
        initDFS(V);
        topologicalSort = new Vector < Integer > ();
        for (int i = 0; i < V; i++)
            if (dfs_num.get(i) == DFS_WHITE)
                topoVisit(i);
    }
    
    public static void topoVisit(int u) {
        dfs_num.set(u, DFS_BLACK);
        Iterator it = adjList.get(u).iterator();
        while (it.hasNext()) {
          Integer v = (Integer) it.next();
          if (dfs_num.get(v) == DFS_WHITE)
            topoVisit(v);
        }
        topologicalSort.add(u);
    }
    
    public Vector<Integer> getTopologicalSort(){
        initTopologicalSort(adjList.size());
        return topologicalSort;
    }
    
    
    
    public void addEdge(int u, int v){
        try{
            adjList.get(u).add(v);
            antecessor.get(v).add(u);
            int grau = pfEntrada.get(v);
            pfEntrada.set(v, grau + 1);
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("u eh " + u + " | v eh " + v);
            throw new RuntimeException();
        }
        
    }
    

    public static ArrayList<ArrayList<Integer>> getAntecessor() {
        return antecessor;
    }
}
