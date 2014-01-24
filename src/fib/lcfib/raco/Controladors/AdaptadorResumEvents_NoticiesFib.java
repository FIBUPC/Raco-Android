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
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.GestorImatges;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.ItemGeneric;

/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class AdaptadorResumEvents_NoticiesFib extends BaseAdapter {

	private Activity mActivityAct;
	private String[] mLUrlImatges;
	private LayoutInflater mInflater;
	public GestorImatges gestorIm;
	private ArrayList<ItemGeneric> mLItems;

	public AdaptadorResumEvents_NoticiesFib(Activity a, String[] im,
			ArrayList<ItemGeneric> it) {

		// Preparem la classe per gestionar la llistes
		mActivityAct = a;
		mLUrlImatges = im;
		mLItems = it;
		mInflater = (LayoutInflater) mActivityAct
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		gestorIm = new GestorImatges(mActivityAct.getApplicationContext());
	}

	public static class VistaH {
		public TextView titol;
		public ImageView image;
		public ImageView barra;
		public ImageView fletxa;
		public TextView data;
	}

	@Override
	public int getCount() {
		return mLUrlImatges.length;
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
		View vi = convertView;
		VistaH vh = null;
		String data = "";

		// Formategem la data perquè surti com volem
		if (mLItems.get(position).getDataPub() != null) {
			data = AndroidUtils.dateToStringVistaInici(mLItems.get(position).getDataPub());
		}

		// Noticies
		if (mLItems.get(position).getTipus() == 0) {
			vi = mInflater.inflate(R.layout.llista_events_resum, null);
			vh = new VistaH();
			vh.titol = (TextView) vi.findViewById(R.id.titol);
			vh.image = (ImageView) vi.findViewById(R.id.image);
			vh.barra = (ImageView) vi.findViewById(R.id.barra);
			vh.barra.setImageResource(R.drawable.barra_blau);
			vh.data = (TextView) vi.findViewById(R.id.dataIni);
			vh.fletxa = (ImageView) vi.findViewById(R.id.fletxa);
			vh.fletxa.setImageResource(R.drawable.fletxa_celda);
			vi.setTag(vh);
			vh.data.setText(data);
		}
		// Correu
		if (mLItems.get(position).getTipus() == 1) {
			vi = mInflater.inflate(R.layout.llista_events_resum, null);
			vh = new VistaH();
			vh.titol = (TextView) vi.findViewById(R.id.titol);
			vh.image = (ImageView) vi.findViewById(R.id.image);
			vh.barra = (ImageView) vi.findViewById(R.id.barra);
			vh.barra.setImageResource(R.drawable.barra_lila);
			vh.data = (TextView) vi.findViewById(R.id.dataIni);
			vh.fletxa = (ImageView) vi.findViewById(R.id.fletxa);
			vh.fletxa.setImageResource(R.drawable.fletxa_celda);
			vi.setTag(vh);
			vh.data.setText(data);
		}
		// Avisos
		if (mLItems.get(position).getTipus() == 2) {
			vi = mInflater.inflate(R.layout.llista_events_resum, null);
			vh = new VistaH();
			vh.titol = (TextView) vi.findViewById(R.id.titol);
			vh.image = (ImageView) vi.findViewById(R.id.image);
			vh.barra = (ImageView) vi.findViewById(R.id.barra);
			vh.barra.setImageResource(R.drawable.barra_vermella);
			vh.data = (TextView) vi.findViewById(R.id.dataIni);
			vh.fletxa = (ImageView) vi.findViewById(R.id.fletxa);
			vh.fletxa.setImageResource(R.drawable.fletxa_celda);
			vi.setTag(vh);
			vh.data.setText(data);
		}
		// Configuracio
		if (mLItems.get(position).getTipus() == 3) {
			vi = mInflater.inflate(R.layout.llista_events_resum, null);
			vh = new VistaH();
			vh.titol = (TextView) vi.findViewById(R.id.titol);
			vh.image = (ImageView) vi.findViewById(R.id.image);
			vi.setTag(vh);
		}

		vh.titol.setText(mLItems.get(position).getTitol().replaceAll("\"", ""));
		vh.image.setTag(mLUrlImatges[position]);
		gestorIm.MostrarImatge(mLUrlImatges[position], vh.image);
		return vi;
	}
}
