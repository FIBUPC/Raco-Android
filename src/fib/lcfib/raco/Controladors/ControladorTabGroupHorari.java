package fib.lcfib.raco.Controladors;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import fib.lcfib.raco.Connexions.GestioActualitzaLlistesGroupActivity;
import fib.lcfib.raco.Connexions.LlistesItems;

public class ControladorTabGroupHorari extends GestioActualitzaLlistesGroupActivity{

	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Intent horari = new Intent(ControladorTabGroupHorari.this,ControladorVistaHorariRaco.class);
	        startChildActivity("HorariRaco",horari);
	    }
	
	@Override
	protected void actualitzarLlista(LlistesItems lli) {
		// TODO Auto-generated method stub
	}

}
