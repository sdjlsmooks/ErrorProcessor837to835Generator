import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ErrorLineItemDAO {

	/**
	 * The filename containing the Beacon errors
	 */
	private String fileName;

	/**
	 * The contents of the ENC_err.txt file
	 */
	private ArrayList<ErrorLineItem> errorList = new ArrayList<>();

	/**
	 * Map - entries   (BeaconEncounterID, ErrorLineItem)
	 */
	private HashMap<String, ErrorLineItem> errorMap = new HashMap<>();

	
	public void readInErrorFile() {

		try (LineNumberReader lnr = new LineNumberReader(new FileReader(fileName))) {
			// Skip first line (header information only
			String line = lnr.readLine();

			// Read in each line item
			System.out.println("BEFORE Total ERRORS read in = " + errorList.size());
			while ((line = lnr.readLine()) != null) {
				String[] individualLineEntries = line.split("\t");
				ErrorLineItem errorItem = new ErrorLineItem();
				System.out.println("ERROR FILE - Number Fields = " + individualLineEntries.length);

				errorItem.setFilename(individualLineEntries[0]);
				errorItem.setRecordNum(individualLineEntries[1]);
				errorItem.setClaimNum(individualLineEntries[2]);
				errorItem.setMemberNum(individualLineEntries[3]);
				errorItem.setServiceDate(individualLineEntries[4]);
				errorItem.setProvFirst(individualLineEntries[5]);
				errorItem.setProvLast(individualLineEntries[6]);
				errorItem.setErrorNum(individualLineEntries[7]);
				errorItem.setMedicaid(individualLineEntries[8]);
				errorItem.setSuppliedValue(individualLineEntries[9]);
				// The size of the error file record is not constant.
				if (individualLineEntries.length > 10) {
					errorItem.setDerivedValue(individualLineEntries[10]);
				}
				if (individualLineEntries.length > 10) {
					errorItem.setEncounterId(individualLineEntries[11]);	
				}
				
				System.out.println("Beacon Record Number, Claim Number = '"+errorItem.getRecordNum()+","+errorItem.getClaimNum()+"'");				
				
				System.out.println("BEACON Encounter ID = '"+errorItem.getEncounterId()+"'");
				errorList.add(errorItem);
				errorMap.put(errorItem.getEncounterId(), errorItem);
			}
			System.out.println("Total ERRORS read in = " + errorList.size());
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public ErrorLineItemDAO(String errorFile) {
		fileName = errorFile;
	}
	
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public ArrayList<ErrorLineItem> getErrorList() {
		if (errorList.size() == 0) {
			readInErrorFile();
		}
		return errorList;
	}


	public HashMap<String, ErrorLineItem> getErrorMap() {
		if (errorList.size() == 0) {
			readInErrorFile();
		}
		return errorMap;
	}
}
