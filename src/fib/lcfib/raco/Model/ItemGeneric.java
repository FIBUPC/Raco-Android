package fib.lcfib.raco.Model;

import java.util.Date;

public abstract class ItemGeneric {

	protected String mId; // Aquest es crea a la Base de dades quan s'incerten
							// els avisos, però no es fan servir mai, és la clau
							// primària de la BD, és a qui per coherència
	protected String mTitol;
	protected String mDescripcio;
	protected Date mDataPub;
	protected String mImatge;
	protected int mTipus;

	/** 0 - noticia , 1 - mail, 2 - avis */

	public ItemGeneric(String titol, String descripcio, String imatge,
			Date pubDate, int tipus) {
		this.setTitol(titol);
		this.setDescripcio(descripcio);
		this.setImatge(imatge);
		this.setDataPub(pubDate);
		this.setTipus(tipus);
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String getImatge() {
		return mImatge;
	}

	public void setImatge(String imatge) {
		this.mImatge = imatge;
	}

	public String getTitol() {
		return mTitol;
	}

	public void setTitol(String titol) {
		this.mTitol = titol;
	}

	public String getDescripcio() {
		return mDescripcio;
	}

	public void setDescripcio(String descripcio) {
		this.mDescripcio = descripcio;
	}

	public Date getDataPub() {
		return mDataPub;
	}

	public void setDataPub(Date dataPub) {
		this.mDataPub = dataPub;
	}

	public int getTipus() {
		return mTipus;
	}

	public void setTipus(int tipus) {
		this.mTipus = tipus;
	}

}
