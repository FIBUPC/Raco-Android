package fib.lcfib.raco.Controladors;

import fib.lcfib.raco.Model.PreferenciesUsuari;
import android.app.TabActivity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.widget.TabHost;

public abstract class ControladorTabGeneric extends TabActivity {

	protected TabHost mTabHost;
	protected Resources mResources;
	protected static final String PREF_STICKY_TAB = "FibTab";
	protected static SharedPreferences sPrefs;
	protected static String getPrefStickyTab (){
		return PREF_STICKY_TAB;
	}
	
}
