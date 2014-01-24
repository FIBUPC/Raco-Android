package fib.lcfib.raco.Controladors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import fib.lcfib.raco.R;

public class ControladorVistaVeureAula extends Activity implements Runnable {

	private String mUrl;
	private ProgressDialog mPd;
	private WebView mWebview;
	private static final FrameLayout.LayoutParams ZOOM_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Mirem si la pantalla pot contenir banner
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.webview_aula);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.vista_banner);
		
		TextView username = (TextView) findViewById(R.id.username_banner);
		username.setText(ControladorTabIniApp.usernameUser);
		
		mWebview = (WebView) findViewById(R.id.webview);

		FrameLayout mContentView = (FrameLayout) getWindow().getDecorView()
				.findViewById(android.R.id.content);
		final View zoom = this.mWebview.getZoomControls();
		mContentView.addView(zoom, ZOOM_PARAMS);
		zoom.setVisibility(View.GONE);
		mWebview.setBackgroundColor(R.color.fonsVistaAula);

		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setSupportZoom(true); // Zoom Control on web (You
													// don't need this
		// if ROM supports Multi-Touch
		mWebview.getSettings().setBuiltInZoomControls(true); // Enable Multitouch
															// if supported by
															// ROM

		Bundle extras = getIntent().getExtras();
		mUrl = extras.getString("url");
		
		mPd = ProgressDialog.show(this, "",
				getResources().getString(R.string.progressDialogInfo),
				true, true);
		Thread thread = new Thread(this);
		thread.start();
		
	}
	
	@Override
	public void run() {
		Looper.prepare();
		String html = new String();
		html = ("<html><meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=4.0; user-scalable=1;\" /><body bgcolor=\"#D9DDF2\"><img style=\"width:312;margin-top:100px;\" src="
				+ mUrl + " \"/><body/> </html>");
		mWebview.loadDataWithBaseURL(null, html, "text/html", "utf-8", "");
		handler.sendEmptyMessage(0);
	}
	
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try{
				if (mPd != null) {
					mPd.dismiss();
				}
				Toast.makeText(getApplicationContext(), R.string.progressDialogInfo,
						Toast.LENGTH_LONG).show();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	};
	
}
