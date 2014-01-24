package fib.lcfib.raco.Model;

import java.util.Calendar;
import java.util.Date;

public class Aula {
	
	private String mNom;
	private String mPlaces;
	private Date mActualitzacio;
	private Date mDataInici;
	private Date mDataFi;
	private String mNomAssig;
	private String mHihaClasse;
	
	
	public Aula(){
		
	}
	
	public Aula(String nom, String places, Date actualitzacio, Date dataInici, Date dataFi, String nomAssig, String hiha){
		this.mNom = nom;
		this.mPlaces = places;
		this.mActualitzacio = actualitzacio;
		this.mDataInici = dataInici;
		this.mDataFi = dataFi;
		this.mNomAssig = nomAssig;
		this.mHihaClasse = hiha;
	}
	
	public String getmNom() {
		return mNom;
	}

	public void setmNom(String mNom) {
		this.mNom = mNom;
	}

	public String getmPlaces() {
		return mPlaces;
	}

	public void setmPlaces(String mPlaces) {
		this.mPlaces = mPlaces;
	}

	public Date getmActualitzacio() {
		return mActualitzacio;
	}

	public void setmActualitzacio(Date mActualitzacio) {
		this.mActualitzacio = mActualitzacio;
	}
	
	public Date getmDataInici() {
		return mDataInici;
	}

	public void setmDataInici(Date mDataInici) {
		this.mDataInici = mDataInici;
	}

	public Date getmDataFi() {
		return mDataFi;
	}

	public void setmDataFi(Date mDataFi) {
		this.mDataFi = mDataFi;
	}

	public String getmNomAssig() {
		return mNomAssig;
	}

	public void setmNomAssig(String mNomAssig) {
		this.mNomAssig = mNomAssig;
	}

	public String getmHihaClasse() {
		return mHihaClasse;
	}

	public void setmHihaClasse(String mHihaClasse) {
		this.mHihaClasse = mHihaClasse;
	}


}