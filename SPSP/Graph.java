package Prob.SPSP;

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

    private static final int DFS_WHITE = -1; // normal DFS
    private static final int DFS_BLACK = 1;
    private static int numComp;
  
    static Vector<Vector<Integer> > adjList;
    static Vector<Integer> dfs_num, topologicalSort;
    
    public Graph(int qtdTasks) {
        dfs_num = new Vector<>();
        adjList = new Vector<>();
        for(int i = 0; i < qtdTasks; i++){
            adjList.add(new Vector<>());
        }
    }
    
    private static void initDFS(int V) { // used in normal DFS
        dfs_num = new Vector < Integer > ();
        dfs_num.addAll(Collections.nCopies(V, DFS_WHITE));
        numComp = 0;
    }
    
    private static void initTopologicalSort(int V) {
        initDFS(V);
        topologicalSort = new Vector < Integer > ();
        for (int i = 0; i < V; i++)
            if (dfs_num.get(i) == DFS_WHITE)
                topoVisit(i);
    }
    
    private static void topoVisit(int u) {
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
