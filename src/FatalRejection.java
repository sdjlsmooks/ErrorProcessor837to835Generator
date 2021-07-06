
public class FatalRejection {

	private String encounterID;

	private String rejectionCode;

	
	public String getEncounterID() {
		return encounterID;
	}

	public void setEncounterID(String encounterID) {
		this.encounterID = encounterID;
	}

	public String getRejectionCode() {
		return rejectionCode;
	}

	public void setRejectionCode(String rejectionCode) {
		this.rejectionCode = rejectionCode;
	}
	
	public FatalRejection(String encID, String rejCode) {
		encounterID = encID;
		rejectionCode = rejCode;
	}

	@Override
	public String toString() {
		return "FatalRejection [encounterID=" + encounterID + ", rejectionCode=" + rejectionCode + "]";
	}

}
