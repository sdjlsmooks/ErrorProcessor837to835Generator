import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLServerConnection {

	private Connection jtdsSQLServer01Connection = null;

	private static SQLServerConnection instance;
	
	public static SQLServerConnection getInstance() {
		if (instance == null) {
			instance = new SQLServerConnection();
		}
		return instance;
	}
	
	public Connection getConnection() {
		if (jtdsSQLServer01Connection == null) {
			try {
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
				jtdsSQLServer01Connection  = DriverManager.getConnection("jdbc:jtds:sqlserver://sqlserver01");
				System.out.println("After gettingConnection");

			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch (SQLException sqe) {
				sqe.printStackTrace();
			}
			
		}
		return jtdsSQLServer01Connection;
	}
	
	public void finalize() {
		try {
			jtdsSQLServer01Connection.close();
		} catch (SQLException sqe) {	
			sqe.printStackTrace();
		}
	}
	
	
	private SQLServerConnection() {
		// Singleton to ensure finalize closes the SQL Connection.
	}

}
