import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LCSWithGUI extends JFrame implements ActionListener {
    private JTextField sequence1Input, sequence2Input;
    private JTextArea outputArea;
    private JButton computeButton;

    public LCSWithGUI() {
        // Setup GUI
        setTitle("Longest Common Subsequence (LCS) Calculator");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        inputPanel.add(new JLabel("Sequence 1:"));
        sequence1Input = new JTextField();
        inputPanel.add(sequence1Input);

        inputPanel.add(new JLabel("Sequence 2:"));
        sequence2Input = new JTextField();
        inputPanel.add(sequence2Input);

        computeButton = new JButton("Compute LCS");
        computeButton.addActionListener(this);
        inputPanel.add(computeButton);

        add(inputPanel, BorderLayout.NORTH);

        // Output area for showing the results
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == computeButton) {
            String sequence1 = sequence1Input.getText().trim();
            String sequence2 = sequence2Input.getText().trim();
            if (sequence1.isEmpty() || sequence2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both sequences.");
                return;
            }
            findLCS(sequence1, sequence2);
        }
    }

    // Function to find the Longest Common Subsequence
    private void findLCS(String sequence1, String sequence2) {
        int m = sequence1.length();
        int n = sequence2.length();
        int[][] lcsMatrix = new int[m + 1][n + 1];

        // Fill the LCS matrix
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    lcsMatrix[i][j] = 0;
                } else if (sequence1.charAt(i - 1) == sequence2.charAt(j - 1)) {
                    lcsMatrix[i][j] = lcsMatrix[i - 1][j - 1] + 1;
                } else {
                    lcsMatrix[i][j] = Math.max(lcsMatrix[i - 1][j], lcsMatrix[i][j - 1]);
                }
            }
        }

        // Display intermediate matrix
        displayMatrix(lcsMatrix, sequence1, sequence2);

        // Get the LCS string
        String lcs = getLCSString(lcsMatrix, sequence1, sequence2);

        // Show the result in the output area
        outputArea.append("\nLongest Common Subsequence: " + lcs + "\n");
    }

    // Function to get the LCS string from the matrix
    private String getLCSString(int[][] lcsMatrix, String sequence1, String sequence2) {
        StringBuilder lcs = new StringBuilder();
        int i = sequence1.length();
        int j = sequence2.length();

        while (i > 0 && j > 0) {
            if (sequence1.charAt(i - 1) == sequence2.charAt(j - 1)) {
                lcs.append(sequence1.charAt(i - 1));
                i--;
                j--;
            } else if (lcsMatrix[i - 1][j] > lcsMatrix[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return lcs.reverse().toString();
    }

    // Function to display the LCS matrix in the output area
    private void displayMatrix(int[][] lcsMatrix, String sequence1, String sequence2) {
        outputArea.setText("LCS Matrix:\n\n");
        outputArea.append("    ");
        for (char ch : sequence2.toCharArray()) {
            outputArea.append(ch + " ");
        }
        outputArea.append("\n");

        for (int i = 0; i <= sequence1.length(); i++) {
            if (i > 0) {
                outputArea.append(sequence1.charAt(i - 1) + " ");
            } else {
                outputArea.append("  ");
            }
            for (int j = 0; j <= sequence2.length(); j++) {
                outputArea.append(lcsMatrix[i][j] + " ");
            }
            outputArea.append("\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LCSWithGUI gui = new LCSWithGUI();
            gui.setVisible(true);
        });
    }
}