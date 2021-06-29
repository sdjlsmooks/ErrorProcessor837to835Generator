import java.util.HashMap;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;

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

	
	
	public FindRejectionsUtility() {
		// TODO Auto-generated constructor stub
	}

	public void run(String[] args) {
		original837FileName = args[0];
		originalEncounterFile = args[1];
		finalEncounterFile = args[2];
		
		System.out.println("Args:  " + args[0] + " " + args[1]);
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
		System.out.println(SetUtils.difference(originalEncFileEncouters.keySet(), finalEncFileEncounters.keySet()));
		System.out.println();

		
	}
	
	public static void main(String[] args) {
		
		FindRejectionsUtility instance = new FindRejectionsUtility();
		
		instance.run(args);

		
		
	}

}
