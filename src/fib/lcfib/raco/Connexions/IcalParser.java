package fib.lcfib.raco.Connexions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.Model.Aula;
import fib.lcfib.raco.Model.EventAgenda;
import fib.lcfib.raco.Model.EventHorari;

public class IcalParser {

	private final String mTAG = "ICALParser";
	private static IcalParser sInstancia = null;

	// creador sincronizado para protegerse de posibles problemas multi-hilo
	// otra prueba para evitar instanciación múltiple
	private synchronized static void crearInstancia() {
		if (sInstancia == null) {
			sInstancia = new IcalParser();
		}
	}

	public static IcalParser getInstance() {
		if (sInstancia == null)
			crearInstancia();
		return sInstancia;
	}

	public LlistesItems parserData(int tipus, URL url, LlistesItems lli) {
		LlistesItems lliAux = new LlistesItems();
		switch (tipus) {
		case AndroidUtils.TIPUS_CLASSES_DIA_RACO:// OCUPACIO AULES SABER
													// ASSIGNATURA
			lliAux = parserClassesDia(url, lli);
			break;
		case AndroidUtils.TIPUS_AGENDA_RACO:
			lliAux = parserAgenda(url);
			break;
		default:
			break;
		}
		return lliAux;
	}

	protected LlistesItems parserClassesDia(URL url, LlistesItems lli) {
		HttpURLConnection con = null;
		AndroidUtils au = AndroidUtils.getInstance();
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000);
			InputStream fin = con.getInputStream();
			Reader reader = new BufferedReader(new InputStreamReader(fin,
					"UTF-8"));
			CalendarBuilder builder = new CalendarBuilder();
			Calendar calendar = builder.build(reader);
			String assig, classe;
			Date dataInici, dataFi;
			int horaInici, horaFi;
			Aula aula;

			for (Object event : calendar.getComponents(Component.VEVENT)) {
				dataInici = ((VEvent) event).getStartDate().getDate();
				horaInici = dataInici.getHours();
				dataFi = ((VEvent) event).getEndDate().getDate();
				horaFi = dataFi.getHours();
				classe = ((VEvent) event).getLocation().getValue();
				assig = ((VEvent) event).getSummary().getValue();
				for (int i = 0; i < lli.getLaula().size(); i++) {
					aula = lli.getLaula().get(i);
					if (aula.getmNom().equals(classe)
							&& (aula.getmDataInici().getHours() == horaInici && aula
									.getmDataFi().getHours() == horaFi)) {
						// modifiquem els paràmetres
						assig = au.htmlToString(assig);
						lli.getLaula().get(i).setmNomAssig(assig);
						break;
					}
				}
			}
			con.disconnect();
			return lli;
		} catch (Exception e) {
			con.disconnect();
			e.printStackTrace();
			return lli;
		}
	}

	public ArrayList<EventHorari> parserHorariComplet(URL url) {
		ArrayList<EventHorari> lli = new ArrayList<EventHorari>();
		HttpURLConnection con = null;

		try {
			con = (HttpURLConnection) url.openConnection();
			// Així tanquem les connexions segur
			System.setProperty("http.keepAlive", "false");
			InputStream fin = con.getInputStream();
			Reader reader = new BufferedReader(new InputStreamReader(fin,
					"UTF-8"));
			CalendarBuilder builder = new CalendarBuilder();
			Calendar calendar = builder.build(reader);
			net.fortuna.ical4j.model.Date data;
			java.util.Calendar cal = java.util.Calendar.getInstance();
			String hora_i, hora_f, assig, aula;
			DtStart DtStart;
			for (Object event : calendar.getComponents(Component.VEVENT)) {
				DtStart = (((VEvent) event).getStartDate());
				data = DtStart.getDate();
				hora_i = DtStart.getDate().getHours() + ":"
						+ DtStart.getDate().getMinutes() + "0";
				hora_f = ((VEvent) event).getEndDate().getDate().getHours()
						+ ":"
						+ ((VEvent) event).getStartDate().getDate()
								.getMinutes() + "0";
				assig = ((VEvent) event).getSummary().getValue();
				aula = ((VEvent) event).getLocation().getValue();

				cal.setTimeInMillis(data.getTime());

				EventHorari eh = new EventHorari(hora_i, hora_f, assig, aula,
						cal.get(java.util.Calendar.DAY_OF_MONTH),
						cal.get(java.util.Calendar.MONTH),
						cal.get(java.util.Calendar.YEAR));
				lli.add(eh);
			}
			con.disconnect();
			return lli;
		} catch (Exception e) {
			con.disconnect();
			return null;
		}
	}

	protected LlistesItems parserAgenda(URL url) {
		LlistesItems lli = new LlistesItems();
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000);
			InputStream fin = con.getInputStream();
			Reader reader = new BufferedReader(new InputStreamReader(fin,
					"UTF-8"));
			CalendarBuilder builder = new CalendarBuilder();
			Calendar calendar = builder.build(reader);
			String titol;
			net.fortuna.ical4j.model.Date dataIniciCal, dataFiCal;
			for (Object event : calendar.getComponents(Component.VEVENT)) {
				dataIniciCal = ((VEvent) event).getStartDate().getDate();
				dataFiCal = ((VEvent) event).getEndDate().getDate();
				Date inici = new Date(dataIniciCal.getTime());
				Date fi = new Date(dataFiCal.getTime());
				titol = ((VEvent) event).getSummary().getValue();
				lli.afegirItemEventAgenda(new EventAgenda(titol, inici, fi));
			}
			con.disconnect();
			return lli;
		} catch (Exception e) {
			con.disconnect();
			e.printStackTrace();
			return lli;
		}
	}

}