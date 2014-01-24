package fib.lcfib.raco.Connexions;

import java.net.URL;

public class ParserAndURL {

	private URL mUrl;
	private int mTipus; // 0.- Noticia, 1.- Correu, 2.-Avis
	private String mUsername;
	private String mPassword;

	public ParserAndURL crearPAU(URL url, int tipus, String username,
			String password) {
		ParserAndURL pau = new ParserAndURL();
		pau.setTipus(tipus);
		pau.setUrl(url);
		pau.setUsername(username);
		pau.setPassword(password);
		return pau;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		this.mUsername = username;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		this.mPassword = password;
	}

	public URL getUrl() {
		return mUrl;
	}

	public void setUrl(URL url) {
		this.mUrl = url;
	}

	public int getTipus() {
		return mTipus;
	}

	public void setTipus(int tipus) {
		this.mTipus = tipus;
	}

}
