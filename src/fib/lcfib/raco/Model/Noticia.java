package fib.lcfib.raco.Model;

import java.util.Date;

public class Noticia extends ItemGeneric{
	
	public final static String mTAG="Noticia";
	private String mLink;
	
	public Noticia(String titol, String descripcio, String imatge, Date pubDate, int tipus, String link) {
		super(titol, descripcio, imatge, pubDate, tipus);
		this.mLink = link;
	}

	public String getmLink() {
		return mLink;
	}

	public void setmLink(String mLink) {
		this.mLink = mLink;
	}	
}
