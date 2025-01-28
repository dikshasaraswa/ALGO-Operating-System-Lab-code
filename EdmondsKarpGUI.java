import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;

class Graph {
    private final int vertices;
    private final int[][] capacity;
    private int[][] flow;

    public Graph(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
        this.flow = new int[vertices][vertices];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] = cap;
    }

    public int maxFlow(int source, int sink) {
        int maxFlow = 0;

        while (true) {
            int[] parent = new int[vertices];
            Arrays.fill(parent, -1);
            parent[source] = source;

            Queue<Integer> queue = new LinkedList<>();
            queue.add(source);

            while (!queue.isEmpty() && parent[sink] == -1) {
                int current = queue.poll();

                for (int next = 0; next < vertices; next++) {
                    if (parent[next] == -1 && capacity[current][next] - flow[current][next] > 0) {
                        parent[next] = current;
                        queue.add(next);
                        if (next == sink) break;
                    }
                }
            }

            if (parent[sink] == -1) break;

            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v] - flow[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                flow[u][v] += pathFlow;
                flow[v][u] -= pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    public int[][] getFlow() {
        return flow;
    }
}

class GraphNode {
    int id;
    Point2D.Double position;
    
    public GraphNode(int id, Point2D.Double position) {
        this.id = id;
        this.position = position;
    }
}

class GraphEdge {
    GraphNode from;
    GraphNode to;
    int capacity;
    int flow;
    
    public GraphEdge(GraphNode from, GraphNode to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }
}

class GraphPanel extends JPanel {
    private ArrayList<GraphNode> nodes;
    private ArrayList<GraphEdge> edges;
    private GraphNode selectedNode;
    private GraphNode sourceNode;
    private GraphNode sinkNode;
    private static final int NODE_RADIUS = 20;
    private static final Color NODE_COLOR = new Color(100, 149, 237); // Cornflower blue
    
    public GraphPanel() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        selectedNode = null;
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedNode = findNode(e.getPoint());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedNode = null;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null) {
                    selectedNode.position = new Point2D.Double(e.getX(), e.getY());
                    repaint();
                }
            }
        });
    }

    public ArrayList<GraphEdge> getEdges() {
        return edges;
    }

    public int getNodeCount() {
        return nodes.size();
    }
    
    private GraphNode findNode(Point p) {
        for (GraphNode node : nodes) {
            if (Point2D.distance(node.position.x, node.position.y, p.x, p.y) < NODE_RADIUS) {
                return node;
            }
        }
        return null;
    }
    
    public void addNode(int id) {
        double x = Math.random() * (getWidth() - 2 * NODE_RADIUS) + NODE_RADIUS;
        double y = Math.random() * (getHeight() - 2 * NODE_RADIUS) + NODE_RADIUS;
        GraphNode node = new GraphNode(id, new Point2D.Double(x, y));
        nodes.add(node);
        
        if (nodes.size() == 1) {
            sourceNode = node;
        }
        sinkNode = node;
        
        repaint();
    }
    
    public void addEdge(int fromId, int toId, int capacity) {
        GraphNode from = nodes.stream()
            .filter(n -> n.id == fromId)
            .findFirst()
            .orElse(null);
        GraphNode to = nodes.stream()
            .filter(n -> n.id == toId)
            .findFirst()
            .orElse(null);
            
        if (from != null && to != null) {
            edges.add(new GraphEdge(from, to, capacity));
            repaint();
        }
    }
    
    public void updateFlow(int[][] flow) {
        for (GraphEdge edge : edges) {
            edge.flow = flow[edge.from.id][edge.to.id];
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw edges
        for (GraphEdge edge : edges) {
            g2d.setStroke(new BasicStroke(2));
            Point2D.Double from = edge.from.position;
            Point2D.Double to = edge.to.position;
            
            drawArrow(g2d, from, to);
            
            String flowText = edge.flow + "/" + edge.capacity;
            Point2D.Double mid = new Point2D.Double(
                (from.x + to.x) / 2,
                (from.y + to.y) / 2
            );
            g2d.setColor(Color.BLUE);
            g2d.drawString(flowText, (float)mid.x + 5, (float)mid.y + 5);
        }
        
        // Draw nodes
        for (GraphNode node : nodes) {
            // Fill with the standard node color
            g2d.setColor(NODE_COLOR);
            g2d.fillOval((int)node.position.x - NODE_RADIUS, 
                        (int)node.position.y - NODE_RADIUS,
                        2 * NODE_RADIUS, 
                        2 * NODE_RADIUS);
            
            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.drawOval((int)node.position.x - NODE_RADIUS, 
                        (int)node.position.y - NODE_RADIUS,
                        2 * NODE_RADIUS, 
                        2 * NODE_RADIUS);
            
            // Draw node ID
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(node.id), 
                        (int)node.position.x - 6,
                        (int)node.position.y + 6);
        }
    }
    
    private void drawArrow(Graphics2D g2d, Point2D.Double from, Point2D.Double to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);
        double len = Math.sqrt(dx * dx + dy * dy);
        
        double startX = from.x + NODE_RADIUS * Math.cos(angle);
        double startY = from.y + NODE_RADIUS * Math.sin(angle);
        double endX = from.x + (len - NODE_RADIUS) * Math.cos(angle);
        double endY = from.y + (len - NODE_RADIUS) * Math.sin(angle);
        
        g2d.setColor(Color.BLACK);
        g2d.draw(new Line2D.Double(startX, startY, endX, endY));
        
        double arrowLength = 15;
        double arrowAngle = Math.PI / 6;
        double x1 = endX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = endY - arrowLength * Math.sin(angle - arrowAngle);
        double x2 = endX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = endY - arrowLength * Math.sin(angle + arrowAngle);
        
        g2d.draw(new Line2D.Double(endX, endY, x1, y1));
        g2d.draw(new Line2D.Double(endX, endY, x2, y2));
    }
}

