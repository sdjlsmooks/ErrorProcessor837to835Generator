
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.util.Precision;
import org.xml.sax.SAXException;

import solutions.health.X12HCCProfessional.X12_835.X12835;
import solutions.health.X12HCCProfessional.X12_835.X12835Factory;
import solutions.health.X12HCCProfessional.X12_837.Loop2000ABillingProviderDetail;
import solutions.health.X12HCCProfessional.X12_837.Loop2000BSubscriberHierarchicalLevel;
import solutions.health.X12HCCProfessional.X12_837.Loop2010BBPayerName;
import solutions.health.X12HCCProfessional.X12_837.ServiceDate;

/**
 * @author DavidL
 *
 */
public class ErrorProcessor837to835Generator {

	private String original837FileName = "";
	private String encounterFileName = "";
	private String errorFilename = "";
	private String logFilename = "";
	private String rejectionFilename = "";
	private String output835Directory = "";

	private String checkNumber = "";
	
	/**
	 * Exception Report - This is the report of Encounter IDs that will require
	 * human intervention (lack of detail in 837 file, etc. Program cannot resolve
	 * error
	 */
	PrintStream exceptionReport;

	/**
	 * Contents of the 837 - Entries - (BeaconEncounterID,
	 * Loop2000ABillingProviderDetail)
	 */
	private HashMap<String, Loop2000ABillingProviderDetail> original837Detail = new HashMap<>();

	/**
	 * Map - entries (BeaconEncounterID, EncounterLineItem)
	 */
	private HashMap<String, EncounterLineItem> encFileEncouters = new HashMap<>();

	/**
	 * The contents of the ENC_err.txt file - used in creation of DENIAL 835s NOT
	 * IMPLEMENTED YET
	 */
	private ArrayList<ErrorLineItem> errorList = new ArrayList<>();

	/**
	 * Map - entries (BeaconEncounterID, ErrorLineItem) - used in creation of DENIAL
	 * 835s NOT IMPLEMENTED YET
	 */
	private HashMap<String, ErrorLineItem> errorMap = new HashMap<>();

	/**
	 * From MockingBird - Claims that were rejected by Beacon. // RETRIEVED FROM
	 * DATABASE - For use later (there may be problems in the requirements) I am
	 * generating 835s that are perfectly valid according to the rules of the 835,
	 * but not posting, more processing needed.
	 */
	private HashMap<String, ProcessedClaim> rejectedClaims = new HashMap<>();

	/**
	 * From Mockingbird - Claims that were accepted by Beacon. // RETRIEVED FROM
	 * DATABASE - For use later (there may be problems in the requirements) I am
	 * generating 835s that are perfectly valid according to the rules of the 835,
	 * but not posting, more processing needed.
	 */
	private HashMap<String, ProcessedClaim> acceptedClaims = new HashMap<>();

	/**
	 * From FindRejectionsUtility - Read in the rejected claims that were found.
	 * This is needed due to the fact it was discovered later, what is read from the
	 * database does not necessary have all the information needed. The last file
	 * does not have the fatal rejections. Those have to be found by an external
	 * utility and passed in. These are those rejections.
	 * 
	 */
	private HashMap<String, FatalRejection> fatalRejections = new HashMap<>();

	/**
	 * For Report on Claims Processed
	 */
	private Set<String> fullyProcessedAcceptedClaims = new HashSet<>();

	/**
	 * For Report on Claims Processed
	 */
	private Set<String> fullyProcessedRejectedClaims = new HashSet<>();

	/**
	 * For report on unprocessed claims
	 */
	private Set<String> unprocessedNoX12Mapping = new HashSet<>();

	/**
	 * For report on unprocessed claims
	 */
	private Set<String> unprocessedInsufficientInformation = new HashSet<>();

	private String aValidBeaconEncounterID = null; // Needed to obtain Payer/Payee information later.

	/**
	 * Maps Beacon Error Codes to X12 Error Codes Used in creation of the denial
	 * 835s (send in the X12 equivalent of the Beacon error code. Used for
	 * generating DENIAL 835s NOT IMPLEMENTED YET
	 */
	private HashMap<String, String> beaconToX12ErrorMap = new HashMap<>();

	// Used to generate All 835s in the output directory.
	private X12835Factory x12835Factory = null;

	// Helper to generate filename in user-friendly way.
	private String currentMemberNumber = null;

	// Helper to generate filename in user-friendly way.
	Integer todaysDateAsControlNumber = null;

	/**
	 * Needed for X12 - the SE segment is expecting the total number of segments in
	 * the transaction NOTE - It would be ***MUCH BETTER*** to have this be tracked
	 * in the X12_835 class itself. However that is generated code. One possible
	 * solution is to subclass or write an adapter class that tracks it for you, but
	 * time limitations, changing requirements and priorities as well as large
	 * number of accessors needed prevented this on the initial run
	 */
	int totalNumberOfSegments = 0; // Need to keep track of the for the SE segment

	// Helper to generate filenames in a more user-friendly way.
	String nextGenEncounterID = null;

	public Set<String> getFullyProcessedAcceptedClaims() {
		return fullyProcessedAcceptedClaims;
	}

	public void setFullyProcessedAcceptedClaims(Set<String> fullyProcessedAcceptedClaims) {
		this.fullyProcessedAcceptedClaims = fullyProcessedAcceptedClaims;
	}

	public Set<String> getFullyProcessedRejectedClaims() {
		return fullyProcessedRejectedClaims;
	}

	public void setFullyProcessedRejectedClaims(Set<String> fullyProcessedRejectedClaims) {
		this.fullyProcessedRejectedClaims = fullyProcessedRejectedClaims;
	}

	public String getOriginal837FileName() {
		return original837FileName;
	}

	public void setOriginal837FileName(String original837FileName) {
		this.original837FileName = original837FileName;
	}

	public String getEncounterFileName() {
		return encounterFileName;
	}

	public void setEncounterFileName(String encounterFileName) {
		this.encounterFileName = encounterFileName;
	}

	public String getErrorFilename() {
		return errorFilename;
	}

	public void setErrorFilename(String errorFilename) {
		this.errorFilename = errorFilename;
	}

