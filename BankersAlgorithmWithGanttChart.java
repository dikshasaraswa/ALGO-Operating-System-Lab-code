import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class BankersAlgorithmWithGanttChart extends JFrame {

    private int[][] max;
    private int[][] allocation;
    private int[][] need;
    private int[] available;
    private int numProcesses, numResources;

    // GUI components
    private JTextArea outputArea;
    private JButton runButton, resetButton;
    private JTextField numProcessField, numResourceField;
    private JPanel inputPanel;
    private JTextField[][] maxFields, allocationFields;
    private JTextField[] availableFields;

    public BankersAlgorithmWithGanttChart() {
        setTitle("Banker's Algorithm GUI with Gantt Chart");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel to input number of processes and resources
        JPanel introPanel = new JPanel(new GridLayout(3, 2));

        JLabel processLabel = new JLabel("Number of Processes: ");
        JLabel resourceLabel = new JLabel("Number of Resources: ");

        numProcessField = new JTextField();
        numResourceField = new JTextField();

        JButton setupButton = new JButton("Setup Inputs");

        introPanel.add(processLabel);
        introPanel.add(numProcessField);
        introPanel.add(resourceLabel);
        introPanel.add(numResourceField);
        introPanel.add(new JLabel()); // Empty label for alignment
        introPanel.add(setupButton);

        add(introPanel, BorderLayout.NORTH);

        // Input panel for max, allocation, and available arrays
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Output area for results
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Button panel for running the algorithm and resetting
        JPanel buttonPanel = new JPanel();
        runButton = new JButton("Run Banker's Algorithm");
        resetButton = new JButton("Reset");
        runButton.setEnabled(false); // Disable until inputs are set up

        buttonPanel.add(runButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set up input fields when user clicks "Setup Inputs"
        setupButton.addActionListener(e -> setupInputFields());

        // Run the Banker's Algorithm when the button is clicked
        runButton.addActionListener(e -> {
            if (parseInput() && validateInput()) {
                calculateNeedMatrix();
                if (isSafeState()) {
                    outputArea.append("The system is in a safe state.\n");
                } else {
                    outputArea.append("The system is NOT in a safe state.\n");
                }
            } else {
                outputArea.append("Invalid input! Please check your values.\n");
            }
        });

        // Reset everything when "Reset" is clicked
        resetButton.addActionListener(e -> resetForm());

        setVisible(true);
    }

    // Set up input fields dynamically based on number of processes and resources
    private void setupInputFields() {
        try {
            numProcesses = Integer.parseInt(numProcessField.getText());
            numResources = Integer.parseInt(numResourceField.getText());
            if (numProcesses <= 0 || numResources <= 0) throw new NumberFormatException();

            inputPanel.removeAll();
            maxFields = new JTextField[numProcesses][numResources];
            allocationFields = new JTextField[numProcesses][numResources];
            availableFields = new JTextField[numResources];

            // Create input fields for max and allocation matrices
            for (int i = 0; i < numProcesses; i++) {
                JPanel processPanel = new JPanel(new GridLayout(1, numResources * 2 + 1));
                processPanel.setBorder(BorderFactory.createTitledBorder("Process " + i));

                // Add max and allocation fields for each process
                for (int j = 0; j < numResources; j++) {
                    maxFields[i][j] = new JTextField(5);
                    allocationFields[i][j] = new JTextField(5);
                    maxFields[i][j].setToolTipText("Maximum demand for Resource " + j);
                    allocationFields[i][j].setToolTipText("Current allocation of Resource " + j);
                    processPanel.add(new JLabel("Max[" + i + "][" + j + "]:"));
                    processPanel.add(maxFields[i][j]);
                    processPanel.add(new JLabel("Alloc[" + i + "][" + j + "]:"));
                    processPanel.add(allocationFields[i][j]);
                }
                inputPanel.add(processPanel);
            }

            // Create input fields for available resources
            JPanel availablePanel = new JPanel(new GridLayout(1, numResources * 2));
            availablePanel.setBorder(BorderFactory.createTitledBorder("Available Resources"));

            for (int i = 0; i < numResources; i++) {
                availableFields[i] = new JTextField(5);
                availableFields[i].setToolTipText("Available instances of Resource " + i);
                availablePanel.add(new JLabel("Resource " + i + ": "));
                availablePanel.add(availableFields[i]);
            }
            inputPanel.add(availablePanel);

            runButton.setEnabled(true); // Enable the "Run" button once fields are set up
            inputPanel.revalidate();
            inputPanel.repaint();
        } catch (NumberFormatException ex) {
            outputArea.append("Please enter valid numbers for processes and resources.\n");
        }
    }

    // Parse input from user for max, allocation, and available arrays
    private boolean parseInput() {
        try {
            max = new int[numProcesses][numResources];
            allocation = new int[numProcesses][numResources];
            available = new int[numResources];

            for (int i = 0; i < numProcesses; i++) {
                for (int j = 0; j < numResources; j++) {
                    max[i][j] = Integer.parseInt(maxFields[i][j].getText());
                    allocation[i][j] = Integer.parseInt(allocationFields[i][j].getText());
                }
            }
            for (int i = 0; i < numResources; i++) {
                available[i] = Integer.parseInt(availableFields[i].getText());
            }
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    // Validate input to ensure allocation doesn't exceed max
    private boolean validateInput() {
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                if (allocation[i][j] > max[i][j]) {
                    outputArea.append("Error: Allocation exceeds maximum for Process " + i + ", Resource " + j + "\n");
                    return false;
                }
            }
        }
        return true;
    }

    // Calculate the Need matrix
    private void calculateNeedMatrix() {
        need = new int[numProcesses][numResources];
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
    }

    // Banker's Algorithm to check if the system is in a safe state
    private boolean isSafeState() {
        boolean[] finish = new boolean[numProcesses];
        int[] work = available.clone();
        int[] safeSeq = new int[numProcesses];
        int count = 0;

        outputArea.append("Initial Work Vector: " + Arrays.toString(work) + "\n");

        while (count < numProcesses) {
            boolean found = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    boolean canProceed = true;
                    for (int j = 0; j < numResources; j++) {
                        if (need[i][j] > work[j]) {
                            canProceed = false;
                            break;
                        }
                    }
                    if (canProceed) {
                        for (int j = 0; j < numResources; j++) {
                            work[j] += allocation[i][j];
                        }
                        safeSeq[count++] = i;
                        finish[i] = true;
                        outputArea.append("Process " + i + " can proceed.\n");
                        outputArea.append("Updated Work Vector: " + Arrays.toString(work) + "\n");
                        found = true;
                    }
                }
            }
            if (!found) {
                outputArea.append("No safe sequence found. System is NOT in a safe state.\n");
                return false;
            }
        }

        outputArea.append("Safe Sequence: " + Arrays.toString(safeSeq) + "\n");
        drawGanttChart(safeSeq);
        showNeedMatrixAndSafeSequenceWindow(safeSeq); // Show need matrix and safe sequence
        return true;
    }

    // Method to display the need matrix and safe sequence in a new window
    private void showNeedMatrixAndSafeSequenceWindow(int[] safeSeq) {
        JFrame needMatrixFrame = new JFrame("Need Matrix and Safe Sequence");
        needMatrixFrame.setSize(500, 400);
        needMatrixFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create Need Matrix Table
        String[] columnNames = new String[numResources + 1];
        columnNames[0] = "Process";
        for (int j = 1; j <= numResources; j++) {
            columnNames[j] = "R" + (j - 1);
        }

        String[][] data = new String[numProcesses][numResources + 1];
        for (int i = 0; i < numProcesses; i++) {
            data[i][0] = "P" + i;
            for (int j = 1; j <= numResources; j++) {
                data[i][j] = String.valueOf(need[i][j - 1]);
            }
        }

        JTable needTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(needTable);

        // Display Safe Sequence
        JPanel sequencePanel = new JPanel();
        sequencePanel.setBorder(BorderFactory.createTitledBorder("Safe Sequence"));
        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < safeSeq.length; i++) {
            sequence.append("P").append(safeSeq[i]);
            if (i != safeSeq.length - 1) {
                sequence.append(" -> ");
            }
        }
        JLabel sequenceLabel = new JLabel(sequence.toString());
        sequenceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sequencePanel.add(sequenceLabel);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(sequencePanel, BorderLayout.SOUTH);

        needMatrixFrame.add(mainPanel);
        needMatrixFrame.setVisible(true);
    }

    // Draw Gantt chart to visualize process execution
    private void drawGanttChart(int[] safeSequence) {
        JFrame ganttFrame = new JFrame("Gantt Chart");
        ganttFrame.setSize(600, 200);
        ganttFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel ganttPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(new Font("Arial", Font.BOLD, 14));

                // Variables for positioning and size
                int boxWidth = 80;
                int boxHeight = 50;
                int padding = 20;
                int startX = 50;
                int startY = 60;

                // Draw each process box in the Gantt chart
                for (int i = 0; i < safeSequence.length; i++) {
                    int process = safeSequence[i];

                    // Draw the rectangle for each process
                    g.drawRect(startX, startY, boxWidth, boxHeight);

                    // Draw the process label (e.g., P0, P1, etc.) centered in the box
                    FontMetrics metrics = g.getFontMetrics();
                    String label = "P" + process;
                    int labelWidth = metrics.stringWidth(label);
                    int labelX = startX + (boxWidth - labelWidth) / 2;
                    int labelY = startY + ((boxHeight - metrics.getHeight()) / 2) + metrics.getAscent();

                    g.drawString(label, labelX, labelY);

                    // Update startX for the next box
                    startX += boxWidth + padding;
                }

                // Add legend
                g.drawString("Safe Sequence (left to right)", 50, 30);
            }
        };

        ganttFrame.add(ganttPanel);
        ganttFrame.setVisible(true);
    }

    // Reset all fields and output area
    private void resetForm() {
        numProcessField.setText("");
        numResourceField.setText("");
        inputPanel.removeAll();
        outputArea.setText("");
        runButton.setEnabled(false);
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankersAlgorithmWithGanttChart::new);
    }
}
