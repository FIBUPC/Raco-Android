package fib.lcfib.raco.Model;

import java.util.Date;

public class Avis extends ItemGeneric{
	
	private String mNomAssig;

	public Avis(String titol, String descripcio, String imatge, Date pubDate, int tipus, String nomAssig) {
		super(titol, descripcio, imatge, pubDate, tipus);
		this.mNomAssig = nomAssig;
	}
	
	public String getNomAssig() {
		return mNomAssig;
	}

	public void setNomAssig(String nomAssig) {
		this.mNomAssig = nomAssig;
	}
}
