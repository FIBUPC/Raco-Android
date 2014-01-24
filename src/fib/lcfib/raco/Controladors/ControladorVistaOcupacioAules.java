package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesActivity;
import fib.lcfib.raco.Connexions.GestioConnexio;
import fib.lcfib.raco.Connexions.LlistesItems;
import fib.lcfib.raco.Connexions.ParserAndURL;
import fib.lcfib.raco.Model.Aula;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorVistaOcupacioAules extends
		GestioActualitzaLlistesActivity {

	private final String mTAG = "OcupacioRaco";

	private ArrayList<Aula> sAules = new ArrayList<Aula>();
	public AdaptadorAulesOcupacio adaptadorLlista;
	private ListView mListOcupacio;
	private TextView mActualitzacio;
	private TextView mSeients;
	private TextView mClasse;
	private TextView mAula;
	public static Aula aulaSeleccionada;

	private static Date actualitzacioTime = Calendar.getInstance().getTime();
	private static boolean primerCop = true;
	private static boolean actualitzacio;
	private SharedPreferences sPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_ocupacio_aules_raco);

		sPrefs = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);

		mListOcupacio = (ListView) findViewById(R.id.listaules);
		mActualitzacio = (TextView) findViewById(R.id.actualitzacio);
		mActualitzacio.setText(R.string.noDataActualitzacio);
		mRLayout = (RelativeLayout) findViewById(R.id.layoutCarregantDadesOcupacio);
		mPd = (ProgressBar) findViewById(R.id.carregantDadesOcupacio);
		mLLayout = (LinearLayout) findViewById(R.id.vista_ocupacio_aules);

		mAula = (TextView) findViewById(R.id.aulaText);
		mAula.setText(getResources().getString(R.string.aulaText));
		mAula.setVisibility(View.GONE);

		mClasse = (TextView) findViewById(R.id.classeText);
		mClasse.setText(getResources().getString(R.string.classeText));
		mClasse.setVisibility(View.GONE);

		mSeients = (TextView) findViewById(R.id.seientsLliuresText);
		mSeients.setText(getResources().getString(R.string.seientsLliuresText));
		mSeients.setVisibility(View.GONE);

		// Gestionar Base de dades
		mBdm = new BaseDadesManager(this);

		sAules.clear();

		// Procurem actualitzar nomes cada minut
		actualitzacio = false;
		if (primerCop
				|| Math.abs(actualitzacioTime.getMinutes()
						- Calendar.getInstance().getTime().getMinutes()) > 1) {
			actualitzacio = true;
			actualitzacioTime = Calendar.getInstance().getTime();
			primerCop = false;
		}

		mostrarLlistes();

		if (actualitzacio) {
			if (hihaInternet()) {
				mostrarProgressBarPantalla(mPd, mRLayout);
				obtenirDadesWeb();
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
				if (sAules.isEmpty()) {
					mostrarVistaNoInformacio(mLLayout);
				}
			}
		}
	}

	@Override
	public void actualitzarLlistaBaseDades(LlistesItems lli) {
		try {
			sAules = lli.getLaula();
			if (sAules.size() > 0) {
				// Actualitzem la Base de dades
				actualitzarTaula();
				mLLayout.setBackgroundColor(AndroidUtils.REMOVE_BACKGROUND);

			} else {
				mostrarVistaNoInformacio(mLLayout);
				Toast.makeText(getApplicationContext(), R.string.errorAules,
						Toast.LENGTH_LONG).show();
			}
			amagarProgressBarPantalla(mPd, mRLayout);
		} catch (Exception e) {
			amagarProgressBarPantalla(mPd, mRLayout);
			mostrarVistaNoInformacio(mLLayout);
			Toast.makeText(getApplicationContext(), R.string.errorAules,
					Toast.LENGTH_LONG).show();
		}

	}

	protected void mostrarLlistes() {

		sAules.clear();
		obtenirDadesBd();
		calcularHoresClasse();

		amagarProgressBarPantalla(mPd, mRLayout);

		if (sAules.isEmpty()) {
			mostrarVistaNoInformacio(mLLayout);
		} else {
			amagarMostrarVistaNoInformacio(mLLayout);
			mClasse.setVisibility(View.VISIBLE);
			mSeients.setVisibility(View.VISIBLE);
			mAula.setVisibility(View.VISIBLE);
			Calendar info = Calendar.getInstance();
			info.setTime(sAules.get(0).getmActualitzacio());
			int hora = info.get(Calendar.HOUR);
			int minuts = info.get(Calendar.MINUTE);
			if (hora < 10 && minuts < 10) {
				mActualitzacio.setText(getString(R.string.actualitzacioHora)
						+ " 0" + hora + ":0" + minuts);
			} else if (hora < 10 && minuts >= 10) {
				mActualitzacio.setText(getString(R.string.actualitzacioHora)
						+ " 0" + hora + ":" + minuts);
			}
			if (hora >= 10 && minuts < 10) {
				mActualitzacio.setText(getString(R.string.actualitzacioHora)
						+ " " + hora + ":0" + minuts);
			} else {
				mActualitzacio.setText(getString(R.string.actualitzacioHora)
						+ " " + hora + ":" + minuts);
			}

			// Gestionar les llistes
			adaptadorLlista = new AdaptadorAulesOcupacio(this, sAules);
			mListOcupacio.setAdapter(adaptadorLlista);

			/** Selected item */
			mListOcupacio.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> av, View view,
						int index, long arg3) {
					aulaSeleccionada = sAules.get(index);
					AndroidUtils au = AndroidUtils.getInstance();
					Intent intent = new Intent(
							ControladorVistaOcupacioAules.this,
							ControladorVistaVeureAula.class);

					SharedPreferences sPrefs = getApplicationContext()
							.getSharedPreferences(
									PreferenciesUsuari.getPreferenciesUsuari(),
									0);
					intent.putExtra("username",
							sPrefs.getString(AndroidUtils.USERNAME, ""));

					if (aulaSeleccionada.getmNom().contains("A5")) {
						intent.putExtra("url", au.URL_AULA_A5);
					} else if (aulaSeleccionada.getmNom().contains("B5")) {
						intent.putExtra("url", au.URL_AULA_B5);
					} else {
						intent.putExtra("url", au.URL_AULA_C6);
					}

					startActivity(intent);
				}
			});

		}

	}

	protected void obtenirDadesBd() {
		try {
			mBdm.open();
			sAules = mBdm.getAllAulaOcupacio();
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

			URL ocupacio = au.crearURL(au.URL_AULES_I_OCUPACIO_RACO);
			ParserAndURL pauO = new ParserAndURL();
			pauO = pauO.crearPAU(ocupacio,
					AndroidUtils.TIPUS_AULES_I_OCUPACIO_RACO, null, null);

			URL aules = au.crearURL(au.URL_CLASSES_DIA_RACO);
			ParserAndURL pauC = new ParserAndURL();
			pauC = pauC.crearPAU(aules, AndroidUtils.TIPUS_CLASSES_DIA_RACO,
					null, null);

			gc.execute(pauO, pauC);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected void actualitzarTaula() {
		Aula aula;
		mBdm.open();
		mBdm.deleteTableAulesOcupacio();

		for (int i = 0; i < sAules.size(); i++) {
			aula = sAules.get(i);
			mBdm.insertItemAulaOcupacio(aula.getmNom(), aula.getmPlaces(), aula
					.getmActualitzacio().toString(), aula.getmDataInici()
					.toString(), aula.getmDataFi().toString(), aula
					.getmNomAssig(), aula.getmHihaClasse());
		}
		mBdm.close();
	}

	private void calcularHoresClasse() {
		Aula aula;
		for (int i = 0; i < sAules.size(); i++) {
			aula = sAules.get(i);

			if (!aula.getmNomAssig().equals(" ")) {
				sAules.get(i).setmHihaClasse("true");
			} else {
				// no hi ha classe
			}
		}
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
				amagarMostrarVistaNoInformacio(mLLayout);
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
			startActivity(new Intent(ControladorVistaOcupacioAules.this,
					ControladorTabIniApp.class));
			break;

		}
		return true;
	}

}
