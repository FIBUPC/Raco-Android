package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import edu.emory.mathcs.backport.java.util.Collections;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesActivity;
import fib.lcfib.raco.Connexions.GestioConnexio;
import fib.lcfib.raco.Connexions.LlistesItems;
import fib.lcfib.raco.Connexions.ParserAndURL;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.EventAgenda;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorVistaAgendaRaco extends GestioActualitzaLlistesActivity {

	private final String mTAG = "Agenda";

	private static ArrayList<EventAgenda> sAgenda = new ArrayList<EventAgenda>();
	public AdaptadorEventsAgenda adaptadorLlista;
	private ListView mListAgenda;
	private SharedPreferences sPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_llistes_general_raco);

		sPrefs = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);

		mPd = (ProgressBar) findViewById(R.id.carregantDades);
		mListAgenda = (ListView) findViewById(R.id.vista_llista_raco);
		mRLayout = (RelativeLayout) findViewById(R.id.layoutCarregantDades);
		mLLayout = (LinearLayout) findViewById(R.id.vistes_generals_raco);

		mostrarProgressBarBanner();

		mListAgenda.setClickable(false);
		sAgenda.clear();

		// Gestionar Base de dades
		mBdm = new BaseDadesManager(this);

		mostrarLlistes();

		if (sAgenda.isEmpty()) {
			if (hihaInternet()) {
				mostrarProgressBarPantalla(mPd, mRLayout);
				amagarProgressBarBanner();
				obtenirDadesWeb();
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
				amagarProgressBarBanner();
			}
		}
	}

	@Override
	public void actualitzarLlistaBaseDades(LlistesItems lli) {
		try {
			if (lli.getLeventAgenda().size() > 0) {
				sAgenda.clear();
				sAgenda = (ArrayList<EventAgenda>) lli.getLeventAgenda();
				// Actualitzem la Base de dades
				actualitzarTaula();
				mLLayout.setBackgroundColor(AndroidUtils.REMOVE_BACKGROUND);
			} else {
				Toast.makeText(getApplicationContext(), R.string.errorAgenda,
						Toast.LENGTH_LONG).show();
				amagarProgressBarBanner();
				amagarProgressBarPantalla(mPd, mRLayout);
				mostrarVistaNoInformacio(mLLayout);
			}

		} catch (Exception e) {
			amagarProgressBarBanner();
			amagarProgressBarPantalla(mPd, mRLayout);
			if (sAgenda.isEmpty()) {
				mostrarVistaNoInformacio(mLLayout);
			}
			Toast.makeText(getApplicationContext(), R.string.errorAgenda,
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void mostrarLlistes() {
		
		Calendar avui = Calendar.getInstance();

		sAgenda.clear();
		obtenirDadesBd();

		amagarProgressBarBanner();
		amagarProgressBarPantalla(mPd, mRLayout);

		int posicio = -1;
		EventAgenda ea = new EventAgenda(getString(R.string.avui), avui.getTime(), avui.getTime());
		if (!sAgenda.isEmpty()) {
			EventAgendaComparator comparator = new EventAgendaComparator();
			Collections.sort(sAgenda, comparator);
			posicio = buscarDiaActual(ea);
		} else {
			mostrarVistaNoInformacio(mLLayout);
		}

		// Invertim la llista perque surti de futur a passat
		Collections.reverse(sAgenda);

		// Gestionar les llistes
		adaptadorLlista = new AdaptadorEventsAgenda(this, sAgenda);
		mListAgenda.setAdapter(adaptadorLlista);

		// Coloquem avui al mig de la vista
		if (posicio != -1) {
			Display display = getWindowManager().getDefaultDisplay();
			int height = display.getHeight();
			mListAgenda.setSelectionFromTop(sAgenda.size() - posicio,
					height / 2);
		}
	}

	protected class EventAgendaComparator implements Comparator<EventAgenda> {
		@Override
		public int compare(EventAgenda o1, EventAgenda o2) {
			return o1.getData().compareTo(o2.getData());
		}
	}

	@Override
	protected void obtenirDadesBd() {
		try {
			mBdm.open();
			sAgenda = mBdm.getAllAgenda();
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
			String keyURL = sPrefs.getString(au.KEY_AGENDA_RACO_CAL, "");
			URL agenda = au.crearURL(au.URL_AGENDA_RACO + "" + keyURL);
			ParserAndURL pauAg = new ParserAndURL();
			pauAg = pauAg.crearPAU(agenda, AndroidUtils.TIPUS_AGENDA_RACO,
					null, null);
			gc.execute(pauAg);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected void actualitzarTaula() {
		EventAgenda e;
		mBdm.open();
		mBdm.deleteTableAgenda();

		for (int i = 0; i < sAgenda.size(); i++) {
			e = sAgenda.get(i);
			mBdm.insertItemAgenda(e.getTitol(), e.getData().toString(), e.getDataFi().toString());
		}
		mBdm.close();
	}
	
	private int buscarDiaActual(EventAgenda ea) {
		EventAgenda actual;
		int i = -1;
		while (i < sAgenda.size() - 1) {
			++i;
			actual = sAgenda.get(i);
			if (!actual.getData().before(ea.getData()))
				break;
		}
		sAgenda.add(i, ea);
		return i;
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
			startActivity(new Intent(ControladorVistaAgendaRaco.this,
					ControladorTabIniApp.class));
			break;

		}
		return true;
	}

}
