#include <iostream>
#include <vector>
#include <queue>
#include <limits.h>

using namespace std;

class Solution {
public:
    // Function to find the shortest distance of all the vertices from the source vertex S.
    vector<int> dijkstra(int V, vector<vector<int> > adj[], int S) {
        // Priority queue to store (distance, node)
        priority_queue<pair<int, int>, vector<pair<int, int> >, greater<pair<int, int> > > pq;
        vector<int> dist(V, INT_MAX); // Distance vector initialized with a large value
        dist[S] = 0;
        pq.push(make_pair(0, S));
        
        while (!pq.empty()) {
            int dis = pq.top().first;
            int node = pq.top().second;
            pq.pop();
            
            for (int i = 0; i < adj[node].size(); i++) {
                int adjNode = adj[node][i][0];
                int edgeWeight = adj[node][i][1];
                
                if (dis + edgeWeight < dist[adjNode]) {
                    dist[adjNode] = dis + edgeWeight;
                    pq.push(make_pair(dist[adjNode], adjNode));
                }
            }
        }
        
        return dist;
    }
};

int main() {
    int t;
    cout << "Enter the number of test cases: ";
    cin >> t;
    
    while (t--) {
        int V, E;
        cout << "Enter the number of vertices: ";
        cin >> V;
        cout << "Enter the number of edges: ";
        cin >> E;
        
        // Initialize adjacency list
        vector<vector<int> > adj[V];
        
        // Read edges
        cout << "Enter the edges (format: u v w) where u and v are vertices and w is the weight:\n";
        for (int i = 0; i < E; i++) {
            int u, v, w;
            cin >> u >> v >> w;
            vector<int> edge1;
            edge1.push_back(v);
            edge1.push_back(w);
            adj[u].push_back(edge1);

           // to add the reverse edge for the undirected graph:
            vector<int> edge2;
            edge2.push_back(u);
            edge2.push_back(w);
            adj[v].push_back(edge2);
        }
        
        int S;
        cout << "Enter the source vertex: ";
        cin >> S;
        
        // Call Dijkstra's algorithm
        Solution obj;
        vector<int> res = obj.dijkstra(V, adj, S);
        
        // Display results
        cout << "Shortest distances from source vertex " << S << ":\n";
        for (int i = 0; i < V; i++) {
            cout << "Vertex " << i << ": " << res[i] << endl;
        }
        cout << endl;
    }
    
    return 0;
}


