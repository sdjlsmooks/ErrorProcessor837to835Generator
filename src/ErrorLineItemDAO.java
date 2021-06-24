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
				String[] fields = line.split("\t");
				ErrorLineItem errorItem = new ErrorLineItem();
				System.out.println("ERROR FILE - Number Error Line Item Fields = " + fields.length);

				errorItem.setFilename(fields[0]);
				errorItem.setRecordNum(fields[1]);
				errorItem.setClaimNum(fields[2]);
				errorItem.setMemberNum(fields[3]);
				errorItem.setServiceDate(fields[4]);
				errorItem.setProvFirst(fields[5]);
				errorItem.setProvLast(fields[6]);
				errorItem.setErrorNum(fields[7]);
				errorItem.setMedicaid(fields[8]);
				errorItem.setSuppliedValue(fields[9]);
				// The size of the error file record is not constant.
				if (fields.length > 10) {
					errorItem.setDerivedValue(fields[10]);
				}
				if (fields.length > 10) {
					errorItem.setEncounterId(fields[11]);	
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
