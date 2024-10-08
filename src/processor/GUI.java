package processor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

/**
 * The GUI Class creates a GUI interface for the user to interact with and display information.
 * Allows the user to upload a csv file which will be processed and then display reports based on it.
 * @author Will
 *
 */
public class GUI {

	final static String BUTTONPANEL = "Enter Data";
    final static String REPORTPANEL = "Display Reports";

    /**
     * Constructor and main method that creates and manages the GUI during run time.
     * @param process MainProcessor object that is injected to let the GUI access its functions.
     */
    public GUI(MainProcessor process) {
    	// Frame that wraps the tabbed pane 
    	JFrame frame = new JFrame("Simple Transaction Processor");
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Creates cards/tabs panels which are wrapped by the tabbed pane
        JPanel card1 = new JPanel();
        card1.setLayout(new GridBagLayout());
        card1.setPreferredSize(new Dimension(1100, 700));
        
        JPanel card2 = new JPanel();
        card1.setLayout(new GridBagLayout());
        
        // panel that is wrapped by the card1 panel for formatting
        JPanel card1DisplayLabel = new JPanel();
        card1DisplayLabel.setLayout(new BoxLayout(card1DisplayLabel, BoxLayout.PAGE_AXIS));
        
        // Constraints variable for formatting the panels
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 1;
        gbc.gridy = 0;
        gbc.gridx = GridBagConstraints.RELATIVE;
        
        card1.add(card1DisplayLabel, gbc);
        
        // Create label to ask the user to enter a csv file and add to panel for formatting
        JLabel label = new JLabel("Please select a csv file name with transactions");
        label.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        card1DisplayLabel.add(label);
      	
        JPanel card1DisplayButton = new JPanel();
        card1DisplayButton.setLayout(new BoxLayout(card1DisplayButton, BoxLayout.PAGE_AXIS));
        
        // Create buttons for letting the user add CSV files and clear any held data
        JButton button1 = new JButton("Select File");
        JButton button2 = new JButton("Clear Data");
   
        button1.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        button2.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        
        // Add buttons to the panel
        card1DisplayButton.add(button1);
        card1DisplayButton.add(button2);

        // Change the grid Y and weight to format the buttons.
        gbc.gridy = 1;
        gbc.weighty = 30;
        card1.add(card1DisplayButton, gbc);
        
        // Add Action Listener which will get the text for the file
        button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// File select pop up for the user to select csv file
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
				int r = fileChooser.showOpenDialog(null);
				String result = "";
				
				// If the user selects a file then call the processor and display results
				if (r == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					try {
						result = process.readFile(selectedFile);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				// Add results as label
				JLabel resultLabel = new JLabel(result);
				resultLabel.setBounds(10, 130, 300, 25);
				card1DisplayButton.add(resultLabel);
				
				// Repaint the panel to display label
				card1DisplayLabel.revalidate();
				card1DisplayLabel.repaint();
				
				// Change gbc constraints for card2
				gbc.anchor = GridBagConstraints.CENTER;
				gbc.gridy = 1;
				
				// Create a textarea for the first report
				JTextArea chartArea = new JTextArea(40,30);
				chartArea.setEditable(false);
				chartArea.setLineWrap(true);
				chartArea.setWrapStyleWord(true);
				
				// Add a scrollbar to the textarea
				JScrollPane chartScroll = new JScrollPane(chartArea);
				chartScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				
				// Get the report and format it for the textarea
				String chartText = "";
				HashMap<String, ArrayList<String>> chartMap = process.getChartedAccounts();
				HashMap<String, ArrayList<String>> displayMap = new HashMap<>();
				
				for(String entry : chartMap.keySet()) {
					chartText += "";
				
					for(String name : chartMap.get(entry).subList(1, chartMap.get(entry).size())) {
						// Temporary list for adding new data
						ArrayList<String> tempList = new ArrayList<String>();
						
						// If the HashMap doesn't already have the account name add it and the relevant information
						if(!displayMap.containsKey(name)) {
							tempList.add(entry); 						// Add card number
							tempList.add(chartMap.get(entry).get(0)); 	// Add balance
							displayMap.put(name, tempList); 			// Add all the information using the account name as key
						}
						else { // If it already has the account name, add the card number to the account and its balance
							tempList = displayMap.get(name); 			// Get account information
							tempList.add(entry);						// Add card number
							tempList.add(chartMap.get(entry).get(0));	// Add balance
							displayMap.put(name, tempList);
							
						}
					}
				}
				
				// Add report to the text for the textarea
				for(String entry : displayMap.keySet()) {
					chartText += "Account Name: " + entry;
					for(int i = 0; i < displayMap.get(entry).size(); i+=2){
						chartText += "\nCard Number and Balance: " + displayMap.get(entry).get(i) + "; " + displayMap.get(entry).get(i+1);
					}
					chartText += "\n\n";
				}
				chartArea.setText(chartText);
				chartArea.setCaretPosition(0); // Resets textarea to the top of the scrollbar
				
				// Create textarea for the second report
				JTextArea collectArea = new JTextArea(40,30);
				collectArea.setEditable(false);
				collectArea.setLineWrap(true);
				collectArea.setWrapStyleWord(true);
				
				// Add scrollbar to the second report
				JScrollPane collectScroll = new JScrollPane(collectArea);
				collectScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				
				// Get second report and format it before adding to the textarea
				String collectText = "";
				HashMap<String, ArrayList<String>> collectMap = process.getAccountsForCollections();
				for(String entry : collectMap.keySet()) {
					collectText += "Card Number: " + entry + "\nBalance: " + collectMap.get(entry).get(0) + "\nNames on Account: " +
							collectMap.get(entry).subList(1, collectMap.get(entry).size()) + " " + "\n\n";
				}
				collectArea.setText(collectText);
				collectArea.setCaretPosition(0); // Resets textarea to the top of the scrollbar
				
				// Create textarea for the third report
				JTextArea failedArea = new JTextArea(40,30);
				failedArea.setEditable(false);
				failedArea.setLineWrap(true);
				failedArea.setWrapStyleWord(true);
				
				// Add scrollbar to the third report
				JScrollPane failedScroll = new JScrollPane(failedArea);
				failedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

				// Get third report and format it before adding to the textarea
				String failedText = "";
				for(List<String> fails : process.failedTransactions()) {
					failedText += String.join(" ", fails) + "\n";
				}
				failedArea.setText(failedText);
				failedArea.setCaretPosition(0);// Resets textarea to the top of the scrollbar
				
				// Remove all displayed panels before populating the tab again
		        card2.removeAll();
		        card2.add(chartScroll, gbc);
		        card2.add(collectScroll, gbc);
		        card2.add(failedScroll, gbc);
		        card2.revalidate();
		        card2.repaint();
			}
        });
        
        // Listener function for deleting all stored data
        button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				process.deleteData();
			}
        });

        
        // Add tabs (panels) to the tabbedpane before adding the tabbedpane to the frame and displaying
        tabbedPane.addTab(BUTTONPANEL, card1);
        tabbedPane.addTab(REPORTPANEL, card2);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null); // Sets the frame to the center of the screen
        frame.setVisible(true);

    }
    

}