package fib.lcfib.raco;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

/**
 * Aquesta classe no conté cap vista però ens farà servei per posar operacions
 * comunes o útils a diferents classes
 */
public class AndroidUtils {

	// Per carregar imatges en la primera vista
	public String URL_AVISOS_IMATGE = "http://www.fib.upc.edu/docroot/androidMobile/assignatures_foto.jpg";
	public String URL_CORREU_IMATGE = "http://www.fib.upc.edu/docroot/androidMobile/correo_foto.jpg";
	public String URL_IMATGE_DEFAULT = "http://www.fib.upc.edu/docroot/androidMobile/default.png";
	public String URL_IMATGE_INFO = "http://www.fib.upc.edu/docroot/androidMobile/info.png";

	// URL'S comunes
	public String URL_LOGIN = "https://raco.fib.upc.edu/cas/login?service=https%3A%2F%2Fraco.fib.upc.edu%2Fservlet%2Fraco.webservices.InitKeys&loginDirecte=true&";
	public String URL_NOTICIES = "http://www.fib.upc.edu/fib/rss.rss";
	public String URL_CORREU = "https://webmail.fib.upc.edu/horde/imp/check_mail/resum_mail_json.php";
	public String URL_AVISOS = "https://raco.fib.upc.edu/extern/rss_avisos.jsp?KEY=";
	public String URL_ASSIGS_FIB = "https://raco.fib.upc.edu/api/assigListFIB?KEY=public";
	public String URL_INFO_ASSIG = "https://raco.fib.upc.edu/api/InfoAssigFIB?KEY=public";
	// public String URL_AGENDA_RACO =
	// "https://raco.fib.upc.edu/ical/portada.rss?KEY=";
	public String URL_AGENDA_RACO = "https://raco.fib.upc.edu/ical/portada.ics?KEY=";
	public String URL_ASSIGS_RACO = "https://raco.fib.upc.edu/api/assigList?KEY=";
	public String URL_AULES_I_OCUPACIO_RACO = "https://raco.fib.upc.edu/api/aules/places-lliures.json";
	public String URL_CLASSES_DIA_RACO = "https://raco.fib.upc.edu/ReservesWebApp/icsAvui.ics";
	public String URL_AULA_A5 = "http://www.fib.upc.edu/mapa.php?mod=a5";
	public String URL_AULA_B5 = "http://www.fib.upc.edu/mapa.php?mod=b5";
	public String URL_AULA_C6 = "http://www.fib.upc.edu/mapa.php?mod=c6";
	public String URL_HORARI_RACO = "https://raco.fib.upc.edu/ical/horari.ics?KEY=";

	// Notificacions
	public String URL_NOTIFICACIO_REGISTRAR = "https://raco.fib.upc.edu/api/subscribeNotificationSystem?system=android";
	public String URL_NOTIFICACIO_DESREGISTRAR = "https://raco.fib.upc.edu/api/unsubscribeNotificationSystem?system=android";
	public String REGISTRE_ID = "REGISTRE_ID";
	// Per saber si en tenim de pendents o no
	public String NOTIFICATION_COUNTER = "NUMERO_NOTIFICACIONS";
	// Constant per les notificacions
	public int NOTIFICATION_ID_ACTIVACIO = 1;
	public int NOTIFICATION_ID_AVISOS = 2;

	// Constants
	public static final int TIPUS_NOTICIA = 0;
	public static final int TIPUS_CORREU = 1;
	public static final int TIPUS_AVISOS = 2;
	public static final int TIPUS_INI_CONFIG = 3; // USERNAME I PASSWORS LLISTA
													// INICIAL
	public static final int TIPUS_ASSIG = 4;
	public static final int TIPUS_AGENDA_RACO = 5; // AGENDA
	public static final int TIPUS_AULES_I_OCUPACIO_RACO = 6;
	public static final int TIPUS_CLASSES_DIA_RACO = 7; // ASSIGNATURES A LA
														// LLISTA OCUPACIO
	public static final int TIPUS_HORARI_RACO = 8; // HORARI
	public final static String CONTACTAR_LCFIB = "http://suport.fib.upc.edu";

	public final String KEY_AVISOS = "KEY_AVISOS";
	public final String KEY_ASSIG_FIB = "KEY_ASSIG_FIB";
	public final String KEY_AGENDA_RACO_XML = "KEY_AGENDA_RACO_XML";
	public final String KEY_AGENDA_RACO_CAL = "KEY_AGENDA_RACO_CAL";
	public final String KEY_ASSIGS_RACO = "KEY_ASSIGS_RACO";
	public final String KEY_HORARI_RACO = "KEY_HORARI_RACO";
	public final String KEY_NOTIFICACIONS_REGISTRAR = "KEY_NOTIFICACIONS_REGISTRAR";
	public final String KEY_NOTIFICACIONS_DESREGISTRAR = "KEY_NOTIFICACIONS_DESREGISTRAR";

