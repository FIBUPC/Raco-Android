package fib.lcfib.raco.Model;

import java.util.ArrayList;

public class Assignatura{
	
	private String mIdAssig; //GRAU-XXXX
	private String mNomAssig; //NOM COMPLET
	private int mCodi;
	private int mCredits;
	private static ArrayList<Professors> sListProfessors = new ArrayList<Professors>();
	private static ArrayList<String> sListObjectius = new ArrayList<String>();
	
	public Assignatura (int codi, String nomA, String idAssig, int cred, ArrayList<Professors> p, ArrayList<String> obj){
		this.mCodi = codi;
		this.mIdAssig = idAssig;
		this.mNomAssig = nomA;
		this.mCredits = cred;
		sListProfessors = p;
		sListObjectius = obj;
	}

	public Assignatura() {
		// TODO Auto-generated constructor stub
	}

	public String getNomAssig() {
		return mNomAssig;
	}
	public void setNomAssig(String nomAssig) {
		this.mNomAssig = nomAssig;
	}
	public int getCredits() {
		return mCredits;
	}
	public void setCredits(int credits) {
		this.mCredits = credits;
	}
	public ArrayList<Professors> getProfessors() {
		return sListProfessors;
	}
	public void setProfessors(ArrayList<Professors> professors) {
		sListProfessors= professors;
	}
	public ArrayList<String> getObjectius() {
		return sListObjectius;
	}
	public void setObjectius(ArrayList<String> objectius) {
		sListObjectius = objectius;
	}
	
	public void setIdAssig(String idAssig) {
		this.mIdAssig = idAssig;
	}

	public String getIdAssig() {
		return mIdAssig;
	}

	public void setCodi(int codi) {
		this.mCodi = codi;
	}

	public int getCodi() {
		return mCodi;
	}
}
