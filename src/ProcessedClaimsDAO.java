import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

public class ProcessedClaimsDAO {

	/**
	 * Returns all the claims MockingBird marked as ACCEPTED.
	 * @param fileName The encounter file that was processed.
	 * @return
	 */
	HashMap<String, ProcessedClaim> getAcceptedClaims(String fileName) {
		
		HashMap<String, ProcessedClaim> acceptedClaims = new HashMap<>();
		try {
			

			// Just in case the filename is fully qualified.
			String baseFilename = FilenameUtils.getName(fileName);
			System.out.println("BaseFilename = "+baseFilename);
			String findAcceptedClaimsSQL = "select Subscriber_Medicaid_ID, Encounter_ID from dwh.dbo.HS_Encounters where filename=?";
			PreparedStatement findAcceptedStatement = SQLServerConnection.getInstance().getConnection().prepareStatement(findAcceptedClaimsSQL);
			findAcceptedStatement.setString(1, baseFilename);
			boolean queryResult = findAcceptedStatement.execute();
			if (queryResult) {
				ResultSet acceptedRS = findAcceptedStatement.getResultSet();
				while (acceptedRS.next()) {
					String acceptedClaimSubscriberMedicaidID = acceptedRS.getString("Subscriber_Medicaid_ID");
					String acceptedClaimBeaconEncounterID = acceptedRS.getString("Encounter_ID");					
					ProcessedClaim acceptedClaim = new ProcessedClaim(acceptedClaimSubscriberMedicaidID, acceptedClaimBeaconEncounterID);
					
					acceptedClaims.put(acceptedClaimBeaconEncounterID, acceptedClaim);								
				}
				acceptedRS.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return acceptedClaims;
	}
	
	/**
	 * Returns all the claims MockingBird marked as REJECTED
	 * @param fileName The encounter file that was processed.
	 * @return
	 */
	HashMap<String, ProcessedClaim> getRejectedClaims(String fileName) {
		
		HashMap<String, ProcessedClaim> rejectedClaims = new HashMap<>();
		
		try {
			// Find REJECTED claims					

			// Just in case the filename is fully qualified.
			String baseFilename = FilenameUtils.getName(fileName);
			System.out.println("BaseFilename = "+baseFilename);

			String findRejectedClaimsSQL = "select Subscriber_Medicaid_ID, Encounter_ID from dwh.dbo.HS_Encounters_removed where filename=?";
			PreparedStatement findRejectedStatement = SQLServerConnection.getInstance().getConnection().prepareStatement(findRejectedClaimsSQL);
			findRejectedStatement.setString(1, baseFilename);
			boolean rejectedQueryResult = findRejectedStatement.execute();
			if (rejectedQueryResult) {
				ResultSet rejectedRS = findRejectedStatement.getResultSet();
				while (rejectedRS.next()) {
					String rejectedClaimSubscriberMedicaidID = rejectedRS.getString("Subscriber_Medicaid_ID");
					String rejectedClaimBeaconEncounterID = rejectedRS.getString("Encounter_ID");
					
					ProcessedClaim rejectedClaim = new ProcessedClaim(rejectedClaimSubscriberMedicaidID, rejectedClaimBeaconEncounterID);
					rejectedClaims.put(rejectedClaimBeaconEncounterID, rejectedClaim);								
				}
				rejectedRS.close();
			}
		} catch (SQLException sqe) {
			sqe.printStackTrace();
		}			
	
		return rejectedClaims;
	}
	
	public ProcessedClaimsDAO() {
		
	}

}
