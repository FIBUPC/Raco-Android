package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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
import fib.lcfib.raco.Model.Assignatura;
import fib.lcfib.raco.Model.BaseDadesManager;


public class ControladorVistaAssigFib extends GestioActualitzaLlistesActivity {

	private final String mTAG = "Assignatures_fib";

	private static ArrayList<Assignatura> sListAssigFib = new ArrayList<Assignatura>();
	private static ArrayList<Assignatura> sListAssigFibSort = new ArrayList<Assignatura>();
	public static Assignatura sSelectedAssig = null;
	private AdaptadorAssignaturesFib mAdaptadorLlista;
	private ListView mListAssig;
	private EditText mEd;
	private int textlength = 0;
	private ImageView separator;
	private TextView assignaturesGrau;
	private boolean searching = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_assignatures_fib);
		mRLayout = (RelativeLayout) findViewById(R.id.layoutCarregantDadesAssigFib);
		mPd = (ProgressBar) findViewById(R.id.carregantDadesAssigFib);
		mListAssig = (ListView) findViewById(R.id.listAssig_fib);
		mEd = (EditText) findViewById(R.id.cercar);
		separator = (ImageView) findViewById(R.id.separator_blau);
		assignaturesGrau = (TextView) findViewById(R.id.assignatures_grau_titol);
		mLLayout = (LinearLayout) findViewById(R.id.vista_assig_fib);

		mostrarProgressBarBanner();

		sListAssigFib.clear();
		// Gestionar Base de dades
		mBdm = new BaseDadesManager(this);

		// Busquem a base de dades si hi ha info
		mostrarLlistes();

		if (sListAssigFib.isEmpty()) {
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

		mEd.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textlength = mEd.getText().length();
				sListAssigFibSort.clear();
				for (int i = 0; i < sListAssigFib.size(); i++) {
					if (textlength <= sListAssigFib.get(i).getIdAssig().length()) {
						if (mEd.getText().toString().equalsIgnoreCase((String) sListAssigFib.get(i).getIdAssig().subSequence(0, textlength))) {
							sListAssigFibSort.add(sListAssigFib.get(i));
						}
						searching = true;
					} else if (textlength == 0) {
						sListAssigFibSort.addAll(sListAssigFib);
						searching = false;
					}
				}
				mAdaptadorLlista = new AdaptadorAssignaturesFib(ControladorVistaAssigFib.this, sListAssigFibSort);
				mListAssig.setAdapter(mAdaptadorLlista);
			}
		});
	}

	@Override
	public void actualitzarLlistaBaseDades(LlistesItems lli) {
		try {

			if (lli.getLassig().size() > 0) {
				sListAssigFib.clear();
				sListAssigFib = (ArrayList<Assignatura>) lli.getLassig();

				/** Actualitzem la Base de dades */
				actualitzarTaula();
				mLLayout.setBackgroundDrawable(null);
			} else {
				amagarProgressBarBanner();
				amagarProgressBarPantalla(mPd, mRLayout);
				mostrarVistaNoInformacio(mLLayout);
			}
		} catch (Exception e) {
			amagarProgressBarBanner();
			amagarProgressBarPantalla(mPd, mRLayout);
			mostrarVistaNoInformacio(mLLayout);
			Toast.makeText(getApplicationContext(), R.string.errorAssig,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void mostrarLlistes() {

		sListAssigFib.clear();
		obtenirDadesBd();

		amagarProgressBarBanner();
		amagarProgressBarPantalla(mPd, mRLayout);

		// Si es 0 no eliminem perquè haurem d'anar a buscar dades a la web
		if (sListAssigFib.isEmpty()) {
			mostrarVistaNoInformacio(mLLayout);
			mEd.setVisibility(AndroidUtils.INVISIBLE);
			separator.setVisibility(AndroidUtils.INVISIBLE);
			assignaturesGrau.setVisibility(AndroidUtils.INVISIBLE);

		} else {
			amagarMostrarVistaNoInformacio(mLLayout);
			mEd.setVisibility(AndroidUtils.VISIBLE);
			separator.setVisibility(AndroidUtils.VISIBLE);
			assignaturesGrau.setVisibility(AndroidUtils.VISIBLE);

			mAdaptadorLlista = new AdaptadorAssignaturesFib(this, sListAssigFib);
			mListAssig.setAdapter(mAdaptadorLlista);

			mListAssig.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> av, View view, int index, long arg3) {
					if (searching) sSelectedAssig = sListAssigFibSort.get(index);
					else sSelectedAssig = sListAssigFib.get(index);
					
					
					//sSelectedAssig = sListAssigFib.get(index);
					Intent intent;
					if (sSelectedAssig.getIdAssig() != null) {
						// Cridem a l'activitat per mosrtar info assig
						intent = new Intent(".Controladors.ControladorVistaAssigFib.assigInfo");
						intent.putExtra("qui", "fib");
					} else {
						intent = new Intent(".Controladors.VistaError");
					}
					startActivity(intent);
				}
			});

		}
	}

	@Override
	protected void obtenirDadesBd() {
		try {
			sListAssigFib.clear();
			sListAssigFibSort.clear();
			mBdm.open();

			sListAssigFib = mBdm.getAllAssigFib();
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
			URL assigFib = au.crearURL(au.URL_ASSIGS_FIB);
			ParserAndURL pauN = new ParserAndURL();
			pauN = pauN
					.crearPAU(assigFib, AndroidUtils.TIPUS_ASSIG, null, null);
			gc.execute(pauN);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected void actualitzarTaula() {
		Assignatura a;

		mBdm.open();
		mBdm.deleteTableAssigFib();

		for (int i = 0; i < sListAssigFib.size(); i++) {
			a = sListAssigFib.get(i);
			mBdm.insertItemAssigFib(a.getCodi(), a.getNomAssig(),
					a.getIdAssig(), a.getCredits());
		}
		mBdm.close();
	}

	/** Gestió del Menú */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actualitza_vista, menu);
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
		}
		return true;
	}

}
