package fib.lcfib.raco.Controladors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorTabIniApp extends ControladorTabGeneric{

	public static ProgressDialog mPd;
	private static final String mTAG = "ControladorTabIniApp";

	// Barra de progrés per quan estem carregant la nova info
	public static ProgressBar carregant;
	public static TextView nomPersonaBanner;
	public static String usernameUser; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Mirem si la pantalla pot contenir banner
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.tab_general);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.vista_banner);
	
		//Inicialitzem les preferencies dels TabHosts
		sPrefs = getApplicationContext().getSharedPreferences(PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);	
		
		//Reportar errors
		//BugSenseHandler.setup(this, "fe4f8707");
		BugSenseHandler.setup(this, "e9c228f1");
		
		// Assignem que el text que es mostrara en els tabs siguin textviews
		// perquè aixi és més configurable el disseny
		mTabHost = getTabHost();
		mResources = getResources();
		
		//Fem sortir la barra blanca el mig dels tabs
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.barra_blanca);

		//Per poder carregar l'horari, sinó no podem incloure el carregador bloquejant el mig
		mPd = ProgressDialog.show(this, getResources().getString(R.string.titolObtenirTotHorari),
				getResources().getString(R.string.obtenirTotHorari),
				true, false);
		mPd.setOnKeyListener(new DialogInterface.OnKeyListener() {

		    @Override
		    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
		            return true; // Pretend we processed it
		        }
		        return false; // Any other keys are still processed as normal
		    }
		});

		mPd.dismiss();
		
		//Per les crides asíncrones
		carregant = (ProgressBar) findViewById(R.id.carregant);
		
		//Per les vistes que estan dins els tabsHost
		nomPersonaBanner = (TextView) findViewById(R.id.username_banner);
		ControladorTabIniApp.nomPersonaBanner.setText(sPrefs.getString(AndroidUtils.USERNAME, ""));
		
		//Per les vistes que són individuals: InfoAssignatura, VeureNoticiaAvis, VeureHorariIndividual, VuereAula
		usernameUser = sPrefs.getString(AndroidUtils.USERNAME, "");

		AfegirPestanya1();
		AfegirPestanya2();
		AfegirPestanya3();

		Bundle extras = getIntent().getExtras();
		if(extras != null){
			// en aquest cas si entra quan venim del login perquè ens obri l'agenda directament
			if("zonaRaco".equals(extras.getString("esLogin"))){
				mTabHost.setCurrentTab(2);
			}else {
				mTabHost.setCurrentTab(0);
			}
		}else{
			mTabHost.setCurrentTab(0);
		}
		// Fem que la pestanya inicial sempre sigui la d'Actualitat
		
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Si l'aplicació entre en PAUSA guarde la pestanya actual
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		int currentTab = mTabHost.getCurrentTab();
		editor.putInt(PREF_STICKY_TAB, currentTab);
		editor.commit();
	}

	private void AfegirPestanya1() {

		Intent intent = new Intent(this, ControladorVistaResumEvents.class);
		
		TabSpec spec = mTabHost.newTabSpec(mTAG);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_titols_textviews, getTabWidget(), false);
		TextView text = (TextView) tabIndicator.findViewById(R.id.menu_inicial_tab);
		text.setText(R.string.actualitat);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	private void AfegirPestanya2() {

		Intent intent = new Intent(this, ControladorTabVistaZonaFib.class);
		TabSpec spec = mTabHost.newTabSpec(mTAG);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_titols_textviews, getTabWidget(), false);
		TextView text = (TextView) tabIndicator.findViewById(R.id.menu_inicial_tab);
		text.setText(R.string.fib);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	private void AfegirPestanya3() {
		SharedPreferences prefs = getSharedPreferences(PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);
		/** Is there a preview login? */
		Intent intent = null;
		String username = prefs.getString("username", null);
		String password = prefs.getString("password", null);
		if (username != null && !username.equals("") && !password.equals("")
				&& password != null) {
			intent = new Intent(this, ControladorTabVistaZonaRaco.class);
		} else {
			if(hihaInternet()){
				intent = new Intent(this, ControladorLoginRaco.class);
				intent.putExtra("queEs", "zonaRaco");
			}else{
				Toast.makeText(getApplicationContext(), R.string.hiha_internet,
						Toast.LENGTH_LONG).show();
			}
			
		}
		TabSpec spec = mTabHost.newTabSpec(mTAG);
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_titols_textviews, getTabWidget(), false);
		TextView text = (TextView) tabIndicator.findViewById(R.id.menu_inicial_tab);
		text.setText(R.string.raco);
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}
	
	protected boolean hihaInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
}
