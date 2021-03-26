package edu.ucalgary.ensf409;

import java.io.*;
import java.util.*;


public class ENSFStorage {
  private static final String DIR = "data";
  private static final String PREFIX = "Error: ";
  private String fileName;
  private LinkedList<String> dataElements; 

  
  //default constructor
  public ENSFStorage() {
	  this.dataElements = new LinkedList<String>();
  }
  
  // One argument constructor
  public ENSFStorage(String fileName) {
    this.fileName = fileName;
    this.dataElements = new LinkedList<String>();
  }

  public LinkedList<String> getDataElements() {
    return dataElements;
  }

  // Return the fileName, which is a relative path
  public String getFileName() {
      return this.DIR + "/" + this.fileName;
  }
  

  // Set the fileName
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  // Add a data element to the end of the list
  public void addDataElement(String dataElement) {
	  File theFile = new File(this.getFileName());
//	  if(this.dataElements.size() == 0) {
//		  this.dataElements.add(dataElement);
//	  }
	  String[] arr = this.asStringArray();
	  int longest = 0;
	  for(int i = 0; i < arr.length; i++) {
		  if(arr[i].length() > longest) {
			  longest = arr[i].length();
		  }
	  }
	  
//	  if((3 * longest) < dataElement.length()) {
//		  System.exit(1);
//	  }else {
//		  this.dataElements.add(dataElement);
//		  writeFile();
//	  }
	  
	  this.dataElements.add(dataElement);
	  
	  if(this.fileName != null) {
		  writeFile();
	  }
	  
	  
	  
	  
  }


  // Given an element and an index, add the element to the list at that index, if it is in bounds.
  // If the file exists, keep it up-to-date
  public void addDataElement(String dataElement, int position) {
	  File theFile = new File(this.getFileName());
	  
	  if(position >= 0 && this.dataElements != null && position < this.dataElements.size()) {
		  this.dataElements.set(position, dataElement);
		  if(theFile.exists()) {
			  writeFile(this.getFileName());
		  }
	  }else {
		  System.exit(1);
	  }
  }

  // Read in the specified file by name.
  public void readFile(String fileName) {
    this.setFileName(fileName);
    this.readFile();
  }


  // Read in the file stored as member data.
  public void readFile() {
    BufferedReader file = null;

    // No filename was set
    if (this.fileName == null) {
      System.err.printf(PREFIX + "FileName must be specified with setter or method call.%n");
      System.exit(1);
    }

    // Empty out the current list, remove any concept of largest size
    dataElements.clear(); 
    
    // Read in the file
    try {
      file = new BufferedReader(new FileReader(this.getFileName()));
      String tmp = new String();
      while ((tmp = file.readLine()) != null) {
         this.addDataElement(tmp);
      }
    } 

    catch (Exception e) {
      System.err.println(PREFIX + "I/O error opening/reading file.");
      System.err.println(e.getMessage());
      closeReader(file);
      System.exit(1);
    }
    
    closeReader(file);

  }
  
  //return a string array produced from the data elements
  public String[] asStringArray() {
	  String[] toReturn = new String[this.dataElements.size()];
	  
	  if(this.dataElements != null) {
		  for(int i = 0; i < toReturn.length; i++) {
			  toReturn[i] = this.dataElements.get(i);
		  }
	  }
	  
	  return toReturn;
  }


  // Delete file or directory
  public void cleanUp() {
    String absolute = System.getProperty("user.dir");
    File abs = new File(absolute);
    File path = new File(abs, this.getFileName());
    cleanUp(path);
  }

  // Write out the file
  public void writeFile() {
    File directory = new File(DIR);
    BufferedWriter file = null;

    // FileName must have been specified
    if (this.fileName == null) {
      System.err.printf(PREFIX + "FileName must be specified with setter or method call.%n");
      System.exit(1);
    }
 
    // Create directory if it doesn't exist; if it does exist, make sure it is a directory
    try {
      if (! directory.exists()) {
        directory.mkdir();
      } else {
        if (! directory.isDirectory()) {
          System.err.printf(PREFIX + "file %s exists but is not a directory.%n", DIR);
          System.exit(1);
        }
      }
      cleanUp(); // Delete any existing file
    }

    catch(Exception e) {
      System.err.printf(PREFIX + "unable to create directory %s.%n", DIR);
      System.err.println(e.getMessage());
      System.exit(1);
    }

    try {
    	if(fileName.contains(DIR)) {
    		file = new BufferedWriter(new FileWriter(this.fileName));
    	}else {
    		file = new BufferedWriter(new FileWriter(this.getFileName()));
    	}

      // For each element, convert to char array and write char array
      // Ensure array is padded to set length
      Iterator<String> it = this.dataElements.iterator();
      while(it.hasNext()) {
        String tmp = new String(it.next());
        file.write(tmp, 0, tmp.length());
        file.newLine();
      }
    }

    catch (Exception e) {
      System.err.println(PREFIX + "I/O error opening/writing file.");
      System.err.println(e.getMessage());
      closeWriter(file);
      System.exit(1);
    }
    
    closeWriter(file);
  }

 
  // writeFile while specifying fileName at same time
  public void writeFile(String fileName) {
    this.setFileName(fileName);
    this.writeFile();
  }



/* Private methods */


  private void closeWriter(BufferedWriter file) {
      try {
        if (file != null) {
          file.close();
        }
      }

      catch (Exception e) {
        System.err.println(PREFIX + "I/O error closing file.");
        System.err.println(e.getMessage());
        System.exit(1);
      }
  }


  private void closeReader(BufferedReader file) {
      try {
        if (file != null) {
          file.close();
        }
      }

      catch (Exception e) {
        System.err.println(PREFIX + "I/O error closing file.");
        System.err.println(e.getMessage());
        System.exit(1);
      }
  }

  // Give us the full relative path to the filename, OS independently
  private String getRelativePath(String filename) {
    File path = new File(DIR);
    File full = new File(path, filename);
    return full.getPath();
  }

  // If the file has been written, a new element cannot be more chars
  // than the largest existing element
  private boolean checkSize(String arg) {
     if (arg.length() > this.getBiggestSize().length()) {
       return false;
     }
     return true; 
  }


  // Get the biggest element we have
  private String getBiggestSize() {
     String big = new String();
     Iterator<String> it = this.dataElements.iterator();
     while(it.hasNext()) {
       String maybe = it.next();
       int tmp = maybe.length();
       if (tmp > big.length()) {
         big = maybe;
       }
     }
     return big;
  }


  // Delete file or directory
  private void cleanUp(File theFile) {
    try {
      if (theFile.isDirectory()) {
        // Get all files in the directory
        File[] files = theFile.listFiles();
      
        // Recursively delete all files/subdirs
        if (files != null) {
          for(File f : files) {
            cleanUp(f);
          }
        }
      }
    
      // Plain file or empty directory
      theFile.delete();
    }

    catch (Exception e) {
      System.err.printf(PREFIX + "unable to remove file %s.%n", this.fileName);
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

}
