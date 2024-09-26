#include <iostream>
#include <vector>
#include <algorithm>
#include <queue>
using namespace std;

// Process Structure
struct Process {
    int id;
    int burst_time;
    int arrival_time;
    int priority;
    int remaining_time;
};

// FCFS Scheduling Algorithm
void FCFS(vector<Process> processes) {
    cout << "\n--- FCFS Scheduling ---\n";
    sort(processes.begin(), processes.end(), [](Process a, Process b) {
        return a.arrival_time < b.arrival_time;
    });
    
    int total_time = 0;
    for (auto &process : processes) {
        if (total_time < process.arrival_time) {
            total_time = process.arrival_time;
        }
        total_time += process.burst_time;
        cout << "Process " << process.id << " finishes at time " << total_time << endl;
    }
}

// SJF Scheduling Algorithm (Non-preemptive)
void SJF(vector<Process> processes) {
    cout << "\n--- SJF Scheduling (Non-preemptive) ---\n";
    int time = 0;
    vector<Process> completed;
    while (!processes.empty()) {
        auto it = min_element(processes.begin(), processes.end(), [&time](Process a, Process b) {
            if (a.arrival_time <= time && b.arrival_time <= time) {
                return a.burst_time < b.burst_time;
            }
            return a.arrival_time < b.arrival_time;
        });
        
        if (it->arrival_time > time) {
            time = it->arrival_time;
        }
        time += it->burst_time;
        cout << "Process " << it->id << " finishes at time " << time << endl;
        processes.erase(it);
    }
}

// Priority Scheduling Algorithm (Non-preemptive)
void PriorityNonPreemptive(vector<Process> processes) {
    cout << "\n--- Priority Scheduling (Non-preemptive) ---\n";
    int time = 0;
    vector<Process> completed;
    while (!processes.empty()) {
        auto it = min_element(processes.begin(), processes.end(), [&time](Process a, Process b) {
            if (a.arrival_time <= time && b.arrival_time <= time) {
                return a.priority < b.priority;
            }
            return a.arrival_time < b.arrival_time;
        });
        
        if (it->arrival_time > time) {
            time = it->arrival_time;
        }
        time += it->burst_time;
        cout << "Process " << it->id << " finishes at time " << time << endl;
        processes.erase(it);
    }
}

// Priority Scheduling Algorithm (Preemptive)
void PriorityPreemptive(vector<Process> processes) {
    cout << "\n--- Priority Scheduling (Preemptive) ---\n";
    int time = 0;
    while (!processes.empty()) {
        auto it = min_element(processes.begin(), processes.end(), [&time](Process a, Process b) {
            if (a.arrival_time <= time && b.arrival_time <= time) {
                return a.priority < b.priority;
            }
            return a.arrival_time < b.arrival_time;
        });
        
        if (it->arrival_time > time) {
            time = it->arrival_time;
        }
        
        // Execute process for 1 unit of time
        cout << "Process " << it->id << " is running at time " << time << endl;
        time++;
        it->remaining_time--;
        
        if (it->remaining_time == 0) {
            cout << "Process " << it->id << " finishes at time " << time << endl;
            processes.erase(it);
        }
    }
}

int main() {
    int n;
    cout << "Enter the number of processes: ";
    cin >> n;

    vector<Process> processes(n);

    // Input process data from user
    for (int i = 0; i < n; ++i) {
        cout << "\nEnter details for process " << i+1 << ":\n";
        cout << "Burst time: ";
        cin >> processes[i].burst_time;
        cout << "Arrival time: ";
        cin >> processes[i].arrival_time;
        cout << "Priority (lower number means higher priority): ";
        cin >> processes[i].priority;
        processes[i].id = i + 1;
        processes[i].remaining_time = processes[i].burst_time;
    }

    // Call the scheduling algorithms
    FCFS(processes);

    // Reset the processes for each scheduling algorithm
    for (auto& process : processes) {
        process.remaining_time = process.burst_time;
    }
    SJF(processes);

    for (auto& process : processes) {
        process.remaining_time = process.burst_time;
    }
    PriorityNonPreemptive(processes);

    for (auto& process : processes) {
        process.remaining_time = process.burst_time;
    }
    PriorityPreemptive(processes);

    return 0;
}
