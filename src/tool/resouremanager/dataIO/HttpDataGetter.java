package resouremanager.dataIO;

import java.net.HttpURLConnection;

import java.net.ProtocolException;

abstract class HttpDataGetter implements DataGetter {
	
	/** url. */
	protected String Url ;
	protected HttpURLConnection connection ;
	
	
	/** constructor. */
	HttpDataGetter(String Url){
		this.Url = Url ;
	}
	
	/**
	 * get url
	 */
	public String Url() {
		return this.Url;
	}
	
	/** 
	 * reset url. 
	 */
	public void reSetUrl(String newUrl){
		this.Url = newUrl ;
	}
	 
	@Override
	public abstract DataGetterResult getData() throws ProtocolException ;

}
