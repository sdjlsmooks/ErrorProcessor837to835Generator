import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;

public class EncounterLineItemDAO {

	/**
	 * The name of the file (_err file passed in on the command line)
	 */
	String fileName;

	/**
	 * The contents of the .ENC file
	 */
	private ArrayList<EncounterLineItem> encounterList = new ArrayList<>();

	/**
	 * Map - entries   (BeaconEncounterID, EncounterLineItem)
	 */
	private HashMap<String, EncounterLineItem> acceptedEncounterMap = new HashMap<>();
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<EncounterLineItem> getEncounterList() {
		if (acceptedEncounterMap.size() == 0) {
			readInEncounterFile();
		}
		return encounterList;
	}

	public void setEncounterList(ArrayList<EncounterLineItem> encounterList) {
		this.encounterList = encounterList;
	}

	public HashMap<String, EncounterLineItem> getAcceptedEncounterMap() {
		if (acceptedEncounterMap.size() == 0) {
			readInEncounterFile();
		}
		return acceptedEncounterMap;
	}

	public void setAcceptedEncounterMap(HashMap<String, EncounterLineItem> acceptedEncounterMap) {
		this.acceptedEncounterMap = acceptedEncounterMap;
	}

	public void readInEncounterFile() {

		try (LineNumberReader lnr = new LineNumberReader(new FileReader(fileName))) {
			// Skip first line (header information only
			String line = lnr.readLine();

			// Read in each line item
			int recordNumber=1;
			while ((line = lnr.readLine()) != null) {
				String[] individualLineEntries = line.split("\t");
				EncounterLineItem lineItem = new EncounterLineItem();
				System.out.println("Number line Entries = " + individualLineEntries.length);
				lineItem.setRecordNumber(recordNumber);
				++recordNumber; // increment on its own seperate line to help with debugging
								// makes an easy breakpoint
				lineItem.setRecordType(individualLineEntries[0]);
				lineItem.setSubscriberFirstName(individualLineEntries[1]);
				lineItem.setSubscriberLastName(individualLineEntries[2]);
				lineItem.setSubscriberMiddleName(individualLineEntries[3]);
				lineItem.setSubscriberSuffix(individualLineEntries[4]);
				lineItem.setSubscriberMedicaidID(individualLineEntries[5]);
				lineItem.setSubscriberAddress(individualLineEntries[6]);
				lineItem.setSubscriberCity(individualLineEntries[7]);
				lineItem.setSubscriberState(individualLineEntries[8]);
				lineItem.setSubscriberZipCode(individualLineEntries[9]);
				lineItem.setSubscriberDOB(individualLineEntries[10]);
				lineItem.setSubscriberGender(individualLineEntries[11]);
				lineItem.setSubscriberSSN(individualLineEntries[12]);
				lineItem.setChargedAmount(individualLineEntries[13]);
				lineItem.setPlaceofService(individualLineEntries[14]);
				lineItem.setClaimNumber(individualLineEntries[15]);
				lineItem.setClaimType(individualLineEntries[16]);
				lineItem.setPrincipalDiagnosis(individualLineEntries[17]);
				lineItem.setProcedureCode(individualLineEntries[18]);
				lineItem.setProcedureCodeModifierNum1(individualLineEntries[19]);
				lineItem.setProcedureCodeModifierNum2(individualLineEntries[20]);
				lineItem.setProcedureCodeModifierNum3(individualLineEntries[21]);
				lineItem.setProcedureCodeModifierNum4(individualLineEntries[22]);
				lineItem.setNumberofUnits(individualLineEntries[23]);
				lineItem.setDateofService(individualLineEntries[24]);
				lineItem.setStartTime(individualLineEntries[25]);
				lineItem.setDuration(individualLineEntries[26]);
				lineItem.setProviderID(individualLineEntries[27]);
				lineItem.setProviderFirstName(individualLineEntries[28]);
				lineItem.setProviderLastName(individualLineEntries[29]);
				lineItem.setProviderMiddleName(individualLineEntries[30]);
				lineItem.setProviderSuffix(individualLineEntries[31]);
				lineItem.setProviderCredentials(individualLineEntries[32]);
				lineItem.setCoreServiceCode(individualLineEntries[33]);
				lineItem.setModalityCode(individualLineEntries[34]);
				lineItem.setProgramCode(individualLineEntries[35]);
				lineItem.setEmergencyInd(individualLineEntries[36]);
				lineItem.setEncounterID(individualLineEntries[37]);
				lineItem.setRevenueCode(individualLineEntries[38]);
				lineItem.setSpecialStudiesCode1(individualLineEntries[39]);
				lineItem.setSpecialStudiesCode2(individualLineEntries[40]);
				lineItem.setNonMedicaidID(individualLineEntries[41]);
				lineItem.setNonMedicaidPayerSource(individualLineEntries[42]);
				lineItem.setRenderingProviderNPI(individualLineEntries[43]);
				lineItem.setSecondDiagnosis(individualLineEntries[44]);
				lineItem.setNonMedicaidFlag(individualLineEntries[45]);
				lineItem.setOverrideFlag(individualLineEntries[46]);
				lineItem.setCcarOnFile(individualLineEntries[47]);
				lineItem.setSubmitterDefined(individualLineEntries[48]);
				// The ENC file record length is not constant
				if (individualLineEntries.length > 49) {
					lineItem.setThirdDiagnosis(individualLineEntries[49]);
				}
				if (individualLineEntries.length > 50) {
					lineItem.setFourthDiagnosis(individualLineEntries[50]);
				}
				if (individualLineEntries.length > 51) {
					lineItem.setLeaveblank30(individualLineEntries[51]);
				}
				if (individualLineEntries.length > 52) {
					lineItem.setServicingProviderID(individualLineEntries[52]);
				}
				if (individualLineEntries.length > 53) {
					lineItem.setServiceProviderFirst(individualLineEntries[53]);
				}
				if (individualLineEntries.length > 54) {
					lineItem.setServiceProviderLast(individualLineEntries[54]);	
				}
				if (individualLineEntries.length > 55) {
					lineItem.setServiceProviderMiddle(individualLineEntries[55]);
				}
				if (individualLineEntries.length > 56) {
					lineItem.setServiceProviderSuffix(individualLineEntries[56]);
				}
				if (individualLineEntries.length > 57) {
					lineItem.setServiceProviderCredentials(individualLineEntries[57]);
				}
				if (individualLineEntries.length > 58) {
					lineItem.setServiceProviderNPI(individualLineEntries[58]);
				}
				if (individualLineEntries.length > 59) {
					lineItem.setOtherPayerCode(individualLineEntries[59]);
				}
				if (individualLineEntries.length > 60) {
					lineItem.setOtherPayerDescription(individualLineEntries[60]);
				}
				if (individualLineEntries.length > 61) {
					lineItem.setOtherPayerAmount(individualLineEntries[61]);
				}
				if (individualLineEntries.length > 62) {
					lineItem.setOtherPayerId(individualLineEntries[62]);
				}
				if (individualLineEntries.length > 63) {
					lineItem.setAssignBenefitsFlag(individualLineEntries[63]);
				}
				if (individualLineEntries.length > 64) {
					lineItem.setBillingProviderNPI(individualLineEntries[64]);
				} else {
					// Sometimes billingProviderNPI is not present
					lineItem.setBillingProviderNPI("");
				}
				System.out.println("ENC FILE Beacon Encounter ID = '" + lineItem.getEncounterID() + "'");
				encounterList.add(lineItem);
				acceptedEncounterMap.put(lineItem.getEncounterID(),lineItem);
			}
			System.out.println("Total encounters read in = " + encounterList.size());

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public EncounterLineItemDAO(String encounterFile) {
		fileName = encounterFile; 
	}

}
