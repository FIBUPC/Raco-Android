package fib.lcfib.raco.Controladors;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import fib.lcfib.raco.R;

public class ControladorVistaError extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Mirem si la pantalla pot contenir banner
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.vista_no_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.vista_banner);
		
	}
}
