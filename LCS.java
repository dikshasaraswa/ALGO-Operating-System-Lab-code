import javax.swing.* ;
import java.awt.* ;

public class LCS{

    private JFrame frame ;
    private JPanel tablePanel ;
    private JTextArea resultArea ;
    private JTextField textField1 ;
    private JTextField textField2 ;

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new LCS().createAndShowGUI()) ;
    }

    //GUI
    private void createAndShowGUI(){
        frame = new JFrame("Longest Common Subsequence (LCS) Calculator") ;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
        frame.setLayout(new BorderLayout()) ;

        //input panel
        JPanel inputPanel = new JPanel(new FlowLayout()) ;
        inputPanel.add(new JLabel("String 1:")) ;
        textField1 = new JTextField(10) ;
        inputPanel.add(textField1) ;
        inputPanel.add(new JLabel("String 2:")) ;
        textField2 = new JTextField(10) ;
        inputPanel.add(textField2) ;
        JButton computeButton = new JButton("Compute LCS") ;
        inputPanel.add(computeButton) ;

        //result
        resultArea = new JTextArea(3, 40) ;
        resultArea.setEditable(false) ;
        resultArea.setBorder(BorderFactory.createLineBorder(Color.BLACK)) ;

        //table
        tablePanel = new JPanel() ;
        JScrollPane tableScrollPane = new JScrollPane(tablePanel) ;
        tableScrollPane.setPreferredSize(new Dimension(600, 400)) ;


        frame.add(inputPanel, BorderLayout.NORTH) ;
        frame.add(tableScrollPane, BorderLayout.CENTER) ;
        frame.add(resultArea, BorderLayout.SOUTH) ;


        computeButton.addActionListener(e -> {
            String str1 = textField1.getText() ;
            String str2 = textField2.getText() ;
            computeLCS(str1, str2) ;
        });

        frame.pack() ;
        frame.setVisible(true) ;
    }

    //lcs
    private void computeLCS(String str1 , String str2){
        int m = str1.length() ;
        int n = str2.length() ;
        int[][] dp = new int[m + 1][n + 1] ;
        JLabel[][] tableLabels = new JLabel[m + 1][n + 1] ;
 

        tablePanel.removeAll() ;
        tablePanel.setLayout(new GridLayout(m + 2 , n + 2)) ; 

        tablePanel.add(new JLabel("")) ; // top left

        for (int j = 0 ; j <= n ; j++){
            if (j > 0){
                tablePanel.add(new JLabel(String.valueOf(str2.charAt(j - 1)), SwingConstants.CENTER));
            }
            else{
                tablePanel.add(new JLabel("")) ; 
            }
        }


        for (int i = 0 ; i <= m ; i++){
            if (i > 0){
                tablePanel.add(new JLabel(String.valueOf(str1.charAt(i - 1)) , SwingConstants.CENTER)) ;
            }
            else{
                tablePanel.add(new JLabel("")) ; // empty 
            }

            for (int j = 0 ; j <= n ; j++){
                dp[i][j] = 0 ; //initialize
                JLabel label = new JLabel("0", SwingConstants.CENTER) ;
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK)) ;
                tablePanel.add(label) ;
                tableLabels[i][j] = label ; 
            }
        }


        for(int i = 1 ; i <= m ; i++){
            for(int j = 1; j <= n; j++){
                if(str1.charAt(i - 1) == str2.charAt(j - 1)){
                    dp[i][j] = dp[i - 1][j - 1] + 1 ;
                }else{
                    dp[i][j] = Math.max(dp[i - 1][j] , dp[i][j - 1]) ;
                }


                tableLabels[i][j].setText(String.valueOf(dp[i][j])) ;


                if(dp[i][j] > dp[i - 1][j] && dp[i][j] > dp[i][j - 1]){
                    tableLabels[i][j].setBackground(Color.GREEN) ;
                    tableLabels[i][j].setOpaque(true) ;
                }
            }
        }


        tablePanel.revalidate() ;
        tablePanel.repaint() ;

        //backtracking
        StringBuilder lcs = new StringBuilder() ;
        int i = m , j = n ;
        while(i > 0 && j > 0){
            if(str1.charAt(i - 1) == str2.charAt(j - 1)){
                lcs.insert(0, str1.charAt(i - 1)) ;
                i-- ;
                j-- ;
            } 
            else if(dp[i - 1][j] > dp[i][j - 1]){
                i-- ;
            }else{
                j-- ;
            }
        }

        resultArea.setText("LCS: " + lcs.toString()) ;
    }
}
