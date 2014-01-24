package fib.lcfib.raco.Controladors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesActivity;
import fib.lcfib.raco.Connexions.LlistesItems;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.EventHorari;

public class ControladorDatePickerHorari extends
		GestioActualitzaLlistesActivity implements Runnable {

	protected static final int DATE_DIALOG_ID = 0;
	private final String mTAG = "ControladorHorariDatePicker";

	private ArrayList<EventHorari> mHorari = new ArrayList<EventHorari>();
	private Calendar mDiaActual;

	private ProgressDialog mPd;
	private BaseDadesManager mBdm;

	private static String[] sHores = { "8:00-9:00", "9:00-10:00",
			"10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00",
			"14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00",
			"18:00-19:00", "19:00-20:00", "20:00-21:00" };

	private String[] mTitols = new String[3];
	private int[] mContingut = new int[3];
	private List<HashMap<String, String>> mInformacio = new ArrayList<HashMap<String, String>>();

	private ListView mListHorari;
	private TextView mTdiaSetmana;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Mirem si la pantalla pot contenir banner
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// mostrem el DatePIcker perquè seleccionin dia
			if (extras.get("queEs").equals("mostrarDatePicker")) {

				setContentView(R.layout.vista_no_info);

				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
						R.layout.vista_banner);
				
				showDialog(DATE_DIALOG_ID);

			} else if (extras.get("queEs").equals("diaSeleccionat")) {
				setContentView(R.layout.vista_horari_datepicker);

				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
						R.layout.vista_banner);
				gestioHorari(extras.getInt("dia"), extras.getInt("mes"),
						extras.getInt("any"));
			}
		}

		TextView username = (TextView) findViewById(R.id.username_banner);
		username.setText(ControladorTabIniApp.usernameUser);
	}

	@Override
	public void run() {
		Looper.prepare();
		handler.sendEmptyMessage(0);
	}

	protected void gestioHorari(int dia, int mes, int any) {

		// Preparem la vista
		mListHorari = (ListView) findViewById(R.id.list_horari);
		mListHorari.setClickable(false);
		mTdiaSetmana = (TextView) findViewById(R.id.diaSetmana);

		// Posem el titol a la taula
		mTitols[0] = getString(R.string.horari_hora);
		mTitols[1] = getString(R.string.horari_assignatura);
		mTitols[2] = getString(R.string.horari_aula);

		// Assignem els mId's per llavors poder assignar els valors
		mContingut[0] = R.id.hora;
		mContingut[1] = R.id.assignatura_horari;
		mContingut[2] = R.id.classe_horari;

		mHorari.clear();
		mDiaActual = Calendar.getInstance();
		mDiaActual.set(Calendar.YEAR, any);
		mDiaActual.set(Calendar.MONTH, mes);
		mDiaActual.set(Calendar.DAY_OF_MONTH, dia);
		int mesMostrar = mDiaActual.get(Calendar.MONTH);
		mesMostrar++;

		String diaString;
		if (mDiaActual.get(Calendar.DAY_OF_MONTH) < 10) {
			diaString = "0"
					+ String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
		} else {
			diaString = String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
		}

		mTdiaSetmana.setText(diaString + "/" + mesMostrar);

		// Gestionar Base de dades
		mBdm = new BaseDadesManager(this);

		mPd = ProgressDialog.show(this, "",
				getResources().getString(R.string.progressDialogInfo), true,
				true);
		Thread thread = new Thread(this);
		thread.start();
	}

	/** DATE PICKER ZONE */
	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);
		switch (id) {
		case DATE_DIALOG_ID:
			DatePickerDialog datePicker = new DatePickerDialog(this,
					mDateSetListener, cyear, cmonth, cday);
			datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_NEGATIVE) {
								finish();
							}
						}
					});
			datePicker.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			return datePicker;
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int any, int mes, int dia) {
			Intent horariConcret = new Intent(ControladorDatePickerHorari.this,
					ControladorDatePickerHorari.class);
			horariConcret.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			horariConcret.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			horariConcret.putExtra("any", any);
			horariConcret.putExtra("mes", mes);
			horariConcret.putExtra("dia", dia);
			horariConcret.putExtra("queEs", "diaSeleccionat");
			startActivity(horariConcret);
		}
	};

	/** DATE PICKER FI ZONE */

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (mPd != null) {
				mPd.dismiss();
			}
			obtenirDadesBd();
			if (mHorari.isEmpty()) {
				Toast.makeText(getApplicationContext(),
						R.string.no_hi_ha_classe, Toast.LENGTH_LONG).show();
			} else {
				omplirEspaisHorari();
				crearHorari();
			}

			// Gestionar les llistes
			AdaptadorHorariTaula adapter = new AdaptadorHorariTaula(
					ControladorDatePickerHorari.this, mInformacio,
					R.layout.llista_horari, mTitols, mContingut);
			mListHorari.setAdapter(adapter);
		}
	};

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

	private void crearHorari() {

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

	@Override
	protected void actualitzarLlistaBaseDades(LlistesItems lli) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void mostrarLlistes() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void obtenirDadesWeb() {
		// TODO Auto-generated method stub

	}
}
