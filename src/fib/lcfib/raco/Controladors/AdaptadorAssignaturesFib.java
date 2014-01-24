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
import fib.lcfib.raco.Model.Assignatura;

/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class AdaptadorAssignaturesFib extends BaseAdapter {

	private Activity mActivity;
	private LayoutInflater mInflater;
	private ArrayList<Assignatura> mLitems;

	public AdaptadorAssignaturesFib(Activity a, ArrayList<Assignatura> it) {
		mActivity = a;
		mLitems = it;
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static class VistaH {
		public TextView nom_curt;
		public TextView nom_llarg;
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
		View vi = convertView;
		VistaH vh;
		vi = mInflater.inflate(R.layout.llista_assignatures_fib, null);
		vh = new VistaH();
		vh.nom_curt = (TextView) vi.findViewById(R.id.nom_assig_curt);
		vh.nom_llarg = (TextView) vi.findViewById(R.id.nom_assig_llarg);
		vh.fletxa = (ImageView) vi.findViewById(R.id.fletxaAssigFib);
		if (position % 2 == 0) {
			vi.setBackgroundResource(R.drawable.list_style_blau_clar);
		}
		vi.setTag(vh);

		vh.nom_curt.setText(mLitems.get(position).getIdAssig());
		vh.nom_llarg.setText(mLitems.get(position).getNomAssig());
		vh.fletxa.setImageResource(R.drawable.fletxa_celda);

		return vi;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLitems.size();
	}

}