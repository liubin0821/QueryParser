package resouremanager.dataIO;

import java.sql.ResultSet;
import java.sql.SQLException;

import resouremanager.db.DataBaseHandler;
import resouremanager.db.DataBaseHandler.DataBase;

public class DBDataGetter implements DataGetter {
	private final DataBase db ;
	private final DataBaseHandler DBHandler ;
	private String sql ;
	
	DBDataGetter(DataBase db, String sql) throws SQLException{
		this.db = db ;
		DBHandler = new DataBaseHandler(this.db) ;
		this.sql = sql ;
	}
	
	@SuppressWarnings("finally")
	@Override
	public DBDataGetterResult getData() {
		// TODO Auto-generated method stub
		ResultSet rs = null;
		try {
			rs = DBHandler.stmt.executeQuery(sql) ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		} finally {
			return new DBDataGetterResult(rs) ;
		}		
	}
}
