package fib.lcfib.raco.Controladors;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.R;
import fib.lcfib.raco.Model.EventAgenda;

/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class AdaptadorEventsAgenda extends BaseAdapter {

	private Activity mActivity;
	private LayoutInflater mInflater;
	private ArrayList<EventAgenda> mLitems;

	public AdaptadorEventsAgenda(Activity a, ArrayList<EventAgenda> it) {
		mActivity = a;
		mLitems = it;
		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static class VistaH {
		public TextView titol;
		public TextView descripcio;
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
		vi = mInflater.inflate(R.layout.llista_agenda, null);
		vh = new VistaH();
		vh.titol = (TextView) vi.findViewById(R.id.nom_event);
		vh.descripcio = (TextView) vi.findViewById(R.id.descripcio_event);
		if (mLitems.get(position).getTitol().equals(mActivity.getApplication().getBaseContext().getString(R.string.avui))) {
			vi.setBackgroundResource(R.color.blauClar);
		} else {
			vi.setBackgroundResource(R.color.llistaDefault);
		}
		vi.setTag(vh);

		String dataIni, dataFi;
		dataIni = AndroidUtils.dateToStringAgenda(mLitems.get(position).getData());
		dataFi = AndroidUtils.dateToStringAgenda(mLitems.get(position).getDataFi());

		vh.titol.setText(mLitems.get(position).getTitol());
		if (mLitems.get(position).getTitol().equals(mActivity.getApplication().getBaseContext().getString(R.string.avui))) {
			vh.descripcio.setText(dataIni);
		} else {
			String inici = mActivity.getApplication().getBaseContext()
					.getString(R.string.iniciEventAgenda);
			String fi = mActivity.getApplication().getBaseContext()
					.getString(R.string.fiEventAgenda);
			vh.descripcio.setText(inici + " " + dataIni + "  |  " + fi + " "
					+ dataFi);
		}
		return vi;
	}

	@Override
	public int getCount() {
		return mLitems.size();
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

}