	public String getLogFilename() {
		return logFilename;
	}

	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}

	public String getOutput835Filename() {
		return output835Directory;
	}

	public void setOutput835Filename(String output835Filename) {
		this.output835Directory = output835Filename;
	}

	/**
	 * Returns the encounter with that number (used in error processing), null if
	 * not found
	 * 
	 * @param encounterNum
	 * @return The encounter with that encounter number or NULL if not found.
	 */
	public EncounterLineItem getEncounterNumber(int encounterNum) {
		for (EncounterLineItem e : encFileEncouters.values()) {
			if (e.getRecordNumber() == encounterNum) {
				return e;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public ErrorProcessor837to835Generator(String[] args) {

		original837FileName = args[0];
		encounterFileName = args[1];
		errorFilename = args[2];
		logFilename = args[3];
		rejectionFilename = args[4];
		output835Directory = args[5];

		try {
			exceptionReport = new PrintStream(new File(output835Directory + "/ExceptionReport.txt"));
			x12835Factory = X12835Factory.getInstance();
		} catch (IOException | SAXException e) {

			e.printStackTrace();
		}
		System.out.println("Original 837 Filename = '" + original837FileName + "'");
		System.out.println("Encounter Filename = '" + encounterFileName + "'");
		System.out.println("Beacon Error Filename = '" + errorFilename + "'");
		// Note Log may be used in creation of Denials (NOT IMPLEMENTED), not currently
		// used
		System.out.println("Beacon Log Filename = '" + logFilename + "'");
		System.out.println("Output 835 Directory = '" + output835Directory + "'");

	}

	@Override
	public void finalize() {
		exceptionReport.close();
	}

	public void readInBeaconToX12Mapping() {
		BeaconToX12DAO btx12DAO = new BeaconToX12DAO();

		beaconToX12ErrorMap = btx12DAO.readInBeaconToX12Mapping();
	}

	public void readInOriginal837Information() {
		Original837DAO the837DAO = new Original837DAO(original837FileName);
		original837Detail = the837DAO.getOriginal837Detail();
		for (String original837DetailEncounter : original837Detail.keySet()) {
			System.out.println("Original 837 Detail Beacon Encounter Number: '" + original837DetailEncounter + "'");
		}
		System.out.println("Original 837 Detail keySet.size() = " + original837Detail.keySet().size());
	}

	public void readInEncounterFile() {
		EncounterLineItemDAO eliDAO = new EncounterLineItemDAO(encounterFileName);
		encFileEncouters = eliDAO.getAcceptedEncounterMap();
	}

	public void readInErrorFile() {
		ErrorLineItemDAO errorLIDaO = new ErrorLineItemDAO(errorFilename);
		errorList = errorLIDaO.getErrorList();
		errorMap = errorLIDaO.getErrorMap();

		// Fill in gaps (sometimes the Beacon Error file doesn't have the encounter
		// number so retrieve it based on the record number in the file
		int LENGTH_OF_BEACON_ENCOUNTER_ID = 12;
		for (ErrorLineItem eli : errorList) {
			EncounterLineItem encounterFromENCFile = getClaimForRecordNumber(eli.getRecordNum());
			if ((eli.getEncounterId() == null) || (eli.getEncounterId().length() < LENGTH_OF_BEACON_ENCOUNTER_ID)) {
				eli.setEncounterId(encounterFromENCFile.getEncounterID());
			}
		}
	}

	/**
	 * Helper to retrieve encounter ID. The error file does not always have the
	 * encounter ID, need to retrieve it from the original file.
	 * 
	 * @param recordNum The (line) number of the record in the file (not including
	 *                  header line)
	 * @return The entire ENC record entry for that record number.
	 */
	private EncounterLineItem getClaimForRecordNumber(int recordNum) {
		EncounterLineItem retVal = null;
		for (EncounterLineItem eli : encFileEncouters.values()) {
			if (eli.getRecordNumber() == recordNum) {
				retVal = eli;
				break;
			}
		}
		return retVal;
	}

	/**
	 * Helper to retrieve errors for a particular claim (encounter)
	 * 
	 * @param encounterID - The Encounter IT to search for
	 * @return The list of errors for that encounter ID. NULL if none found
	 */
	public List<ErrorLineItem> getErrorsForEncounterID(String encounterID) {
		ArrayList<ErrorLineItem> retVal = new ArrayList<>();
		;

		for (ErrorLineItem eli : errorList) {
			if (eli.getEncounterId().equals(encounterID)) {
				retVal.add(eli);
			}
		}
		return retVal;
	}

	public void findProcessedClaims() {
		ProcessedClaimsDAO pcDAO = new ProcessedClaimsDAO();
		acceptedClaims = pcDAO.getAcceptedClaims(encounterFileName);
		System.out.println("DB - Accepted Claims Size: " + acceptedClaims.size());
		rejectedClaims = pcDAO.getRejectedClaims(encounterFileName);
		System.out.println("DB - Rejected Claims Size: " + rejectedClaims.size());
		System.out.println("DB - Found Processed Claims: Accepted = " + acceptedClaims.size() + " Rejected = "
				+ rejectedClaims.size());
	}

	/**
	 * Generate the actual 835 for the particular encounter ID. The original 837 has
	 * all claims in one 837 file. For testing purposes, put 1 claim per file for
	 * now.
	 * 
	 * @param beaconEncounterID
	 * @return The smooks representation of the 835. NOTE - There is a bug in smooks
	 *         where it does not generate the correct ISA segment due to the ISA
	 *         having self-defining delimiters. so there is a workaround later in
	 *         the code to compensate for this.
	 * @throws ParseException
	 */
	private X12835 generateAcceptedClaim_X12835(String beaconEncounterID) throws ParseException {
		X12835 accepted835 = new X12835();

		// Used to populate the Payer/Payee Information
		Loop2000ABillingProviderDetail payerPayeeBillingDetail = original837Detail.get(beaconEncounterID);

		// ISA Segment
		accepted835.setInterchange(new solutions.health.X12HCCProfessional.X12_835.Interchange());
		solutions.health.X12HCCProfessional.X12_835.InterchangeControlHeader isa = new solutions.health.X12HCCProfessional.X12_835.InterchangeControlHeader();

		isa.setAuthorInfoQualifier("00");
		isa.setAuthorInformation("          "); // Number of spaces exactly matches ISA segment definition
		isa.setSecurityInfoQualifier("00");
		isa.setSecurityInformation("          ");
		isa.setSenderIDQualifier("ZZ");
		isa.setSenderID("TX             ");
		isa.setReceiverIDQualifier("ZZ");
		isa.setReceiverID("COMEDASSISTPROG");
		isa.setInterchangeDate(new Date()); // Default to today
		isa.setInterchangeTime(new Date()); // Default to "now"
		isa.setRepetitionSeparator("^");
		isa.setInterchangeVersionNumber("00501");
		isa.setInterchangeControlNumber(35);
		isa.setAcknowledgementRequested("1");
		isa.setInterchangeUsageIndicator("P"); // Per implementation guide - "P" ==> Production Data
		isa.setComponentSeparator("\u1200"); // work around bug in Smooks 1.7 (doesn't output colon on EDI write")
												// Use unicode \u1200 as a placeholder, do a string replace on toEDI
												// output
												// This is due to the self-defining delimeters in X12, highly
												// non-standard
												// industry.
		++totalNumberOfSegments;
		accepted835.getInterchange().setInterchangeControlHeader(isa);

		// GS Segment
		solutions.health.X12HCCProfessional.X12_835.FunctionalGroup fg = new solutions.health.X12HCCProfessional.X12_835.FunctionalGroup();
		solutions.health.X12HCCProfessional.X12_835.FunctionalGroupHeader gs = new solutions.health.X12HCCProfessional.X12_835.FunctionalGroupHeader();
		int groupControlNumber = 98765;

		gs.setFunctionalIDCode("HP");
		gs.setApplicationSenderCode("COMEDASSISTPROG");
		gs.setApplicationReceiverCode("1953012");
		gs.setDate(new Date()); // Default to today
		gs.setTime(new Date()); // Default to "now"

		gs.setGroupControlNumber(groupControlNumber); // Use dummy number for now.
		gs.setAgencyResponsibleCode("X");
		gs.setVersionReleaseIDCode("005010X221A1");

		++totalNumberOfSegments;
		fg.setFunctionalGroupHeader(gs);

		// START Transaction Set (SE/SE Segments)
		solutions.health.X12HCCProfessional.X12_835.TransactionSet transactionSet = new solutions.health.X12HCCProfessional.X12_835.TransactionSet();

		// ST Segment
		solutions.health.X12HCCProfessional.X12_835.TransactionSetHeader st = new solutions.health.X12HCCProfessional.X12_835.TransactionSetHeader();
		st.setTransactionSetIDCode("835");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd"); // use dummy control number (todays date)
		Date todaysDate = new Date();
		String todaysDateStr = dateFormatter.format(todaysDate);
		todaysDateAsControlNumber = Integer.parseInt(todaysDateStr);
		st.setTransactionSetControlNumber(todaysDateAsControlNumber);

		++totalNumberOfSegments;
		transactionSet.setTransactionSetHeader(st);

		Double totalPaymentForThis835 = 0.0; // BPR02 = SUM(CLP04) - need to keep track of total as we build the 835.
		// BPR Segment
		solutions.health.X12HCCProfessional.X12_835.FinancialInformation bpr = new solutions.health.X12HCCProfessional.X12_835.FinancialInformation();
		bpr.setTransactionHandlingCode("I");
		bpr.setMonetaryAmount(Double.toString(Precision.round(totalPaymentForThis835, 2)));
		bpr.setCreditDebitFlag("C");
		bpr.setPaymentMethodCode("NON");

		// Comment out of 5/24/2022 - Per Terri, hard code BPR16 (Check Issue Date/EFT
		// Effective Date) for 1 run
		// 5/24/2022 - Per Terri, hard code the Check Issue Date (EFT Effective Date)
		// for 1 particular run
		// Re-enabled old code on 6/20/2022
		//Date hardCodeDate = dateFormatter.parse("20210826");
		//bpr.setDate(hardCodeDate); // Default to today;

		// re-enabled code on 06/20/2022 - 
		bpr.setDate(todaysDate); // Default to today;
		++totalNumberOfSegments;
		transactionSet.setFinancialInformation(bpr);

		// TRN Segment
		solutions.health.X12HCCProfessional.X12_835.ReAssociationTraceNumber trn = new solutions.health.X12HCCProfessional.X12_835.ReAssociationTraceNumber();
		trn.setTraceTypeCode("1"); // According to implementation guide '1' -> 'Current Transaction Trace Number'
		//Integer todaysDateAsTRNNumber = Integer.parseInt(todaysDateStr);
		//trn.setReferenceIdentification(todaysDateAsTRNNumber.toString()); // Dummy value - according to IG - "Check or
		// EFT Trace Number" - Use Todays Date
		
		// PER BECKY (Beatriz Granillo) - 08/04/2022 - Set TRN02 to match the output file name.
		trn.setReferenceIdentification(checkNumber);
		trn.setOriginatingCompanyIdentifier("81-1725341"); // Hard coded value from Terri in requirements analysis
		++totalNumberOfSegments;
		transactionSet.setReAssociationTraceNumber(trn);

		// DTM*405 -> Production Date segment
		solutions.health.X12HCCProfessional.X12_835.ProductionDate dtm405 = new solutions.health.X12HCCProfessional.X12_835.ProductionDate();
		dtm405.setDateTimeQualifier("405");
		dtm405.setDate(new Date()); // Default to today
		++totalNumberOfSegments;
		transactionSet.setProductionDate(dtm405);

		// Payer Identification (Loop 1000A)
		solutions.health.X12HCCProfessional.X12_835.Loop1000APayerIdentification loop1000APayerIdentification = new solutions.health.X12HCCProfessional.X12_835.Loop1000APayerIdentification();
		// N1 Segment
		solutions.health.X12HCCProfessional.X12_835.PayerIdentification payerID = new solutions.health.X12HCCProfessional.X12_835.PayerIdentification();

		// Needed to get Payer Identification Information
		Loop2000BSubscriberHierarchicalLevel loop2000bSubscriberHierarchicalLevel = payerPayeeBillingDetail
				.getLoop2000BSubscriberHierarchicalLevel().get(0);
		Loop2010BBPayerName loop2010bbPayerName = loop2000bSubscriberHierarchicalLevel.getLoop2010BBPayerName();
		payerID.setName(loop2010bbPayerName.getPayerName().getLastName());
		payerID.setEntityIDCode("PR"); // By Implementation Guide - PR = Payer
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerIdentification(payerID);

		// N3 segment
		solutions.health.X12HCCProfessional.X12_835.PayerAddress payerAddress = new solutions.health.X12HCCProfessional.X12_835.PayerAddress();
		payerAddress.setAddressInformation(loop2010bbPayerName.getPayerAddress().getAddressInformation());
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerAddress(payerAddress);

		// N4 segment
		solutions.health.X12HCCProfessional.X12_835.PayerCityStateZipCode payerCityStateZip = new solutions.health.X12HCCProfessional.X12_835.PayerCityStateZipCode();
		payerCityStateZip.setCity(loop2010bbPayerName.getPayerCityStateZipCode().getCity());
		payerCityStateZip.setState(loop2010bbPayerName.getPayerCityStateZipCode().getState());
		payerCityStateZip.setPostalCode(loop2010bbPayerName.getPayerCityStateZipCode().getPostalCode());
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerCityStateZipCode(payerCityStateZip);

		// PER*BL - Technical Contact Segment
		solutions.health.X12HCCProfessional.X12_835.PayerTechnicalContactInformation payerTechnicalContactInfo = new solutions.health.X12HCCProfessional.X12_835.PayerTechnicalContactInformation();
		payerTechnicalContactInfo.setContactFunctionCode("BL"); // BL ==> Implementation guide says PER*BL is "Technical
																// Department"
		payerTechnicalContactInfo.setName("David Lloyd");
		payerTechnicalContactInfo.setCommunicationNumberQualifier("EM");
		payerTechnicalContactInfo.setCommunicationNumber("davidl@health.solutions");
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerTechnicalContactInformation(payerTechnicalContactInfo);

		transactionSet.setLoop1000APayerIdentification(loop1000APayerIdentification);

		// Payee Identification (Loop 1000B)
		solutions.health.X12HCCProfessional.X12_835.Loop1000BPayeeIdentification loop1000BPayeeIdentification = new solutions.health.X12HCCProfessional.X12_835.Loop1000BPayeeIdentification();
		// N1 Segment
		solutions.health.X12HCCProfessional.X12_835.PayeeIdentification payeeID = new solutions.health.X12HCCProfessional.X12_835.PayeeIdentification();
		payeeID.setName(payerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpName().get(0).getLastName());
		payeeID.setEntityIDCode("PE"); // By Implementation Guide - PE = Payer
		payeeID.setIdCodeQualifier(
				payerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpName().get(0).getIdCodeQualifier());
		payeeID.setIdCode(payerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpName().get(0).getIdCode());
		++totalNumberOfSegments;
		loop1000BPayeeIdentification.setPayeeIdentification(payeeID);

		// N3 segment
		solutions.health.X12HCCProfessional.X12_835.PayeeAddress payeeAddress = new solutions.health.X12HCCProfessional.X12_835.PayeeAddress();
		payeeAddress.setAddressInformation(payerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpAddress()
				.getAdditionalAddressInformation());
		++totalNumberOfSegments;
		loop1000BPayeeIdentification.setPayeeAddress(payeeAddress);

		// N4 segment
		solutions.health.X12HCCProfessional.X12_835.PayeeCityStateZipCode payeeCityStateZip = new solutions.health.X12HCCProfessional.X12_835.PayeeCityStateZipCode();
		payeeCityStateZip
				.setCity(payerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpCityStateZipCode().getCity());
		payeeCityStateZip.setState(
				payerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpCityStateZipCode().getState());
		payeeCityStateZip.setPostalCode(
				payerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpCityStateZipCode().getPostalCode());
		++totalNumberOfSegments;
		loop1000BPayeeIdentification.setPayeeCityStateZipCode(payeeCityStateZip);

		// Add the Loop 1000B (N1/N3/N4 to the final output)
		transactionSet.setLoop1000BPayeeIdentification(loop1000BPayeeIdentification);

		// HERE IS WHERE YOU NEED TO LOOP THROUGH ALL ORIGINAL 837 Loop 2000 Detail
		// informations
		int headerNumberCounter = 1;
		transactionSet.setLoop2000Detail(new ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2000Detail>());
		System.out.println("Generate Accepted: keySet.size() = " + acceptedClaims.keySet().size());
		for (String acceptedClaim : acceptedClaims.keySet()) {
			// Here is the actual CLAIM - CLP and SVC segment information

			Loop2000ABillingProviderDetail acceptedClaimBillingDetail = original837Detail.get(acceptedClaim);
			if (acceptedClaimBillingDetail == null) {
				System.out.println("NO DETAIL FOR ACCEPTED: " + acceptedClaim + " moving on");
				exceptionReport.println("Exception - Accepted Claim: " + acceptedClaim);
				unprocessedInsufficientInformation.add(acceptedClaim);
				continue; // No detail for the accepted claim, move on.
			} else {
				System.out.println("Processing Accepted Encounter ID: '" + acceptedClaim + "'");
			}

			Loop2000BSubscriberHierarchicalLevel acLoop2000bSubscriberHierarchicalLevel = acceptedClaimBillingDetail
					.getLoop2000BSubscriberHierarchicalLevel().get(0);
			solutions.health.X12HCCProfessional.X12_835.Loop2000Detail loop2000Detail = new solutions.health.X12HCCProfessional.X12_835.Loop2000Detail();
			transactionSet.getLoop2000Detail().add(loop2000Detail);

			// LX segment (header)
			solutions.health.X12HCCProfessional.X12_835.HeaderNumber headerNumber = new solutions.health.X12HCCProfessional.X12_835.HeaderNumber();
			headerNumber.setAssignedNumber(headerNumberCounter++);

			++totalNumberOfSegments;
			loop2000Detail.setHeaderNumber(new ArrayList<solutions.health.X12HCCProfessional.X12_835.HeaderNumber>());
			loop2000Detail.getHeaderNumber().add(headerNumber);

			// CLP SEGMENT
			// The actual Claim information (Claim Payment)
			solutions.health.X12HCCProfessional.X12_835.ClaimPaymentInformation claimPaymentInfo = new solutions.health.X12HCCProfessional.X12_835.ClaimPaymentInformation();

			// This is the ***NEXTGEN Counter ID***
			nextGenEncounterID = acLoop2000bSubscriberHierarchicalLevel.getLoop2000CPatientHierarchicalLevel().get(0)
					.getLoop2300ClaimInformation().get(0).getClaimInformation().getClaimSubmitterIdentifier();
			claimPaymentInfo.setClaimSubmitIdentifierWithFacility(nextGenEncounterID);
			claimPaymentInfo.setClaimStatusCode("1");

			// HERE IS WHERE the BRP and CLP segment need to sum up according
			// to the balancing rules of the 835 Implementation Guide.
			String claimPaymentAmountStr = acLoop2000bSubscriberHierarchicalLevel.getLoop2000CPatientHierarchicalLevel()
					.get(0).getLoop2300ClaimInformation().get(0).getClaimInformation().getMonetaryAmount();
			Double claimPaymentAmount = Double.parseDouble(claimPaymentAmountStr);
			totalPaymentForThis835 += claimPaymentAmount;
			bpr.setMonetaryAmount(Double.toString(Precision.round(totalPaymentForThis835, 2)));
			claimPaymentInfo.setMonetaryAmount(Double.toString(claimPaymentAmount));
			claimPaymentInfo.setMonetaryAmount2(Double.toString(claimPaymentAmount));
			claimPaymentInfo.setMonetaryAmount3(Double.toString(0));
			claimPaymentInfo.setClaimFileIndicatorCode("MC"); // According to Implementation guide MC==Medicaid
			claimPaymentInfo.setReferenceIdentification("1");
			claimPaymentInfo.setFacilityCode("53");

			// Add the loop to the final output.
			++totalNumberOfSegments;
			ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation> loop2100ClaimPaymentInformation = new ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation>();
			solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation loop2100 = new solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation();
			loop2100.setClaimPaymentInformation(claimPaymentInfo);
			loop2100ClaimPaymentInformation.add(loop2100);

			loop2000Detail.setLoop2100ClaimPaymentInformation(loop2100ClaimPaymentInformation);

			// NM1*QC Segment - Patient Name
			solutions.health.X12HCCProfessional.X12_835.PatientName patientName = new solutions.health.X12HCCProfessional.X12_835.PatientName();
			patientName.setEntityIDCode("QC");// Implementation Guide QC==>Patient Name
			patientName.setEntityTypeQualifier("1"); // Implementation Guide 1==>Person
			patientName.setLastName(acLoop2000bSubscriberHierarchicalLevel.getLoop2010BASubscriberName()
					.getPatientName().getLastName());
			patientName.setFirstName(acLoop2000bSubscriberHierarchicalLevel.getLoop2010BASubscriberName()
					.getPatientName().getFirstName());
			patientName.setMiddleName(acLoop2000bSubscriberHierarchicalLevel.getLoop2010BASubscriberName()
					.getPatientName().getMiddleName());
			patientName.setIdCodeQualifier(acLoop2000bSubscriberHierarchicalLevel.getLoop2010BASubscriberName()
					.getPatientName().getIdCodeQualifier());
			currentMemberNumber = acLoop2000bSubscriberHierarchicalLevel.getLoop2010BASubscriberName().getPatientName()
					.getIdCode();
			patientName.setIdCode(currentMemberNumber);

			// Add to final output.
			++totalNumberOfSegments;
			loop2100.setPatientName(patientName);

			// Add the Service Line information (SVC and DTM Segments).
			solutions.health.X12HCCProfessional.X12_835.Loop2110SPI loop2110SPI = new solutions.health.X12HCCProfessional.X12_835.Loop2110SPI();

			// This is the SVC Segment
			solutions.health.X12HCCProfessional.X12_835.ClaimSPI claimSPI = new solutions.health.X12HCCProfessional.X12_835.ClaimSPI();
			claimSPI.setCompositeMPI(acLoop2000bSubscriberHierarchicalLevel.getLoop2000CPatientHierarchicalLevel()
					.get(0).getLoop2300ClaimInformation().get(0).getLoop2400ServiceLineNumber().get(0)
					.getProfessionalService().getCompMedProcedID());
			claimSPI.setSubmittedServiceCharge(acLoop2000bSubscriberHierarchicalLevel
					.getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
					.getLoop2400ServiceLineNumber().get(0).getProfessionalService().getMonetaryAmount());
			claimSPI.setAmountPaid(acLoop2000bSubscriberHierarchicalLevel.getLoop2000CPatientHierarchicalLevel().get(0)
					.getLoop2300ClaimInformation().get(0).getLoop2400ServiceLineNumber().get(0).getProfessionalService()
					.getMonetaryAmount());

			++totalNumberOfSegments;
			loop2110SPI.setClaimSPI(claimSPI);
			ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2110SPI> loop2110SPIArray = new ArrayList<>();
			loop2110SPIArray.add(loop2110SPI);
			loop2100.setLoop2110SPI(loop2110SPIArray);

			// This is the DTM*472 segment
			solutions.health.X12HCCProfessional.X12_835.ServiceDate serviceDate = new solutions.health.X12HCCProfessional.X12_835.ServiceDate();
			serviceDate.setDateTimeQualifier("472"); // Implementation Guide 472-->Service Date
			ServiceDate original837ServiceDate = acLoop2000bSubscriberHierarchicalLevel
					.getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
					.getLoop2400ServiceLineNumber().get(0).getServiceDate();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String original837DateTimePeriod = original837ServiceDate.getDateTimePeriod();
			Date parseOriginal837Date = formatter.parse(original837DateTimePeriod);
			serviceDate.setDate(parseOriginal837Date);
			loop2110SPI.setServiceDate(new ArrayList<>());
			++totalNumberOfSegments;
			loop2110SPI.getServiceDate().add(serviceDate);
			fullyProcessedAcceptedClaims.add(acceptedClaim);
		} // END FOR LOOP

		// HERE IS WHERE YOU WOULD PUT IN THE REJECTED CLAIMS

		// SE Segment
		solutions.health.X12HCCProfessional.X12_835.TransactionSetTrailer se = new solutions.health.X12HCCProfessional.X12_835.TransactionSetTrailer();
		se.setNumberOfIncludedSegments(0); // calculated later, use placeholder here
		se.setTransactionSetControlNumber(todaysDateAsControlNumber);

		++totalNumberOfSegments;
		transactionSet.setTransactionSetTrailer(se);

		fg.setTransactionSet(transactionSet);

		// GE Segment
		solutions.health.X12HCCProfessional.X12_835.FunctionalGroupTrailer ge = new solutions.health.X12HCCProfessional.X12_835.FunctionalGroupTrailer();
		ge.setNumberIncludedTransactionSets("1");
		ge.setGroupControlNumber(groupControlNumber);

		++totalNumberOfSegments;
		fg.setFunctionalGroupTrailer(ge);

		accepted835.getInterchange().setFunctionalGroup(fg);

		// IEA Segment
		solutions.health.X12HCCProfessional.X12_835.InterchangeControlTrailer iea = new solutions.health.X12HCCProfessional.X12_835.InterchangeControlTrailer();
		iea.setNumberIncludedFunctionalGroups("1");
		iea.setInterchangeControlNumber(35);
		++totalNumberOfSegments;
		se.setNumberOfIncludedSegments(totalNumberOfSegments - 4);
		accepted835.getInterchange().setInterchangeControlTrailer(iea);
		totalNumberOfSegments = 0;
		return accepted835;
	}

	private X12835 generateRejectedClaim_X12835(String rejectedBeaconEncounterID,
			List<ErrorLineItem> errorsForEncounter) throws ParseException {
		X12835 rejected835 = new X12835();

		Loop2000ABillingProviderDetail rejectedClaimPayerPayeeBillingDetail = original837Detail
				.get(rejectedBeaconEncounterID);

		// ISA Segment
		rejected835.setInterchange(new solutions.health.X12HCCProfessional.X12_835.Interchange());
		solutions.health.X12HCCProfessional.X12_835.InterchangeControlHeader isa = new solutions.health.X12HCCProfessional.X12_835.InterchangeControlHeader();

		isa.setAuthorInfoQualifier("00");
		isa.setAuthorInformation("          ");
		isa.setSecurityInfoQualifier("00");
		isa.setSecurityInformation("          ");
		isa.setSenderIDQualifier("ZZ");
		isa.setSenderID("TX             ");
		isa.setReceiverIDQualifier("ZZ");
		isa.setReceiverID("COMEDASSISTPROG");
		isa.setInterchangeDate(new Date()); // Default to today
		isa.setInterchangeTime(new Date()); // Default to "now"
		isa.setRepetitionSeparator("^");
		isa.setInterchangeVersionNumber("00501");
		isa.setInterchangeControlNumber(35);
		isa.setAcknowledgementRequested("1");
		isa.setInterchangeUsageIndicator("P"); // "P" ==> Production Data, need to set to P to complete
												// testing the way the test system is set up.

		isa.setComponentSeparator("\u1200"); // work around bug in Smooks 1.7 (doesn't output colon on EDI write")
												// Use unicode \u1200 as a placeholder, do a string replace on toEDI
												// output
												// This is due to the self-defining delimeters in X12, highly
												// non-standard
												// industry.
		++totalNumberOfSegments;
		rejected835.getInterchange().setInterchangeControlHeader(isa);

		// GS Segment
		solutions.health.X12HCCProfessional.X12_835.FunctionalGroup fg = new solutions.health.X12HCCProfessional.X12_835.FunctionalGroup();
		solutions.health.X12HCCProfessional.X12_835.FunctionalGroupHeader gs = new solutions.health.X12HCCProfessional.X12_835.FunctionalGroupHeader();
		int groupControlNumber = 98765;

		gs.setFunctionalIDCode("HP");
		gs.setApplicationSenderCode("COMEDASSISTPROG");
		gs.setApplicationReceiverCode("1953012");
		gs.setDate(new Date()); // Default to today
		gs.setTime(new Date()); // Default to "now"

		gs.setGroupControlNumber(groupControlNumber); // Use dummy number for now.
		gs.setAgencyResponsibleCode("X");
		gs.setVersionReleaseIDCode("005010X221A1");

		++totalNumberOfSegments;
		fg.setFunctionalGroupHeader(gs);

		// Transaction Set (ST/SE Group - ST Segment)
		solutions.health.X12HCCProfessional.X12_835.TransactionSet transactionSet = new solutions.health.X12HCCProfessional.X12_835.TransactionSet();

		// ST Segment
		solutions.health.X12HCCProfessional.X12_835.TransactionSetHeader st = new solutions.health.X12HCCProfessional.X12_835.TransactionSetHeader();
		st.setTransactionSetIDCode("835");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd"); // use dummy control number (todays date)
		String todaysDate = dateFormatter.format(new Date());
		todaysDateAsControlNumber = Integer.parseInt(todaysDate);
		st.setTransactionSetControlNumber(todaysDateAsControlNumber);

		++totalNumberOfSegments;
		transactionSet.setTransactionSetHeader(st);

		Double totalPaymentForThis835 = 0.0; // BPR02 = SUM(CLP04) - need to keep track of total as we build the 835.
		// BPR Segment
		solutions.health.X12HCCProfessional.X12_835.FinancialInformation bpr = new solutions.health.X12HCCProfessional.X12_835.FinancialInformation();
		bpr.setTransactionHandlingCode("I");
		bpr.setMonetaryAmount(Double.toString(totalPaymentForThis835));
		bpr.setCreditDebitFlag("C");
		bpr.setPaymentMethodCode("NON");

		// 5/24/2022 - Per Terri, hard code the Check Issue Date (EFT Effective Date)
		// for 1 particular run
		Date hardCodeDate = dateFormatter.parse("20210826");
		bpr.setDate(hardCodeDate); // Default to today;
		// Original code - commented out on 5/24/2022 for hard-coding run
		// bpr.setDate(new Date()); // Default to today;

		++totalNumberOfSegments;
		transactionSet.setFinancialInformation(bpr);

		// TRN Segment
		solutions.health.X12HCCProfessional.X12_835.ReAssociationTraceNumber trn = new solutions.health.X12HCCProfessional.X12_835.ReAssociationTraceNumber();
		trn.setTraceTypeCode("1"); // According to implementation guide '1' -> 'Current Transaction Trace Number'
		Integer todaysDateAsTRNNumber = Integer.parseInt(todaysDate);
		trn.setReferenceIdentification(todaysDateAsTRNNumber.toString()); // Dummy value - according to IG - "Check or
																			// EFT Trace Number" - Use Todays Date
		trn.setOriginatingCompanyIdentifier("81-1725341"); // Hard coded value from Terri in requirements analysis
		++totalNumberOfSegments;
		transactionSet.setReAssociationTraceNumber(trn);

		// DTM*405 -> Production Date segment
		solutions.health.X12HCCProfessional.X12_835.ProductionDate dtm405 = new solutions.health.X12HCCProfessional.X12_835.ProductionDate();
		dtm405.setDateTimeQualifier("405");
		dtm405.setDate(new Date()); // Default to today
		++totalNumberOfSegments;
		transactionSet.setProductionDate(dtm405);

		// Payer Identification (Loop 1000A)
		solutions.health.X12HCCProfessional.X12_835.Loop1000APayerIdentification loop1000APayerIdentification = new solutions.health.X12HCCProfessional.X12_835.Loop1000APayerIdentification();
		// N1 Segment
		solutions.health.X12HCCProfessional.X12_835.PayerIdentification payerID = new solutions.health.X12HCCProfessional.X12_835.PayerIdentification();
		payerID.setName(rejectedClaimPayerPayeeBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
				.getLoop2010BBPayerName().getPayerName().getLastName());
		payerID.setEntityIDCode("PR"); // By Implementation Guide - PR = Payer
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerIdentification(payerID);

		// N3 segment
		solutions.health.X12HCCProfessional.X12_835.PayerAddress payerAddress = new solutions.health.X12HCCProfessional.X12_835.PayerAddress();
		payerAddress
				.setAddressInformation(rejectedClaimPayerPayeeBillingDetail.getLoop2000BSubscriberHierarchicalLevel()
						.get(0).getLoop2010BBPayerName().getPayerAddress().getAddressInformation());
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerAddress(payerAddress);

		// N4 segment
		solutions.health.X12HCCProfessional.X12_835.PayerCityStateZipCode payerCityStateZip = new solutions.health.X12HCCProfessional.X12_835.PayerCityStateZipCode();
		payerCityStateZip.setCity(rejectedClaimPayerPayeeBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
				.getLoop2010BBPayerName().getPayerCityStateZipCode().getCity());
		payerCityStateZip.setState(rejectedClaimPayerPayeeBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
				.getLoop2010BBPayerName().getPayerCityStateZipCode().getState());
		payerCityStateZip.setPostalCode(rejectedClaimPayerPayeeBillingDetail.getLoop2000BSubscriberHierarchicalLevel()
				.get(0).getLoop2010BBPayerName().getPayerCityStateZipCode().getPostalCode());
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerCityStateZipCode(payerCityStateZip);

		// PER*BL - Technical Contact Segment
		solutions.health.X12HCCProfessional.X12_835.PayerTechnicalContactInformation payerTechnicalContactInfo = new solutions.health.X12HCCProfessional.X12_835.PayerTechnicalContactInformation();
		payerTechnicalContactInfo.setContactFunctionCode("BL"); // BL ==> Implementation guide says PER*BL is "Technical
																// Department"
		payerTechnicalContactInfo.setName("David Lloyd");
		payerTechnicalContactInfo.setCommunicationNumberQualifier("EM");
		payerTechnicalContactInfo.setCommunicationNumber("davidl@health.solutions");
		++totalNumberOfSegments;
		loop1000APayerIdentification.setPayerTechnicalContactInformation(payerTechnicalContactInfo);

		transactionSet.setLoop1000APayerIdentification(loop1000APayerIdentification);

		// Payee Identification (Loop 1000B)
		solutions.health.X12HCCProfessional.X12_835.Loop1000BPayeeIdentification loop1000BPayeeIdentification = new solutions.health.X12HCCProfessional.X12_835.Loop1000BPayeeIdentification();
		// N1 Segment
		solutions.health.X12HCCProfessional.X12_835.PayeeIdentification payeeID = new solutions.health.X12HCCProfessional.X12_835.PayeeIdentification();
		payeeID.setName(rejectedClaimPayerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpName().get(0)
				.getLastName());
		payeeID.setEntityIDCode("PE"); // By Implementation Guide - PE = Payer
		payeeID.setIdCodeQualifier(rejectedClaimPayerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpName()
				.get(0).getIdCodeQualifier());
		payeeID.setIdCode(
				rejectedClaimPayerPayeeBillingDetail.getLoop2010AABillingProviderName().getBpName().get(0).getIdCode());
		++totalNumberOfSegments;
		loop1000BPayeeIdentification.setPayeeIdentification(payeeID);

		// N3 segment
		solutions.health.X12HCCProfessional.X12_835.PayeeAddress payeeAddress = new solutions.health.X12HCCProfessional.X12_835.PayeeAddress();
		payeeAddress.setAddressInformation(rejectedClaimPayerPayeeBillingDetail.getLoop2010AABillingProviderName()
				.getBpAddress().getAdditionalAddressInformation());
		++totalNumberOfSegments;
		loop1000BPayeeIdentification.setPayeeAddress(payeeAddress);

		// N4 segment
		solutions.health.X12HCCProfessional.X12_835.PayeeCityStateZipCode payeeCityStateZip = new solutions.health.X12HCCProfessional.X12_835.PayeeCityStateZipCode();
		payeeCityStateZip.setCity(rejectedClaimPayerPayeeBillingDetail.getLoop2010AABillingProviderName()
				.getBpCityStateZipCode().getCity());
		payeeCityStateZip.setState(rejectedClaimPayerPayeeBillingDetail.getLoop2010AABillingProviderName()
				.getBpCityStateZipCode().getState());
		payeeCityStateZip.setPostalCode(rejectedClaimPayerPayeeBillingDetail.getLoop2010AABillingProviderName()
				.getBpCityStateZipCode().getPostalCode());
		++totalNumberOfSegments;
		loop1000BPayeeIdentification.setPayeeCityStateZipCode(payeeCityStateZip);

		// Add the Loop 1000B (N1/N3/N4 to the final output)
		transactionSet.setLoop1000BPayeeIdentification(loop1000BPayeeIdentification);

		// Here is the same for the REJECTED claims - Loop through ALL rejected
		// Encounters

		// Here is the actual CLAIM - CLP and SVC segment information
		int headerNumberCounter = 0;
		transactionSet.setLoop2000Detail(new ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2000Detail>());
		for (String rejectedClaim : fatalRejections.keySet()) {
			Loop2000ABillingProviderDetail rejectedClaimBillingDetail = original837Detail.get(rejectedClaim);
			solutions.health.X12HCCProfessional.X12_835.Loop2000Detail loop2000Detail = new solutions.health.X12HCCProfessional.X12_835.Loop2000Detail();
			if (rejectedClaimBillingDetail == null) {
				System.out.println("NO DETAIL FOR REJECTED: " + rejectedClaim + " moving on");
				exceptionReport.println("Exception - Rejected Claim: " + rejectedClaim);
				unprocessedInsufficientInformation.add(rejectedClaim);
				continue; // Sometimes the original 837 does not contain all 837 details, move on.
			} else {
				System.out.println("Processing Reject Encounter ID: '" + rejectedClaim + "'");
			}

			// LX segment (header)
			solutions.health.X12HCCProfessional.X12_835.HeaderNumber headerNumber = new solutions.health.X12HCCProfessional.X12_835.HeaderNumber();
			headerNumber.setAssignedNumber(++headerNumberCounter);

			++totalNumberOfSegments;
			loop2000Detail.setHeaderNumber(new ArrayList<>());
			loop2000Detail.getHeaderNumber().add(headerNumber);

			// CLP SEGMENT
			// The actual Claim information (Claim Payment)
			solutions.health.X12HCCProfessional.X12_835.ClaimPaymentInformation claimPaymentInfo = new solutions.health.X12HCCProfessional.X12_835.ClaimPaymentInformation();

			// This is the ***NEXTGEN Counter ID***
			nextGenEncounterID = rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
					.getClaimInformation().getClaimSubmitterIdentifier();
			claimPaymentInfo.setClaimSubmitIdentifierWithFacility(nextGenEncounterID);
			claimPaymentInfo.setClaimStatusCode("1");

			// HERE IS WHERE the BRP and CLP segment need to sum up according
			// to the balancing rules of the 835 Implementation Guide.
			String claimPaymentAmountStr = rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
					.getClaimInformation().getMonetaryAmount();
			Double claimPaymentAmount = Double.parseDouble(claimPaymentAmountStr);
			totalPaymentForThis835 += 0;
			bpr.setMonetaryAmount(Double.toString(totalPaymentForThis835));

			claimPaymentInfo.setMonetaryAmount(Double.toString(claimPaymentAmount)); // SUBMITTED CHARGES --> CLP03 ==
																						// Amount4 == 0 ALL ADJUSTMENTS
																						// ARE SERVICE LINE LEVEL
			claimPaymentInfo.setMonetaryAmount2(Double.toString(0)); // AMOUNT PAID == 0 --> How will this affect
																		// balancing? Amount5 = SUM(CAS Monetary
																		// Amounts) Amount4 - Amount == CLP04
			claimPaymentInfo.setMonetaryAmount3(Double.toString(0)); // SO SUM(CAS Monetary Amount) ==
																		// CLP03(claimPaymentAmount) == PATIENT
																		// RESPONSIBILITY
			claimPaymentInfo.setClaimFileIndicatorCode("MC"); // According to Implementation guide MC==Medicaid
			claimPaymentInfo.setReferenceIdentification("1");
			claimPaymentInfo.setFacilityCode("53");

			// CAS Segment
			solutions.health.X12HCCProfessional.X12_835.ClaimAdjustment casClaimAdjustment = new solutions.health.X12HCCProfessional.X12_835.ClaimAdjustment();
			casClaimAdjustment.setClaimAdjustmentGroupCode("CO"); // Per TERRI - all adjustments are CO
																	// Per Implementation GuidE CO==>Contractual
																	// Oblications
			// Add EACH error found in the error file
			// NOTE - ALL MONETARY ADJUSTMENTS ARE SERVICE LEVEL ADJUSTMENTS
			// NOTE - The CAS segment may need to be broken into multiple
			// segments if there are more than 6 errors found per claim.
			int errorNumber = 0;
			for (ErrorLineItem eli : errorsForEncounter) {
				String errorNum = eli.getErrorNum();
				System.out.println("Beacon Error Number: '" + errorNum + "'");
				String x12Error = beaconToX12ErrorMap.get(errorNum);
				if (x12Error == null) {
					System.out.println("CLP LEVEL: No X12 Mapping for: " + errorNum);
					continue; // There is not X12 Error Code to map to, move on.
				}
				System.out.println("CLP LEVEL REJECT PROCESSING ERROR: " + errorNum + " Maps to X12: " + x12Error);
				switch (errorNumber) {
				case 0:
					if (x12Error != null) {
						casClaimAdjustment.setClaimAdjustmentReasonCode(x12Error);
						casClaimAdjustment.setMonetaryAmount("0.0");
						casClaimAdjustment.setQuantity(1);
						++errorNumber;
					}
					break;

				case 1:
					if (x12Error != null) {
						casClaimAdjustment.setClaimAdjustmentReasonCode2(x12Error);
						casClaimAdjustment.setMonetaryAmount2("0.0");
						casClaimAdjustment.setQuantity2(1);
						++errorNumber;
					}
					break;

				case 2:
					if (x12Error != null) {
						casClaimAdjustment.setClaimAdjustmentReasonCode3(x12Error);
						casClaimAdjustment.setMonetaryAmount3("0.0");
						casClaimAdjustment.setQuantity3(1);
						++errorNumber;
					}
					break;

				case 3:
					if (x12Error != null) {
						casClaimAdjustment.setClaimAdjustmentReasonCode4(x12Error);
						casClaimAdjustment.setMonetaryAmount4("0.0");
						casClaimAdjustment.setQuantity4(1);
						++errorNumber;
					}
					break;

				case 4:
					if (x12Error != null) {
						casClaimAdjustment.setClaimAdjustmentReasonCode5(x12Error);
						casClaimAdjustment.setMonetaryAmount5("0.0");
						casClaimAdjustment.setQuantity5(1);
						++errorNumber;
					}
					break;

				case 5:
					if (x12Error != null) {
						casClaimAdjustment.setClaimAdjustmentReasonCode6(x12Error);
						casClaimAdjustment.setMonetaryAmount6("0.0");
						casClaimAdjustment.setQuantity6(1);
						++errorNumber;
					}
					break;
				}
			}

			// Add the loop to the final output.
			++totalNumberOfSegments;
			ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation> loop2100ClaimPaymentInformation = new ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation>();
			solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation loop2100 = new solutions.health.X12HCCProfessional.X12_835.Loop2100ClaimPaymentInformation();
			loop2100.setClaimPaymentInformation(claimPaymentInfo);

			if (errorNumber > 0) { // If there are no X12 errors to add, skip over
				++totalNumberOfSegments;
				ArrayList<solutions.health.X12HCCProfessional.X12_835.ClaimAdjustment> claimAdjustmentInformation = new ArrayList<>();
				claimAdjustmentInformation.add(casClaimAdjustment);
				loop2100.setClaimAdjustment(claimAdjustmentInformation);
			} else {
				// NO X12 Error Number
				System.out.println("No X12 Error Codes map to error For: " + rejectedClaim
						+ ", CAS segment requies at least 1, moving on");
				transactionSet.getLoop2000Detail().remove(loop2000Detail);
				totalNumberOfSegments -= 2;
				continue;
			}
			loop2100ClaimPaymentInformation.add(loop2100);
			loop2000Detail.setLoop2100ClaimPaymentInformation(loop2100ClaimPaymentInformation);

			// NM1*QC Segment - Patient Name
			solutions.health.X12HCCProfessional.X12_835.PatientName patientName = new solutions.health.X12HCCProfessional.X12_835.PatientName();
			patientName.setEntityIDCode("QC");// Implementation Guide QC==>Patient Name
			patientName.setEntityTypeQualifier("1"); // Implementation Guide 1==>Person
			patientName.setLastName(rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2010BASubscriberName().getPatientName().getLastName());
			patientName.setFirstName(rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2010BASubscriberName().getPatientName().getFirstName());
			patientName.setMiddleName(rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2010BASubscriberName().getPatientName().getMiddleName());
			patientName.setIdCodeQualifier(rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2010BASubscriberName().getPatientName().getIdCodeQualifier());
			currentMemberNumber = rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2010BASubscriberName().getPatientName().getIdCode();
			patientName.setIdCode(currentMemberNumber);

			// Add to final output.
			++totalNumberOfSegments;
			loop2100.setPatientName(patientName);

			int rejectedHeaderNumberCounter = 1; // When moving to combined accepted/rejected this will already be
													// there, do not replicate, reuse existing

			// This is the SVC Segment
			solutions.health.X12HCCProfessional.X12_835.ClaimSPI claimSPI = new solutions.health.X12HCCProfessional.X12_835.ClaimSPI();
			claimSPI.setCompositeMPI(rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel().get(0)
					.getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
					.getLoop2400ServiceLineNumber().get(0).getProfessionalService().getCompMedProcedID());
			claimSPI.setSubmittedServiceCharge(rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel()
					.get(0).getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
					.getLoop2400ServiceLineNumber().get(0).getProfessionalService().getMonetaryAmount());
			claimSPI.setAmountPaid(Double.toString(0.0)); // Here is where the amount paid == 0 since this is a DENIED
															// claim.
															// Here is also where the CLP - CAS segments must equal 0
															// (amount paid for claim = 0);

			// Add the Service Line information (SVC and DTM Segments).
			solutions.health.X12HCCProfessional.X12_835.Loop2110SPI loop2110SPI = new solutions.health.X12HCCProfessional.X12_835.Loop2110SPI();
			++totalNumberOfSegments;
			transactionSet.getLoop2000Detail().add(loop2000Detail);

			loop2110SPI.setClaimSPI(claimSPI);
			ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2110SPI> loop2110SPIArray = new ArrayList<solutions.health.X12HCCProfessional.X12_835.Loop2110SPI>();
			loop2110SPIArray.add(loop2110SPI);
			loop2100.setLoop2110SPI(loop2110SPIArray);

			// SVC CAS Segment
			solutions.health.X12HCCProfessional.X12_835.ServiceAdjustment casServiceAdjustment = new solutions.health.X12HCCProfessional.X12_835.ServiceAdjustment();
			casServiceAdjustment.setServiceAdjGroupCode("CO"); // Per TERRI - all adjustments are CO
																// Per Implementation GuidE CO==>Contractual Oblications
																// Here is where the CLP - SUM(CAS monetaryAmounts) ===
																// 0 (amount paid for the claim)
			// Add EACH error found in the error file
			// NOTE - The CAS segment may need to be broken into multiple
			// segments if there are more than 6 errors found per claim.
			int serviceErrorNumber = 0;
			for (ErrorLineItem eli : errorsForEncounter) {
				String x12Error = beaconToX12ErrorMap.get(eli.getErrorNum());
				if (x12Error == null) {
					System.out.println("SVC Level No X12 Mapping for: " + x12Error);
					continue;
				}
				// System.out.println("SVC LEVEL REJECT PROCESSING ERROR: "+eli.getErrorNum()+"
				// Maps to X12: "+x12Error);
				System.out.println("Rejected Service Error Number: " + serviceErrorNumber);

				switch (serviceErrorNumber) {
				case 0:
					if (x12Error != null) {
						casServiceAdjustment.setServiceARCode(x12Error);
						casServiceAdjustment.setMonetaryAmount(claimPaymentAmountStr); // SPECIAL CASE - For 1st error,
																						// set adjustment amount to
																						// total
																						// This will need to be tested
																						// to see if NextGen accepts it.
																						// It may not if it checks the
																						// maximum allowed amount for
																						// the
																						// Reason Code.
						casServiceAdjustment.setQuantity(1);
						++serviceErrorNumber;
					}
					break;

				case 1:
					if (x12Error != null) {
						casServiceAdjustment.setServiceARCode2(x12Error);
						casServiceAdjustment.setMonetaryAmount2("0.0"); // SPECIAL CASE - For 1st error, set adjustment
																		// amount to total
																		// All other 0 to keep balancing working
																		// This will need to be tested to see if NextGen
																		// accepts it.
																		// It may not if it checks the maximum allowed
																		// amount for the
																		// Reason Code.
						casServiceAdjustment.setQuantity2(1);
						++serviceErrorNumber;
					}
					break;

				case 2:
					if (x12Error != null) {
						casServiceAdjustment.setServiceARCode3(x12Error);
						casServiceAdjustment.setMonetaryAmount3("0.0");
						casServiceAdjustment.setQuantity3(1);
						++errorNumber;
					}
					break;

				case 3:
					if (x12Error != null) {
						casServiceAdjustment.setServiceARCode4(x12Error);
						casServiceAdjustment.setMonetaryAmount4("0.0");
						casServiceAdjustment.setQuantity4(1);
						++errorNumber;
					}
					break;

				case 4:
					if (x12Error != null) {
						casServiceAdjustment.setServiceARCode5(x12Error);
						casServiceAdjustment.setMonetaryAmount5("0.0");
						casServiceAdjustment.setQuantity5(1);
						++errorNumber;
					}
					break;

				case 5:
					if (x12Error != null) {
						casServiceAdjustment.setServiceARCode6(x12Error);
						casServiceAdjustment.setMonetaryAmount6("0.0");
						casServiceAdjustment.setQuantity6(1);
						++errorNumber;
					}
					break;
				}
			}

			if (serviceErrorNumber > 0) { // If there are no X12 errors, nothing to add, move on
				++totalNumberOfSegments;
				ArrayList<solutions.health.X12HCCProfessional.X12_835.ServiceAdjustment> serviceAdjustmentInformation = new ArrayList<>();
				serviceAdjustmentInformation.add(casServiceAdjustment);
				loop2110SPI.setServiceAdjustment(serviceAdjustmentInformation);
			} else {
				System.out.println("No X12 Service Error Codes map to error For: " + rejectedClaim
						+ ", CAS segment requies at least 1, moving on");
			}

			// This is the DTM*472 segment
			solutions.health.X12HCCProfessional.X12_835.ServiceDate serviceDate = new solutions.health.X12HCCProfessional.X12_835.ServiceDate();
			serviceDate.setDateTimeQualifier("472"); // Implementation Guide 472-->Service Date
			ServiceDate original837ServiceDate = rejectedClaimBillingDetail.getLoop2000BSubscriberHierarchicalLevel()
					.get(0).getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
					.getLoop2400ServiceLineNumber().get(0).getServiceDate();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String original837DateTimePeriod = original837ServiceDate.getDateTimePeriod();
			Date parseOriginal837Date = formatter.parse(original837DateTimePeriod);
			serviceDate.setDate(parseOriginal837Date);
			loop2110SPI.setServiceDate(new ArrayList<solutions.health.X12HCCProfessional.X12_835.ServiceDate>());
			++totalNumberOfSegments;
			loop2110SPI.getServiceDate().add(serviceDate);
			fullyProcessedRejectedClaims.add(rejectedClaim);
		} // END FOR LOOP

		// SE Segment
		solutions.health.X12HCCProfessional.X12_835.TransactionSetTrailer se = new solutions.health.X12HCCProfessional.X12_835.TransactionSetTrailer();
		se.setNumberOfIncludedSegments(0); // calculated later, use placeholder here
		se.setTransactionSetControlNumber(todaysDateAsControlNumber);

		++totalNumberOfSegments;
		transactionSet.setTransactionSetTrailer(se);

		fg.setTransactionSet(transactionSet);

		// GE Segment
		solutions.health.X12HCCProfessional.X12_835.FunctionalGroupTrailer ge = new solutions.health.X12HCCProfessional.X12_835.FunctionalGroupTrailer();
		ge.setNumberIncludedTransactionSets("1");
		ge.setGroupControlNumber(groupControlNumber);

		++totalNumberOfSegments;
		fg.setFunctionalGroupTrailer(ge);

		rejected835.getInterchange().setFunctionalGroup(fg);

		// IEA Segment
		solutions.health.X12HCCProfessional.X12_835.InterchangeControlTrailer iea = new solutions.health.X12HCCProfessional.X12_835.InterchangeControlTrailer();
		iea.setNumberIncludedFunctionalGroups("1");
		iea.setInterchangeControlNumber(35);
		++totalNumberOfSegments;
		se.setNumberOfIncludedSegments(totalNumberOfSegments - 4);
		rejected835.getInterchange().setInterchangeControlTrailer(iea);

		totalNumberOfSegments = 0;
		return rejected835;
	}

	public void generatedRejectedRecords835() {

		// These variables are used to maintain 835 balance and follow
		// the Implementation Guide naming Coventions (names follow what is
		// implementation guide)
		//
		// slbAmount1 == SVC02 == submitted charges for service line
		// slbAmount2 == SUM(CAS03, 06, 09, 12, 15, 18) (no known monetary Amounts yet
		// ==> slbAmount2 = 0)
		// slbAmount3 == SVC03 == the amount paid for the service
		// Claim Level Balancing equation to satisfy:
		// slbAmount1 - slbAmount2 = slbAmount3
		//
		// Claim Level Balancing
		// cbAmount4 == CLP03 == total submitted charges for this claim
		// cbAmount5 == SUM(CAS03, 06, 09, 12, 15, 18)
		// cbAmount6 == CLP04 == the paid amount for the claim

		// Claim Level Balancing equation to satisfy:
		// cbAmount4 - cbAmount5 = cbAmount6
		//
		// Transaction Level Balancing.
		// tbAmount10 = sum all CLP04 amounts in the CLP segments
		// tbAmount11 = sum PLB04, 06, 08, 10, 12, 14 (we don't have PLB segments ==>
		// tbAmount11 = 0)
		// tbAmount12 = BPR02 = total amount of this claim payment
		// Transaction Level Balancing equation to satisfy:
		// tbAmount10 - tbAmount11 = tbAmount12

		try {
			File rejectedPath = new File(output835Directory + "\\rejected");
			org.apache.commons.io.FileUtils.forceMkdir(rejectedPath);

			System.out.println("Total Number of Rejected Encounters to process: " + encFileEncouters.keySet().size());

			// Need at least 1 valid rejected Beacon Encounter ID to retrieve Payer/Payee
			// Identification Information
			String aValidRejectedBeaconEncounterID = null;
			if (fatalRejections.keySet().size() == 0) {
				System.out.println("No Fatal Rejections - there will be no rejected file produced.");
			}
			for (String rejectedBeaconEncounterID : fatalRejections.keySet()) {
				Set<String> original837KeySet = original837Detail.keySet();
				if (!(original837KeySet.contains(rejectedBeaconEncounterID))) {
					System.out.println("Rejected Beacon Encounter: " + rejectedBeaconEncounterID
							+ " Not in original 837s, NO SOURCE DATA moving on");
					exceptionReport.println("Exception - Rejection: " + rejectedBeaconEncounterID);
					continue; // sometimes the 837 file does not contain
				} else {
					aValidRejectedBeaconEncounterID = rejectedBeaconEncounterID;
					System.out.println(
							"Payer/Payee ID - Found Valid Rejected Beancon ID: " + aValidRejectedBeaconEncounterID);
					exceptionReport.println("Exception - Rejected Claim: " + rejectedBeaconEncounterID);
					break;
				}
			}
			if (aValidRejectedBeaconEncounterID == null) { // For debug purposes only
				System.out.println("NO REJECTED BEACON IDs in the original 835 data, file will be empty");
			}
			if (aValidRejectedBeaconEncounterID != null) {

				List<ErrorLineItem> errorsForEncounter = getErrorsForEncounterID(aValidRejectedBeaconEncounterID);
				System.out.println("REJECTED Beacon Encounter ID: " + aValidRejectedBeaconEncounterID
						+ " Number Errors: " + errorsForEncounter.size());

				System.out.println("REJECTED BEACON Encounter: " + aValidRejectedBeaconEncounterID
						+ " ------------------------------------------------");
				System.out.println("START REJECTED X12 835---------------------------------\n");

				X12835 rejected835 = generateRejectedClaim_X12835(aValidRejectedBeaconEncounterID, errorsForEncounter);

				// Get Text representation of X12 835
				StringWriter writer = new StringWriter();
				x12835Factory.toEDI(rejected835, writer);

				// Work around bug in Smooks 1.7 - doesn't like ISA embedded component separator
				// definition
				// It doesn't output the ':' so use garbage character and replacement
				// afterwards.
				String rejectedX12835Str = writer.toString();
				rejectedX12835Str = rejectedX12835Str.replace('\u1200', ':');

				// Add \n for pretty printing (separate out individual segments)
				rejectedX12835Str = rejectedX12835Str.replace("~", "~\n");

				System.out.println(rejectedX12835Str);

				Integer nextGenEncounterIDInt = Integer.parseInt(nextGenEncounterID);
				nextGenEncounterIDInt -= 100000000; // NextGen Encounter ID trimmed (take > 99999 into account)

				String baseName = FilenameUtils.getBaseName(encounterFileName);
				String rejectedFilename = output835Directory + "\\rejected\\Output835_" + baseName + "_rejected.x12";
				System.out.println("Writing to file: '" + rejectedFilename + "'");

				try (FileWriter outputFileWriter = new FileWriter(rejectedFilename)) {
					outputFileWriter.write(rejectedX12835Str);
				} catch (IOException ioe) {
					System.out.println("ERROR Writing Rejected File!");
					ioe.printStackTrace();
					System.exit(1);
				}
				System.out.println("END REJECTED  X12 835---------------------------------");
				System.out.println("END REJECTED BEACON Encounter: " + aValidRejectedBeaconEncounterID
						+ " ------------------------------------------------\n\n");

			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParseException pe) {
			System.out.println("Parse Exception: ");
			pe.printStackTrace();
		}

	}

	public void generateAcceptedRecords835() {
		try {
			String baseName = FilenameUtils.getBaseName(encounterFileName);
			File acceptedPath = new File(output835Directory + "\\accepted");
			org.apache.commons.io.FileUtils.forceMkdir(acceptedPath);

			// Generate Check Number based on filename - Result of meeting from 08/04/2022
			// Per BECKY (Beatriz Granillo)
			String[] baseNameParts = baseName.split("_");
			checkNumber = baseNameParts[0];
			
			
			System.out.println("Total Number of Accepted Encounters to process: " + encFileEncouters.keySet().size());

			// Loop through all accepted encounters
			// Need at least 1 valid Beacon Encounter to retrieve the Payer/Payee
			// Identification Information
			// String aValidBeaconEncounterID = null;

			for (String beaconEncounterID : acceptedClaims.keySet()) {

				Set<String> original837KeySet = original837Detail.keySet();
				if (!(original837KeySet.contains(beaconEncounterID))) {
					System.out.println("Beacon Encounter: " + beaconEncounterID
							+ " Not in original 837s, Not in original 837s, NO SOURCE DATA moving on");
					exceptionReport.println("Exception - Accepted Claim: " + beaconEncounterID);
					continue; // sometimes the 837 file does not contain all Beacon Encounters
				} else {
					aValidBeaconEncounterID = beaconEncounterID;
					break;
				}
			}
			if (aValidBeaconEncounterID != null) {
				System.out.println("BEACON Encounter: " + aValidBeaconEncounterID
						+ " ------------------------------------------------");
				System.out.println("START X12 835---------------------------------\n");

				X12835 accepted835 = generateAcceptedClaim_X12835(aValidBeaconEncounterID);

				// Get Text representation of X12 835
				StringWriter writer = new StringWriter();
				if (x12835Factory == null) {
					try {
						x12835Factory = X12835Factory.getInstance();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				x12835Factory.toEDI(accepted835, writer);

				// Work around bug in Smooks 1.7 - doesn't like ISA embedded component separator
				// definition
				// It doesn't output the ':' so use garbage character and replacement
				// afterwards.
				String acceptedX12835Str = writer.toString();
				acceptedX12835Str = acceptedX12835Str.replace('\u1200', ':');

				// Add \n for pretty printing (separate out individual segments)
				acceptedX12835Str = acceptedX12835Str.replace("~", "~\n");

				System.out.println(acceptedX12835Str);

				
				String acceptedFilename = output835Directory + "\\accepted\\Output835_" + baseName + "_accepted.x12";
				System.out.println("Writing to file: '" + acceptedFilename + "'");

				try (FileWriter outputFileWriter = new FileWriter(acceptedFilename)) {
					outputFileWriter.write(acceptedX12835Str);
				} catch (IOException ioe) {
					System.out.println("ERROR Writing Accepted File!");
					ioe.printStackTrace();
					System.exit(1);
				}
				System.out.println("END   X12 835---------------------------------");
				System.out.println("END BEACON Accepted Encounter: " + aValidBeaconEncounterID
						+ " ------------------------------------------------\n\n");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ParseException pe) {
			System.out.println("Parse Exception: ");
			pe.printStackTrace();
		}

	}

	private void generateOutputReport() {

		try {
			String baseName = FilenameUtils.getBaseName(encounterFileName);
			String outputFilename = output835Directory + "/OutputReport_" + baseName + ".txt";
			FileWriter fw = new FileWriter(outputFilename);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("*********************** PROCESSED CLAIMS ********************************");
			pw.println("Number Encounters in the ENC File: " + encFileEncouters.size() + "\n");

			pw.println("Accepted Encounters Processed and put into 835");
			for (String processedAccepted : getFullyProcessedAcceptedClaims()) {
				Loop2000ABillingProviderDetail acceptedClaimBillingDetail = original837Detail.get(processedAccepted);
				Loop2000BSubscriberHierarchicalLevel acLoop2000bSubscriberHierarchicalLevel = acceptedClaimBillingDetail
						.getLoop2000BSubscriberHierarchicalLevel().get(0);
				String nextGenEncounterID = acLoop2000bSubscriberHierarchicalLevel
						.getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
						.getClaimInformation().getClaimSubmitterIdentifier();
				Integer nextGenEIDInt = Integer.parseInt(nextGenEncounterID) - 100000000;
				pw.println("\t\t" + processedAccepted + " (" + nextGenEIDInt + ")");
			}
			pw.println("\n\tTotal: " + getFullyProcessedAcceptedClaims().size());

			pw.println("\nRejected Encouters Processed and put into 835");
			for (String processedRejected : getFullyProcessedRejectedClaims()) {
				Loop2000ABillingProviderDetail rejectedClaimBillingDetail = original837Detail.get(processedRejected);
				Loop2000BSubscriberHierarchicalLevel acLoop2000bSubscriberHierarchicalLevel = rejectedClaimBillingDetail
						.getLoop2000BSubscriberHierarchicalLevel().get(0);
				String rejectedNextGenEncounterID = acLoop2000bSubscriberHierarchicalLevel
						.getLoop2000CPatientHierarchicalLevel().get(0).getLoop2300ClaimInformation().get(0)
						.getClaimInformation().getClaimSubmitterIdentifier();
				Integer rejectedNextGenEIDInt = Integer.parseInt(rejectedNextGenEncounterID) - 100000000;

				pw.println("\t\t" + processedRejected + " (" + rejectedNextGenEIDInt + ")");
			}
			pw.println("\n\tTotal: " + getFullyProcessedRejectedClaims().size());
			pw.println("\n");

			pw.println("GRAND TOTAL: "
					+ (getFullyProcessedAcceptedClaims().size() + getFullyProcessedRejectedClaims().size()));
			pw.println("*********************** END PROCESSED CLAIMS ********************************");
			pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readInRejectedClaimsInformation() {
		try {
			FatalRejectionDAO dao = new FatalRejectionDAO(rejectionFilename);

			fatalRejections = dao.getFatalRejections();
			System.out.println("Read In Rejection Filename: " + rejectionFilename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 6) {
			System.out.println(
					"Usage:  java -jar ErrorProcessor.jar <837 filename> <Encounter filename> <Error filename> <Log filename> <Output 835 Directory>");
			System.out.println(
					"The <Output 835 Directory> should initially be empty and will have an \"accepted\" and a \"rejected\" ");
			System.out.println(
					"directory underneath it at the end.  Accepted will contain the 835s for claims accepted by Bean");

			System.exit(-1);
		}
		ErrorProcessor837to835Generator processor = new ErrorProcessor837to835Generator(args);
		processor.readInBeaconToX12Mapping();
		processor.readInEncounterFile(); // load in the .ENC file from Beacon
		processor.readInErrorFile(); // load in the _err.txt file from Beacon
		processor.readInOriginal837Information(); // Original 837 ==> SOURCE OF TRUTH
		processor.readInRejectedClaimsInformation(); // read in the output of find rejections utility.
		processor.findProcessedClaims(); // Load in accepted/rejected claim sets from the database
											// These are what MOCKINGBIRD marked as accepted/rejected

		
		processor.generateAcceptedRecords835();
		processor.generatedRejectedRecords835();
		processor.generateOutputReport();

		System.exit(0);

	}
}
