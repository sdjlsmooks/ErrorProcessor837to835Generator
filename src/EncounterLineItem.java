
/**
 * Class to store each line item in the ENC file
 *  REFACTOR INTO DAO/DTO pattern once you figure out what the problem is with
 *  the 835 not posting 
 */
public class EncounterLineItem {
	private int    recordNumber; // Artificial key - used in conjunction with the error file
	                             // in error processing, the error file does not always include
	                             // the encounter ID, work my way back to it through the linear record
	                             // number in the file.
	private String recordType;
	private String subscriberFirstName;
	private String subscriberLastName;
	private String subscriberMiddleName;
	private String subscriberSuffix;
	private String subscriberMedicaidID;
	private String subscriberAddress;
	private String subscriberCity;
	private String subscriberState;
	private String subscriberZipCode;
	private String subscriberDOB;
	private String subscriberGender;
	private String subscriberSSN;
	private String chargedAmount;
	private String placeofService;
	private String claimNumber;
	private String claimType;
	private String principalDiagnosis;
	private String procedureCode;
	private String procedureCodeModifierNum1;
	private String procedureCodeModifierNum2;
	private String procedureCodeModifierNum3;
	private String procedureCodeModifierNum4;
	private String numberofUnits;
	private String dateofService;
	private String startTime;
	private String duration;
	private String providerID;
	private String providerFirstName;
	private String providerLastName;
	private String providerMiddleName;
	private String providerSuffix;
	private String providerCredentials;
	private String coreServiceCode;
	private String modalityCode;
	private String programCode;
	private String emergencyInd;
	private String encounterID;
	private String revenueCode;
	private String specialStudiesCode1;
	private String specialStudiesCode2;
	private String nonMedicaidID;
	private String nonMedicaidPayerSource;
	private String renderingProviderNPI;
	private String secondDiagnosis;
	private String nonMedicaidFlag;
	private String overrideFlag;
	private String ccarOnFile;
	private String submitterDefined;
	private String thirdDiagnosis;
	private String fourthDiagnosis;
	private String leaveblank30;
	private String servicingProviderID;
	private String serviceProviderFirst;
	private String serviceProviderLast;
	private String serviceProviderMiddle;
	private String serviceProviderSuffix;
	private String serviceProviderCredentials;
	private String serviceProviderNPI;
	private String otherPayerCode;
	private String otherPayerDescription;
	private String otherPayerAmount;
	private String otherPayerId;
	private String assignBenefitsFlag;
	private String billingProviderNPI;

