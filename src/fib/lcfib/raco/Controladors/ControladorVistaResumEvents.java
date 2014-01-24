package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesActivity;
import fib.lcfib.raco.Connexions.GestioConnexio;
import fib.lcfib.raco.Connexions.LlistesItems;
import fib.lcfib.raco.Connexions.ParserAndURL;
import fib.lcfib.raco.Model.Avis;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.Correu;
import fib.lcfib.raco.Model.ItemGeneric;
import fib.lcfib.raco.Model.Noticia;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorVistaResumEvents extends
		GestioActualitzaLlistesActivity {

	// Variable que fem servir per saber cada quan s'ha de refrescar, ara per
	// ara, seran 5 minuts (android utils hi ha la variable)
	private static int updateTime = -1;

	private final String mTAG = "VistaInici";
	private String mUsername;
	private String mPassword;
	private static SharedPreferences sPrefs;
	private int mTotalNoticies;
	private int mTotalAvisos;
	private int mTotalCorreus;
	private static boolean sEsLogin;
	AndroidUtils au = AndroidUtils.getInstance();
	private static boolean errorAlCarregar;
	// Llistes que tindran les dades
	private ArrayList<ItemGeneric> sListItems = new ArrayList<ItemGeneric>();
	private ArrayList<String> sListImatges = new ArrayList<String>();

	// Llista de la vista
	public ListView sLlistaVista;
	public AdaptadorResumEvents_NoticiesFib sAdaptadorLlista;
	public static ItemGeneric itemSeleccionat = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_llistes_general);

		sLlistaVista = (ListView) findViewById(R.id.vista_inici_llista);
		mRLayout = (RelativeLayout) findViewById(R.id.layoutCarregantDadesGenerals);
		mPd = (ProgressBar) findViewById(R.id.carregantDadesGenerals);
		mLLayout = (LinearLayout) findViewById(R.id.vista_llistes_generals);

		// Base de dades
		mBdm = new BaseDadesManager(this);

		// Per si hi ha error i no té usuari no es mostri l'opció username i
		// password
		errorAlCarregar = false;

		sPrefs = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);
		// Inicialitzem les preferències
		mUsername = sPrefs.getString("username", "");
		mPassword = sPrefs.getString("password", "");

		// Preferencies a zero
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putInt(au.NOTIFICATION_COUNTER, 0);
		editor.commit();

		mostrarLlistes();

		// per la primera vegada posem el updateTime = -1 i per la resta juguem
		// amb la funcio que ens dirà si toca o no
		if (updateTime == -1 || enableActualitzarVista()) {
			// Obtenir de la web
			if (!hihaInternet()) {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
				amagarProgressBarPantalla(mPd, mRLayout);
				if (sListItems.isEmpty()) {
					mostrarVistaNoInformacio(mLLayout);
				}
			} else {
				if (sListItems.isEmpty()) {
					mostrarProgressBarPantalla(mPd, mRLayout);
				} else {
					// es pot posar progressbarBanner també és igual
					mostrarProgressBarPantalla(mPd, mRLayout);
				}
				obtenirDadesWeb(au);
			}

			// Activem la variable
			updateTime = Calendar.getInstance().getTime().getMinutes();
		}
	}

	/**
	 * Quan tornen a la classe només carreguem al informació que hi havia a la
	 * BD
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// Preferencies a zero
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putInt(au.NOTIFICATION_COUNTER, 0);
		editor.commit();

		// si vinc del login també hem d'actualitzar forçadament
		if (sEsLogin || enableActualitzarVista()) {
			// Obtenir de la web
			if (!hihaInternet()) {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
				mostrarProgressBarBanner();
				mostrarLlistes();
				amagarProgressBarBanner();
				amagarProgressBarPantalla(mPd, mRLayout);
			} else {
				if (sListItems.isEmpty()) {
					mostrarProgressBarPantalla(mPd, mRLayout);
				} else {
					mostrarProgressBarPantalla(mPd, mRLayout);
					// mostrarProgressBarBanner();
				}
				obtenirDadesWeb(au);
			}
			sEsLogin = false;
			// Activem la variable
			updateTime = Calendar.getInstance().getTime().getMinutes();
		} else {
			// mostrarProgressBarPantalla(mPd, mRLayout);
			// mostrarLlistes();
		}
	}

	public ArrayList<ItemGeneric> getLlistaItems() {
		return sListItems;
	}

	public void setLlistaItems(ArrayList<ItemGeneric> llistaItems) {
		this.sListItems = llistaItems;
	}

	public ArrayList<String> getLlistaUrlImatges() {
		return sListImatges;
	}

	public void setLlistaUrlImatges(ArrayList<String> llistaUrlImatges) {
		this.sListImatges = llistaUrlImatges;
	}

	@Override
	public void actualitzarLlistaBaseDades(LlistesItems lli) {
		try {

			if (lli.getLitemsGenerics().size() > 0) {
				sListItems = (ArrayList<ItemGeneric>) lli.getLitemsGenerics();
				sListImatges = (ArrayList<String>) lli.getLimatges();
				/** Actualitzem la Base de dades */
				actualitzarTaula();
				amagarProgressBarBanner();
				amagarProgressBarPantalla(mPd, mRLayout);
			} else {
				amagarProgressBarBanner();
				amagarProgressBarPantalla(mPd, mRLayout);
				mostrarVistaNoInformacio(mLLayout);
			}
		} catch (Exception e) {
			amagarProgressBarBanner();
			amagarProgressBarPantalla(mPd, mRLayout);
			mostrarVistaNoInformacio(mLLayout);
			errorAlCarregar = true;
			Toast.makeText(getApplicationContext(), R.string.error,
					Toast.LENGTH_LONG).show();
		}
	}

	protected void ResetLlistes() {
		sListItems.clear();
		sListImatges.clear();
	}

	@Override
	protected void mostrarLlistes() {

		ResetLlistes();
		obtenirDadesBd();
		ordenarPerData();

		// Mirem si l'usuari ha configurat les preferencies
		tractarPreferencies();

		if ((mUsername == null || mUsername.equals("") || mPassword.equals("") || mPassword == null)
				&& !errorAlCarregar && !sListItems.isEmpty()) {

			String imatge = au.URL_IMATGE_INFO;
			String titol = getResources().getString(
					R.string.configurarIniciTitol);
			String descripcio = getResources().getString(
					R.string.configurarIniciDescripcio);

			int tipus = AndroidUtils.TIPUS_INI_CONFIG;
			Noticia itemInformatiu = new Noticia(titol, descripcio, imatge,
					null, tipus, null);

			// L'afegim a la llista com a primer element
			sListItems.add(0, itemInformatiu);
			sListImatges.add(0, imatge);
		}

		// Gestionar les llistes
		sAdaptadorLlista = new AdaptadorResumEvents_NoticiesFib(this,
				tractarImatges(), sListItems);
		sLlistaVista.setAdapter(sAdaptadorLlista);

		// Per cada element que cliquin
		sLlistaVista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View view, int index,
					long arg3) {

				itemSeleccionat = sListItems.get(index);
				// Si seleccionen el de configuracio
				if (itemSeleccionat.getTipus() == 3) {
					if (hihaInternet()) {
						sEsLogin = true;
						Intent intent = new Intent(".Controladors.LoginRaco");
						intent.putExtra("queEs", "actualitat");
						startActivity(intent);
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.hiha_internet, Toast.LENGTH_LONG)
								.show();
					}

					// i seleccionen qualsevol event
				} else {
					Intent intent = new Intent(
							".Controladors.ControladorVistaResumEvents.itemInfo");
					intent.putExtra("qui", "actualitat");
					SharedPreferences sPrefs = getApplicationContext()
							.getSharedPreferences(
									PreferenciesUsuari.getPreferenciesUsuari(),
									0);
					intent.putExtra("username",
							sPrefs.getString(AndroidUtils.USERNAME, ""));
					startActivity(intent);
				}
			}
		});

		amagarProgressBarPantalla(mPd, mRLayout);
	}

	// Actualitzem al base de dades
	protected void actualitzarTaula() {
		ItemGeneric ig;

		mBdm.open();
		mBdm.deleteTableNoticies();
		mBdm.deleteTableCorreus();
		mBdm.deleteTableAvisos();

		// comptadors per saber els elements a mostrar amb les preferencies
		mTotalAvisos = 0;
		mTotalCorreus = 0;
		mTotalNoticies = 0;

		String[] idAssig;
		for (int i = 0; i < sListItems.size(); i++) {
			ig = sListItems.get(i);
			if (ig.getTipus() == 0) {
				Noticia n = (Noticia) ig;
				mBdm.insertItemNoticia(n.getTitol(), n.getDescripcio(),
						sListImatges.get(i), n.getDataPub().toString(),
						n.getTipus(), n.getmLink());
				mTotalNoticies++;
			} else if (ig.getTipus() == 1) {
				ig = (Correu) ig;
				mBdm.insertItemCorreu(ig.getTitol(), ig.getDescripcio(),
						sListImatges.get(i), ig.getDataPub().toString(),
						ig.getTipus(), 0, 0);
				mTotalCorreus++;
			} else if (ig.getTipus() == 2) {
				String idAssigFinal = "";
				idAssig = ig.getTitol().split("-");
				if (idAssig.length == 3) {
					idAssigFinal = idAssig[0].trim() + "-" + idAssig[1].trim();
				} else {
					idAssigFinal = idAssig[0].trim();
				}
				mBdm.insertItemAvis(ig.getTitol(), ig.getDescripcio(),
						sListImatges.get(i), ig.getDataPub().toString(),
						ig.getTipus(), idAssigFinal);
				mTotalAvisos++;
			}
		}
		mBdm.close();
	}

	@Override
	protected void obtenirDadesBd() {
		obtenirNoticies();
		obtenirAvisos();
		obtenirCorreus();
		if (sListItems.isEmpty()) {
			// Toast.makeText(getApplicationContext(), R.string.noBaseDades,
			// Toast.LENGTH_LONG).show();
		}
	}

	protected void obtenirDadesWeb(AndroidUtils au) {

		GestioConnexio gc = new GestioConnexio(this);
		SharedPreferences prefs = getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);

		/** Preparem les URL i les connexions per obtenir les dades */
		try {
			URL not = au.crearURL(au.URL_NOTICIES);
			ParserAndURL pauN = new ParserAndURL();
			pauN = pauN.crearPAU(not, AndroidUtils.TIPUS_NOTICIA, null, null);

			// Si ja té username i password mostrem correu i assignatures també
			if (mUsername != null && mUsername != "" && mPassword != ""
					&& mPassword != null) {

				// Afegim a la URLAssig la KEY de l'alumne
				String keyURL = prefs.getString(au.KEY_AVISOS, "");
				URL avisos = au.crearURL(au.URL_AVISOS + "" + keyURL);
				ParserAndURL pauA = new ParserAndURL();

				pauA = pauA.crearPAU(avisos, AndroidUtils.TIPUS_AVISOS, null,
						null);
				URL correu = au.crearURL(au.URL_CORREU);
				ParserAndURL pauC = new ParserAndURL();

				pauC = pauC.crearPAU(correu, AndroidUtils.TIPUS_CORREU,
						mUsername, mPassword);

				gc.execute(pauN, pauC, pauA);

			} else {
				gc.execute(pauN);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected void tancarApp() {
		super.onDestroy();
		System.runFinalizersOnExit(true);
		System.exit(0);
	}

	private void obtenirCorreus() {
		mBdm.open();
		ArrayList<Correu> list = mBdm.getAllCorreus();
		for (Correu c : list) {
			sListItems.add(c);
			sListImatges.add(c.getImatge());
		}
		mTotalCorreus = list.size();
		mBdm.close();
	}

	private void obtenirAvisos() {
		mBdm.open();
		ArrayList<ItemGeneric> list = mBdm.getAllAvisos();
		for (ItemGeneric a : list) {
			sListItems.add(a);
			sListImatges.add(a.getImatge());
		}
		mTotalAvisos = list.size();
		mBdm.close();
	}

	private void obtenirNoticies() {
		mBdm.open();
		ArrayList<ItemGeneric> list = mBdm.getAllNoticies();
		for (ItemGeneric n : list) {
			sListItems.add(n);
			sListImatges.add(n.getImatge());
		}
		mTotalNoticies = list.size();
		mBdm.close();
	}

	private void ordenarPerData() {
		try {
			int minIndex;
			ItemGeneric rig, rag;
			int n = sListItems.size();
			for (int i = 0; i < n - 1; i++) {
				minIndex = i;
				for (int j = i + 1; j < n; j++) {
					rig = sListItems.get(j);
					rag = sListItems.get(minIndex);
					if (rig.getDataPub().after(rag.getDataPub())) {
						minIndex = j;
					}
				}
				if (minIndex != i) {
					Collections.swap(sListImatges, i, minIndex);
					Collections.swap(sListItems, i, minIndex);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String[] tractarImatges() {
		String link[] = null;
		try {
			link = (String[]) sListImatges.toArray(new String[sListImatges
					.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return link;
	}

	private void tractarPreferencies() {
		String ressaltat = sPrefs.getString("ListPreferenceElement", "0");
		Boolean checkboxSeleccionat = sPrefs.getBoolean(
				"Active_box_preference", false);

		if (checkboxSeleccionat) {
			if (ressaltat.equals("1")) { // vol notícies
				eliminarCorreuAvisos();
			} else if (ressaltat.equals("2")) { // vol correus
				eliminarNoticiesAvisos();
			} else if (ressaltat.equals("3")) { // vol avisos
				eliminarCorreusNoticies();
			} else {
				// no hi ha cap preferencia
			}
		} else {
			// no hi ha el checkboxseleccionat
		}

	}

	private void eliminarCorreuAvisos() {
		ItemGeneric ig;
		for (int i = sListItems.size() - 1; i > 0; i--) {
			ig = sListItems.get(i);
			if (ig.getTipus() == 1) {
				if (mTotalCorreus <= 2) {
					// no fem res
				} else {
					sListItems.remove(i);
					sListImatges.remove(i);
					i = sListItems.size();
					mTotalCorreus--;
				}
			}
			if (ig.getTipus() == 2) {
				if (mTotalAvisos <= 2) {
					// no fem res
				} else {
					sListItems.remove(i);
					sListImatges.remove(i);
					i = sListItems.size();
					mTotalAvisos--;
				}
			}
		}
	}

	private void eliminarNoticiesAvisos() {
		ItemGeneric ig;
		for (int i = sListItems.size() - 1; i > 0; i--) {
			ig = sListItems.get(i);
			if (ig.getTipus() == 0) {
				if (mTotalNoticies <= 2) {
					// no fem res
				} else {
					sListItems.remove(i);
					sListImatges.remove(i);
					i = sListItems.size();
					mTotalNoticies--;
				}
			}
			if (ig.getTipus() == 2) {
				if (mTotalAvisos <= 2) {
					// no fem res
				} else {
					sListItems.remove(i);
					sListImatges.remove(i);
					i = sListItems.size();
					mTotalAvisos--;
				}
			}
		}
	}

	private void eliminarCorreusNoticies() {
		ItemGeneric ig;
		for (int i = sListItems.size() - 1; i > 0; i--) {
			ig = sListItems.get(i);
			if (ig.getTipus() == 1) {
				if (mTotalCorreus <= 2) {
					// no fem res
				} else {
					sListItems.remove(i);
					sListImatges.remove(i);
					i = sListItems.size();
					mTotalCorreus--;
				}
			}
			if (ig.getTipus() == 0) {
				if (mTotalNoticies <= 2) {
					// no fem res
				} else {
					sListItems.remove(i);
					sListImatges.remove(i);
					i = sListItems.size();
					mTotalNoticies--;
				}
			}
		}
	}

	private boolean enableActualitzarVista() {
		if (updateTime != -1) {
			if (Math.abs(updateTime
					- Calendar.getInstance().getTime().getMinutes()) >= au.TEMPS_REFRESC) {
				return true;
			}
		}
		return false;
	}

	/** Gestió del Menú */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.resum_events, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actualitzaResumEvents:
			if (hihaInternet()) {
				mostrarProgressBarPantalla(mPd, mRLayout);
				obtenirDadesWeb(au);
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.about:
			Intent intentAbout = new Intent(this, ControladorVistaAbout.class);
			startActivity(intentAbout);
			break;
		case R.id.preferencies:
			if (!"".equals(mUsername)) {
				Intent intentPreferencies = new Intent(this,
						PreferenciesUsuari.class);
				intentPreferencies.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentPreferencies.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intentPreferencies);
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.necessita_username, Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.sortir:
			tancarApp();
			break;
		}
		return true;
	}

	@Override
	protected void obtenirDadesWeb() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBackPressed() {
		finish();
	}

}