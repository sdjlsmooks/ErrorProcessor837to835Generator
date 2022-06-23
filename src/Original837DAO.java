import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.transform.Result;

import org.milyn.io.FileUtils;
import org.xml.sax.SAXException;

import solutions.health.X12HCCProfessional.X12_837.Loop2000ABillingProviderDetail;
import solutions.health.X12HCCProfessional.X12_837.Loop2000BSubscriberHierarchicalLevel;
import solutions.health.X12HCCProfessional.X12_837.Loop2000CPatientHierarchicalLevel;
import solutions.health.X12HCCProfessional.X12_837.Loop2300ClaimInformation;
import solutions.health.X12HCCProfessional.X12_837.Loop2400ServiceLineNumber;
import solutions.health.X12HCCProfessional.X12_837.TransactionSet;
import solutions.health.X12HCCProfessional.X12_837.X12837;
import solutions.health.X12HCCProfessional.X12_837.X12837Factory;



public class Original837DAO {

	/**
	 * The file containing the 837 (passed in on command loine)
	 */
	private String fileName;
	
	/**
	 * Contents of the 837 - Entries - (BeaconEncounterID, Loop2000ABillingProviderDetail) 
	 */
	private HashMap<String,Loop2000ABillingProviderDetail> original837Detail = new HashMap<>();
	
	/**
	 * The original 837 - source of truth
	 */
	private X12837 x12Message = null;

	

	public HashMap<String,Loop2000ABillingProviderDetail> getOriginal837Detail() {

		Result[] additionalResults = new Result[1000];
		if (original837Detail.size() == 0) {
			try {
	
	
				// Create the Factory class instance - used to parse the original 837
				X12837Factory x12837Factory = X12837Factory.getInstance();
	
				// Read in the 835 from the file into in-memory representation
				String ediMessage = new String(FileUtils.readFile(new File(fileName)));
				StringReader ediStream = new StringReader(ediMessage);
				x12Message = x12837Factory.fromEDI(ediStream, additionalResults);
				System.out.println("Successfully Parsed");
	
				// Loop through record in the 837, find the BEACON encounter ID
				// ID should be in the "Line Item Control Number" --- the REF*6R segment.
				TransactionSet ts = x12Message.getInterchange().getFunctionalGroup().getTransactionSet().get(0);
				for (Loop2000ABillingProviderDetail loop2000ABillingProviderDetail : ts
						.getLoop2000ABillingProviderDetail()) {
					Loop2000BSubscriberHierarchicalLevel loop2000B = loop2000ABillingProviderDetail
							.getLoop2000BSubscriberHierarchicalLevel().get(0);
					Loop2000CPatientHierarchicalLevel phl = loop2000B.getLoop2000CPatientHierarchicalLevel().get(0);
					Loop2300ClaimInformation loop2300ClaimInformation = phl.getLoop2300ClaimInformation().get(0);
					Loop2400ServiceLineNumber sln = loop2300ClaimInformation.getLoop2400ServiceLineNumber().get(0);
					System.out.println("Line Item Control Number: " + sln.getLiControlNumber().getReferenceIdentification());
					
					// Save Original Source of Truth data for error processing				
					String serviceLineNumberBeaconEncounterID = loop2000ABillingProviderDetail.getLoop2000BSubscriberHierarchicalLevel().get(0).getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0).getLoop2400ServiceLineNumber().get(0).getLiControlNumber().getReferenceIdentification().toString().trim();
					original837Detail.put(serviceLineNumberBeaconEncounterID, loop2000ABillingProviderDetail);
				}
				System.out.println("Total Original 837 Claims - " + original837Detail.size());			
			} catch (SAXException sae) {
				sae.printStackTrace();			
				System.exit(1);
			} catch (IOException ioe) {
				ioe.printStackTrace();			
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();			
				System.exit(1);
			}
		}
		return original837Detail;
	}

	
	
	public Original837DAO(String original837Filename) {
		// TODO Auto-generated constructor stub
		fileName = original837Filename;
	}

}
