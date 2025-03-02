//1.a)
class CriticalTemperature {
    public static int minMeasurements(int k, int n) {
        // dp[moves][k_samples] stores the max temperature level we can check
        int[][] dp = new int[n + 1][k + 1];

        int moves = 0;
        while (dp[moves][k] < n) {
            moves++;
            for (int j = 1; j <= k; j++) {
                dp[moves][j] = dp[moves - 1][j - 1] + dp[moves - 1][j] + 1;
            }
        }
        return moves;
    }

    public static void main(String[] args) {
        System.out.println(minMeasurements(1, 2));  // Output: 2
        System.out.println(minMeasurements(2, 6));  // Output: 3
        System.out.println(minMeasurements(3, 14)); // Output: 4
    }
}
