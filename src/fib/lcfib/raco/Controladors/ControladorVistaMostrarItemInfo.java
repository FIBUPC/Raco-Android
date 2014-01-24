package fib.lcfib.raco.Controladors;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.ItemGeneric;
import fib.lcfib.raco.Model.Noticia;

public class ControladorVistaMostrarItemInfo extends Activity {

	public String url = "www.fib.upc.edu";
	// per poder fer la descarrega en background
	public ImageView imatge;
	public ItemGeneric itemSeleccio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Mirem si la pantalla pot contenir banner
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.vista_events_resum_item_individual);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.vista_banner);
		TextView username = (TextView) findViewById(R.id.username_banner);
		username.setText(ControladorTabIniApp.usernameUser);

		Bundle extras = getIntent().getExtras();
		itemSeleccio = null;
		String content = "";

		TextView titol = (TextView) findViewById(R.id.titolIndiv);
		TextView dataTv = (TextView) findViewById(R.id.dataIndiv);
		TextView descripcio = (TextView) findViewById(R.id.descripIndiv);
		imatge = (ImageView) findViewById(R.id.imageIndiv);
		Button linkWeb = (Button) findViewById(R.id.linkIndiv);

		linkWeb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri.parse(url);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		});
		try {
			if (extras.get("qui").equals("actualitat")) {
				itemSeleccio = fib.lcfib.raco.Controladors.ControladorVistaResumEvents.itemSeleccionat;
				content = itemSeleccio.getDescripcio();
				if (itemSeleccio.getTipus() == AndroidUtils.TIPUS_AVISOS) {
					String[] link = content.split("https");
					descripcio.setText(Html.fromHtml(link[0]));
					url = "https" + link[1];
					linkWeb.setText(getResources()
							.getString(R.string.veureAvis) + "  ");
				} else if (itemSeleccio.getTipus() == AndroidUtils.TIPUS_NOTICIA) {
					Noticia n = (Noticia) itemSeleccio;
					url = n.getmLink();
					linkWeb.setText(getResources().getString(
							R.string.veureNoticia)
							+ "  ");
					descripcio.setText(Html.fromHtml(content));
				} else { // Correu
					descripcio.setText(Html.fromHtml(getResources().getString(
							R.string.fromCorreu)
							+ " " + content.replace("\"", "")));
					linkWeb.setVisibility(AndroidUtils.INVISIBLE);
				}
			} else if (extras.get("qui").equals("noticiesFib")) {
				itemSeleccio = (Noticia) fib.lcfib.raco.Controladors.ControladorVistaNoticiesFib.sItemSeleccionat;
				content = itemSeleccio.getDescripcio();
				descripcio.setText(Html.fromHtml(content));
				Noticia n = (Noticia) itemSeleccio;
				url = n.getmLink();
				linkWeb.setText(getResources().getString(R.string.veureNoticia)
						+ "  ");
			} else if (extras.get("qui").equals("assigRaco")) {
				itemSeleccio = fib.lcfib.raco.Controladors.ControladorVistaAssigRaco.sSelectedAvis;
				content = itemSeleccio.getDescripcio();
				String[] link = content.split("https");
				descripcio.setText(Html.fromHtml(link[0]));
				url = "https" + link[1];
				linkWeb.setText(getResources().getString(R.string.veureAvis)
						+ "  ");
			}

			if (hihaInternet()) {
				imatge.setImageResource(R.drawable.default_rm);
				descarregarImatge();
			} else {
				imatge.setImageResource(R.drawable.default_rm);
			}

			String data;
			
			data = AndroidUtils.dateToStringMostraItemInfo(itemSeleccio.getDataPub());

			String title = "";
			title = itemSeleccio.getTitol().replace("\"", "") + "\n";

			dataTv.setText(data);
			dataTv.setGravity(0x05);
			titol.setText(title);
		} catch (Exception e) {
			finish();
		}
	}

	private void descarregarImatge() {

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				// Need to subclass to use Asynctask
				class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
					@Override
					protected Bitmap doInBackground(Void... params) {

						// Així tanquem les connexions segur
						System.setProperty("http.keepAlive", "false");
						return ImageOperations(
								ControladorVistaMostrarItemInfo.this,
								itemSeleccio.getImatge(), "image.jpg");
					}

					@Override
					protected void onPostExecute(Bitmap d) {
						imatge.setImageBitmap(d);
						// imatge.setImageDrawable(d);
					}

					private Bitmap ImageOperations(Context ctx, String url,
							String saveFilename) {
						try {
							InputStream is = (InputStream) this.fetch(url);
							Bitmap b = BitmapFactory
									.decodeStream(new FlushedInputStream(is));
							// Drawable d = Drawable.createFromStream(is,
							// "src");
							return b;
						} catch (MalformedURLException e) {
							e.printStackTrace();
							return null;
						} catch (IOException e) {
							e.printStackTrace();
							return null;
						}
					}

					private Object fetch(String address)
							throws MalformedURLException, IOException {
						URL url = new URL(address);
						Object content = url.getContent();
						return content;
					}
				}
				new DownloadImage().execute();
			}

			class FlushedInputStream extends FilterInputStream {
				public FlushedInputStream(InputStream inputStream) {
					super(inputStream);
				}

				@Override
				public long skip(long n) throws IOException {
					long totalBytesSkipped = 0L;
					while (totalBytesSkipped < n) {
						long bytesSkipped = in.skip(n - totalBytesSkipped);
						if (bytesSkipped == 0L) {
							int b = read();
							if (b < 0) {
								break; // we reached EOF
							} else {
								bytesSkipped = 1; // we read one byte
							}
						}
						totalBytesSkipped += bytesSkipped;
					}
					return totalBytesSkipped;
				}
			}

		});

	}

	protected boolean hihaInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

}