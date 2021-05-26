
public class ProcessedClaim {
	private String subscriberMedicaidID;
	private String beaconEncounterID;
	
	
	
	public ProcessedClaim(String subscriberMedicaidID, String beaconEncounterID) {
		super();
		this.subscriberMedicaidID = subscriberMedicaidID;
		this.beaconEncounterID = beaconEncounterID;
	}
	
	public String getSubscriberMedicaidID() {
		return subscriberMedicaidID;
	}
	public void setSubscriberMedicaidID(String subscriberMedicaidID) {
		this.subscriberMedicaidID = subscriberMedicaidID;
	}
	public String getBeaconEncounterID() {
		return beaconEncounterID;
	}
	public void setBeaconEncounterID(String beaconEncounterID) {
		this.beaconEncounterID = beaconEncounterID;
	}
}