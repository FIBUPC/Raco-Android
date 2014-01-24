package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
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
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesActivity;
import fib.lcfib.raco.Connexions.GestioConnexio;
import fib.lcfib.raco.Connexions.LlistesItems;
import fib.lcfib.raco.Connexions.ParserAndURL;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.ItemGeneric;
import fib.lcfib.raco.Model.Noticia;

public class ControladorVistaNoticiesFib extends
		GestioActualitzaLlistesActivity {

	private String mTAG = "ControladorVistaNoticiesFib";

	// Llistes que tindran les dades
	public static ArrayList<ItemGeneric> sListNoticies = new ArrayList<ItemGeneric>();
	public static ArrayList<String> sListImatges = new ArrayList<String>();

	// Llista de la vista
	public ListView sLlistaVista;
	public AdaptadorResumEvents_NoticiesFib sAdaptadorLlista;
	public static ItemGeneric sItemSeleccionat = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.vista_llistes_general);
		sLlistaVista = (ListView) findViewById(R.id.vista_inici_llista);
		mRLayout = (RelativeLayout) findViewById(R.id.layoutCarregantDadesGenerals);
		mPd = (ProgressBar) findViewById(R.id.carregantDadesGenerals);
		mLLayout = (LinearLayout) findViewById(R.id.vista_llistes_generals);

		mBdm = new BaseDadesManager(this);

		resetLlistes();
		mostrarLlistes();
	}

	protected void resetLlistes() {
		sListNoticies.clear();
		sListImatges.clear();
	}

	@Override
	public void actualitzarLlistaBaseDades(LlistesItems lli) {

		try {

			if (lli.getLitemsGenerics().size() > 0) {
				sListNoticies.clear();
				sListNoticies = (ArrayList<ItemGeneric>) lli
						.getLitemsGenerics();
				sListImatges = (ArrayList<String>) lli.getLimatges();

				// Actualitzem la Base de dades
				ActualitzarTaula();

			} else {
				amagarProgressBarPantalla(mPd, mRLayout);
			}
		} catch (Exception e) {
			amagarProgressBarPantalla(mPd, mRLayout);
			mostrarVistaNoInformacio(mLLayout);
			Toast.makeText(getApplicationContext(), R.string.errorNoticies,
					Toast.LENGTH_LONG).show();
		}
	}

	protected void obtenirDadesBd() {
		try {
			mBdm.open();
			sListNoticies = mBdm.getAllNoticies();
			for (ItemGeneric n: sListNoticies)
				sListImatges.add(n.getImatge());
			mBdm.close();
		} catch (SQLException e) {
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

	protected void obtenirDadesWeb() {
		GestioConnexio gc = new GestioConnexio(this);
		AndroidUtils au = AndroidUtils.getInstance();

		// Preparem les URL i les connexions per obtenir les dades
		try {
			URL not = au.crearURL(au.URL_NOTICIES);
			ParserAndURL pauN = new ParserAndURL();
			pauN = pauN.crearPAU(not, AndroidUtils.TIPUS_NOTICIA, null, null);

			gc.execute(pauN);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	protected void mostrarLlistes() {

		resetLlistes();
		obtenirDadesBd();

		amagarProgressBarPantalla(mPd, mRLayout);

		if (sListNoticies.isEmpty()) {
			Toast.makeText(getApplicationContext(), R.string.noHiHaNoticies,
					Toast.LENGTH_LONG).show();
			mostrarVistaNoInformacio(mLLayout);
		} else {
			// Gestionar les llistes
			sAdaptadorLlista = new AdaptadorResumEvents_NoticiesFib(this,
					tractarImatges(), sListNoticies);
			sLlistaVista.setAdapter(sAdaptadorLlista);

			// Per cada element que cliquin
			sLlistaVista.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> av, View view,
						int index, long arg3) {

					sItemSeleccionat = sListNoticies.get(index);

					Intent intent = new Intent(
							".Controladors.ControladorVistaResumEvents.itemInfo");
					intent.putExtra("qui", "noticiesFib");
					startActivity(intent);
				}
			});

		}
	}

	protected void ActualitzarTaula() {
		Noticia ig;

		mBdm.open();
		mBdm.deleteTableNoticies();

		for (int i = 0; i < sListNoticies.size(); i++) {
			ig = (Noticia) sListNoticies.get(i);
			mBdm.insertItemNoticia(ig.getTitol(), ig.getDescripcio(),
					sListImatges.get(i), ig.getDataPub().toString(),
					ig.getTipus(), ig.getmLink());
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
