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

public class FileSearcher
{
	static BigInteger nDirs, nFiles;
	
	public static boolean regexIsValid(String regex)
	{
	 try 
	 {
	  Pattern.compile(regex);
	 } 
	 catch (Exception e) 
	 {
	  return false;
	 }
	 return true; 
	}
	
	public static boolean isPositiveFloat(String entry)
	{
	 return	entry.matches("\\d+\\.?\\d*");
	}
	
	public static boolean isPositiveInteger(String entry)
	{
	 return	entry.matches("\\d+");
	}
	
	public static boolean elementExists(String path, boolean isDir, boolean isFile)
	{
	 File element = new File(path);
	 if (isDir && (!element.exists() || !element.isDirectory()))
	 {
	  return false;
	 }
	 if (isFile && (!element.exists() || !element.isFile()))
	 {
	  return false;
	 }
	 return true;
	}
	
	public static boolean addToReport(File element, String regex, BigDecimal minSize, BigDecimal maxSize, 
			           BigInteger minNameLength, BigInteger maxNameLength, boolean searchByName, boolean searchBySize, boolean searchByNameLength)
	{
	 if(element.isDirectory())
	 {
	  if(searchByName && !element.getName().matches(regex))
	  {
	   return false;
	  }  
	 }
	 if(element.isFile())
	 {
      BigDecimal fileSize = new BigDecimal ((double) element.length()/1000000);
	  if(searchByName && !element.getName().matches(regex))
	  {
	   return false;
	  }
	  if(searchBySize && (fileSize.compareTo(minSize) != 1 || fileSize.compareTo(maxSize) != -1))
	  {
       return false;
	  }
	 }
	 BigInteger nameLength = BigInteger.valueOf(element.getName().length());
	 if(searchByNameLength && (nameLength.compareTo(minNameLength) != 1 || nameLength.compareTo(maxNameLength) != -1))
	 {
	  return false;
	 }
	 return true;	
	}
	
	public static void search(String regex, String targetDirPath, StringBuilder reportString, DateFormat dateFormat, BigDecimal minSize, BigDecimal maxSize, BigInteger minNameLength,
			BigInteger maxNameLength, boolean searchFiles, boolean searchDirs, boolean scanSubdirs, boolean searchByName, boolean searchBySize, boolean searchByNameLength) 
    {
	 File[] elements = (new File(targetDirPath)).listFiles();
	 for (File element : elements)
	 {
	  String lastModifiedTime = dateFormat.format(element.lastModified());
	  if (lastModifiedTime.matches(".+1970.+")) 
	  {
	   lastModifiedTime = "";
	  }
	  if (element.isDirectory())
	  {
	   if (searchDirs) 
	   {
		if(addToReport(element, regex, minSize, maxSize, minNameLength, maxNameLength, searchByName, searchBySize, searchByNameLength))
		{
		 nDirs = nDirs.add(new BigInteger("1"));
		 reportString.append(nDirs + " | Directory | " + lastModifiedTime + " | | " + element.getParentFile() + " | " + element.getName() + System.getProperty("line.separator"));
        }
	   }
	   if (scanSubdirs)
	   {
		try 
		{
		 search(regex, element.getAbsolutePath(), reportString, dateFormat, minSize, maxSize, minNameLength, maxNameLength, searchFiles, searchDirs, scanSubdirs, searchByName, searchBySize, searchByNameLength);
		}
		catch(Exception e){ }
	   }
	  }
	  if (element.isFile()) 
	  {
	   if(searchFiles)
	   {
		if(addToReport(element, regex, minSize, maxSize, minNameLength, maxNameLength, searchByName, searchBySize, searchByNameLength))
		{
		 nFiles = nFiles.add(new BigInteger("1"));
		 BigDecimal fileSize = new BigDecimal ((double) element.length()/1000000).setScale(4, BigDecimal.ROUND_HALF_UP);
		 reportString.append(nFiles + " | File | " + lastModifiedTime + " | " + fileSize + " | " + element.getParentFile() + " | " + element.getName() + System.getProperty("line.separator"));
		}
	   }
	  }
	 }
	}
	
