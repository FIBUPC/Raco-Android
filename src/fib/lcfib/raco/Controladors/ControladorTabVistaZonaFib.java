package fib.lcfib.raco.Controladors;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorTabVistaZonaFib extends ControladorTabGeneric {

	private static final String mTAG = "ControladorVistaZonaFib";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_vista_fib_raco);

		mTabHost = getTabHost();
		mResources = getResources();
		
		//Fem sortir la barra blanca el mig dels tabs
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.barra_blanca);


		afegirPestanya1();
		afegirPestanya2();
		afegirPestanya3();

		// A l'obrir aquesta zona restaurem la �ltima pestanya seleccionada
		int currentTab = sPrefs.getInt(PREF_STICKY_TAB, 0);
		mTabHost.setCurrentTab(currentTab);
	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		int currentTab = mTabHost.getCurrentTab();
		editor.putInt(PREF_STICKY_TAB, currentTab);
		editor.commit();
	}

	private void afegirPestanya1() {

		Intent intent = new Intent(this, ControladorVistaNoticiesFib.class);

		TabSpec spec = mTabHost.newTabSpec(mTAG);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_interiors, getTabWidget(), false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.imatge_subtabs);
		icon.setImageResource(R.drawable.ic_noticies);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	private void afegirPestanya2() {

		Intent intent = new Intent(this, ControladorVistaLocalitzacio.class);

		TabSpec spec = mTabHost.newTabSpec(mTAG);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_interiors, getTabWidget(), false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.imatge_subtabs);
		icon.setImageResource(R.drawable.ic_mapa_fib);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	private void afegirPestanya3() {

		Intent intent = new Intent(this, ControladorVistaAssigFib.class);

		TabSpec spec = mTabHost.newTabSpec(mTAG);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_interiors, getTabWidget(), false);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.imatge_subtabs);
		icon.setImageResource(R.drawable.ic_assignatures_fib);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

}
