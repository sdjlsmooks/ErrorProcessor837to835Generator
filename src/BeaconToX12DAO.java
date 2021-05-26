import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;

public class BeaconToX12DAO {

	public HashMap<String,String> readInBeaconToX12Mapping() {
		
		HashMap<String,String> theMapping = new HashMap<>();
		
		try (LineNumberReader lnr = new LineNumberReader(
				new FileReader("Q:\\NextGen Implementation\\Flat_Files\\Beacon_To_X12Mapping.csv"));) {			
			
			
			String line = null;
			// Skip over header line.
			lnr.readLine();
			
			while ((line = lnr.readLine()) != null) {
				String[] pair = line.split(",");
				theMapping.put(pair[0], pair[1]);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return theMapping;
	}
	
	public BeaconToX12DAO() {
		
	}

}
