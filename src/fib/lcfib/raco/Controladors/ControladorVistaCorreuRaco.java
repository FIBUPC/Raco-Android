package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesActivity;
import fib.lcfib.raco.Connexions.GestioConnexio;
import fib.lcfib.raco.Connexions.LlistesItems;
import fib.lcfib.raco.Connexions.ParserAndURL;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.Correu;
import fib.lcfib.raco.Model.ItemGeneric;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorVistaCorreuRaco extends GestioActualitzaLlistesActivity {

	private final String mTAG = "CorreuRaco";

	private static ArrayList<Correu> sCorreus = new ArrayList<Correu>();
	private AdaptadorCorreusRaco mAdaptadorLlista;
	private ListView mListAgenda;
	private SharedPreferences sPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_llistes_general_raco);

		sPrefs = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);

		mRLayout = (RelativeLayout) findViewById(R.id.layoutCarregantDades);
		mPd = (ProgressBar) findViewById(R.id.carregantDades);
		mListAgenda = (ListView) findViewById(R.id.vista_llista_raco);
		mLLayout = (LinearLayout) findViewById(R.id.vistes_generals_raco);

		mostrarProgressBarBanner();

		sCorreus.clear();
		// Gestionar Base de dades
		mBdm = new BaseDadesManager(this);

		mostrarLlistes();

		if (sCorreus.isEmpty()) {
			if (hihaInternet()) {
				mostrarProgressBarPantalla(mPd, mRLayout);
				amagarProgressBarBanner();
				obtenirDadesWeb();
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
				amagarProgressBarBanner();
				setContentView(R.layout.vista_no_mail);
			}
		}
	}

	@Override
	public void actualitzarLlistaBaseDades(LlistesItems lli) {
		try {
			ArrayList<ItemGeneric> correu = new ArrayList<ItemGeneric>();
			correu = (ArrayList<ItemGeneric>) lli.getLitemsGenerics();
			if (correu.size() > 0) {
				sCorreus.clear();
				// Actualitzem la Base de dades
				actualitzarTaula(correu);
				mLLayout.setBackgroundColor(AndroidUtils.REMOVE_BACKGROUND);
			} else {
				amagarProgressBarBanner();
				amagarProgressBarPantalla(mPd, mRLayout);
				setContentView(R.layout.vista_no_mail);
			}
		} catch (Exception e) {
			amagarProgressBarBanner();
			amagarProgressBarPantalla(mPd, mRLayout);
			Toast.makeText(getApplicationContext(), R.string.errorCorreu,
					Toast.LENGTH_LONG).show();
			if (sCorreus.isEmpty()) {
				setContentView(R.layout.vista_no_mail);
			}
		}

	}

	protected void mostrarLlistes() {

		sCorreus.clear();
		obtenirDadesBd();

		amagarProgressBarBanner();
		amagarProgressBarPantalla(mPd, mRLayout);

		if (sCorreus.isEmpty()) {
			setContentView(R.layout.vista_no_mail);
		} else {
			// Gestionar les llistes
			mAdaptadorLlista = new AdaptadorCorreusRaco(this, sCorreus);
			mListAgenda.setAdapter(mAdaptadorLlista);
		}
	}

	protected void obtenirDadesBd() {
		try {
			mBdm.open();
			sCorreus = mBdm.getAllCorreus();
			mBdm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void obtenirDadesWeb() {
		GestioConnexio gc = new GestioConnexio(this);
		AndroidUtils au = AndroidUtils.getInstance();

		// Preparem les URL i les connexions per obtenir les dades
		try {
			// ¿Usuari i contrassenya al móbil?
			String username = sPrefs.getString(AndroidUtils.USERNAME, "");
			String password = sPrefs.getString(AndroidUtils.PASSWORD, "");

			URL correu = au.crearURL(au.URL_CORREU);
			ParserAndURL pauC = new ParserAndURL();
			pauC = pauC.crearPAU(correu, AndroidUtils.TIPUS_CORREU, username,
					password);
			gc.execute(pauC);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected void actualitzarTaula(ArrayList<ItemGeneric> lCorreu) {
		Correu correu;
		mBdm.open();
		mBdm.deleteTableCorreus();

		for (int i = 0; i < lCorreu.size(); i++) {
			correu = (Correu) lCorreu.get(i);
			mBdm.insertItemCorreu(correu.getTitol(), correu.getDescripcio(),
					correu.getImatge(), correu.getDataPub().toString(),
					correu.getTipus(), correu.getLlegits(),
					correu.getNo_llegits());
		}
		mBdm.close();
	}

	/** Gestió del Menú */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.zona_raco, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actualitza:
			if (hihaInternet()) {
				mostrarProgressBarPantalla(mPd, mRLayout);
				obtenirDadesWeb();
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.logout:
			sPrefs.edit().clear().commit();
			Toast.makeText(getApplicationContext(), R.string.logout_correcte,
					Toast.LENGTH_SHORT).show();
			mBdm.open();
			mBdm.deleteTablesLogout();
			mBdm.close();
			startActivity(new Intent(ControladorVistaCorreuRaco.this,
					ControladorTabIniApp.class));
			break;

		}
		return true;
	}

}