public class EdmondsKarpGUI {
    private JFrame frame;
    private GraphPanel graphPanel;
    private JTextArea outputArea;
    private ArrayList<GraphEdge> edges;
    private static int nodeCount = 0;
    
    public EdmondsKarpGUI() {
        frame = new JFrame("Visual Edmonds-Karp Max Flow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());
        
        // Create main panels
        JPanel controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(300, 0));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        
        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(900, 600));
        
        // Add Node section
        JPanel addNodePanel = new JPanel();
        addNodePanel.setBorder(BorderFactory.createTitledBorder("Add Node"));
        JButton addNodeButton = new JButton("Add Node");
        addNodeButton.addActionListener(e -> {
            graphPanel.addNode(nodeCount++);
            frame.repaint();
        });
        addNodePanel.add(addNodeButton);
        controlPanel.add(addNodePanel);
        
        // Add Edge section
        JPanel addEdgePanel = new JPanel();
        addEdgePanel.setBorder(BorderFactory.createTitledBorder("Add Edge"));
        addEdgePanel.setLayout(new GridLayout(4, 2, 5, 5));
        
        JTextField fromField = new JTextField(5);
        JTextField toField = new JTextField(5);
        JTextField capacityField = new JTextField(5);
        
        addEdgePanel.add(new JLabel("From:"));
        addEdgePanel.add(fromField);
        addEdgePanel.add(new JLabel("To:"));
        addEdgePanel.add(toField);
        addEdgePanel.add(new JLabel("Capacity:"));
        addEdgePanel.add(capacityField);
        
        JButton addEdgeButton = new JButton("Add Edge");
        addEdgeButton.addActionListener(e -> {
            try {
                int from = Integer.parseInt(fromField.getText());
                int to = Integer.parseInt(toField.getText());
                int capacity = Integer.parseInt(capacityField.getText());
                graphPanel.addEdge(from, to, capacity);
                
                fromField.setText("");
                toField.setText("");
                capacityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Please enter valid numbers", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        addEdgePanel.add(addEdgeButton);
        controlPanel.add(addEdgePanel);
        
        // Calculate section
        JPanel calculatePanel = new JPanel();
        calculatePanel.setBorder(BorderFactory.createTitledBorder("Calculate"));
        JButton calculateButton = new JButton("Calculate Max Flow");
        calculateButton.addActionListener(e -> calculateMaxFlow());
        calculatePanel.add(calculateButton);
        controlPanel.add(calculatePanel);
        
        // Output section
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setPreferredSize(new Dimension(300, 200));
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));
        controlPanel.add(outputScroll);
        
        // Add panels to frame
        frame.add(controlPanel, BorderLayout.EAST);
        frame.add(graphPanel, BorderLayout.CENTER);
        
        frame.setVisible(true);
    }
    
    private void calculateMaxFlow() {
        try {
            int n = graphPanel.getNodeCount();
            if (n < 2) {
                JOptionPane.showMessageDialog(frame, 
                    "Please add at least 2 nodes (source and sink)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Graph graph = new Graph(n);
            
            for (GraphEdge edge : graphPanel.getEdges()) {
                graph.addEdge(edge.from.id, edge.to.id, edge.capacity);
            }
            
            int maxFlow = graph.maxFlow(0, n-1);
            int[][] flow = graph.getFlow();
            
            graphPanel.updateFlow(flow);
            
            StringBuilder result = new StringBuilder();
            result.append("Maximum Flow: ").append(maxFlow).append("\n\n");
            result.append("Flow through each edge:\n");
            
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (flow[i][j] > 0) {
                        result.append(i).append(" -> ").append(j)
                              .append(": ").append(flow[i][j]).append("\n");
                    }
                }
            }
            
            outputArea.setText(result.toString());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, 
                "Error calculating max flow: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EdmondsKarpGUI::new);
    }
}