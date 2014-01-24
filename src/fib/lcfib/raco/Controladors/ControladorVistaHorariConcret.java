package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesActivity;
import fib.lcfib.raco.Connexions.GestioConnexio;
import fib.lcfib.raco.Connexions.LlistesItems;
import fib.lcfib.raco.Connexions.ParserAndURL;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.EventHorari;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorVistaHorariConcret extends
		GestioActualitzaLlistesActivity {

	protected static final int DATE_DIALOG_ID = 0;
	private final String mTAG = "ControladorHorariConcret";
	private ArrayList<EventHorari> mHorari = new ArrayList<EventHorari>();
	private Calendar mDiaActual;
	private int mDiesConsultats;
	private SharedPreferences sPrefs;

	private List<HashMap<String, String>> mInformacio = new ArrayList<HashMap<String, String>>();

	private ListView mListHorari;
	private ImageButton mSeguent;
	private ImageButton mAnterior;
	private ImageButton mEscollirDia;
	private TextView mTdiaSetmana;
	private TextView mAssig;
	private TextView mHora;
	private TextView mAula;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.vista_horari);

		sPrefs = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);

		ControladorTabIniApp.carregant.setVisibility(ProgressBar.VISIBLE);

		mListHorari = (ListView) findViewById(R.id.list_horari);
		mListHorari.setClickable(false);
		mTdiaSetmana = (TextView) findViewById(R.id.diaSetmana);
		mHora = (TextView) findViewById(R.id.hora);
		mAula = (TextView) findViewById(R.id.classe_horari);
		mAssig = (TextView) findViewById(R.id.assignatura_horari);

		crearBotonsAbansDespres();
		// Posem el titol a la taula
		mTitols[0] = getString(R.string.horari_hora);
		mTitols[1] = getString(R.string.horari_assignatura);
		mTitols[2] = getString(R.string.horari_aula);
		// Assignem els mId's per llavors poder assignar els valors
		mContingut[0] = R.id.hora;
		mContingut[1] = R.id.assignatura_horari;
		mContingut[2] = R.id.classe_horari;

		mHorari.clear();

		// Gestionar Base de dades
		mBdm = new BaseDadesManager(this);

		// agafem els parametres que ens passen des de ControladorVistaHorari o
		// la mateixa classe
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			mDiesConsultats = extras.getInt("diesConsultats");

			int dia = extras.getInt("dia");
			int mes = extras.getInt("mes");
			int any = extras.getInt("any");
			mDiaActual = Calendar.getInstance();
			mDiaActual.set(any, mes, dia);

			if (extras.getString("queEs").equals("abans")) {
				mDiaActual.setTimeInMillis(mDiaActual.getTimeInMillis()
						- AndroidUtils.MILLSECS_PER_DIA);
			} else {
				mDiaActual.setTimeInMillis(mDiaActual.getTimeInMillis()
						+ AndroidUtils.MILLSECS_PER_DIA);
			}

			mes = mDiaActual.get(Calendar.MONTH) + 1;

			if (extras.getString("queEs").equals("abans")
					|| extras.getString("queEs").equals("despres")) {

				String diaSet = buscarNomDia(mDiaActual
						.get(Calendar.DAY_OF_WEEK));
				String diaString;
				if (mDiaActual.get(Calendar.DAY_OF_MONTH) < 10) {
					diaString = "0"
							+ String.valueOf(mDiaActual
									.get(Calendar.DAY_OF_MONTH));
				} else {
					diaString = String.valueOf(mDiaActual
							.get(Calendar.DAY_OF_MONTH));
				}

				mTdiaSetmana.setText(diaSet + " | " + diaString + "/" + mes);
			}
		} else {
			Toast.makeText(getApplicationContext(), R.string.errorHorari,
					Toast.LENGTH_LONG).show();
			ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
		}
	}

	/**
	 * Quan tornen a la classe només carreguem al informació que hi havia a la
	 * BD
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mostrarLlistes();
	}

	@Override
	protected void mostrarLlistes() {

		mHorari.clear();
		obtenirDadesBd();
		omplirEspaisHorari();
		crearHorari();

		// tornem a mostrar els botons
		mSeguent.setVisibility(AndroidUtils.VISIBLE);
		mAnterior.setVisibility(AndroidUtils.VISIBLE);
		mEscollirDia.setVisibility(AndroidUtils.VISIBLE);
		mTdiaSetmana.setVisibility(AndroidUtils.VISIBLE);
		mHora.setVisibility(AndroidUtils.VISIBLE);
		mAula.setVisibility(AndroidUtils.VISIBLE);
		mAssig.setVisibility(AndroidUtils.VISIBLE);

		ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
		// Gestionar les llistes
		AdaptadorHorariTaula adapter = new AdaptadorHorariTaula(this,
				mInformacio, R.layout.llista_horari, mTitols, mContingut);
		mListHorari.setAdapter(adapter);
	}

	@Override
	protected void obtenirDadesBd() {
		try {
			mBdm.open();
			mHorari = mBdm.getDiaHorari(
					Integer.toString(mDiaActual.get(Calendar.DAY_OF_MONTH)),
					Integer.toString(mDiaActual.get(Calendar.MONTH)),
					Integer.toString(mDiaActual.get(Calendar.YEAR)));
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
			URL not = au.crearURL(au.URL_HORARI_RACO);
			ParserAndURL pauHor = new ParserAndURL();
			pauHor = pauHor.crearPAU(not, AndroidUtils.TIPUS_HORARI_RACO, null,
					null);

			gc.execute(pauHor);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected void actualitzarTaula() {
		EventHorari e;
		mBdm.open();

		for (int i = 0; i < mHorari.size(); i++) {
			e = mHorari.get(i);
			mBdm.insertItemHorari(e.getmHoraInici(), e.getmHoraFi(),
					e.getmAssignatura(), e.getmAula(), e.getmDia(),
					e.getmMes(), e.getmAny());
		}
		mBdm.close();
	}

	private String buscarNomDia(int dia) {
		switch (dia) {
		case 1:
			return getResources().getString(R.string.diumenge);
		case 2:
			return getResources().getString(R.string.dilluns);
		case 3:
			return getResources().getString(R.string.dimarts);
		case 4:
			return getResources().getString(R.string.dimecres);
		case 5:
			return getResources().getString(R.string.dijous);
		case 6:
			return getResources().getString(R.string.divendres);
		case 7:
			return getResources().getString(R.string.dissabte);
		default:
			return " ";
		}
	}

	private void omplirEspaisHorari() {
		int diferencia = Math.abs(mHorari.size() - sHores.length);
		Calendar cDataNoValida = Calendar.getInstance();
		cDataNoValida.set(Calendar.YEAR, mDiaActual.get(Calendar.YEAR) + 1);
		cDataNoValida.set(Calendar.MONTH, mDiaActual.get(Calendar.MONTH));
		cDataNoValida.set(Calendar.DAY_OF_MONTH, mDiaActual.get(Calendar.DAY_OF_MONTH));
		Date dataNoValida = cDataNoValida.getTime();
		for (int i = 0; i < diferencia; i++) {
			EventHorari ehTemp = new EventHorari("23:00", "23:00", "", "",
					dataNoValida.getDay(), dataNoValida.getMonth(),
					dataNoValida.getYear());
			mHorari.add(ehTemp);
		}
	}

	protected void crearHorari() {

		// Per si ja hem consultat al principi no cal tornar-hi
		if (mHorari.isEmpty()) {
			obtenirDadesBd(); // Buscar dia actual a la BD
		}

		if (mHorari.isEmpty()) {
			Toast.makeText(getApplicationContext(), R.string.no_hi_ha_classe,
					Toast.LENGTH_LONG).show();
		} else {
			String[] horaAct; // SPLIT: el format de la taula es XX:00-ZZ:00 i
								// aixi tindrem el XX:00 i el ZZ:00 per separat
			String[] horaActInici;
			String[] horaActFi;

			mInformacio.clear();

			// Assegurem que els elements del vector estan ordenats per
			// horaInici
			ordenarPerData();
			int i = 0;
			String assignatura, aula;
			while (i < sHores.length) {

				// Vector hores
				horaAct = sHores[i].split("-");
				horaActInici = horaAct[0].split(":");
				horaActFi = horaAct[1].split(":");

				ArrayList<EventHorari> classeEnUnaHora = obtenirElementsEnHora(
						Integer.parseInt(horaActInici[0]),
						Integer.parseInt(horaActFi[0]));

				if (classeEnUnaHora.size() > 0) {
					int m = 0;
					assignatura = classeEnUnaHora.get(m).getmAssignatura();
					aula = "";
					// si no hi ha hores solapades
					if (classeEnUnaHora.size() == 1) {
						// mirem ara si hi ha més d'una classe on es fa aquella
						// assignatura
						String[] aulaV = classeEnUnaHora.get(m).getmAula()
								.split(",");
						aula = aulaV[0];
						if (aulaV.length > 1) {
							for (int k = 1; k < aulaV.length; k++) {
								aula = aula + "\n" + aulaV[k];
							}
						}
					} else {
						aula = classeEnUnaHora.get(m).getmAula();
						for (m = 1; m < classeEnUnaHora.size(); m++) {
							assignatura = assignatura + "\n"
									+ classeEnUnaHora.get(m).getmAssignatura();
							aula = aula + "\n"
									+ classeEnUnaHora.get(m).getmAula();
						}
					}
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(mTitols[0], sHores[i]);
					map.put(mTitols[1], assignatura);
					map.put(mTitols[2], aula);
					mInformacio.add(i, map);
					i++;
				} else {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(mTitols[0], sHores[i]);
					map.put(mTitols[1], "");
					map.put(mTitols[2], "");
					mInformacio.add(i, map);
					i++;
				}
			}
		}
	}

	private ArrayList<EventHorari> obtenirElementsEnHora(int inici, int fi) {
		ArrayList<EventHorari> eh = new ArrayList<EventHorari>();

		EventHorari event;
		String[] horaEventInici;
		String[] horaEventFi;

		for (int i = 0; i < mHorari.size(); i++) {
			event = mHorari.get(i);
			horaEventInici = event.getmHoraInici().split(":");
			horaEventFi = event.getmHoraFi().split(":");
			int horaIniciEvent = Integer.parseInt(horaEventInici[0]);
			int horaFiEvent = Integer.parseInt(horaEventFi[0]);

			if (inici >= horaIniciEvent && fi <= horaFiEvent) {
				EventHorari evAux = new EventHorari();
				evAux.setmAssignatura(mHorari.get(i).getmAssignatura());
				evAux.setmAny(mHorari.get(i).getmAny());
				evAux.setmAula(mHorari.get(i).getmAula());
				evAux.setmDia(mHorari.get(i).getmDia());
				evAux.setmHoraFi(mHorari.get(i).getmHoraFi());
				evAux.setmHoraInici(mHorari.get(i).getmHoraInici());
				evAux.setmMes(mHorari.get(i).getmMes());
				eh.add(evAux);
			}
		}
		return eh;
	}

	private void ordenarPerData() {
		int minIndex;
		EventHorari rig, rag;
		int n = mHorari.size();
		String[] horaEventIniciRig;
		String[] horaEventIniciRag;
		for (int i = 0; i < n - 1; i++) {
			minIndex = i;
			for (int j = i + 1; j < n; j++) {
				rig = mHorari.get(j);
				rag = mHorari.get(minIndex);
				horaEventIniciRig = rig.getmHoraInici().split(":");
				horaEventIniciRag = rag.getmHoraInici().split(":");
				if (Integer.parseInt(horaEventIniciRig[0]) < (Integer
						.parseInt(horaEventIniciRag[0]))) {
					minIndex = j;
				}
			}
			if (minIndex != i) {
				Collections.swap(mHorari, i, minIndex);
			}
		}
	}

	private void crearBotonsAbansDespres() {

		mSeguent = (ImageButton) findViewById(R.id.seguent);
		mSeguent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (mDiesConsultats + 1 == AndroidUtils.DIES_HORARI_SEGUENT) {
					Toast.makeText(getApplicationContext(),
							R.string.maxim_dies_horari, Toast.LENGTH_LONG)
							.show();
					ControladorTabIniApp.carregant
							.setVisibility(ProgressBar.GONE);
				} else {
					Intent horariConcret = new Intent(getParent(),
							ControladorVistaHorariConcret.class);
					horariConcret.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					horariConcret.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					horariConcret.putExtra("any", mDiaActual.get(Calendar.YEAR));
					horariConcret.putExtra("mes",
							mDiaActual.get(Calendar.MONTH));
					horariConcret.putExtra("dia",
							mDiaActual.get(Calendar.DAY_OF_MONTH));
					horariConcret.putExtra("queEs", "despres");
					horariConcret.putExtra("diesConsultats",
							mDiesConsultats + 1);
					ControladorTabGroupHorari parentActivity = (ControladorTabGroupHorari) getParent();
					parentActivity.startChildActivity(
							"ControladorVistaHorariConcret", horariConcret);
				}
			}
		});

		mAnterior = (ImageButton) findViewById(R.id.abans);
		mAnterior.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mDiesConsultats - 1 == AndroidUtils.DIES_HORARI_ANTERIOR) {
					Toast.makeText(getApplicationContext(),
							R.string.maxim_dies_horari, Toast.LENGTH_LONG)
							.show();
					ControladorTabIniApp.carregant
							.setVisibility(ProgressBar.GONE);
				} else {
					Intent horariConcret = new Intent(getParent(),
							ControladorVistaHorariConcret.class);
					horariConcret.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					horariConcret.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					horariConcret.putExtra("any", mDiaActual.get(Calendar.YEAR));
					horariConcret.putExtra("mes",
							mDiaActual.get(Calendar.MONTH));
					horariConcret.putExtra("dia",
							mDiaActual.get(Calendar.DAY_OF_MONTH));
					horariConcret.putExtra("queEs", "abans");
					horariConcret.putExtra("diesConsultats",
							mDiesConsultats - 1);
					ControladorTabGroupHorari parentActivity = (ControladorTabGroupHorari) getParent();
					parentActivity.startChildActivity(
							"ControladorVistaHorariConcret", horariConcret);
				}
			}
		});

		/** El tema del DatePicker */
		mEscollirDia = (ImageButton) findViewById(R.id.escollirDia);
		mEscollirDia.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent datePicker = new Intent(
						ControladorVistaHorariConcret.this,
						ControladorDatePickerHorari.class);
				SharedPreferences sPrefs = getApplicationContext()
						.getSharedPreferences(
								PreferenciesUsuari.getPreferenciesUsuari(), 0);
				datePicker.putExtra("username",
						sPrefs.getString(AndroidUtils.USERNAME, ""));
				datePicker.putExtra("queEs", "mostrarDatePicker");
				startActivity(datePicker);
			}
		});
	}

	@Override
	protected void actualitzarLlistaBaseDades(LlistesItems lli) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) { // add menus or inflate
														// here
		return true;
	}

	/** Gestió del Menú */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.zona_raco, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actualitza:
			if (hihaInternet()) {
				mBdm.open();
				mBdm.deleteTableHorari();
				mBdm.close();
				Toast.makeText(getParent().getBaseContext(),
						R.string.infoActualitzaHorari, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.logout:
			sPrefs.edit().clear().commit();
			Toast.makeText(getParent(), R.string.logout_correcte,
					Toast.LENGTH_SHORT).show();
			mBdm.open();
			mBdm.deleteTablesLogout();
			mBdm.close();
			startActivity(new Intent(getParent(), ControladorTabIniApp.class));
			break;

		}
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
