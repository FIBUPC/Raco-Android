package fib.lcfib.raco.Controladors;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class AdaptadorHorariTaula extends SimpleAdapter {
	private int[] colors;

	public AdaptadorHorariTaula(Context context,
			List<HashMap<String, String>> items, int resource, String[] from,
			int[] to) {
		
		super(context, items, resource, from, to);
		colors = new int[2];
		colors [0] = 0x00000000;
		float[] hsvBlau = { 205, (float) 0.1, (float) 0.92 };
		colors[1] = Color.HSVToColor(hsvBlau);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;

		view.setBackgroundColor(colors[colorPos]);
		return view;
	}
}
