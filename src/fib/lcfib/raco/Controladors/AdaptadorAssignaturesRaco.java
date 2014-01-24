package fib.lcfib.raco.Controladors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.AssignaturesAvisos;

/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class AdaptadorAssignaturesRaco extends BaseAdapter {

	private Activity mActivity;
	private LayoutInflater mInflater;
	private ArrayList<AssignaturesAvisos> mLitems;

	public AdaptadorAssignaturesRaco(Activity a,
			ArrayList<AssignaturesAvisos> it) {
		mActivity = a;
		mLitems = it;
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static class VistaH {
		public TextView titol;
		public TextView data;
		public ImageView fletxa;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			View vi = convertView;
			VistaH vh;
			if (mLitems.get(position).getData() == null) {
				vi = mInflater.inflate(R.layout.llista_assignatures_raco, null);
				vh = new VistaH();
				vh.titol = (TextView) vi.findViewById(R.id.nomAssigRaco);
				vh.titol.setText(mLitems.get(position).getIdAssig());
				vh.fletxa = (ImageView) vi.findViewById(R.id.fletxaAssig);
				vh.fletxa.setImageResource(R.drawable.fletxa_celda);
				vi.setBackgroundResource(R.drawable.list_style_blau_fosc);
				vi.setTag(vh);
			} else {
				vi = mInflater.inflate(R.layout.llista_avisos_raco, null);
				vh = new VistaH();
				vh.titol = (TextView) vi.findViewById(R.id.nomAvis);
				vh.data = (TextView) vi.findViewById(R.id.dataAvis);
				vh.fletxa = (ImageView) vi.findViewById(R.id.fletxaAvis);
				vh.fletxa.setImageResource(R.drawable.fletxa_celda);
				SimpleDateFormat sdf = AndroidUtils.crearSimpleDateFormat();
				String data = sdf.format(mLitems.get(position).getData());
				String[] titolAux = mLitems.get(position).getTitolAvis()
						.split(" - ");
				vi.setBackgroundResource(R.drawable.list_style_blau_clar);
				if (titolAux.length == 2) {
					// aqui tenim EDSA - TITOLAVIS i mostrarem nomes TITOLAVIS
					vh.titol.setText(titolAux[1].trim());
				} else {
					// aqui tenim GRAU - EDSA - TITOLAVIS i mostrarem nomes
					// TITOLAVIS
					vh.titol.setText(titolAux[2].trim());
				}
				vh.data.setText(data);
				vi.setTag(vh);
			}

			return vi;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int getCount() {
		return mLitems.size();
	}

}