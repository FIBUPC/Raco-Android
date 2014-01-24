package fib.lcfib.raco.Connexions;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Controladors.ControladorTabIniApp;
import fib.lcfib.raco.Model.BaseDadesManager;

public abstract class GestioActualitzaLlistesGroupActivity extends
		ActivityGroup {

	private BaseDadesManager mBdm = new BaseDadesManager(this);
	private ArrayList<String> mIdList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new BaseDadesManager(this);
		if (mIdList == null)
			mIdList = new ArrayList<String>();
	}

	/**
	 * Funció que implementa cada classe que necessita actualitzar la llista i
	 * la Base de dades
	 */
	protected abstract void actualitzarLlista(LlistesItems lli);

	@Override
	public void finishFromChild(Activity child) {
		try {
			LocalActivityManager manager = getLocalActivityManager();
			int index = mIdList.size() - 1;

			if (index < 1) {
				finish();
				return;
			}

			manager.destroyActivity(mIdList.get(index), true);
			mIdList.remove(index);
			index--;
			String lastId = mIdList.get(index);
			Intent lastIntent = manager.getActivity(lastId).getIntent();
			Window newWindow = manager.startActivity(lastId, lastIntent);
			setContentView(newWindow.getDecorView());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startChildActivity(String Id, Intent intent) {
		Window window = getLocalActivityManager().startActivity(Id,
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		if (window != null) {
			mIdList.add(Id);
			setContentView(window.getDecorView());
		}
	}
	
	/**
	 * The primary purpose is to prevent systems before
	 * android.os.Build.VERSION_CODES.ECLAIR from calling their default
	 * KeyEvent.KEYCODE_BACK during onKeyDown.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// preventing default implementation previous to
			// android.os.Build.VERSION_CODES.ECLAIR
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	/**
	 * If a Child Activity handles KeyEvent.KEYCODE_BACK. Simply override and
	 * add this method.
	 */
	@Override
	public void onBackPressed() {
		int length = mIdList.size();
		if (length > 1) {
			Activity current = getLocalActivityManager().getActivity(
					mIdList.get(length - 1));
			current.finish();
		}
	}

	protected boolean hihaInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return getLocalActivityManager().getCurrentActivity()
				.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return getLocalActivityManager().getCurrentActivity()
				.onMenuItemSelected(featureId, item);
	}

}