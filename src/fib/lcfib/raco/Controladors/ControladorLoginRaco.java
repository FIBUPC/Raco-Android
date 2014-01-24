package fib.lcfib.raco.Controladors;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Connexions.GestorCertificats;
import fib.lcfib.raco.Model.PreferenciesUsuari;

public class ControladorLoginRaco extends Activity {

	public EditText username;
	public EditText password;

	private final String mTAG = "LoginRaco";
	private Dialog loginDialog;
	private String queEs; // per saber si venim de la vista actualitat o bé de
							// la pestanya Racó

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_login);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			queEs = extras.getString("queEs");
		}

		loginDialog = new Dialog(this);
		showAddDialog();
	}

	private void showAddDialog() {

		loginDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		loginDialog.setTitle(R.string.loginRaco);

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = li.inflate(R.layout.contingut_login, null);
		loginDialog.setContentView(dialogView);

		username = (EditText) dialogView.findViewById(R.id.login);
		password = (EditText) dialogView.findViewById(R.id.password);

		Button okButton = (Button) dialogView
				.findViewById(R.id.acceptar_button);
		Button cancelButton = (Button) dialogView
				.findViewById(R.id.cancel_button);
		loginDialog.setCancelable(false);
		loginDialog.show();

		okButton.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				try {
					String usernameAux = username.getText().toString().trim();
					String passwordAux = password.getText().toString().trim();

					usernameAux = URLEncoder.encode(usernameAux, "UTF-8");
					passwordAux = URLEncoder.encode(passwordAux, "UTF-8");

					boolean correcte = check_user(usernameAux, passwordAux);
					if (correcte) {
						Toast.makeText(getApplicationContext(),
								R.string.login_correcte, Toast.LENGTH_LONG)
								.show();
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.errorLogin, Toast.LENGTH_LONG).show();
					}

					if ("zonaRaco".equals(queEs)) {
						Intent intent = new Intent(ControladorLoginRaco.this,
								ControladorTabIniApp.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("esLogin", "zonaRaco");
						startActivity(intent);

					} else {
						Intent act = new Intent(ControladorLoginRaco.this,
								ControladorTabIniApp.class);
						// Aquestes 2 línies provoquen una excepció però no peta
						// simplement informa és normal
						act.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						act.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(act);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

	private boolean check_user(String username, String password) {

				GestorCertificats.allowAllSSL();
				AndroidUtils au = AndroidUtils.getInstance();
				/** open connection */
				
				//Així tanquem les connexions segur
				System.setProperty("http.keepAlive", "false");
				
				try {
					InputStream is = null;
					HttpGet request = new HttpGet(au.URL_LOGIN + "username="
							+ username + "&password=" + password);
					HttpClient client = new DefaultHttpClient();
					HttpResponse response = client.execute(request);
					final int statusCode = response.getStatusLine()
							.getStatusCode();
					if (statusCode != HttpStatus.SC_OK) {
						Header[] headers = response.getHeaders("Location");
						if (headers != null && headers.length != 0) {
							String newUrl = headers[headers.length - 1]
									.getValue();
							request = new HttpGet(newUrl);
							client.execute(request);
						}
					}

					/** Get Keys */
					is = response.getEntity().getContent();
					ObjectMapper m = new ObjectMapper();
					JsonNode rootNode = m.readValue(is, JsonNode.class);

					is.close();
					client.getConnectionManager().closeExpiredConnections();
					
					if (rootNode.isNull()) {
						return false;
					} else {

						// GenerarUrl();
						/** calendari ics */
						String KEYportadaCal = rootNode
								.path("/ical/portada.ics").getTextValue()
								.toString();

						/** calendari rss */
						String KEYportadaRss = rootNode
								.path("/ical/portada.rss").getTextValue()
								.toString();

						/** Avisos */
						String KEYavisos = rootNode
								.path("/extern/rss_avisos.jsp").getTextValue()
								.toString();

						/** Assigraco */
						String KEYAssigRaco = rootNode.path("/api/assigList")
								.getTextValue().toString();

						/** Horari */
						String KEYIcalHorari = rootNode
								.path("/ical/horari.ics").getTextValue()
								.toString();
						
						/**Notificacions */
						String KEYRegistrar = rootNode
						.path("/api/subscribeNotificationSystem").getTextValue()
						.toString();
						
						String KEYDesregistrar = rootNode
						.path("/api/unsubscribeNotificationSystem").getTextValue()
						.toString();
						
						SharedPreferences sp = getSharedPreferences(
								PreferenciesUsuari.getPreferenciesUsuari(),
								MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();

						/** Save Username and Password */
						editor.putString(AndroidUtils.USERNAME, username);
						editor.putString(AndroidUtils.PASSWORD, password);

						/** Save Keys */
						editor.putString(au.KEY_AGENDA_RACO_XML, KEYportadaRss);
						editor.putString(au.KEY_AGENDA_RACO_CAL, KEYportadaCal);
						editor.putString(au.KEY_AVISOS, KEYavisos);
						editor.putString(au.KEY_ASSIG_FIB, "public");
						editor.putString(au.KEY_ASSIGS_RACO, KEYAssigRaco);
						editor.putString(au.KEY_HORARI_RACO, KEYIcalHorari);
						editor.putString(au.KEY_NOTIFICACIONS_REGISTRAR, KEYRegistrar);
						editor.putString(au.KEY_NOTIFICACIONS_DESREGISTRAR, KEYDesregistrar);

						/** Save changes */
						editor.commit();
					}
					return true;

				} catch (ProtocolException e) {
					Toast.makeText(getApplicationContext(),
							R.string.errorLogin, Toast.LENGTH_LONG).show();
					return false;
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(),
							R.string.errorLogin, Toast.LENGTH_LONG).show();
					return false;
				}

			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				Intent act = new Intent(ControladorLoginRaco.this, ControladorTabIniApp.class);
				act.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				act.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(act);
			}
		});

	}
}