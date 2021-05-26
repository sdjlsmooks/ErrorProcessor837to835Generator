import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;

public class BeaconToX12ErrorDAO {

	/**
	 * The filename containing the errors
	 * 
	 */
	private String fileName;
	
	/**
	 * The Map of beacon errors to X12 Errors
	 */
	private HashMap<String,String> beaconToX12ErrorMap = new HashMap<>();
	
	public void readInBeaconToX12ErrorCodeMapping() {

		try (LineNumberReader lnr = new LineNumberReader(new FileReader("H:\\projects\\835Process\\BeaconToX12ErrorCodes.csv"))) {
			String line = null;
			while ((line = lnr.readLine()) != null) {
				String[] errorMappings = line.split(",");
				System.out.println("Beacon Error Code: '"+errorMappings[0]+"' = '"+errorMappings[1]+"'");
				beaconToX12ErrorMap.put(errorMappings[0], errorMappings[1]);
			}
			System.out.println("Total Error Mappings : " + beaconToX12ErrorMap.size());
		} 
		
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	public HashMap<String,String> getBeaconToX12ErrorMap() {
		if (beaconToX12ErrorMap.size() == 0) {
			readInBeaconToX12ErrorCodeMapping();
		}
		return beaconToX12ErrorMap;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public BeaconToX12ErrorDAO() {
		// TODO Auto-generated constructor stub
	}

}
