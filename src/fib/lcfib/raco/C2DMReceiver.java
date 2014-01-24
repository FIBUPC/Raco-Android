package fib.lcfib.raco;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import fib.lcfib.raco.Controladors.ControladorTabIniApp;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class C2DMReceiver extends C2DMBaseReceiver {

	private String mTAG = "C2DMReceiver";
	private final String REGISTRAT = "on";
	private final String ERROR = "error";
	private AndroidUtils au = AndroidUtils.getInstance();

	public C2DMReceiver() {
		super("lcfibc2dm@gmail.com");
	}

	@Override
	public void onRegistered(Context context, String registrationId)
			throws java.io.IOException {

		// Guardem al dispositiu
		SharedPreferences sp = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), 0);

		SharedPreferences.Editor editor = sp.edit();
		editor.putString(au.REGISTRE_ID, registrationId);
		editor.commit();
		// FIB
		HttpURLConnection con = null;
		String key = sp.getString(au.KEY_NOTIFICACIONS_REGISTRAR, "");
		try {
			URL url = new URL(au.URL_NOTIFICACIO_REGISTRAR
					+ "&registration_id=" + registrationId + "&KEY=" + key);
			con = (HttpURLConnection) url.openConnection();
			System.setProperty("http.keepAlive", "false");
			con.setConnectTimeout(1000);
			if (con.getResponseCode() == 200) {
				crearNotificacio(REGISTRAT);
			} else {
				crearNotificacio(ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	};

	@Override
	public void onUnregistered(Context context) {
		// Guardem al dispositiu
		SharedPreferences sp = getApplicationContext().getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), 0);
		String registrationId = sp.getString(au.REGISTRE_ID, "");
		// FIB
		HttpURLConnection con = null;
		String key = sp.getString(au.KEY_NOTIFICACIONS_DESREGISTRAR, "");

		try {
			URL url = new URL(au.URL_NOTIFICACIO_DESREGISTRAR
					+ "&registration_id=" + registrationId + "&KEY=" + key);
			con = (HttpURLConnection) url.openConnection();
			System.setProperty("http.keepAlive", "false");
			con.setConnectTimeout(1000);

			SharedPreferences.Editor editor = sp.edit();
			editor.remove(au.REGISTRE_ID);
			editor.commit();

			if (con.getResponseCode() == 200) {
				crearNotificacio(null);
			} else {
				crearNotificacio(ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// Extract the payload from the message
		Bundle extras = intent.getExtras();
		SharedPreferences sPrefs = getApplicationContext()
				.getSharedPreferences(
						PreferenciesUsuari.getPreferenciesUsuari(), 0);

		Boolean rebreNotificacio = sPrefs.getBoolean(
				AndroidUtils.PREFERENCE_NOTIFICACIONS, false);
		// si té el checkbox activat la rebu sinó no
		if (rebreNotificacio) {
			if (extras != null) {

				String action, missatge, url, titol;

				action = intent.getStringExtra("action");
				missatge = intent.getStringExtra("titol");
				url = intent.getStringExtra("url");
				String message = "";
				int iconId = R.drawable.ic_notificacio_raco_blanc;

				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification(iconId, message,
						System.currentTimeMillis());

				notification.defaults |= Notification.DEFAULT_LIGHTS;
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				notification.flags |= Notification.FLAG_AUTO_CANCEL;

				RemoteViews contentView = new RemoteViews(getPackageName(),
						R.layout.notificacions_bar);
				
				contentView.setTextViewText(R.id.missatgeNotificacio, this.getString(R.string.notificacio));
				PendingIntent contentIntent;

				if ("nouAvis".equals(action)
						|| "modificacioAvis".equals(action)) {

					titol = getResources().getString(R.string.racoNotificacio);
					contentView.setTextViewText(R.id.titolNotificacio, titol);

					Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
					notificationIntent.setClass(getApplicationContext(),
							ControladorTabIniApp.class);
					notificationIntent.putExtra("Notificacio", "true");
					contentIntent = PendingIntent.getActivity(this, 0,
							notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT
									| Notification.FLAG_AUTO_CANCEL);

					SharedPreferences sp = getApplicationContext()
							.getSharedPreferences(
									PreferenciesUsuari.getPreferenciesUsuari(),
									0);
					int nPref = sp.getInt(au.NOTIFICATION_COUNTER, 0);
					nPref = nPref + 1;
					if (nPref > 1) {
						missatge = getResources().getString(
								R.string.notifiacio1)
								+ " "
								+ nPref
								+ " "
								+ getString(R.string.notifiacio2);
						contentView.setTextViewText(R.id.missatgeNotificacio,
								missatge);
					} else {
						// deixem el que hi havia
					}

					SharedPreferences.Editor editor = sp.edit();
					editor.putInt(au.NOTIFICATION_COUNTER, nPref);
					editor.commit();
					notification.number = nPref;

					notification.contentIntent = contentIntent;

				} else {

					titol = getResources().getString(R.string.pfcNotificacio);
					contentView.setTextViewText(R.id.titolNotificacio, titol);
					Intent notificationIntent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					contentIntent = PendingIntent.getActivity(this, 0,
							notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT
									| Notification.FLAG_AUTO_CANCEL);
					notification.contentIntent = contentIntent;
				}

				notification.contentView = contentView;
				notificationManager.notify(au.NOTIFICATION_ID_AVISOS,
						notification);

			}
		}

	}

	@Override
	public void onError(Context context, String errorId) {
		// notify the user
		Log.e(mTAG, "error with C2DM receiver: " + errorId);

		if ("ACCOUNT_MISSING".equals(errorId)) {
			// no Google account on the phone; ask the user to open the account
			// manager and add a google account and then try again
			// TODO
			Log.d(mTAG, "ACCOUNT_MISSING");

		} else if ("AUTHENTICATION_FAILED".equals(errorId)) {
			// bad password (ask the user to enter password and try. Q: what
			// password - their google password or the sender_id password? ...)
			// i _think_ this goes hand in hand with google account; have them
			// re-try their google account on the phone to ensure it's working
			// and then try again
			// TODO

		} else if ("TOO_MANY_REGISTRATIONS".equals(errorId)) {
			Log.d(mTAG, "TO MORE REGISTRATIONS");

		} else if ("INVALID_SENDER".equals(errorId)) {
			// this shouldn't happen in a properly configured system
			// TODO: send a message to app publisher?, inform user that service
			// is down
			Log.d(mTAG, "INVALID_SENDER");

		} else if ("PHONE_REGISTRATION_ERROR".equals(errorId)) {
			// the phone doesn't support C2DM; inform the user
			// TODO
			Log.d(mTAG, "PHONE_REGISTRATION_ERROR");

		}// else: SERVICE_NOT_AVAILABLE is handled by the super class and does
	}

	// exponential backoff retries

	private void crearNotificacio(String registre) {

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.ic_notificacio_raco_blanc;
		CharSequence missatge;

		// Les cometes "" treuen un missatge previ a la barra
		Notification notification = new Notification(icon, getResources()
				.getString(R.string.notificacions), System.currentTimeMillis());

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		Context context = getApplicationContext();
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notificacions_bar);

		contentView.setTextViewText(R.id.titolNotificacio, getResources()
				.getString(R.string.Tnotificacio));

		if ("on".equals(registre)) {
			missatge = getResources().getString(R.string.notificacions_on);
			contentView.setTextViewText(R.id.missatgeNotificacio, missatge);
		} else if ("error".equals(registre)) {
			missatge = getResources().getString(R.string.errorNotificacio);
			contentView.setTextViewText(R.id.missatgeNotificacio, missatge);

		} else {
			missatge = getResources().getString(R.string.notificacions_off);
			contentView.setTextViewText(R.id.missatgeNotificacio, missatge);
		}

		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, new Intent(),
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context,
				getResources().getString(R.string.Tnotificacio), missatge,
				contentIntent);
		notification.contentView = contentView;
		mNotificationManager.notify(au.NOTIFICATION_ID_ACTIVACIO, notification);

	}
}