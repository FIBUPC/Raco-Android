package fib.lcfib.raco.Model;

import java.util.Date;

/**Aquesta classe serveix per poder mostrar de manera comuna la llista d'assignatures + avisos en el Racó*/

public class AssignaturesAvisos {
	
	private String mNomAssig;
	private String mTitolAvis;
	private String mDescripcioAvis;
	private Date mData;
	private int mCodiAssig;
	private String mIdAssig;
	private String mImatge;

	public AssignaturesAvisos(String nomAssig, int codiAssig, String idAssig, String titolAvis, String descripcioAvis, Date data, String imatge) {
		this.mNomAssig = nomAssig;
		this.mDescripcioAvis = descripcioAvis;
		this.mData = data;
		this.mTitolAvis = titolAvis;
		this.mCodiAssig = codiAssig;
		this.mIdAssig = idAssig;
		this.mImatge = imatge;
	}
	
	public String getmImatge() {
		return mImatge;
	}

	public void setmImatge(String mImatge) {
		this.mImatge = mImatge;
	}

	public String getIdAssig() {
		return mIdAssig;
	}

	public void setIdAssig(String idAssig) {
		this.mIdAssig = idAssig;
	}



	public String getNomAssig() {
		return mNomAssig;
	}

	public void setNomAssig(String nomAssig) {
		this.mNomAssig = nomAssig;
	}
	
	public String getDescripcioAvis() {
		return mDescripcioAvis;
	}

	public void setDescripcioAvis(String descripcioAvis) {
		this.mDescripcioAvis = descripcioAvis;
	}

	public Date getData() {
		return mData;
	}

	public void setData(Date data) {
		this.mData = data;
	}
	
	public String getTitolAvis() {
		return mTitolAvis;
	}

	public void setTitolAvis(String titolAvis) {
		this.mTitolAvis = titolAvis;
	}

	public int getCodiAssig() {
		return mCodiAssig;
	}

	public void setCodiAssig(int codiAssig) {
		this.mCodiAssig = codiAssig;
	}

}
