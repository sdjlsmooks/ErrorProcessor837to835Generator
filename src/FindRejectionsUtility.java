import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.SetUtils.SetView;
import org.apache.commons.io.FilenameUtils;

import solutions.health.X12HCCProfessional.X12_837.Loop2000ABillingProviderDetail;

public class FindRejectionsUtility {

	/**
	 * Encounter file name
	 */
	private String originalEncounterFile = null;

	/**
	 * Encounter file name
	 */
	private String finalEncounterFile = null;
	
	/**
	 * Original 837 Detail filename
	 */
	private String original837FileName = null;
	
	/**
	 * Desired Output File
	 */
	private String outputFileName = null;

	/**
	 * Map - Original entries   (BeaconEncounterID, EncounterLineItem)
	 */	
	private HashMap<String, EncounterLineItem> originalEncFileEncouters = new HashMap<>();

	/**
	 * Map - Final entries   (BeaconEncounterID, EncounterLineItem)
	 */	
	private HashMap<String, EncounterLineItem> finalEncFileEncounters = new HashMap<>();
	
	/**
	 * Contents of the 837 - Entries - (BeaconEncounterID, Loop2000ABillingProviderDetail) 
	 */
	private HashMap<String,Loop2000ABillingProviderDetail> original837Detail = new HashMap<>();


	/**
	 * Maps Beacon Error Codes to X12 Error Codes
	 * Used in creation of the denial 835s (send in the X12 equivalent of the
	 * Beacon error code.  Used for generating DENIAL 835s 
	 *    NOT IMPLEMENTED YET
	 */
	private HashMap<String,String> beaconToX12ErrorMap = new HashMap<>();	
	
	public void readInOriginal837Information() {
		Original837DAO the837DAO = new Original837DAO(original837FileName);
		original837Detail = the837DAO.getOriginal837Detail();
		for (String original837DetailEncounter: original837Detail.keySet()) {
//			System.out.println("Original 837 Detail Beacon Encounter Number: '"+original837DetailEncounter+"'");
		}
		System.out.println("Original 837 Detail keySet.size() = "+original837Detail.keySet().size());
	}
	
	
	public void readInOriginalEncounterFile() {
		EncounterLineItemDAO eliDAO = new EncounterLineItemDAO(originalEncounterFile);
		originalEncFileEncouters = eliDAO.getAcceptedEncounterMap();
	}

	public void readInFinalEncounterFile() {
		EncounterLineItemDAO eliDAO = new EncounterLineItemDAO(finalEncounterFile);
		finalEncFileEncounters = eliDAO.getAcceptedEncounterMap();
	}

	public void readInBeaconToX12Mapping() {
		BeaconToX12DAO btx12DAO = new BeaconToX12DAO();
		
		beaconToX12ErrorMap = btx12DAO.readInBeaconToX12Mapping();
	}

	
	public FatalRejection findFatalRejection(String encounterID) {
		FatalRejection retVal = null;
	
		try {
			// The error list will probably need to be dynamically created from the read in Beacon-to-X12 mapping file.
			String findRejectionSQL = "select top 1 EncounterId,Error,l.ts from dwh.dbo.HS_Encounters_logs l "+  
					                  " left outer join dwh.dbo.HS_Encounters_removed r on l.EncounterId = r.Encounter_ID "+
					                  " where l.filename='"+FilenameUtils.getName(finalEncounterFile)+ "' and "+ 
					                  " l.EncounterId='"+encounterID+"' and "+ 
					                  " Error in (48, 85, 34, 39, 46, 47, 54, 59, 64, 77, 94, 100, 110, 112, 11, 12, 22, 23, 24, 38, 49, 50, 52, 53, 74, 86, 113, 114) "+
					                  " order by Error desc,l.ts desc";
			
//			System.out.println("--- Query:");
//			System.out.println(findRejectionSQL);
//			System.out.println("--- END Query:");
			
			Connection sqlConn = SQLServerConnection.getInstance().getConnection();
			
			PreparedStatement statement = sqlConn.prepareStatement(findRejectionSQL);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				String foundFatalEncounterID = result.getString("EncounterId");
				String foundFatalRejectionCode = result.getString("Error");
				
				FatalRejection fr = new FatalRejection(foundFatalEncounterID, foundFatalRejectionCode);
				retVal = fr;
//				System.out.println("FatalRejection: "+fr);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 
		return retVal;
	}
	
	public FindRejectionsUtility() {
		// Auto-generated constructor
	}

	public void run(String[] args) {

		if (args.length >= 4) {
			original837FileName = args[0];
			originalEncounterFile = args[1];
			finalEncounterFile = args[2];
			outputFileName = args[3];
		}
		
		System.out.println("Args:  " + args[0] + " " + args[1]);
		System.out.println("Original 837 Filename:       " + original837FileName);
		System.out.println("Original Encounter Filename: " + originalEncounterFile);
		System.out.println("Final Encounter Filename:    " + finalEncounterFile);
		System.out.println("Desired Outuput Filename:    " + outputFileName);
		
		readInBeaconToX12Mapping();
		readInOriginal837Information();
		readInOriginalEncounterFile();
		readInFinalEncounterFile();
		
		System.out.println("************");
		
		System.out.println("Original 837 Key Set Size:        " + original837Detail.keySet().size());
		System.out.println("Original Encounter File Set Size: " + originalEncFileEncouters.keySet().size());
		System.out.println("Final Encounter File Set Size:    " + finalEncFileEncounters.keySet().size());		
		System.out.println();
		
		System.out.println("In original 837 file and NOT IN original Encounter file");
		System.out.println(SetUtils.difference(original837Detail.keySet(), originalEncFileEncouters.keySet()));
		System.out.println();

		
		System.out.println("In the original 837 NOT IN Final Encounter File");
		System.out.println(SetUtils.difference(original837Detail.keySet(), finalEncFileEncounters.keySet()));
		System.out.println();

		System.out.println("In the Final Encounter File NOT IN original Encounter");
		System.out.println(SetUtils.difference(finalEncFileEncounters.keySet(), originalEncFileEncouters.keySet()));
		System.out.println();
		
		
		System.out.println("In the Final Encounter File NOT IN original 837");
		System.out.println(SetUtils.difference(finalEncFileEncounters.keySet(), original837Detail.keySet()));
		System.out.println();
		
		
		System.out.println("Differences between original Encounter File and Final Encounter File");
		SetView<String> differences = SetUtils.difference(originalEncFileEncouters.keySet(), finalEncFileEncounters.keySet());
		System.out.println(differences);
		System.out.println();
		
		Set<String> potentialFatalRejections = differences.toSet();
		
		try (PrintStream ps = new PrintStream(new File(outputFileName))) {
			for (String rejectionEncounterID : potentialFatalRejections) {
				FatalRejection fr = findFatalRejection(rejectionEncounterID);
				ps.println(fr.toTabbed());
				System.out.println("Fatal Rejection: "+fr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		FindRejectionsUtility instance = new FindRejectionsUtility();
		
		instance.run(args);
	}

}

