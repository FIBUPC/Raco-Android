package fib.lcfib.raco.Controladors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.JsonParser;
import fib.lcfib.raco.Model.Assignatura;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.PreferenciesUsuari;
import fib.lcfib.raco.Model.Professors;

public class ControladorVistaInformacioAssignatura extends Activity implements
		Runnable {

	private String mTAG = "InformacioAssignaturaFIB";
	private ArrayList<String> mProf = new ArrayList<String>();
	private static ArrayList<Professors> sListProfessor = new ArrayList<Professors>();
	private BaseDadesManager mBdm;
	private Assignatura mAssig;
	private ProgressDialog mPd;
	private ScrollView mScrollLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// Mirem si la pantalla pot contenir banner
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

			setContentView(R.layout.vista_assignatura_fib_info);

			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.vista_banner);

			TextView username = (TextView) findViewById(R.id.username_banner);
			username.setText(ControladorTabIniApp.usernameUser);

			Bundle extras = getIntent().getExtras();
			if (extras != null) {

				if (extras.getString("qui").equals("raco")) {
					mAssig = fib.lcfib.raco.Controladors.ControladorVistaAssigRaco.sSelectedAssig;

				} else {// VistaAssigFib
					mAssig = fib.lcfib.raco.Controladors.ControladorVistaAssigFib.sSelectedAssig;
				}

			}
			mScrollLayout = (ScrollView) findViewById(R.id.vista_assignatura_info_fib);
			// ScrollView a dalt de tot de la view
			mScrollLayout.smoothScrollBy(0, 0);
			mBdm = new BaseDadesManager(this);

			// Mirem si la informacio estava a la BD
			obtenirDadesBd();

			if (sListProfessor.isEmpty()
					&& !"PFC-EI".equals(mAssig.getIdAssig())) {
				carregarInformació();
			} else {
				handler.sendEmptyMessage(0);
			}
		} catch (Exception e) {
			finish();
		}
	}

	protected void obtenirDadesBd() {
		try {
			mBdm.open();
			sListProfessor.clear();
			mProf.clear();
			sListProfessor = mBdm.getProfessorsAssig(Integer.toString(mAssig.getCodi()));
			mAssig.setProfessors(sListProfessor);
			mAssig.setCredits(mBdm.getCreditsFib(Integer.toString(mAssig.getCodi())));
			mAssig.setObjectius(mBdm.getObjectiuAssig(Integer.toString(mAssig.getCodi())));
			mBdm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Looper.prepare();
		obtenirDadesWeb();
	}

	protected void obtenirDadesWeb() {
		JsonParser jp = JsonParser.getInstance();
		URL url1 = null;
		AndroidUtils au = AndroidUtils.getInstance();
		try {
			url1 = new URL(au.URL_INFO_ASSIG + "&id=" + mAssig.getCodi());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Assignatura a = new Assignatura();
		a = jp.parserInfoAssig(url1);
		if (a != null) {
			actualitzarBd(a);
			handler.sendEmptyMessage(0);
		} else {
			handler.sendEmptyMessage(1);
			// En principi esta controlat a VistaAssigRaco però per si de cas...
			mPd.cancel();
			mPd.dismiss();
			return;
		}
	}

	protected void actualitzarBd(Assignatura a) {
		mBdm.open();
		ArrayList<Long> list = new ArrayList<Long> ();
		
		list = mBdm.getProfessorPrimaryKey(Integer.toString(mAssig.getCodi()));
		for (Long item: list)
			mBdm.deleteItemProfessor(item);
		
		list = mBdm.getObjectiuPrimaryKey(Integer.toString(mAssig.getCodi()));
		for (Long item: list) 
			mBdm.deleteItemObjectiu(item);

		mBdm.updateInfoAssig(Integer.toString(mAssig.getCodi()), Integer.toString(a.getCredits()));
		
		for (Professors p: a.getProfessors())
			mBdm.insertItemProfessors(p.getNom(), p.getCorreu(), Integer.toString(mAssig.getCodi()));
		
		for (String objectiu: a.getObjectius())
			mBdm.insertItemObjectiu(Integer.toString(mAssig.getCodi()), objectiu);

		mBdm.close();
	}

	private void carregarInformació() {
		mPd = ProgressDialog.show(this, "",
				getResources().getString(R.string.progressDialogInfo), true,
				false);
		mPd.setCancelable(false);
		Thread thread = new Thread(this);
		thread.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (mPd != null) {
				mPd.dismiss();
			}

			TextView tnomAssig = (TextView) findViewById(R.id.nomAssig);
			TextView tprofessor = (TextView) findViewById(R.id.professor);
			TextView tobjectiu = (TextView) findViewById(R.id.objectiu);
			TextView tcredits = (TextView) findViewById(R.id.credits);
			ListView Lprofessors = (ListView) findViewById(R.id.professors);
			TextView objectiu = (TextView) findViewById(R.id.objectius);

			if (msg.what == 1) {
				tnomAssig.setVisibility(AndroidUtils.INVISIBLE);
				tcredits.setVisibility(AndroidUtils.INVISIBLE);
				Lprofessors.setVisibility(AndroidUtils.INVISIBLE);
				tprofessor.setVisibility(AndroidUtils.INVISIBLE);
				objectiu.setVisibility(AndroidUtils.INVISIBLE);
				tobjectiu.setVisibility(AndroidUtils.INVISIBLE);

				mScrollLayout.setBackgroundColor(R.color.noInfo);
				mScrollLayout.setBackgroundResource(R.drawable.vista_no_info);
				Toast.makeText(getApplicationContext(),
						R.string.errorObtenirInfo, Toast.LENGTH_LONG).show();
			} else {

				obtenirDadesBd();
				String nomAssig = "";
				nomAssig = mAssig.getNomAssig();

				tnomAssig.setVisibility(AndroidUtils.VISIBLE);
				tcredits.setVisibility(AndroidUtils.VISIBLE);
				tobjectiu.setVisibility(AndroidUtils.VISIBLE);
				objectiu.setVisibility(AndroidUtils.VISIBLE);
				tprofessor.setVisibility(AndroidUtils.VISIBLE);
				Lprofessors.setVisibility(AndroidUtils.VISIBLE);

				mScrollLayout.setBackgroundDrawable(null);
				mScrollLayout.setBackgroundResource(R.drawable.fons_detall);

				mProf.clear();
				Professors p;
				String correuString = getResources().getString(R.string.correu);
				for (int i = 0; i < mAssig.getProfessors().size(); i++) {
					p = mAssig.getProfessors().get(i);
					if ("".equals(p.getCorreu().trim())) {
						mProf.add(p.getNom() + "\n" + correuString
								+ getResources().getString(R.string.noCorreu));
					} else {
						mProf.add(p.getNom() + "\n" + correuString + " "
								+ p.getCorreu());
					}

				}

				tnomAssig.setText(nomAssig);

				tcredits.setText(getResources().getString(R.string.credits)
						+ ": " + mAssig.getCredits());

				StringBuilder objectiuText = new StringBuilder();
				objectiuText.append("\n");
				for (int i = 0; i < mAssig.getObjectius().size(); i++) {
					objectiuText.append("  " + (i + 1) + ".- "
							+ mAssig.getObjectius().get(i) + "\n\n");
				}
				objectiu.setText(objectiuText);

				Lprofessors.setAdapter(new ArrayAdapter<String>(
						ControladorVistaInformacioAssignatura.this,
						R.layout.llista_professors, R.id.profeInfo, mProf));

				/*
				 * Lprofessors.setAdapter(new ArrayAdapter<String>(
				 * ControladorVistaInformacioAssignatura.this,
				 * R.layout.llista_professors_objectius_textviews, mProf));
				 */

				// Enviar correu als professors
				Lprofessors.setClickable(true);

				Lprofessors.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> av, View view,
							int index, long arg3) {
						String[] correu = mProf.get(index).split(":");

						/*
						 * Intent intent = new Intent(
						 * ".Controladors.ControladorCorreuProfessor.Correu");
						 * intent.putExtra("correuProfessor", correu[1].trim());
						 * startActivity(intent);
						 */

						if (!"".equals(correu[1].trim())) {
							final Intent emailIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							emailIntent.setType("plain/text");
							emailIntent.putExtra(
									android.content.Intent.EXTRA_EMAIL,
									new String[] { correu[1].trim() });
							emailIntent.putExtra(
									android.content.Intent.EXTRA_SUBJECT, "");
							ControladorVistaInformacioAssignatura.this
									.startActivity(Intent.createChooser(
											emailIntent,
											getString(R.string.enviantCorreu)));
						} else {
							Toast.makeText(getApplicationContext(),
									R.string.noCorreu, Toast.LENGTH_LONG)
									.show();
						}

					}
				});
				int listSize = Utility
						.setListViewHeightBasedOnChildren(Lprofessors);
				ViewGroup.LayoutParams params = Lprofessors.getLayoutParams();
				params.height = listSize;
				Lprofessors.setLayoutParams(params);
			}
		}
	};

	protected boolean hihaInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

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
				carregarInformació();
			} else {
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
			}
			break;
		}
		return true;
	}

	public static class Utility {
		public static int setListViewHeightBasedOnChildren(ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				// pre-condition
				return 0;
			}
			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
				// dividreHeight
				totalHeight++;
			}
			return totalHeight;
		}
	}

}
