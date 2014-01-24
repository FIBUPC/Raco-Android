package fib.lcfib.raco.Controladors;

import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ControladorVistaAbout extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Mirem si la pantalla pot contenir banner
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.vista_about);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.vista_banner);

		String app_ver = "Versió ";
		try {
			app_ver += this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {}
		TextView versio = (TextView) findViewById(R.id.versio);
		versio.setText(app_ver);

		Button email = (Button) findViewById(R.id.contacteViaEmail);
		email.setText(getResources().getString(R.string.suport_fib) + "  ");
		email.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri.parse(AndroidUtils.CONTACTAR_LCFIB);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});
	}

}
