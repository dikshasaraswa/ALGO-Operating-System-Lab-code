import java.util.*;

class DisjointSet {
    private int[] size, parent;

    public DisjointSet(int n) {
        size = new int[n + 1];
        parent = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int findUPar(int node) {
        if (node == parent[node])
            return node;
        return parent[node] = findUPar(parent[node]);
    }

    public void unionBySize(int u, int v) {
        int ulp_u = findUPar(u);
        int ulp_v = findUPar(v);
        if (ulp_u == ulp_v) return;
        if (size[ulp_u] < size[ulp_v]) {
            parent[ulp_u] = ulp_v;
            size[ulp_v] += size[ulp_u];
        } else {
            parent[ulp_v] = ulp_u;
            size[ulp_u] += size[ulp_v];
        }
    }
}

class Solution {
    public int kruskalMST(int V, List<int[]> edges) {
        DisjointSet ds = new DisjointSet(V);
        
        // Sort edges by weight
        System.out.println("Sorting edges by weight...");
        edges.sort(Comparator.comparingInt(a -> a[0]));
        for (int[] edge : edges) {
            System.out.println("Edge: " + edge[1] + " - " + edge[2] + " with weight: " + edge[0]);
        }

        int mstWt = 0;
        List<int[]> mstEdges = new ArrayList<>();

        System.out.println("\nProcessing edges...");
        for (int[] edge : edges) {
            int wt = edge[0];
            int u = edge[1];
            int v = edge[2];
            
            // Find the ultimate parent of u and v
            int parentU = ds.findUPar(u);
            int parentV = ds.findUPar(v);
            System.out.println("Considering edge " + u + " - " + v + " with weight: " + wt);
            System.out.println("Parent of " + u + " is " + parentU);
            System.out.println("Parent of " + v + " is " + parentV);
            
            // If parents are different, include this edge in the MST
            if (parentU != parentV) {
                System.out.println("Edge " + u + " - " + v + " will be included in the MST.");
                mstWt += wt;
                ds.unionBySize(u, v);
                mstEdges.add(new int[]{u, v, wt});  // Add this edge to the MST list
            } else {
                System.out.println("Edge " + u + " - " + v + " creates a cycle and is skipped.");
            }
            System.out.println();
        }
        
        // Print the MST
        System.out.println("The edges in the Minimum Spanning Tree are:");
        for (int[] mstEdge : mstEdges) {
            System.out.println("Edge: " + mstEdge[0] + " - " + mstEdge[1] + " with weight: " + mstEdge[2]);
        }

        return mstWt;
    }
}

public class KruskalMST {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of vertices: ");
        int V = sc.nextInt();

        System.out.print("Enter the number of edges: ");
        int E = sc.nextInt();

        List<int[]> edges = new ArrayList<>();
        System.out.println("Enter the edges in the format: node1 node2 weight");
        for (int i = 0; i < E; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();
            edges.add(new int[]{w, u, v});  // Storing edges as {weight, u, v}
        }

        Solution sol = new Solution();
        int mstWeight = sol.kruskalMST(V, edges);
        System.out.println("The weight of the Minimum Spanning Tree is: " + mstWeight);
        
        sc.close();
    }
}

