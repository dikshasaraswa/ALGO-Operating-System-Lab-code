import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class DiskSchedulingGUI extends JFrame {
    private JComboBox<String> algorithmComboBox;
    private JTextField requestsTextField;
    private JTextField initialPositionTextField;
    private JTextField directionTextField;
    private JTextArea outputTextArea;
    private VisualizationPanel visualizationPanel;
    private ArrayList<Integer> currentSequence;
    private int diskSize = 200;  // Maximum disk size

    public DiskSchedulingGUI() {
        setTitle("Disk Scheduling Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Create the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create the control panel
        JPanel controlPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Algorithm selection
        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmComboBox = new JComboBox<>(new String[]{"FCFS", "SCAN"});
        controlPanel.add(algorithmLabel);
        controlPanel.add(algorithmComboBox);

        // Requests input
        JLabel requestsLabel = new JLabel("Requests (comma-separated):");
        requestsTextField = new JTextField();
        controlPanel.add(requestsLabel);
        controlPanel.add(requestsTextField);

        // Initial position input
        JLabel initialPositionLabel = new JLabel("Initial Position:");
        initialPositionTextField = new JTextField();
        controlPanel.add(initialPositionLabel);
        controlPanel.add(initialPositionTextField);

        // Direction input
        JLabel directionLabel = new JLabel("Direction (for SCAN - left/right):");
        directionTextField = new JTextField();
        controlPanel.add(directionLabel);
        controlPanel.add(directionTextField);

        // Disk size input
        JLabel diskSizeLabel = new JLabel("Disk Size:");
        JTextField diskSizeTextField = new JTextField(String.valueOf(diskSize));
        controlPanel.add(diskSizeLabel);
        controlPanel.add(diskSizeTextField);

        // Create visualization panel
        visualizationPanel = new VisualizationPanel();
        visualizationPanel.setPreferredSize(new Dimension(800, 400));

        // Output area
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setPreferredSize(new Dimension(800, 150));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diskSize = Integer.parseInt(diskSizeTextField.getText());
                runDiskScheduling();
            }
        });
        buttonPanel.add(runButton);

        // Layout components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(visualizationPanel, BorderLayout.CENTER);
        centerPanel.add(outputScrollPane, BorderLayout.SOUTH);

        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set the main panel as the content pane
        setContentPane(mainPanel);
    }

    private void runDiskScheduling() {
        String algorithm = (String) algorithmComboBox.getSelectedItem();
        String requestsStr = requestsTextField.getText();
        int initialPosition = Integer.parseInt(initialPositionTextField.getText());
        String direction = directionTextField.getText();

        ArrayList<Integer> requests = parseRequests(requestsStr);
        StringBuilder outputBuilder = new StringBuilder();
        currentSequence = new ArrayList<>();
        currentSequence.add(initialPosition);

        int totalMovement;
        if (algorithm.equals("FCFS")) {
            totalMovement = fcfs(requests, initialPosition, outputBuilder);
        } else {
            totalMovement = scan(requests, initialPosition, direction, outputBuilder);
        }

        outputTextArea.setText(outputBuilder.toString() + "\nTotal Head Movement: " + totalMovement);
        visualizationPanel.repaint();
    }

    private int fcfs(ArrayList<Integer> requests, int initialPosition, StringBuilder outputBuilder) {
        int totalMovement = 0;
        outputBuilder.append("FCFS Disk Scheduling:\n");
        outputBuilder.append("Initial Head Position: ").append(initialPosition).append("\n");

        int currentPosition = initialPosition;
        for (int request : requests) {
            outputBuilder.append("Head moves from ").append(currentPosition).append(" to ").append(request).append("\n");
            totalMovement += Math.abs(request - currentPosition);
            currentPosition = request;
            currentSequence.add(request);
        }

        return totalMovement;
    }

    private int scan(ArrayList<Integer> requests, int initialPosition, String direction, StringBuilder outputBuilder) {
        int totalMovement = 0;
        outputBuilder.append("SCAN Disk Scheduling:\n");
        outputBuilder.append("Initial Head Position: ").append(initialPosition).append("\n");
        outputBuilder.append("Direction: ").append(direction).append("\n");

        // Create a copy of requests and add initial position
        ArrayList<Integer> allRequests = new ArrayList<>(requests);
        if (!allRequests.contains(initialPosition)) {
            allRequests.add(initialPosition);
        }

        // Sort all requests
        Collections.sort(allRequests);

        // Find the position where initialPosition would be inserted
        int startIndex = Collections.binarySearch(allRequests, initialPosition);
        if (startIndex < 0) {
            startIndex = -(startIndex + 1);
        }

        ArrayList<Integer> sequence = new ArrayList<>();
        int currentPosition = initialPosition;
        sequence.add(currentPosition);

        if (direction.equalsIgnoreCase("right")) {
            // Move right first
            // Add all requests to the right of initial position
            for (int i = startIndex; i < allRequests.size(); i++) {
                int request = allRequests.get(i);
                if (request != currentPosition) {
                    outputBuilder.append("Head moves from ").append(currentPosition)
                            .append(" to ").append(request).append("\n");
                    totalMovement += Math.abs(request - currentPosition);
                    currentPosition = request;
                    sequence.add(request);
                }
            }
            
            // If we haven't reached the end of disk, go there
            if (currentPosition < diskSize) {
                outputBuilder.append("Head moves from ").append(currentPosition)
                        .append(" to ").append(diskSize).append("\n");
                totalMovement += Math.abs(diskSize - currentPosition);
                currentPosition = diskSize;
                sequence.add(diskSize);
            }

            // Now move left through remaining requests
            for (int i = allRequests.size() - 1; i >= 0; i--) {
                int request = allRequests.get(i);
                if (request < currentPosition) {
                    outputBuilder.append("Head moves from ").append(currentPosition)
                            .append(" to ").append(request).append("\n");
                    totalMovement += Math.abs(currentPosition - request);
                    currentPosition = request;
                    sequence.add(request);
                }
            }
        } else {  // direction is left
            // Move left first
            // Add all requests to the left of initial position
            for (int i = startIndex - 1; i >= 0; i--) {
                int request = allRequests.get(i);
                if (request != currentPosition) {
                    outputBuilder.append("Head moves from ").append(currentPosition)
                            .append(" to ").append(request).append("\n");
                    totalMovement += Math.abs(request - currentPosition);
                    currentPosition = request;
                    sequence.add(request);
                }
            }
            
            // If we haven't reached the beginning of disk, go there
            if (currentPosition > 0) {
                outputBuilder.append("Head moves from ").append(currentPosition)
                        .append(" to 0\n");
                totalMovement += currentPosition;
                currentPosition = 0;
                sequence.add(0);
            }

            // Now move right through remaining requests
            for (int i = 0; i < allRequests.size(); i++) {
                int request = allRequests.get(i);
                if (request > currentPosition) {
                    outputBuilder.append("Head moves from ").append(currentPosition)
                            .append(" to ").append(request).append("\n");
                    totalMovement += Math.abs(request - currentPosition);
                    currentPosition = request;
                    sequence.add(request);
                }
            }
        }

        // Update the currentSequence for visualization
        currentSequence = sequence;
        return totalMovement;
    }
    private ArrayList<Integer> parseRequests(String requestsStr) {
        ArrayList<Integer> requests = new ArrayList<>();
        String[] parts = requestsStr.split(",");
        for (String part : parts) {
            requests.add(Integer.parseInt(part.trim()));
        }
        return requests;
    }

    class VisualizationPanel extends JPanel {
        private static final int PADDING = 50;
        private static final int POINT_SIZE = 8;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (currentSequence == null || currentSequence.isEmpty()) {
                return;
            }

            int width = getWidth() - 2 * PADDING;
            int height = getHeight() - 2 * PADDING;

            // Draw axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(PADDING, height + PADDING, width + PADDING, height + PADDING);  // X-axis
            g2d.drawLine(PADDING, PADDING, PADDING, height + PADDING);  // Y-axis

            // Draw labels
            g2d.drawString("Disk Position", width / 2, height + PADDING + 30);
            g2d.drawString("Time", 10, height / 2);

            // Scale factors
            double xScale = (double) width / diskSize;
            double yScale = (double) height / (currentSequence.size() - 1);

            // Draw points and lines
            g2d.setColor(Color.BLUE);
            for (int i = 0; i < currentSequence.size(); i++) {
                int x = PADDING + (int) (currentSequence.get(i) * xScale);
                int y = PADDING + (int) (i * yScale);

                // Draw point
                g2d.fillOval(x - POINT_SIZE/2, y - POINT_SIZE/2, POINT_SIZE, POINT_SIZE);

                // Draw line to next point
                if (i < currentSequence.size() - 1) {
                    int nextX = PADDING + (int) (currentSequence.get(i + 1) * xScale);
                    int nextY = PADDING + (int) ((i + 1) * yScale);
                    g2d.drawLine(x, y, nextX, nextY);
                }

                // Draw coordinate labels
                g2d.drawString(String.valueOf(currentSequence.get(i)), x - 20, height + PADDING + 15);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DiskSchedulingGUI().setVisible(true);
            }
        });
    }
}