	// SharedPreferences
	public static final String PREFERENCE_NOTIFICACIONS = "notificacionsEnabled";
	public static final String PREFERENCE_LISTCONFIG = "listconfig";
	public static final String PREFERENCE_CHECKBOX_FILTER = "Active_box_preference";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	// Localització
	public final Long INTERVAL_TIME = new Long(5 * 1000);
	public final float METERS_MOVED_VALUE = 500.0f;
	public final int LATITUDE_FIB = (int) (41.38926983970336 * 1E6);
	public final int LONGITUDE_FIB = (int) (2.113073766231537 * 1E6);
	public final float RADI = (float) 2.0;

	// UTILS
	public final static int DIES_HORARI_SEGUENT = 10;
	public final static int DIES_HORARI_ANTERIOR = -10;
	public final int MAX_CORREUS = 8;
	public final static long MILLSECS_PER_DIA = 24 * 60 * 60 * 1000;
	public final int TEMPS_REFRESC = 5; // minuts

	// VISIBILITAT DELS ELEMENTS
	public final static int INVISIBLE = 4;
	public final static int VISIBLE = 0;
	public final static int REMOVE_BACKGROUND = 0;
	public static final int DATE_DIALOG_ID = 0;

	private static AndroidUtils sInstancia = null;
	
	// Formatters
	public final static SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yy", Locale.US);
	
	// creador sincronizado para protegerse de posibles problemas multi-hilo
	// otra prueba para evitar instanciación múltiple
	private synchronized static void crearInstancia() {
		if (sInstancia == null) {
			sInstancia = new AndroidUtils();
		}
	}

	public static AndroidUtils getInstance() {
		if (sInstancia == null)
			crearInstancia();
		return sInstancia;
	}

	public URL crearURL(String url) throws MalformedURLException {
		return new URL(url);
	}

	public Date crearDataActual() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getDefault());
		return cal.getTime();
	}

	public VEvent crearVEventAvui() {
		java.util.Calendar startDate = Calendar.getInstance();
		startDate.setTimeZone(java.util.TimeZone.getDefault());
		// java.util.Calendar startDate = Calendar.getInstance();
		// startDate.set(2011, 8, 8);
		DateTime start = new DateTime(startDate.getTime());
		start.setUtc(true);
		VEvent Eavui = new VEvent(start, start, "avui");
		return Eavui;
	}

	public VEvent crearVEvent(Date data) {
		// java.util.Calendar startDate =
		// Calendar.getInstance(java.util.TimeZone.getTimeZone("Europe/Madrid"));
		java.util.Calendar startDate = Calendar.getInstance();
		startDate.set(data.getYear(), data.getMonth() + 1, data.getDate());
		DateTime start = new DateTime(startDate.getTime());
		start.setUtc(true);
		VEvent Eavui = new VEvent(start, start, "diaConcret");
		return Eavui;
	}

	public Date resetHora() {
		TimeZone timeZone = TimeZone.getTimeZone("Europe/Madrid");
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timeZone);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public boolean compararDates(net.fortuna.ical4j.model.Date avui,
			net.fortuna.ical4j.model.Date data) {
		if (avui.getYear() != data.getYear()) {
			return false;
		}
		if (avui.getMonth() != data.getMonth()) {
			return false;
		}
		if (avui.getDate() != data.getDate()) {
			return false;
		}
		return true;
	}

	public String htmlToString(String html) {
		// Remove HTML tag from java String
		String noHTMLString = html.replaceAll("\\<.*?\\>", "");

		// Remove Carriage return from java String
		noHTMLString = noHTMLString.replaceAll("\r", "<br/>");

		// Remove New line from java string and replace html break
		noHTMLString = noHTMLString.replaceAll("\n", " ");
		noHTMLString = noHTMLString.replaceAll("\'", "&#39;");
		noHTMLString = noHTMLString.replaceAll("\"", "&quot;");
		return noHTMLString;
	}
	
	public static SimpleDateFormat crearSimpleDateFormat(){
		return format;
	}

	public static Date dateXMLStringToDateParser(String sDate) {
		Date date = null;
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
			date = formatter.parse(sDate);
		}catch (ParseException e){
			e.printStackTrace();
		}
		return date;
	}

	public static Date dateXMLStringToDateControlador(SimpleDateFormat formatter, String sDate) {
		Date date = null;
		try {
			
			date = formatter.parse(sDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date dateJSONStringToDateCorreu (String sDate){
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
			"\"dd/MM/yyyy HH:mm\"");
			date = formatter.parse(sDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date dateJSONStringToDateOcupacioAules (String sDate){
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");
			date = formatter.parse(sDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	public static String dateToStringAssigRaco(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM - hh:mm a");
		String sDate = sdf.format(date);
		return sDate;
	}
	
	public static String dateToStringAgenda(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM - hh:mm a");
		String sDate = sdf.format(date);
		return sDate;
	}
	
	public static String dateToStringVistaInici(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
		String sDate = sdf.format(date);
		return sDate;
	}
	
	public static String dateToStringMostraItemInfo (Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM - hh:mm");
		String sDate = sdf.format(date);
		return sDate;
	}

}
