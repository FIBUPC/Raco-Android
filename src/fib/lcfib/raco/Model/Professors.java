package fib.lcfib.raco.Model;

public class Professors {

	private String mNom;
	private String mCorreu;
	private int mIdAssig;
	
	public Professors (String nom, String correu, int id){
		this.mNom = nom;
		this.mCorreu = correu;
		this.mIdAssig = id;
	}
	
	public String getNom() {
		return mNom;
	}

	public void setNom(String nom) {
		this.mNom = nom;
	}

	public String getCorreu() {
		return mCorreu;
	}

	public void setCorreu(String correu) {
		this.mCorreu = correu;
	}

	public void setIdAssig(int idAssig) {
		this.mIdAssig = idAssig;
	}

	public int getIdAssig() {
		return mIdAssig;
	}

}
