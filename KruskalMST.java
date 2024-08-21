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
        edges.sort(Comparator.comparingInt(a -> a[0])); // Sort edges by weight
        int mstWt = 0;

        for (int[] edge : edges) {
            int wt = edge[0];
            int u = edge[1];
            int v = edge[2];
            if (ds.findUPar(u) != ds.findUPar(v)) {
                mstWt += wt;
                ds.unionBySize(u, v);
            }
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
