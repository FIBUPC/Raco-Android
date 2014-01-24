package fib.lcfib.raco.Model;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.C2DMessaging;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Controladors.ControladorTabIniApp;

public class PreferenciesUsuari extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private String sTAG = "PreferenciesUsuari";
	private static final String RACO_PREFS = "PREF_USUARI_ANDROID_RACO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.vista_banner);

	}

	public static String getPreferenciesUsuari() {
		return RACO_PREFS;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intentPreferencies = new Intent(PreferenciesUsuari.this,
					ControladorTabIniApp.class);
			intentPreferencies.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentPreferencies.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentPreferencies);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(AndroidUtils.PREFERENCE_CHECKBOX_FILTER)) {// CheckBoxPreference
			ActualitzaCheckbox(findPreference(key));
		} else if (key.equals(AndroidUtils.PREFERENCE_LISTCONFIG)) { // ListPreference
			ActualitzaLLista(findPreference(key));
		} else if (key.equals(AndroidUtils.PREFERENCE_NOTIFICACIONS)) { // Notificaciones
			ActualitzaNotificacions(findPreference(key));
		}
	}

	private void ActualitzaCheckbox(Preference p) {
		CheckBoxPreference ch = (CheckBoxPreference) p;
		SharedPreferences sp = getSharedPreferences(getPreferenciesUsuari(),
				MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		ch.setPersistent(true);
		editor.putBoolean(p.getKey(), ch.isChecked());
		editor.commit();
	}

	private void ActualitzaLLista(Preference p) {
		ListPreference lp = (ListPreference) p;
		SharedPreferences sp = getSharedPreferences(getPreferenciesUsuari(),
				MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		lp.setPersistent(true);
		editor.putString("ListPreferenceElement", lp.getValue());
		editor.commit();
		if (lp.getValue().equals("1"))
			Toast.makeText(getApplicationContext(), R.string.esNoticia,
					Toast.LENGTH_LONG).show();
		if (lp.getValue().equals("2"))
			Toast.makeText(getApplicationContext(), R.string.esCorreu,
					Toast.LENGTH_LONG).show();
		if (lp.getValue().equals("3"))
			Toast.makeText(getApplicationContext(), R.string.esAvis,
					Toast.LENGTH_LONG).show();

	}

	private void ActualitzaNotificacions(Preference p) {
		CheckBoxPreference ch = (CheckBoxPreference) p;
		AndroidUtils au = AndroidUtils.getInstance();
		// Mirem si pertany al Racó
		SharedPreferences sPrefs = getSharedPreferences(
				getPreferenciesUsuari(), MODE_PRIVATE);
		String pertanyRaco = sPrefs.getString(au.KEY_ASSIGS_RACO, "");
		if (pertanyRaco.equals("")) {
			Toast.makeText(getApplicationContext(), R.string.usuariRaco,
					Toast.LENGTH_LONG).show();
		} else {
			if (ch.isChecked()) {
				String email = getEmailMobile();
				if (!email.equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.notificacions_mail_raco, Toast.LENGTH_LONG)
							.show();
					C2DMessaging.register(this, email);
					SharedPreferences.Editor editor = sPrefs.edit();
					ch.setPersistent(true);
					editor.putBoolean(p.getKey(), ch.isChecked());
					editor.commit();
				}
			} else {
				// desregistrar
				String email = getEmailMobile();
				if (!email.equals("")) {
					C2DMessaging.unregister(this);
				}
				SharedPreferences.Editor editor = sPrefs.edit();
				ch.setPersistent(true);
				editor.putBoolean(p.getKey(), ch.isChecked());
				editor.commit();
			}
		}

	}

	private String getEmailMobile() {
		Account[] accounts = AccountManager.get(this).getAccounts();
		String email = "";
		for (Account account : accounts) {
			if (account.type.endsWith("com.google")) {
				email = account.name;
			}
		}
		if (email.equals("")) {
			Toast.makeText(this, R.string.errorMail, Toast.LENGTH_LONG).show();
		}
		return email;
	}
}