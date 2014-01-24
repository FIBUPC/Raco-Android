package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
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
import fib.lcfib.raco.Model.Assignatura;
import fib.lcfib.raco.Model.AssignaturesAvisos;
import fib.lcfib.raco.Model.Avis;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.ItemGeneric;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorVistaAssigRaco extends GestioActualitzaLlistesActivity {

	private final String mTAG = "Assignatures_raco";

	private static ArrayList<AssignaturesAvisos> sRacoAssig = new ArrayList<AssignaturesAvisos>();
	public static Assignatura sSelectedAssig = null;
	public static Avis sSelectedAvis = null;
	private AdaptadorAssignaturesRaco mAdaptadorLlista;
	private ListView mListAssig;
	private SharedPreferences sPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_llistes_general_raco);

		sPrefs = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);

		mListAssig = (ListView) findViewById(R.id.vista_llista_raco);
		mRLayout = (RelativeLayout) findViewById(R.id.layoutCarregantDades);
		mPd = (ProgressBar) findViewById(R.id.carregantDades);
		mLLayout = (LinearLayout) findViewById(R.id.vistes_generals_raco);

		mostrarProgressBarBanner();

		sRacoAssig.clear();
		// Gestionar Base de dades
		mBdm = new BaseDadesManager(this);

		mostrarLlistes();

		if (sRacoAssig.isEmpty()) {
			if (hihaInternet()) {
				amagarProgressBarBanner();
				mostrarProgressBarPantalla(mPd, mRLayout);
				obtenirDadesWeb();
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
				amagarProgressBarBanner();
				mostrarVistaNoInformacio(mLLayout);
			}
		}

	}

	@Override
	public void actualitzarLlistaBaseDades(LlistesItems lli) {
		try {
			ArrayList<ItemGeneric> avisos = new ArrayList<ItemGeneric>();
			avisos = lli.getLitemsGenerics();
			ArrayList<Assignatura> assig = new ArrayList<Assignatura>();
			assig = lli.getLassig();
			if (assig.size() > 0) {
				sRacoAssig.clear();
				combinarLlistes(avisos, assig);

				// Actualitzem la Base de dades
				actualitzarTaula(avisos, assig);
				mLLayout.setBackgroundColor(AndroidUtils.REMOVE_BACKGROUND);

			} else {
				amagarProgressBarBanner();
				amagarProgressBarPantalla(mPd, mRLayout);
				Toast.makeText(getApplicationContext(), R.string.errorAssig,
						Toast.LENGTH_LONG).show();
				mostrarVistaNoInformacio(mLLayout);
			}
		} catch (Exception e) {
			amagarProgressBarBanner();
			amagarProgressBarPantalla(mPd, mRLayout);
			if (sRacoAssig.isEmpty()) {
				mostrarVistaNoInformacio(mLLayout);
			}
			Toast.makeText(getApplicationContext(), R.string.errorAssig,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void mostrarLlistes() {

		sRacoAssig.clear();
		obtenirDadesBd();

		amagarProgressBarBanner();
		amagarProgressBarPantalla(mPd, mRLayout);

		if (sRacoAssig.isEmpty()) {
			mostrarVistaNoInformacio(mLLayout);
		} else {
			mAdaptadorLlista = new AdaptadorAssignaturesRaco(this, sRacoAssig);
			mListAssig.setAdapter(mAdaptadorLlista);

			mListAssig.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> av, View view,
						int index, long arg3) {
					AssignaturesAvisos assigAvis = sRacoAssig.get(index);
					// Detall assignatura
					Intent intent;
					if (assigAvis.getData() == null) {
						Assignatura a = new Assignatura(assigAvis
								.getCodiAssig(), assigAvis.getNomAssig(), null,
								0, null, null);
						sSelectedAssig = a;
						if (sSelectedAssig.getIdAssig() == null) {
							//Log.d("asdfa", "aviam si pilla el id de l'assgiantura (que és un numero estrany)");
							//intent = new Intent(".Controladors.VistaError");
							intent = new Intent(".Controladors.ControladorVistaAssigFib.assigInfo");
							intent.putExtra("qui", "raco");
						} else {
							intent = new Intent(
									".Controladors.ControladorVistaAssigFib.assigInfo");
							intent.putExtra("qui", "raco");
						}
						startActivity(intent);

					} else {
						// Detall avís
						Avis a = new Avis(assigAvis.getTitolAvis(), assigAvis
								.getDescripcioAvis(), assigAvis.getmImatge(),
								assigAvis.getData(), AndroidUtils.TIPUS_AVISOS,
								assigAvis.getNomAssig());
						sSelectedAvis = a;
						intent = new Intent(
								".Controladors.ControladorVistaResumEvents.itemInfo");
						intent.putExtra("qui", "assigRaco");
						SharedPreferences sPrefs = getApplicationContext()
								.getSharedPreferences(
										PreferenciesUsuari
												.getPreferenciesUsuari(), 0);
						intent.putExtra("username",
								sPrefs.getString(AndroidUtils.USERNAME, ""));
						startActivity(intent);
					}

				}
			});

		}

	}

	@Override
	protected void obtenirDadesBd() {
		try {
			sRacoAssig.clear();
			mBdm.open();
			ArrayList<AssignaturesAvisos> list = mBdm.getAllAssigRaco();
			for (AssignaturesAvisos aa : list) {
				sRacoAssig.add(aa);
				ArrayList<AssignaturesAvisos> aaList = mBdm.getAvisAssig(aa
						.getIdAssig());
				for (AssignaturesAvisos avis : aaList)
					sRacoAssig.add(avis);
			}
			mBdm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void obtenirDadesWeb() {
		GestioConnexio gc = new GestioConnexio(this);
		AndroidUtils au = AndroidUtils.getInstance();
		// Preparem les URL i les connexions per obtenir les dades
		try {
			// Afegim a la URLAssig la KEY de l'alumne
			String keyURL = sPrefs.getString(au.KEY_ASSIGS_RACO, "");
			URL assigRaco = au.crearURL(au.URL_ASSIGS_RACO + "" + keyURL);

			ParserAndURL pauAssig = new ParserAndURL();
			pauAssig = pauAssig.crearPAU(assigRaco, AndroidUtils.TIPUS_ASSIG,
					null, null);

			keyURL = sPrefs.getString(au.KEY_AVISOS, "");
			URL avisos = au.crearURL(au.URL_AVISOS + "" + keyURL);

			ParserAndURL pauAvis = new ParserAndURL();
			pauAvis = pauAvis.crearPAU(avisos, AndroidUtils.TIPUS_AVISOS, null,
					null);

			gc.execute(pauAssig, pauAvis);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void combinarLlistes(ArrayList<ItemGeneric> avisos,
			ArrayList<Assignatura> assig) {
		Assignatura assignatura;
		Avis avis;
		AssignaturesAvisos actual;
		// Posem les assigs que fa
		for (int i = 0; i < assig.size(); i++) {
			assignatura = assig.get(i);
			actual = new AssignaturesAvisos(assignatura.getNomAssig(),
					assignatura.getCodi(), assignatura.getIdAssig(), null,
					null, null, null);
			sRacoAssig.add(actual);
			for (int j = 0; j < avisos.size(); j++) {
				avis = (Avis) avisos.get(j);
				if (assignatura.getNomAssig().equals(avis.getNomAssig().trim())) {
					actual = new AssignaturesAvisos(avis.getNomAssig().trim(),
							0, avis.getNomAssig(), avis.getTitol(),
							avis.getDescripcio(), avis.getDataPub(),
							avis.getImatge());
					sRacoAssig.add(actual);
				}
			}
		}

	}

	protected void actualitzarTaula(ArrayList<ItemGeneric> avisos,
			ArrayList<Assignatura> assig) {
		Assignatura assignatura;
		Avis avis;
		mBdm.open();
		mBdm.deleteTableAssigRaco();
		mBdm.deleteTableAvisos();

		for (int i = 0; i < assig.size(); i++) {
			assignatura = assig.get(i);
			mBdm.insertItemAssigRaco(assignatura.getCodi(),
					assignatura.getNomAssig(), assignatura.getIdAssig());
		}
		// Avis
		String[] idAssig;
		String idAssigFinal;
		for (int i = 0; i < avisos.size(); i++) {
			avis = (Avis) avisos.get(i);

			// el titol pot ser GRAU-EDSA - TITOL AVIS | EDSA - TITOL AVIS
			idAssig = avis.getTitol().split("-");
			if (idAssig.length == 3) {
				idAssigFinal = idAssig[0].trim() + "-" + idAssig[1].trim();
			} else {
				idAssigFinal = idAssig[0].trim();
			}

			mBdm.insertItemAvis(avis.getTitol(), avis.getDescripcio(),
					avis.getImatge(), avis.getDataPub().toString(),
					avis.getTipus(), idAssigFinal);
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
			startActivity(new Intent(ControladorVistaAssigRaco.this,
					ControladorTabIniApp.class));
			break;

		}
		return true;
	}
}
