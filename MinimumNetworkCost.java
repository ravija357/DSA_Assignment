//3.a)

import java.util.*;

class MinimumNetworkCost {
    static class Edge {
        int u, v, cost;
        Edge(int u, int v, int cost) {
            this.u = u;
            this.v = v;
            this.cost = cost;
        }
    }

    static class UnionFind {
        int[] parent, rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        boolean union(int x, int y) {
            int rootX = find(x), rootY = find(y);
            if (rootX == rootY) return false;

            if (rank[rootX] > rank[rootY]) parent[rootY] = rootX;
            else if (rank[rootX] < rank[rootY]) parent[rootX] = rootY;
            else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            return true;
        }
    }

    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        List<Edge> edges = new ArrayList<>();

        // Add edges for direct connections
        for (int[] conn : connections) {
            edges.add(new Edge(conn[0] - 1, conn[1] - 1, conn[2])); // Convert 1-based index to 0-based
        }

        // Add virtual node (index n) with edges to all devices for module installation
        for (int i = 0; i < n; i++) {
            edges.add(new Edge(i, n, modules[i]));  // Virtual node = n
        }

        // Sort edges based on cost (Kruskal's Algorithm)
        edges.sort(Comparator.comparingInt(e -> e.cost));

        UnionFind uf = new UnionFind(n + 1); // Include virtual node
        int totalCost = 0, connectedEdges = 0;

        for (Edge e : edges) {
            if (uf.union(e.u, e.v)) { // If adding this edge connects new components
                totalCost += e.cost;
                connectedEdges++;
                if (connectedEdges == n) break; // Stop when all devices are connected
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};

        System.out.println(minTotalCost(n, modules, connections)); // Output: 3
    }
}