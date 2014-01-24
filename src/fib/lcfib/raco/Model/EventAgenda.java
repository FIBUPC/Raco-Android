package fib.lcfib.raco.Model;

import java.util.Date;

public class EventAgenda {

	private String mTitol;
	private Date mDataFi; //En realitat és la duració de l'event 
	private Date mDataInici;
	
	public EventAgenda(String title, Date dataFi, Date dataInici){
		this.mTitol = title;
		this.mDataFi = dataFi;
		this.mDataInici = dataInici;
	}

	public String getTitol() {
		return mTitol;
	}

	public void setTitol(String titol) {
		this.mTitol = titol;
	}

	public Date getDataFi() {
		return mDataFi;
	}

	public void setDateFi(Date descripcio) {
		this.mDataFi = descripcio;
	}
	
	public Date getData() {
		return mDataInici;
	}

	public void setData(Date data) {
		this.mDataInici = data;
	}

}
