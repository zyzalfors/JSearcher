package fileSearcher;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileSearcher {
	
	static BigInteger nDirs, nFiles;
	
	static String filterRegex(String regex) {
	 try {
	  Pattern.compile(regex);
	 } 
	 catch (Exception e) {
	  return ".+";
	 }
	 return regex; 
	}
	
	static BigDecimal filterDecimal(String decimal, BigDecimal value) {
	 return decimal.matches("\\d+\\.?\\d*") ? new BigDecimal(decimal) : value; 
	}
	
	static boolean addToReport(File element, String regex, BigDecimal fileSize, BigDecimal minSize, BigDecimal maxSize) {
	 if(element.isDirectory()) {
	  return element.getName().matches(regex);
	 }	
	 if(element.isFile()) {
          return element.getName().matches(regex) && (fileSize.compareTo(minSize) == 1 || fileSize.compareTo(minSize) == 0) && (fileSize.compareTo(maxSize) == -1 || fileSize.compareTo(maxSize) == 0);
	 }
	 return false;
	}
	
	public static void search(String regex, String dirPath, StringBuilder reportString, DateFormat dateFormat, String minSize, String maxSize, boolean searchFiles, boolean searchDirs, boolean scanSubdirs) {
	 File[] elements = new File(dirPath).listFiles();
	 for(File element : elements) {
	  String lastModifiedTime = dateFormat.format(element.lastModified());
	  lastModifiedTime = lastModifiedTime.matches(".+1970.+") ? "undefined" : lastModifiedTime;
	  if(element.isDirectory()) {
	   if(searchDirs) {
	    if(addToReport(element, regex, null, null, null)) {
	     nDirs = nDirs.add(new BigInteger("1"));
	     reportString.append(nDirs + " | Directory | " + lastModifiedTime + " | | " + element.getParentFile() + " | " + element.getName() + System.getProperty("line.separator"));
            }
	   }
	   if(scanSubdirs) {
	    try {
	     search(regex, element.getAbsolutePath(), reportString, dateFormat, minSize, maxSize, searchFiles, searchDirs, scanSubdirs);
	    }
	    catch(Exception e){ }
	   }
	  }
	  if(element.isFile() && searchFiles) {
	   BigDecimal fileSize = new BigDecimal(element.length());
	   if(addToReport(element, regex, fileSize, filterDecimal(minSize,fileSize), filterDecimal(maxSize,fileSize))) {
	    nFiles = nFiles.add(new BigInteger("1"));
	    reportString.append(nFiles + " | File | " + lastModifiedTime + " | " + fileSize + " | " + element.getParentFile() + " | " + element.getName() + System.getProperty("line.separator"));
	   }
	  }
	 }
	}
	
        public static void main(String[] args) {
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         String[] columnNames = {"", "Type", "Last modified", "Size (byte)", "Parent directory", "Name"};
         DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
         JTable reportTable = new JTable(tableModel);
         JFrame mainFrame = new JFrame("JSearcher 1.0.0");
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         JTextField dirPathText = new JTextField(40);
         JTextField regexText = new JTextField(40);
         JTextField minSizeText = new JTextField(40);
         JTextField maxSizeText = new JTextField(40);
         JCheckBox searchFilesCheck = new JCheckBox("Search files");
         JCheckBox searchDirsCheck = new JCheckBox("Search directories");
         JCheckBox scanSubdirsCheck = new JCheckBox("Scan subdirectories");
         JLabel dirPathLabel = new JLabel("Directory path:");
         JLabel regexLabel = new JLabel("Regular expression:");
         JLabel minSizeLabel = new JLabel("Mimimun file size limit (byte):");
         JLabel maxSizeLabel = new JLabel("Maximum file size limit (byte):");
         JLabel emptyLabel1 = new JLabel("");
         JLabel emptyLabel2 = new JLabel("");
         JLabel emptyLabel3 = new JLabel("");
         JButton selectDirButton = new JButton("Select directory");
         JButton startButton = new JButton("Start");
         JButton aboutButton = new JButton("About");
         JButton resetButton = new JButton("Reset");
     
         aboutButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(null, "Author: Al Armato\nVersion: 1.0.0\n\n" + 
                "This program is free software: you can redistribute it and/or modify\n" + 
       	        "it under the terms of the GNU General Public License as published by\n" + 
       	        "the Free Software Foundation, either version 3 of the License, or\n" + 
       	        "(at your option) any later version.\n\n" + 
       	        "This program is distributed in the hope that it will be useful,\n" + 
       	        "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" + 
       	        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n" + 
       	        "GNU General Public License for more details.\n\n" + 
       	        "Visit https://www.gnu.org/licenses.", "About JSearcher", JOptionPane.INFORMATION_MESSAGE);   		
          }
	 });
     
         resetButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
           dirPathText.setText("");
           regexText.setText("");
           minSizeText.setText("");
           maxSizeText.setText("");
           searchFilesCheck.setSelected(false);
           searchDirsCheck.setSelected(false);
           scanSubdirsCheck.setSelected(false);
          }
         });
     
         selectDirButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
           JFileChooser dirChooser = new JFileChooser();
           dirChooser.setDialogTitle("Select directory");
           dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
           if(dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    	    dirPathText.setText(dirChooser.getSelectedFile().getAbsolutePath());
           }    		
          }
         });
     
         startButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
           String regex = filterRegex(regexText.getText());
           String dirPath = dirPathText.getText().trim();
           String minSize = minSizeText.getText().trim();
           String maxSize = maxSizeText.getText().trim();
           boolean searchFiles = searchFilesCheck.isSelected();
           boolean searchDirs = searchDirsCheck.isSelected();
           boolean scanSubdirs = scanSubdirsCheck.isSelected();
	   if(!new File(dirPath).exists()) {
	    JOptionPane.showMessageDialog(null, "Directory not found\nSelect a valid directory", "Error", JOptionPane.ERROR_MESSAGE);
	   }
	   else {
	    StringBuilder reportString = new StringBuilder();
	    nDirs = nFiles = new BigInteger("0");
	    tableModel.setRowCount(0);
	    search(regex, dirPath, reportString, dateFormat, minSize, maxSize, searchFiles, searchDirs, scanSubdirs);	
	    String[] stringRows = reportString.toString().split("\\n");
	    for(String stringRow : stringRows) {
	     String[] row = stringRow.split("\\|");
	     tableModel.addRow(row);
	    }
	    JFrame reportFrame = new JFrame ("JSearcher 1.0.0 Report");
	    JMenuBar menuBar = new JMenuBar();
	    JMenu menu = new JMenu("File");
	    JMenuItem saveItem = new JMenuItem("Save as");
	    
            saveItem.addActionListener(new ActionListener() {
	     public void actionPerformed(ActionEvent e) {
	      JFileChooser saveChooser = new JFileChooser();
	      saveChooser.setDialogTitle("Save as file");
	      if(saveChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
	       String filePath = saveChooser.getSelectedFile().getAbsolutePath();
	       try {
	        FileWriter writeFile = new FileWriter(filePath);
	        writeFile.write("Time: " + dateFormat.format(new Date()) + System.getProperty("line.separator"));
		writeFile.write("Directory: " + dirPath + System.getProperty("line.separator"));
		writeFile.write("Search files: " + searchFiles + System.getProperty("line.separator"));
		writeFile.write("Search directories: " + searchDirs + System.getProperty("line.separator"));
  		writeFile.write("Scan subdirectories: " + scanSubdirs + System.getProperty("line.separator"));
		writeFile.write("Minimum size: " + filterDecimal(minSize,null) + " byte " + System.getProperty("line.separator"));
		writeFile.write("Maximum size: " + filterDecimal(maxSize,null) + " byte " + System.getProperty("line.separator"));
		writeFile.write("  | Type | Last modified | Size (byte) | Parent directory | Name" + System.getProperty("line.separator"));
		writeFile.write(reportString.toString());
  		writeFile.close();
	       }
	       catch (Exception ex) {
	    	JOptionPane.showMessageDialog(null, "File not created", "Error", JOptionPane.ERROR_MESSAGE);
	       }
	      }
	     }
	    });
	    
	    reportTable.setModel(tableModel);
	    reportTable.setEnabled(false);
	    menu.add(saveItem);
	    menuBar.add(menu);
	    reportFrame.setJMenuBar(menuBar);  
	    reportFrame.getContentPane().add(new JScrollPane(reportTable));
	    reportFrame.setSize(1000,500);
	    reportFrame.setVisible(true);     
	   }   
          }
         });
		
         mainFrame.setLayout(new GridLayout(6,3));
         mainFrame.add(dirPathLabel);
         mainFrame.add(dirPathText);
         mainFrame.add(selectDirButton);
         mainFrame.add(regexLabel);
         mainFrame.add(regexText);
         mainFrame.add(emptyLabel1);
         mainFrame.add(minSizeLabel);
         mainFrame.add(minSizeText);
         mainFrame.add(emptyLabel2);
         mainFrame.add(maxSizeLabel);
         mainFrame.add(maxSizeText);
         mainFrame.add(emptyLabel3);
         mainFrame.add(searchFilesCheck);
         mainFrame.add(searchDirsCheck);
         mainFrame.add(scanSubdirsCheck);
         mainFrame.add(startButton);
         mainFrame.add(resetButton);
         mainFrame.add(aboutButton);
         mainFrame.setSize(800,450);
         mainFrame.setLocationRelativeTo(null);
         mainFrame.setResizable(true);
   	 mainFrame.setVisible(true);  	 
        }
	
}
