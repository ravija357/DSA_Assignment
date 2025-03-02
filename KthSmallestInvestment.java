//1.b)


import java.util.PriorityQueue;

class KthSmallestInvestment {
    public static int findKthSmallest(int[] returns1, int[] returns2, int k) {
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) ->
                (returns1[a[0]] * returns2[a[1]]) - (returns1[b[0]] * returns2[b[1]]));

        // Initialize the heap with the first pair (0,0)
        for (int i = 0; i < returns1.length; i++) {
            minHeap.offer(new int[]{i, 0});
        }

        int result = 0;

        while (k > 0 && !minHeap.isEmpty()) {
            int[] current = minHeap.poll();
            int i = current[0], j = current[1];

            result = returns1[i] * returns2[j]; // Compute the product

            // Push the next possible combination from returns2
            if (j + 1 < returns2.length) {
                minHeap.offer(new int[]{i, j + 1});
            }

            k--; // Decrease k
        }

        return result;
    }

    public static void main(String[] args) {
        int[] returns1 = {2, 5};
        int[] returns2 = {3, 4};
        System.out.println(findKthSmallest(returns1, returns2, 2));  // Output: 8

        int[] returns3 = {-4, -2, 0, 3};
        int[] returns4 = {2, 4};
        System.out.println(findKthSmallest(returns3, returns4, 6));  // Output: 0
    }
}