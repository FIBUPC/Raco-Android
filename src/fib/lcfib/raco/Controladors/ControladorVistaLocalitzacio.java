package fib.lcfib.raco.Controladors;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;

public class ControladorVistaLocalitzacio extends MapActivity implements
		LocationListener {

	private MapView mMap = null;
	private LocationManager mLmanager;
	private GeoPoint mMyPosicio;
	private MyLocationOverlay mMyLocationOverlay;
	private AndroidUtils au = AndroidUtils.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_localitzacio_fib);

		mMap = (MapView) findViewById(R.id.mapView);

		// Zoom
		final MapController mC = mMap.getController();
		ZoomControls zoom = (ZoomControls) findViewById(R.id.zoomcontrols);
		zoom.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mC.zoomIn();
			}
		});
		zoom.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mC.zoomOut();
			}
		});

		crearMapa();

	}

	private void crearMapa() {
		/**
		 * Una persona
		 * */
		mLmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (mLmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			mLmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					au.INTERVAL_TIME, au.METERS_MOVED_VALUE, this);

			mMyLocationOverlay = new MyLocationOverlay(this, mMap);
			List<Overlay> list = mMap.getOverlays();
			list.add(mMyLocationOverlay);
			mMyLocationOverlay.enableMyLocation();
		} else {
			Toast.makeText(getApplicationContext(), R.string.noEsPotPosicio,
					Toast.LENGTH_LONG).show();
		}

		/**
		 * Punts
		 * */

		List<Overlay> mapOverlays = mMap.getOverlays();
		HelloItemizedOverlay itemizedoverlayFib = new HelloItemizedOverlay(
				getResources().getDrawable(R.drawable.pin_fib));
		HelloItemizedOverlay itemizedoverlayUpc = new HelloItemizedOverlay(
				getResources().getDrawable(R.drawable.pin_upc));

		GeoPoint salaActes = new GeoPoint((int) (41.38932 * 1E6),
				(int) (2.11332 * 1E6));
		OverlayItem overlayitem = new OverlayItem(salaActes, "BCN",
				getString(R.string.salaActes));
		itemizedoverlayFib.addOverlay(overlayitem);

		GeoPoint plasaFib = new GeoPoint((int) (41.38926983970336 * 1E6),
				(int) (2.113073766231537 * 1E6));
		overlayitem = new OverlayItem(plasaFib, "BCN",
				getString(R.string.plasaFib));
		itemizedoverlayFib.addOverlay(overlayitem);

		GeoPoint omega = new GeoPoint((int) (41.388456870743326 * 1E6),
				(int) (2.113889157772064 * 1E6));
		overlayitem = new OverlayItem(omega, "BCN", getString(R.string.omega));
		itemizedoverlayFib.addOverlay(overlayitem);

		GeoPoint vertex = new GeoPoint((int) (41.390408 * 1E6),
				(int) (2.114363 * 1E6));
		overlayitem = new OverlayItem(vertex, "BCN", getString(R.string.vertex));
		itemizedoverlayUpc.addOverlay(overlayitem);

		GeoPoint biblioteca = new GeoPoint((int) (41.38765999031285 * 1E6),
				(int) (2.112443447113037 * 1E6));
		overlayitem = new OverlayItem(biblioteca, "BCN",
				getString(R.string.biblioteca));
		itemizedoverlayUpc.addOverlay(overlayitem);

		mapOverlays.add(itemizedoverlayFib);
		mapOverlays.add(itemizedoverlayUpc);

		mMap.getController().setZoom(17);
	}

	/*@Override
	protected void onResume() {
		mLmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				au.INTERVAL_TIME, au.METERS_MOVED_VALUE, this);
		if (mMyPosicio != null) {
			mMap.getController().animateTo(mMyPosicio);
		}

		super.onResume();
	}*/
	
	@Override
    protected void onResume() {
        if (mLmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    au.INTERVAL_TIME, au.METERS_MOVED_VALUE, this);
            if (mMyPosicio != null)
                mMap.getController().animateTo(mMyPosicio);
        }
        super.onResume();
    }

	@Override
	protected void onDestroy() {
		if (mMyLocationOverlay != null) {
			if (mMyLocationOverlay.isMyLocationEnabled()) {
				mMyLocationOverlay.disableMyLocation();
			}
		}
		mLmanager.removeUpdates(this);
		super.onDestroy();
	}

	/*
	 * protected void onDestroy() { // TODO Auto-generated method stub if
	 * (mMyLocationOverlay.isMyLocationEnabled()) {
	 * mMyLocationOverlay.disableMyLocation(); } mLmanager.removeUpdates(this);
	 * super.onDestroy(); }
	 */

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mLmanager.removeUpdates(this);
		super.onResume();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			mMyPosicio = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			GeoPoint posicioPersona = buscarPuntMig((int) mMyPosicio
					.getLatitudeE6(), (int) mMyPosicio.getLongitudeE6());
			mMap.getController().animateTo(
					new GeoPoint(posicioPersona.getLatitudeE6(), posicioPersona
							.getLongitudeE6()));
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_S) {
			mMap.setSatellite(!mMap.isSatellite());
			return (true);
		} else if (keyCode == KeyEvent.KEYCODE_Z) {
			mMap.displayZoomControls(true);
			return (true);
		}

		return (super.onKeyDown(keyCode, event));
	}

	private GeoPoint buscarPuntMig(int latitudePersona, int longitudePersona) {

		GeoPoint posicioFib = new GeoPoint(au.LATITUDE_FIB, au.LONGITUDE_FIB);
		GeoPoint posicioPersona = new GeoPoint(latitudePersona,
				longitudePersona);

		mMap.getController().setZoom(17);

		float zoomLatitude = (float) (Math.abs(posicioFib.getLatitudeE6()
				- posicioPersona.getLatitudeE6()) * au.RADI);
		float zoomLongitude = (float) (Math.abs(posicioFib.getLongitudeE6()
				- posicioPersona.getLongitudeE6()) * au.RADI);

		if (zoomLatitude == 0.0)
			zoomLatitude = (float) 0.3;
		if (zoomLongitude == 0.0)
			zoomLongitude = (float) 0.3;

		// Calculate the lat, lon spans from the given pois and zoom
		mMap.getController()
				.zoomToSpan((int) zoomLatitude, (int) zoomLongitude);

		return posicioPersona;
	}

	private class HelloItemizedOverlay extends ItemizedOverlay {

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public HelloItemizedOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item = mOverlays.get(index);
			Toast.makeText(ControladorVistaLocalitzacio.this,
					item.getSnippet(), Toast.LENGTH_SHORT).show();

			return true;
		}

		public HelloItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
			// TODO Auto-generated constructor stub
		}

		@Override
		protected OverlayItem createItem(int i) {
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			return mOverlays.size();
		}

		public void addOverlay(OverlayItem overlay) {
			mOverlays.add(overlay);
			populate();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.localitzacio, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.satellit:
			ControladorTabIniApp.carregant.setVisibility(ProgressBar.VISIBLE);
			mMap.setStreetView(false);
			mMap.setSatellite(true);
			mMap.invalidate();
			ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
			break;
		case R.id.mapa:
			ControladorTabIniApp.carregant.setVisibility(ProgressBar.VISIBLE);
			mMap.setSatellite(false);
			mMap.setStreetView(true);
			mMap.invalidate();
			ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
			break;
		}
		return true;
	}
}
