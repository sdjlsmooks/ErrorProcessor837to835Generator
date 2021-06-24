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
				String[] fields = line.split("\t");
				EncounterLineItem lineItem = new EncounterLineItem();
				System.out.println("Number Encounter Line Item fields = " + fields.length);
				lineItem.setRecordNumber(recordNumber);
				++recordNumber; // increment on its own seperate line to help with debugging
								// makes an easy breakpoint
				lineItem.setRecordType(fields[0]);
				lineItem.setSubscriberFirstName(fields[1]);
				lineItem.setSubscriberLastName(fields[2]);
				lineItem.setSubscriberMiddleName(fields[3]);
				lineItem.setSubscriberSuffix(fields[4]);
				lineItem.setSubscriberMedicaidID(fields[5]);
				lineItem.setSubscriberAddress(fields[6]);
				lineItem.setSubscriberCity(fields[7]);
				lineItem.setSubscriberState(fields[8]);
				lineItem.setSubscriberZipCode(fields[9]);
				lineItem.setSubscriberDOB(fields[10]);
				lineItem.setSubscriberGender(fields[11]);
				lineItem.setSubscriberSSN(fields[12]);
				lineItem.setChargedAmount(fields[13]);
				lineItem.setPlaceofService(fields[14]);
				lineItem.setClaimNumber(fields[15]);
				lineItem.setClaimType(fields[16]);
				lineItem.setPrincipalDiagnosis(fields[17]);
				lineItem.setProcedureCode(fields[18]);
				lineItem.setProcedureCodeModifierNum1(fields[19]);
				lineItem.setProcedureCodeModifierNum2(fields[20]);
				lineItem.setProcedureCodeModifierNum3(fields[21]);
				lineItem.setProcedureCodeModifierNum4(fields[22]);
				lineItem.setNumberofUnits(fields[23]);
				lineItem.setDateofService(fields[24]);
				lineItem.setStartTime(fields[25]);
				lineItem.setDuration(fields[26]);
				lineItem.setProviderID(fields[27]);
				lineItem.setProviderFirstName(fields[28]);
				lineItem.setProviderLastName(fields[29]);
				lineItem.setProviderMiddleName(fields[30]);
				lineItem.setProviderSuffix(fields[31]);
				lineItem.setProviderCredentials(fields[32]);
				lineItem.setCoreServiceCode(fields[33]);
				lineItem.setModalityCode(fields[34]);
				lineItem.setProgramCode(fields[35]);
				lineItem.setEmergencyInd(fields[36]);
				lineItem.setEncounterID(fields[37]);
				lineItem.setRevenueCode(fields[38]);
				lineItem.setSpecialStudiesCode1(fields[39]);
				lineItem.setSpecialStudiesCode2(fields[40]);
				lineItem.setNonMedicaidID(fields[41]);
				lineItem.setNonMedicaidPayerSource(fields[42]);
				lineItem.setRenderingProviderNPI(fields[43]);
				lineItem.setSecondDiagnosis(fields[44]);
				lineItem.setNonMedicaidFlag(fields[45]);
				lineItem.setOverrideFlag(fields[46]);
				lineItem.setCcarOnFile(fields[47]);
				lineItem.setSubmitterDefined(fields[48]);
				// The ENC file record length is not constant
				if (fields.length > 49) {
					lineItem.setThirdDiagnosis(fields[49]);
				}
				if (fields.length > 50) {
					lineItem.setFourthDiagnosis(fields[50]);
				}
				if (fields.length > 51) {
					lineItem.setLeaveblank30(fields[51]);
				}
				if (fields.length > 52) {
					lineItem.setServicingProviderID(fields[52]);
				}
				if (fields.length > 53) {
					lineItem.setServiceProviderFirst(fields[53]);
				}
				if (fields.length > 54) {
					lineItem.setServiceProviderLast(fields[54]);	
				}
				if (fields.length > 55) {
					lineItem.setServiceProviderMiddle(fields[55]);
				}
				if (fields.length > 56) {
					lineItem.setServiceProviderSuffix(fields[56]);
				}
				if (fields.length > 57) {
					lineItem.setServiceProviderCredentials(fields[57]);
				}
				if (fields.length > 58) {
					lineItem.setServiceProviderNPI(fields[58]);
				}
				if (fields.length > 59) {
					lineItem.setOtherPayerCode(fields[59]);
				}
				if (fields.length > 60) {
					lineItem.setOtherPayerDescription(fields[60]);
				}
				if (fields.length > 61) {
					lineItem.setOtherPayerAmount(fields[61]);
				}
				if (fields.length > 62) {
					lineItem.setOtherPayerId(fields[62]);
				}
				if (fields.length > 63) {
					lineItem.setAssignBenefitsFlag(fields[63]);
				}
				if (fields.length > 64) {
					lineItem.setBillingProviderNPI(fields[64]);
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