	public int getRecordNumber() {
		return recordNumber;
	}


	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getRecordType() {
		return recordType;
	}


	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}


	public String getSubscriberFirstName() {
		return subscriberFirstName;
	}


	public void setSubscriberFirstName(String subscriberFirstName) {
		this.subscriberFirstName = subscriberFirstName;
	}


	public String getSubscriberLastName() {
		return subscriberLastName;
	}


	public void setSubscriberLastName(String subscriberLastName) {
		this.subscriberLastName = subscriberLastName;
	}


	public String getSubscriberMiddleName() {
		return subscriberMiddleName;
	}


	public void setSubscriberMiddleName(String subscriberMiddleName) {
		this.subscriberMiddleName = subscriberMiddleName;
	}


	public String getSubscriberSuffix() {
		return subscriberSuffix;
	}


	public void setSubscriberSuffix(String subscriberSuffix) {
		this.subscriberSuffix = subscriberSuffix;
	}


	public String getSubscriberMedicaidID() {
		return subscriberMedicaidID;
	}


	public void setSubscriberMedicaidID(String subscriberMedicaidID) {
		this.subscriberMedicaidID = subscriberMedicaidID;
	}


	public String getSubscriberAddress() {
		return subscriberAddress;
	}


	public void setSubscriberAddress(String subscriberAddress) {
		this.subscriberAddress = subscriberAddress;
	}


	public String getSubscriberCity() {
		return subscriberCity;
	}


	public void setSubscriberCity(String subscriberCity) {
		this.subscriberCity = subscriberCity;
	}


	public String getSubscriberState() {
		return subscriberState;
	}


	public void setSubscriberState(String subscriberState) {
		this.subscriberState = subscriberState;
	}


	public String getSubscriberZipCode() {
		return subscriberZipCode;
	}


	public void setSubscriberZipCode(String subscriberZipCode) {
		this.subscriberZipCode = subscriberZipCode;
	}


	public String getSubscriberDOB() {
		return subscriberDOB;
	}


	public void setSubscriberDOB(String subscriberDOB) {
		this.subscriberDOB = subscriberDOB;
	}


	public String getSubscriberGender() {
		return subscriberGender;
	}


	public void setSubscriberGender(String subscriberGender) {
		this.subscriberGender = subscriberGender;
	}


	public String getSubscriberSSN() {
		return subscriberSSN;
	}


	public void setSubscriberSSN(String subscriberSSN) {
		this.subscriberSSN = subscriberSSN;
	}


	public String getChargedAmount() {
		return chargedAmount;
	}


	public void setChargedAmount(String chargedAmount) {
		this.chargedAmount = chargedAmount;
	}


	public String getPlaceofService() {
		return placeofService;
	}


	public void setPlaceofService(String placeofService) {
		this.placeofService = placeofService;
	}


	public String getClaimNumber() {
		return claimNumber;
	}


	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}


	public String getClaimType() {
		return claimType;
	}


	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}


	public String getPrincipalDiagnosis() {
		return principalDiagnosis;
	}


	public void setPrincipalDiagnosis(String principalDiagnosis) {
		this.principalDiagnosis = principalDiagnosis;
	}


	public String getProcedureCode() {
		return procedureCode;
	}


	public void setProcedureCode(String procedureCode) {
		this.procedureCode = procedureCode;
	}


	public String getProcedureCodeModifierNum1() {
		return procedureCodeModifierNum1;
	}


	public void setProcedureCodeModifierNum1(String procedureCodeModifierNum1) {
		this.procedureCodeModifierNum1 = procedureCodeModifierNum1;
	}


	public String getProcedureCodeModifierNum2() {
		return procedureCodeModifierNum2;
	}


	public void setProcedureCodeModifierNum2(String procedureCodeModifierNum2) {
		this.procedureCodeModifierNum2 = procedureCodeModifierNum2;
	}


	public String getProcedureCodeModifierNum3() {
		return procedureCodeModifierNum3;
	}


	public void setProcedureCodeModifierNum3(String procedureCodeModifierNum3) {
		this.procedureCodeModifierNum3 = procedureCodeModifierNum3;
	}


	public String getProcedureCodeModifierNum4() {
		return procedureCodeModifierNum4;
	}


	public void setProcedureCodeModifierNum4(String procedureCodeModifierNum4) {
		this.procedureCodeModifierNum4 = procedureCodeModifierNum4;
	}


	public String getNumberofUnits() {
		return numberofUnits;
	}


	public void setNumberofUnits(String numberofUnits) {
		this.numberofUnits = numberofUnits;
	}


	public String getDateofService() {
		return dateofService;
	}


	public void setDateofService(String dateofService) {
		this.dateofService = dateofService;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getDuration() {
		return duration;
	}


	public void setDuration(String duration) {
		this.duration = duration;
	}


	public String getProviderID() {
		return providerID;
	}


	public void setProviderID(String providerID) {
		this.providerID = providerID;
	}


	public String getProviderFirstName() {
		return providerFirstName;
	}


	public void setProviderFirstName(String providerFirstName) {
		this.providerFirstName = providerFirstName;
	}


	public String getProviderLastName() {
		return providerLastName;
	}


	public void setProviderLastName(String providerLastName) {
		this.providerLastName = providerLastName;
	}


	public String getProviderMiddleName() {
		return providerMiddleName;
	}


	public void setProviderMiddleName(String providerMiddleName) {
		this.providerMiddleName = providerMiddleName;
	}


	public String getProviderSuffix() {
		return providerSuffix;
	}


	public void setProviderSuffix(String providerSuffix) {
		this.providerSuffix = providerSuffix;
	}


	public String getProviderCredentials() {
		return providerCredentials;
	}


	public void setProviderCredentials(String providerCredentials) {
		this.providerCredentials = providerCredentials;
	}


	public String getCoreServiceCode() {
		return coreServiceCode;
	}


	public void setCoreServiceCode(String coreServiceCode) {
		this.coreServiceCode = coreServiceCode;
	}


	public String getModalityCode() {
		return modalityCode;
	}


	public void setModalityCode(String modalityCode) {
		this.modalityCode = modalityCode;
	}


	public String getProgramCode() {
		return programCode;
	}


	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}


	public String getEmergencyInd() {
		return emergencyInd;
	}


	public void setEmergencyInd(String emergencyInd) {
		this.emergencyInd = emergencyInd;
	}


	public String getEncounterID() {
		return encounterID;
	}


	public void setEncounterID(String encounterID) {
		this.encounterID = encounterID;
	}


	public String getRevenueCode() {
		return revenueCode;
	}


	public void setRevenueCode(String revenueCode) {
		this.revenueCode = revenueCode;
	}


	public String getSpecialStudiesCode1() {
		return specialStudiesCode1;
	}


	public void setSpecialStudiesCode1(String specialStudiesCode1) {
		this.specialStudiesCode1 = specialStudiesCode1;
	}


	public String getSpecialStudiesCode2() {
		return specialStudiesCode2;
	}


	public void setSpecialStudiesCode2(String specialStudiesCode2) {
		this.specialStudiesCode2 = specialStudiesCode2;
	}


	public String getNonMedicaidID() {
		return nonMedicaidID;
	}


	public void setNonMedicaidID(String nonMedicaidID) {
		this.nonMedicaidID = nonMedicaidID;
	}


	public String getNonMedicaidPayerSource() {
		return nonMedicaidPayerSource;
	}


	public void setNonMedicaidPayerSource(String nonMedicaidPayerSource) {
		this.nonMedicaidPayerSource = nonMedicaidPayerSource;
	}


	public String getRenderingProviderNPI() {
		return renderingProviderNPI;
	}


	public void setRenderingProviderNPI(String renderingProviderNPI) {
		this.renderingProviderNPI = renderingProviderNPI;
	}


	public String getSecondDiagnosis() {
		return secondDiagnosis;
	}


	public void setSecondDiagnosis(String secondDiagnosis) {
		this.secondDiagnosis = secondDiagnosis;
	}


	public String getNonMedicaidFlag() {
		return nonMedicaidFlag;
	}


	public void setNonMedicaidFlag(String nonMedicaidFlag) {
		this.nonMedicaidFlag = nonMedicaidFlag;
	}


	public String getOverrideFlag() {
		return overrideFlag;
	}


	public void setOverrideFlag(String overrideFlag) {
		this.overrideFlag = overrideFlag;
	}


	public String getCcarOnFile() {
		return ccarOnFile;
	}


	public void setCcarOnFile(String ccarOnFile) {
		this.ccarOnFile = ccarOnFile;
	}


	public String getSubmitterDefined() {
		return submitterDefined;
	}


	public void setSubmitterDefined(String submitterDefined) {
		this.submitterDefined = submitterDefined;
	}


	public String getThirdDiagnosis() {
		return thirdDiagnosis;
	}


	public void setThirdDiagnosis(String thirdDiagnosis) {
		this.thirdDiagnosis = thirdDiagnosis;
	}


	public String getFourthDiagnosis() {
		return fourthDiagnosis;
	}


	public void setFourthDiagnosis(String fourthDiagnosis) {
		this.fourthDiagnosis = fourthDiagnosis;
	}


	public String getLeaveblank30() {
		return leaveblank30;
	}


	public void setLeaveblank30(String leaveblank30) {
		this.leaveblank30 = leaveblank30;
	}


	public String getServicingProviderID() {
		return servicingProviderID;
	}


	public void setServicingProviderID(String servicingProviderID) {
		this.servicingProviderID = servicingProviderID;
	}


	public String getServiceProviderFirst() {
		return serviceProviderFirst;
	}


	public void setServiceProviderFirst(String serviceProviderFirst) {
		this.serviceProviderFirst = serviceProviderFirst;
	}


	public String getServiceProviderLast() {
		return serviceProviderLast;
	}


	public void setServiceProviderLast(String serviceProviderLast) {
		this.serviceProviderLast = serviceProviderLast;
	}


	public String getServiceProviderMiddle() {
		return serviceProviderMiddle;
	}


	public void setServiceProviderMiddle(String serviceProviderMiddle) {
		this.serviceProviderMiddle = serviceProviderMiddle;
	}


	public String getServiceProviderSuffix() {
		return serviceProviderSuffix;
	}


	public void setServiceProviderSuffix(String serviceProviderSuffix) {
		this.serviceProviderSuffix = serviceProviderSuffix;
	}


	public String getServiceProviderCredentials() {
		return serviceProviderCredentials;
	}


	public void setServiceProviderCredentials(String serviceProviderCredentials) {
		this.serviceProviderCredentials = serviceProviderCredentials;
	}


	public String getServiceProviderNPI() {
		return serviceProviderNPI;
	}


	public void setServiceProviderNPI(String serviceProviderNPI) {
		this.serviceProviderNPI = serviceProviderNPI;
	}


	public String getOtherPayerCode() {
		return otherPayerCode;
	}


	public void setOtherPayerCode(String otherPayerCode) {
		this.otherPayerCode = otherPayerCode;
	}


	public String getOtherPayerDescription() {
		return otherPayerDescription;
	}


	public void setOtherPayerDescription(String otherPayerDescription) {
		this.otherPayerDescription = otherPayerDescription;
	}


	public String getOtherPayerAmount() {
		return otherPayerAmount;
	}


	public void setOtherPayerAmount(String otherPayerAmount) {
		this.otherPayerAmount = otherPayerAmount;
	}


	public String getOtherPayerId() {
		return otherPayerId;
	}


	public void setOtherPayerId(String otherPayerId) {
		this.otherPayerId = otherPayerId;
	}


	public String getAssignBenefitsFlag() {
		return assignBenefitsFlag;
	}


	public void setAssignBenefitsFlag(String assignBenefitsFlag) {
		this.assignBenefitsFlag = assignBenefitsFlag;
	}


	public String getBillingProviderNPI() {
		return billingProviderNPI;
	}


	public void setBillingProviderNPI(String billingProviderNPI) {
		this.billingProviderNPI = billingProviderNPI;
	}


	/**
	 * Auto-generated
	 */
	@Override
	public String toString() {
		return "EncounterLineItem [recordType=" + recordType + ", subscriberFirstName=" + subscriberFirstName
				+ ", subscriberLastName=" + subscriberLastName + ", subscriberMiddleName=" + subscriberMiddleName
				+ ", subscriberSuffix=" + subscriberSuffix + ", subscriberMedicaidID=" + subscriberMedicaidID
				+ ", subscriberAddress=" + subscriberAddress + ", subscriberCity=" + subscriberCity
				+ ", subscriberState=" + subscriberState + ", subscriberZipCode=" + subscriberZipCode
				+ ", subscriberDOB=" + subscriberDOB + ", subscriberGender=" + subscriberGender + ", subscriberSSN="
				+ subscriberSSN + ", chargedAmount=" + chargedAmount + ", placeofService=" + placeofService
				+ ", claimNumber=" + claimNumber + ", claimType=" + claimType + ", principalDiagnosis="
				+ principalDiagnosis + ", procedureCode=" + procedureCode + ", procedureCodeModifierNum1="
				+ procedureCodeModifierNum1 + ", procedureCodeModifierNum2=" + procedureCodeModifierNum2
				+ ", procedureCodeModifierNum3=" + procedureCodeModifierNum3 + ", procedureCodeModifierNum4="
				+ procedureCodeModifierNum4 + ", numberofUnits=" + numberofUnits + ", dateofService=" + dateofService
				+ ", startTime=" + startTime + ", duration=" + duration + ", providerID=" + providerID
				+ ", providerFirstName=" + providerFirstName + ", providerLastName=" + providerLastName
				+ ", providerMiddleName=" + providerMiddleName + ", providerSuffix=" + providerSuffix
				+ ", providerCredentials=" + providerCredentials + ", coreServiceCode=" + coreServiceCode
				+ ", modalityCode=" + modalityCode + ", programCode=" + programCode + ", emergencyInd=" + emergencyInd
				+ ", encounterID=" + encounterID + ", revenueCode=" + revenueCode + ", specialStudiesCode1="
				+ specialStudiesCode1 + ", specialStudiesCode2=" + specialStudiesCode2 + ", nonMedicaidID="
				+ nonMedicaidID + ", nonMedicaidPayerSource=" + nonMedicaidPayerSource + ", renderingProviderNPI="
				+ renderingProviderNPI + ", secondDiagnosis=" + secondDiagnosis + ", nonMedicaidFlag=" + nonMedicaidFlag
				+ ", overrideFlag=" + overrideFlag + ", ccarOnFile=" + ccarOnFile + ", submitterDefined="
				+ submitterDefined + ", thirdDiagnosis=" + thirdDiagnosis + ", fourthDiagnosis=" + fourthDiagnosis
				+ ", leaveblank30=" + leaveblank30 + ", servicingProviderID=" + servicingProviderID
				+ ", serviceProviderFirst=" + serviceProviderFirst + ", serviceProviderLast=" + serviceProviderLast
				+ ", serviceProviderMiddle=" + serviceProviderMiddle + ", serviceProviderSuffix="
				+ serviceProviderSuffix + ", serviceProviderCredentials=" + serviceProviderCredentials
				+ ", serviceProviderNPI=" + serviceProviderNPI + ", otherPayerCode=" + otherPayerCode
				+ ", otherPayerDescription=" + otherPayerDescription + ", otherPayerAmount=" + otherPayerAmount
				+ ", otherPayerId=" + otherPayerId + ", assignBenefitsFlag=" + assignBenefitsFlag
				+ ", billingProviderNPI=" + billingProviderNPI + "]";
	}

}