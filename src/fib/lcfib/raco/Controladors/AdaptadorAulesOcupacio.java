package fib.lcfib.raco.Controladors;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.Aula;

/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class AdaptadorAulesOcupacio extends BaseAdapter {

	private Activity mActivity;
	private LayoutInflater mInflater;
	private ArrayList<Aula> mLitems;

	public AdaptadorAulesOcupacio(Activity a, ArrayList<Aula> it) {
		mActivity = a;
		mLitems = it;
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static class VistaH {
		public TextView nomAula;
		public TextView assig;
		public TextView places;
		public ImageView fletxa;
	}

	@Override
	public Object getItem(int position) {
		// TODO returnar al posicó de l'Item
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO returnar al posicó de l'Item
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Assignar la vista i insertar la informació
		View vi = convertView;
		VistaH vh;
		
		vi = mInflater.inflate(R.layout.llista_ocupacio_classes_places_raco,
				null);
		vh = new VistaH();
		vh.nomAula = (TextView) vi.findViewById(R.id.aulaOcupada);
		vh.assig = (TextView) vi.findViewById(R.id.assigAula);
		vh.places = (TextView) vi.findViewById(R.id.places);
		vh.fletxa = (ImageView) vi.findViewById(R.id.fletxaOcupacio);
		vi.setTag(vh);

		vh.nomAula.setText(mLitems.get(position).getmNom());
		if (mLitems.get(position).getmNom().contains("A5")) {
			vi.setBackgroundResource(R.drawable.list_style_default);
		} else if (mLitems.get(position).getmNom().contains("B5")) {
			vi.setBackgroundResource(R.drawable.list_style_blau_clar);
		} else {
			vi.setBackgroundResource(R.drawable.list_style_default);
		}

		vh.assig.setText(mLitems.get(position).getmNomAssig());
		vh.places.setText(mLitems.get(position).getmPlaces());
		vh.fletxa.setImageResource(R.drawable.fletxa_celda);
		if (mLitems.get(position).getmHihaClasse().equals("true")) {
			// el color s'ha de canviar
			vi.setBackgroundResource(R.drawable.list_style_blau_aules_classe_ocupada);
		}

		return vi;
	}

	@Override
	public int getCount() {
		// TODO Retornar el tamany de la llista mLitems
		return mLitems.size();
	}
}