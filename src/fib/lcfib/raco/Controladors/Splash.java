package fib.lcfib.raco.Controladors;

import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.BaseDadesManager;
import fib.lcfib.raco.Model.PreferenciesUsuari;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class Splash extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		try {
			Thread splashThread = new Thread() {
				@Override
				public void run() {
					try {
						int waited = 0;
						while (waited < 800) {
							sleep(80);
							waited += 100;
						}
						// perquè aixi quan premin back no apareixi aquesta
						// vista
						finish();
						Intent intent = new Intent(Splash.this,
								ControladorTabIniApp.class);
						startActivity(intent);
					} catch (InterruptedException e) {
					}
				}
			};
			splashThread.start();
		} catch (Exception e) {
		}

	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}