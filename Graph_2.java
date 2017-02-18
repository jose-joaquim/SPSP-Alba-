package jmetal.problems.SPSP_2;

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
public class Graph_2 {

    public static final int DFS_WHITE = -1; // normal DFS
    public static final int DFS_BLACK = 1;
    public static int numComp;
  
    public static Vector<Vector<Integer> > adjList;
    public static Vector<Integer> dfs_num, topologicalSort;
    public static ArrayList<Integer> pfEntrada;
    
    public Graph_2(int qtdTasks) {
        dfs_num = new Vector<>();
        adjList = new Vector<>();
        for(int i = 0; i < qtdTasks; i++){
            adjList.add(new Vector<>());
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
        adjList.get(u).add(v);
        int grau = pfEntrada.get(v);
        pfEntrada.set(v, grau + 1);
    }
    
    /*public static void main(String args[]){
        Graph graph = new Graph(8);
        graph.addEdge(0, 2);
        graph.addEdge(0, 1);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(2, 5);
        graph.addEdge(7, 6);
        graph.getTopologicalSort();
        for(int i = topologicalSort.size() - 1; i >= 0; i--){
            System.out.println(topologicalSort.get(i));
        }
    }*/
}