    public static void main(String[] args)
    {
     DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
     String info = "<html>User: " + System.getProperty("user.name") + "<br/>"
    		         + "Operating system: " + System.getProperty("os.name") + "<br/>"
    		         + "Architecture: " + System.getProperty("os.arch") + "<br/>";
     String[] columnNames={"","Type","Last modified","Size (MB)","Parent directory","Name"};
     DefaultTableModel tableModel=new DefaultTableModel(columnNames,0);
     JTable reportTable = new JTable(tableModel);
     JFrame mainFrame = new JFrame("JSearcher v1.0");
     mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     JTextField enterTargetDirPathText = new JTextField(40);
     JTextField enterRegexText = new JTextField(40);
     JTextField enterMinSizeText = new JTextField(40);
     JTextField enterMaxSizeText = new JTextField(40);
     JTextField enterMinNameLengthText = new JTextField(40);
     JTextField enterMaxNameLengthText = new JTextField(40);
     JCheckBox searchFilesCheck = new JCheckBox("Search files");
     JCheckBox searchDirsCheck = new JCheckBox("Search directories");
     JCheckBox scanSubdirsCheck = new JCheckBox("Scan subdirectories");
     JCheckBox searchByNameCheck = new JCheckBox("Search by name");
     JCheckBox searchBySizeCheck = new JCheckBox("Search by size (only files)");
     JCheckBox searchByNameLengthCheck = new JCheckBox("Search by name length");
     JLabel enterTargetDirPathLabel = new JLabel("Enter target directory path:");
     JLabel enterRegexLabel = new JLabel("Enter regular expression:");
     JLabel enterMinSizeLabel = new JLabel("Enter minimum file size limit:");
     JLabel enterMaxSizeLabel = new JLabel("Enter maximum file size limit:");
     JLabel enterMinNameLengthLabel = new JLabel("Enter minimum name length:");
     JLabel enterMaxNameLengthLabel = new JLabel("Enter maximum name length:");
     JLabel infoLabel = new JLabel(info);
     JLabel emptyLabel1 = new JLabel("");
     JLabel emptyLabel2 = new JLabel("");
     JLabel emptyLabel3 = new JLabel("");
     JLabel emptyLabel4 = new JLabel("MB");
     JLabel emptyLabel5 = new JLabel("MB");
     JLabel emptyLabel6 = new JLabel("");
     JLabel emptyLabel7 = new JLabel("");
     JButton about = new JButton("About");
     about.addActionListener(new ActionListener() 
     {
      public void actionPerformed(ActionEvent e)
      {
       JOptionPane.showMessageDialog(null, "Author: Al Armato\n\nGNU General Public License:\nThis program is free software: you can redistribute it and/or modify\n" + 
       		"it under the terms of the GNU General Public License as published by\n" + 
       		"the Free Software Foundation, either version 3 of the License, or\n" + 
       		"(at your option) any later version.\n\n" + 
       		"This program is distributed in the hope that it will be useful,\n" + 
       		"but WITHOUT ANY WARRANTY; without even the implied warranty of\n" + 
       		"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" + 
       		"GNU General Public License for more details.\n\n" + 
       		"You should have received a copy of the GNU General Public License\r\n" + 
       		"along with this program.  If not, see https://www.gnu.org/licenses/.", "About JSearcher v1.0", JOptionPane.INFORMATION_MESSAGE);   		
      }
     });
     JButton clearFields = new JButton("Clear fields");
     clearFields.addActionListener(new ActionListener() 
     {
      public void actionPerformed(ActionEvent e)
      {
       enterTargetDirPathText.setText("");
       enterRegexText.setText("");
       enterMinSizeText.setText("");
       enterMaxSizeText.setText("");
       enterMinNameLengthText.setText("");
       enterMaxNameLengthText.setText("");
      }
     });
     JButton selectTargetDir = new JButton("Select target directory");
     selectTargetDir.addActionListener(new ActionListener() 
     {
      public void actionPerformed(ActionEvent e)
      {
       JFileChooser chooseTargetDir = new JFileChooser();
       chooseTargetDir.setDialogTitle("Select target directory");
       chooseTargetDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
       if (chooseTargetDir.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
       {
    	enterTargetDirPathText.setText(chooseTargetDir.getSelectedFile().getAbsolutePath());
       }    		
      }
     });
     JButton startResearch = new JButton("Start research");
     startResearch.addActionListener(new ActionListener() 
     {
      public void actionPerformed(ActionEvent e)
      {
       String regex = enterRegexText.getText(), targetDirPath = enterTargetDirPathText.getText().trim();
       String minSizeText = enterMinSizeText.getText().trim(), maxSizeText = enterMaxSizeText.getText().trim();
       String minNameLengthText = enterMinNameLengthText.getText().trim(), maxNameLengthText = enterMaxNameLengthText.getText().trim();
       BigDecimal minSize = null, maxSize = null;
	   BigInteger minNameLength = null, maxNameLength = null;
       boolean start = true;
       boolean searchFiles = searchFilesCheck.isSelected();
       boolean searchDirs = searchDirsCheck.isSelected();
       boolean scanSubdirs = scanSubdirsCheck.isSelected();
       boolean searchByName = searchByNameCheck.isSelected();
       boolean searchBySize = searchBySizeCheck.isSelected();
       boolean searchByNameLength = searchByNameLengthCheck.isSelected();
	   if(!elementExists(targetDirPath,true,false)) 
	   {
	    start = false;
	    JOptionPane.showMessageDialog(null, "Target directory not found\nSelect a valid target directory", "Error", JOptionPane.ERROR_MESSAGE);
	   }
	   else 
	   {
		if(searchByName && !regexIsValid(regex)) 
		{
		 start = false;
		 JOptionPane.showMessageDialog(null, "Invalid regular expression\nEnter a valid regular expression", "Error", JOptionPane.ERROR_MESSAGE);
		}
		if(searchBySize && (!isPositiveFloat(minSizeText) || !isPositiveFloat(maxSizeText)))
		{
		 start = false;
		 JOptionPane.showMessageDialog(null, "Invalid file sizes\nEnter valid file sizes", "Error", JOptionPane.ERROR_MESSAGE);
		}
		if(searchBySize && isPositiveFloat(minSizeText) && isPositiveFloat(maxSizeText))
		{
		 if((new BigDecimal(minSizeText)).compareTo(new BigDecimal(maxSizeText)) == 1)
		 {	
		  start = false;
		  JOptionPane.showMessageDialog(null, "Maximum file size is less than minimum file size\nEnter valid file sizes", "Error", JOptionPane.ERROR_MESSAGE);
		 }
		}
		if(searchByNameLength && (!isPositiveInteger(minNameLengthText) || !isPositiveInteger(maxNameLengthText)))
		{
		 start = false;
		 JOptionPane.showMessageDialog(null, "Invalid name lengths\nEnter valid name lengths", "Error", JOptionPane.ERROR_MESSAGE);	
		}
		if(searchByNameLength && isPositiveInteger(minNameLengthText) && isPositiveInteger(maxNameLengthText))
		{
		 if((new BigInteger(minNameLengthText)).compareTo(new BigInteger(maxNameLengthText)) == 1)
		 {	 
		  start = false;
		  JOptionPane.showMessageDialog(null, "Maximum name length is less than minimum name length\nEnter valid name lengths", "Error", JOptionPane.ERROR_MESSAGE);
		 }
		}
	   }
	   if(start)
	   {	
		if(searchBySize)
		{
		 minSize = new BigDecimal(minSizeText);
		 maxSize = new BigDecimal(maxSizeText);
		}
		if(searchByNameLength)
		{
		 minNameLength = new BigInteger(minNameLengthText);
		 maxNameLength = new BigInteger(maxNameLengthText);
		}
		StringBuilder reportString = new StringBuilder();
		nDirs = nFiles = new BigInteger("0");
		tableModel.setRowCount(0);
		search(regex, targetDirPath, reportString, dateFormat, minSize, maxSize, minNameLength, maxNameLength, searchFiles, searchDirs, scanSubdirs, searchByName, searchBySize, searchByNameLength);	
		String[] stringRows = reportString.toString().split("\\n");
		for(String stringRow : stringRows)
		{
		 String[] row = stringRow.split("\\|");
		 tableModel.addRow(row);
		}
		JFrame reportFrame = new JFrame ("JSearcher v1.0 Report");
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("Save as");
		item.addActionListener(new ActionListener() 
	    {
	     public void actionPerformed(ActionEvent e)
	     {
	      BigDecimal minSize = new BigDecimal("0");
		  BigDecimal maxSize = new BigDecimal("0");
		  BigInteger minNameLength = new BigInteger("0");
		  BigInteger maxNameLength = new BigInteger("0");
	      JFileChooser chooseSaveFile = new JFileChooser();
	      chooseSaveFile.setDialogTitle("Save as file");
	      if (chooseSaveFile.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
	      {
	       String filePath = chooseSaveFile.getSelectedFile().getAbsolutePath();
	       try 
	       {
	        FileWriter writeFile = new FileWriter(filePath);
	        writeFile.write("Time: " + dateFormat.format(new Date()) + System.getProperty("line.separator"));
			writeFile.write("User: " + System.getProperty("user.name") + System.getProperty("line.separator"));
			writeFile.write("Operating system: " + System.getProperty("os.name") + System.getProperty("line.separator"));
			writeFile.write("Architecture: " + System.getProperty("os.arch") + System.getProperty("line.separator"));
			writeFile.write("Target directory: " + targetDirPath + System.getProperty("line.separator"));
			writeFile.write("Search for files: " + searchFiles + System.getProperty("line.separator"));
			writeFile.write("Search for directories: " + searchDirs + System.getProperty("line.separator"));
  		    writeFile.write("Scan of subdirectories: " + scanSubdirs + System.getProperty("line.separator"));
    		writeFile.write("Search by name: " + searchByName + System.getProperty("line.separator"));
    		if(searchByName)
    		{
    		 writeFile.write("Regular expression: " + regex + System.getProperty("line.separator"));	
    		}
			writeFile.write("Search by size: " + searchBySize + System.getProperty("line.separator"));
			if(searchBySize)
			{
			 minSize = new BigDecimal(minSizeText);
			 maxSize = new BigDecimal(maxSizeText);
			 writeFile.write("Minimum size: " + minSize + " MB " + System.getProperty("line.separator"));
			 writeFile.write("Maximum size: " + maxSize + " MB " + System.getProperty("line.separator"));
			}
			writeFile.write("Search by name length: " + searchByNameLength + System.getProperty("line.separator"));
			if(searchByNameLength)
			{
			 minNameLength = new BigInteger(minNameLengthText);
			 maxNameLength = new BigInteger(maxNameLengthText);	   
			 writeFile.write("Minimum name length: " + minNameLength + System.getProperty("line.separator"));
			 writeFile.write("Maximum name length: " + maxNameLength + System.getProperty("line.separator"));
			}
			writeFile.write("  | Type | Last modified | Size (MB) | Parent directory | Name" + System.getProperty("line.separator"));
			writeFile.write(reportString.toString());
  		    writeFile.close();
		   }
	       catch (Exception ex) 
	       {
	    	JOptionPane.showMessageDialog(null, "File not created", "Error", JOptionPane.ERROR_MESSAGE);
		   }
	      }
	     }
	    });
		reportTable.setModel(tableModel);
		reportTable.setEnabled(false);
		menu.add(item);
		menuBar.add(menu);
		reportFrame.setJMenuBar(menuBar);  
		reportFrame.getContentPane().add(new JScrollPane(reportTable));
		reportFrame.setSize(1000,500);
		reportFrame.setVisible(true);     
	   }   
      }
     });
     mainFrame.setLayout(new GridLayout(10,3,10,0));
     mainFrame.add(infoLabel);
     mainFrame.add(emptyLabel1);
     mainFrame.add(emptyLabel2);
     mainFrame.add(enterTargetDirPathLabel);
     mainFrame.add(enterTargetDirPathText);
     mainFrame.add(selectTargetDir);
     mainFrame.add(enterRegexLabel);
     mainFrame.add(enterRegexText);
     mainFrame.add(emptyLabel3);
     mainFrame.add(enterMinSizeLabel);
     mainFrame.add(enterMinSizeText);
     mainFrame.add(emptyLabel4);
     mainFrame.add(enterMaxSizeLabel);
     mainFrame.add(enterMaxSizeText);
     mainFrame.add(emptyLabel5);
     mainFrame.add(enterMinNameLengthLabel);
     mainFrame.add(enterMinNameLengthText);
     mainFrame.add(emptyLabel6);
     mainFrame.add(enterMaxNameLengthLabel);
     mainFrame.add(enterMaxNameLengthText);
     mainFrame.add(emptyLabel7);
     mainFrame.add(searchFilesCheck);
     mainFrame.add(searchDirsCheck);
     mainFrame.add(scanSubdirsCheck);
     mainFrame.add(searchByNameCheck);
     mainFrame.add(searchBySizeCheck);
     mainFrame.add(searchByNameLengthCheck);
     mainFrame.add(startResearch);
     mainFrame.add(clearFields);
     mainFrame.add(about);
     mainFrame.setSize(800,450);
     mainFrame.setLocationRelativeTo(null);
     mainFrame.setResizable(false);
   	 mainFrame.setVisible(true);
   }
  
}
