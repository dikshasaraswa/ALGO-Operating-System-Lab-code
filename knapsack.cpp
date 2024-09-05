#include <iostream>
#include <vector>
#include <algorithm>
#include <iomanip>

using namespace std;

struct Item {
    double weight;
    double profit;
    double index;
    double cost; // Profit-to-weight ratio
};

// Comparator function to sort items by profit-to-weight ratio in descending order
bool compare(Item a, Item b) {
    return a.cost > b.cost;
}

// Function to solve the 0-1 Knapsack problem
int knapsack01(const vector<int>& weights, const vector<int>& profits, int capacity) {
    int n = profits.size();
    vector<vector<int> > dp(n + 1, vector<int>(capacity + 1, 0));

    // Build the DP table
    for (int i = 1; i <= n; i++) {
        for (int w = 1; w <= capacity; w++) {
            if (weights[i - 1] <= w) {
                dp[i][w] = max(profits[i - 1] + dp[i - 1][w - weights[i - 1]], dp[i - 1][w]);
            } else {
                dp[i][w] = dp[i - 1][w];
            }
        }
    }

    // Print the DP table
    cout << "\n0-1 Knapsack DP Table:\n";
    for (int i = 0; i <= n; i++) {
        for (int w = 0; w <= capacity; w++) {
            cout << setw(3) << dp[i][w] << " ";
        }
        cout << endl;
    }

    // Show which items are included in the knapsack
    int w = capacity;
    cout << "Items included in 0-1 Knapsack: ";
    for (int i = n; i > 0 && w > 0; i--) {
        if (dp[i][w] != dp[i - 1][w]) {
            cout << i << " ";
            w -= weights[i - 1];
        }
    }
    cout << endl;

    return dp[n][capacity];
}

// Function to solve the fractional knapsack problem
double fractionalKnapsack(int capacity, vector<int>& weights, vector<int>& profits) {
    int n = weights.size();
    vector<Item> items(n);

    for (int i = 0; i < n; i++) {
        items[i].weight = weights[i];
        items[i].profit = profits[i];
        items[i].index = i + 1;
        items[i].cost = profits[i] / weights[i];
    }

    // Sort items by profit-to-weight ratio
    sort(items.begin(), items.end(), compare);

    double totalProfit = 0.0;
    cout << "\nFractional Knapsack Steps:\n";

    for (int i = 0; i < n; i++) {
        if (capacity - items[i].weight >= 0) {
            // If the item can fit in the knapsack, take it all
            capacity -= items[i].weight;
            totalProfit += items[i].profit;
            cout << "Taking full item " << items[i].index << " with weight " << items[i].weight 
                 << " and profit " << items[i].profit << endl;
        } else {
            // Take the fraction of the remaining capacity
            double fraction = (double)capacity / items[i].weight;
            totalProfit += items[i].profit * fraction;
            cout << "Taking fraction " << fraction << " of item " << items[i].index 
                 << " with weight " << items[i].weight << " and profit " << items[i].profit << endl;
            break;
        }
    }

    return totalProfit;
}

int main() {
    int n;
    cout << "Enter the number of items: ";
    cin >> n;

    vector<int> weights(n);
    vector<int> profits(n);

    cout << "Enter the weights of the items:\n";
    for (int i = 0; i < n; i++) {
        cin >> weights[i];
    }

    cout << "Enter the profits of the items:\n";
    for (int i = 0; i < n; i++) {
        cin >> profits[i];
    }

    int capacity;
    cout << "Enter the capacity of the knapsack: ";
    cin >> capacity;

    // 0-1 Knapsack
    int maxProfit01 = knapsack01(weights, profits, capacity);
    cout << "\nMaximum profit (0-1 Knapsack): " << maxProfit01 << endl;

    // Fractional Knapsack
    double maxProfitFractional = fractionalKnapsack(capacity, weights, profits);
    cout << "\nMaximum profit (Fractional Knapsack): " << maxProfitFractional << endl;

    return 0;
}
