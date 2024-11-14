#include <iostream>
#include <vector>
#include <algorithm>
#include <iomanip>

using namespace std;

// Function to print the current state of frames and additional information in tabular format
void printTableHeader() {
    cout << left << setw(8) << "Page" 
         << setw(20) << "Action"
         << setw(25) << "Frames (after action)" 
         << setw(12) << "Page Fault?" 
         << "Replaced Page" << endl;
    cout << string(80, '-') << endl;
}

void printTableRow(int page, string action, const vector<int>& frames, int capacity, bool pageFault, int replacedPage) {
    cout << left << setw(8) << page 
         << setw(20) << action;

    // Print the current state of frames
    for (int i = 0; i < capacity; ++i) {
        if (i < frames.size()) {
            cout << setw(2) << frames[i] << " ";
        } else {
            cout << "-- ";
        }
    }

    cout << setw(12) << (pageFault ? "Yes" : "No") 
         << setw(15) << (replacedPage != -1 ? to_string(replacedPage) : "--") << endl;
}

int optPageFaults(const vector<int>& pages, int capacity, int &pageHits) {
    int pageFaults = 0;
    pageHits = 0; // Initialize page hits
    vector<int> frames;

    cout << "\nOptimal (OPT) Page Replacement Algorithm\n";
    printTableHeader();

    for (int i = 0; i < pages.size(); i++) {
        string action;
        bool pageFault = false;
        int replacedPage = -1;

        if (find(frames.begin(), frames.end(), pages[i]) == frames.end()) {
            pageFault = true;
            if (frames.size() < capacity) {
                frames.push_back(pages[i]);
                action = "Page inserted";
            } else {
                int farthest = i, replaceIndex = 0;
                for (int j = 0; j < frames.size(); j++) {
                    int k;
                    for (k = i + 1; k < pages.size(); k++) {
                        if (frames[j] == pages[k]) {
                            if (k > farthest) {
                                farthest = k;
                                replaceIndex = j;
                            }
                            break;
                        }
                    }
                    if (k == pages.size()) {
                        replaceIndex = j;
                        break;
                    }
                }
                replacedPage = frames[replaceIndex];
                frames[replaceIndex] = pages[i];
                action = "Page replaced";
            }
            pageFaults++;
        } else {
            action = "Page hit";
            pageHits++; // Increment page hits
        }

        printTableRow(pages[i], action, frames, capacity, pageFault, replacedPage);
    }

    return pageFaults;
}

int fifoPageFaults(const vector<int>& pages, int capacity, int &pageHits) {
    int pageFaults = 0;
    pageHits = 0; // Initialize page hits
    vector<int> frames;
    int frameIndex = 0;

    cout << "\nFirst-In-First-Out (FIFO) Page Replacement Algorithm\n";
    printTableHeader();

    for (int i = 0; i < pages.size(); i++) {
        string action;
        bool pageFault = false;
        int replacedPage = -1;

        if (find(frames.begin(), frames.end(), pages[i]) == frames.end()) {
            pageFault = true;
            if (frames.size() < capacity) {
                frames.push_back(pages[i]);
                action = "Page inserted";
            } else {
                replacedPage = frames[frameIndex];
                frames[frameIndex] = pages[i];
                action = "Page replaced";
                frameIndex = (frameIndex + 1) % capacity;
            }
            pageFaults++;
        } else {
            action = "Page hit";
            pageHits++; // Increment page hits
        }

        printTableRow(pages[i], action, frames, capacity, pageFault, replacedPage);
    }

    return pageFaults;
}

int main() {
    vector<int> pages;
    int capacity;

    // Input
    cout << "Enter the number of pages: ";
    int n;
    cin >> n;

    cout << "Enter the page reference sequence:\n";
    for (int i = 0; i < n; i++) {
        int page;
        cin >> page;
        pages.push_back(page);
    }

    cout << "Enter the number of frames: ";
    cin >> capacity;

    cout << "\nPage reference sequence: ";
    for (int page : pages) {
        cout << page << " ";
    }
    cout << endl;

    cout << "Number of frames: " << capacity << endl;

    // Variables to hold page fault and hit counts
    int optFaults, fifoFaults, optHits, fifoHits;

    // Run and display results for OPT and FIFO
    optFaults = optPageFaults(pages, capacity, optHits);
    fifoFaults = fifoPageFaults(pages, capacity, fifoHits);

    // Display summary
    cout << "\nSummary of Page Faults:\n";
    cout << "-----------------------\n";
    cout << left << setw(30) << "Algorithm" 
         << setw(10) << "Page Faults" 
         << "   Page Hits" << endl;
    cout << left << setw(30) << "Optimal (OPT)" 
         << setw(10) << optFaults 
         << optHits << endl;
    cout << left << setw(30) << "First-In-First-Out (FIFO)" 
         << setw(10) << fifoFaults 
         << fifoHits << endl;

    return 0;
}
