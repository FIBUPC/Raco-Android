package fib.lcfib.raco.Connexions;

import java.util.ArrayList;

import fib.lcfib.raco.Model.Assignatura;
import fib.lcfib.raco.Model.Aula;
import fib.lcfib.raco.Model.EventAgenda;
import fib.lcfib.raco.Model.ItemGeneric;

public class LlistesItems {

	private ArrayList<ItemGeneric> mLitems; // items de la llista ResumEvents i NoticiesFib
	private ArrayList<String> mLimatges;	// Imatges dels items de la llista
	private ArrayList<Assignatura> mLassig;
	private ArrayList<EventAgenda> mLeventAgenda;
	private ArrayList<Aula> mLaula;

	public LlistesItems() {
		// TODO Es podria passar un identificador per saber quin new s'ha de fer
		mLitems = new ArrayList<ItemGeneric>();
		mLimatges = new ArrayList<String>();
		mLassig = new ArrayList<Assignatura>();
		mLeventAgenda = new ArrayList<EventAgenda>();
		mLaula = new ArrayList<Aula>();
	}
	
	/**GET's*/
	public ArrayList<ItemGeneric> getLitemsGenerics() {
		return mLitems;
	}

	public ArrayList<String> getLimatges() {
		return mLimatges;
	}
	
	public ArrayList<Assignatura> getLassig() {
		return mLassig;
	}
	
	public ArrayList<EventAgenda> getLeventAgenda() {
		return mLeventAgenda;
	}
	
	public ArrayList<Aula> getLaula() {
		return mLaula;
	}
	
	/**SET's*/
	public void setLassig(ArrayList<Assignatura> lassig) {
		this.mLassig = lassig;
	}

	public void setLeventAgenda(ArrayList<EventAgenda> leventAgenda) {
		this.mLeventAgenda = leventAgenda;
	}

	public void setLitemsGenerics(ArrayList<ItemGeneric> litems) {
		this.mLitems = litems;
	}
	
	public void setLimatges(ArrayList<String> limatges) {
		this.mLimatges = limatges;
	}

	public void setLaula(ArrayList<Aula> laula) {
		this.mLaula = laula;
	}

	/**AFEGIR's*/
	public void afegirItemEventAgenda(EventAgenda it) {
		this.mLeventAgenda.add(it);
	}
	
	public void afegirItemGeneric(ItemGeneric it) {
		this.mLitems.add(it);
	}

	public void afegirImatge(String im) {
		this.mLimatges.add(im);
	}

	public void afegirItemAssig(Assignatura it) {
		this.mLassig.add(it);
	}
	
	public void afegirItemAula(Aula it) {
		this.mLaula.add(it);
	}

}
