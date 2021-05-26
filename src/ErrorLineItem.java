
public class ErrorLineItem {
	private String filename;
	private int recordNum;
	private String claimNum;
	private String memberNum;
	private String serviceDate;
	private String provFirst;
	private String provLast;
	private String errorNum;
	private String medicaid;
	private String suppliedValue;
	private String derivedValue;
	private String encounterId; // BEACON Encounter ID

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(String recordNum) {
		this.recordNum = Integer.parseInt(recordNum);
	}

	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}

	public String getClaimNum() {
		return claimNum;
	}

	public void setClaimNum(String claimNum) {
		this.claimNum = claimNum;
	}

	public String getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(String memberNum) {
		this.memberNum = memberNum;
	}

	public String getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(String serviceDate) {
		this.serviceDate = serviceDate;
	}

	public String getProvFirst() {
		return provFirst;
	}

	public void setProvFirst(String provFirst) {
		this.provFirst = provFirst;
	}

	public String getProvLast() {
		return provLast;
	}

	public void setProvLast(String provLast) {
		this.provLast = provLast;
	}

	public String getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(String errorNum) {
		this.errorNum = errorNum;
	}

	public String getMedicaid() {
		return medicaid;
	}

	public void setMedicaid(String medicaid) {
		this.medicaid = medicaid;
	}

	public String getSuppliedValue() {
		return suppliedValue;
	}

	public void setSuppliedValue(String suppliedValue) {
		this.suppliedValue = suppliedValue;
	}

	public String getDerivedValue() {
		return derivedValue;
	}

	public void setDerivedValue(String derivedValue) {
		this.derivedValue = derivedValue;
	}

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}

	/**
	 * Auto-generated
	 */
	@Override
	public String toString() {
		return "ErrorLineItem [filename=" + filename + ", recordNum=" + recordNum + ", claimNum=" + claimNum
				+ ", memberNum=" + memberNum + ", serviceDate=" + serviceDate + ", provFirst=" + provFirst
				+ ", provLast=" + provLast + ", errorNum=" + errorNum + ", medicaid=" + medicaid + ", suppliedValue="
				+ suppliedValue + ", derivedValue=" + derivedValue + ", encounterId=" + encounterId + "]";
	}

}