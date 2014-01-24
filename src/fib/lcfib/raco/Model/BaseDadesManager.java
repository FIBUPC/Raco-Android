package fib.lcfib.raco.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fib.lcfib.raco.AndroidUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Classe Controlador: Creates, opens, closes, and uses a SQLite database */
public class BaseDadesManager {

	private static final String mTAG = "BaseDades";

	/** taula: NOTICIA, AVISOS, CORREU, AGENDA */

	public static final String COL_ID = "_id";
	public static final String COL_TITOL = "titol"; // agenda
	public static final String COL_DESCRIP = "descripcio"; // agenda
	public static final String COL_IMATGE = "imatge";
	public static final String COL_DATAPUB = "dataPub"; // agenda
	public static final String COL_TIPUS = "tipus";

	/** taula:NOTICIES PARTICULAR */
	public static final String COL_LINK = "link";

	/** Taula: CORREU PARTICULAR */
	public static final String COL_LLEGITS = "llegits";
	public static final String COL_NO_LLEGITS = "no_llegits";

	/** taula: ASSIGNATURES_FIB */
	public static final String COL_NOM_ASSIG = "nomAssig"; // aula (ocupacio) i
															// horari
	public static final String COL_ID_ASSIG = "idAssig";
	public static final String COL_CREDITS = "credits";
	public static final String COL_OBJECTIUS = "objectiu";

	/** taula: PROFESSORS */
	public static final String COL_NOM_PROF = "nom";
	public static final String COL_EMAIL = "email";
	public static final String COL_CODI_ASSIG = "codi";

	/** taula: AULA */
	public static final String COL_NOM_AULA = "nomAula"; // horari
	public static final String COL_PLACES = "places";
	public static final String COL_DATA_ACTUALITZACIO = "dataActualitzacio";
	public static final String COL_DATA_INICI = "dataInici";
	public static final String COL_DATA_FI = "dataFi";
	public static final String COL_HI_HA_CLASSE = "hihaClasse";

	/** taula: HORARI */
	public static final String COL_HORA_INICI = "horaInici";
	public static final String COL_HORA_FI = "horaFi";
	public static final String COL_DIA = "dia";
	public static final String COL_MES = "mes";
	public static final String COL_ANY = "any";

	/** NOM DE LES TAULES */
	private static final String DATABASE_NAME = "RacoMobile";
	private static final String DATABASE_TABLE_NOTICIES = "noticies";
	private static final String DATABASE_TABLE_AVISOS = "avisos";
	private static final String DATABASE_TABLE_CORREUS = "correus";
	private static final String DATABASE_TABLE_ASSIG_FIB = "assigFib";
	private static final String DATABASE_TABLE_PROFESSORS = "professors";
	private static final String DATABASE_TABLE_OBJECTIUS = "objectius";
	private static final String DATABASE_TABLE_AGENDA = "agenda";
	private static final String DATABASE_TABLE_ASSIG_RACO = "assigRaco";
	private static final String DATABASE_TABLE_AULES_OCUPACIO = "aulesOcupacio";
	private static final String DATABASE_TABLE_HORARI = "horari";
	private static final int DATABASE_VERSION = 1;

	/** SQL PER CREAR LES TAULES */
	private static final String DATABASE_CREATE_TABLE_NOTICIES = "create table if not exists noticies (_id integer primary key autoincrement, titol text not null, descripcio text not null, imatge text not null, dataPub text not null, tipus text integer not null, link text);";
	private static final String DATABASE_CREATE_TABLE_AVISOS = "create table if not exists avisos (_id integer primary key autoincrement, titol text not null, descripcio text, imatge text not null, dataPub text not null, tipus text integer not null, idAssig text);";
	private static final String DATABASE_CREATE_TABLE_CORREUS = "create table if not exists correus (_id integer primary key autoincrement, titol text not null, descripcio text not null, imatge text not null, dataPub text not null, tipus text integer not null, llegits text integer, no_llegits text integer);";
	private static final String DATABASE_CREATE_TABLE_ASSIGNATURES_FIB = "create table assigFib (_id integer primary key autoincrement, codi text integer not null , nomAssig text not null, idAssig text not null, credits text integer);";
	private static final String DATABASE_CREATE_TABLE_ASSIGNATURES_RACO = "create table assigRaco (_id integer primary key autoincrement, codi text integer not null , nomAssig text not null, idAssig text not null);";
	private static final String DATABASE_CREATE_TABLE_PROFESSORS = "create table professors (_id integer primary key autoincrement, nom text not null, email text integer not null, codi text not null);";
	private static final String DATABASE_CREATE_TABLE_OBJECTIUS = "create table objectius (_id integer primary key autoincrement, codi text not null, objectiu text not null);";
	private static final String DATABASE_CREATE_TABLE_AGENDA = "create table agenda (_id integer primary key autoincrement, titol text not null, descripcio text not null, dataPub text not null);";
	private static final String DATABASE_CREATE_TABLE_AULES_OCUPACIO = "create table aulesOcupacio (_id integer primary key autoincrement, nomAula text not null, places text not null, dataActualitzacio text not null, dataInici text, dataFi text, nomAssig text, hihaClasse text);";
	private static final String DATABASE_CREATE_TABLE_HORARI = "create table horari (_id integer primary key autoincrement, horaInici text, horaFi text, nomAssig text, nomAula text, dia text integer, mes text integer, any text integer);";
	private final Context mContext;

	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDb;

	public BaseDadesManager(Context ctx) {
		this.mContext = ctx;
		mDBHelper = new DatabaseHelper(mContext);
	}

	/** Obrim BD */
	public BaseDadesManager open() throws SQLException {
		mDb = mDBHelper.getWritableDatabase();
		return this;
	}

	/** Tanquem BD */
	public void close() throws SQLException {
		mDBHelper.close();
	}

	/** INSERTAR ITEM A LA TAULA X */

	public long insertItemNoticia(String titol, String descripcio,
			String imatge, String dataPub, int tipus, String link)
			throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_TITOL, titol);
		initialValues.put(COL_DESCRIP, descripcio);
		initialValues.put(COL_IMATGE, imatge);
		initialValues.put(COL_DATAPUB, dataPub);
		initialValues.put(COL_TIPUS, tipus);
		initialValues.put(COL_LINK, link);
		return mDb.insert(DATABASE_TABLE_NOTICIES, null, initialValues);
	}

	public long insertItemAvis(String titol, String descripcio, String imatge,
			String dataPub, int tipus, String nomAssig) throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_TITOL, titol);
		initialValues.put(COL_DESCRIP, descripcio);
		initialValues.put(COL_IMATGE, imatge);
		initialValues.put(COL_DATAPUB, dataPub);
		initialValues.put(COL_TIPUS, tipus);
		initialValues.put(COL_ID_ASSIG, nomAssig);
		return mDb.insert(DATABASE_TABLE_AVISOS, null, initialValues);
	}

	public long insertItemCorreu(String titol, String descripcio,
			String imatge, String dataPub, int tipus, int llegits,
			int no_llegits) throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_TITOL, titol);
		initialValues.put(COL_DESCRIP, descripcio);
		initialValues.put(COL_IMATGE, imatge);
		initialValues.put(COL_DATAPUB, dataPub);
		initialValues.put(COL_TIPUS, tipus);
		initialValues.put(COL_LLEGITS, llegits);
		initialValues.put(COL_NO_LLEGITS, no_llegits);
		return mDb.insert(DATABASE_TABLE_CORREUS, null, initialValues);
	}

	public long insertItemAssigFib(int codi, String nomAssig, String idAssig,
			int credits) throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_CODI_ASSIG, codi);
		initialValues.put(COL_NOM_ASSIG, nomAssig);
		initialValues.put(COL_ID_ASSIG, idAssig);
		initialValues.put(COL_CREDITS, credits);
		return mDb.insert(DATABASE_TABLE_ASSIG_FIB, null, initialValues);
	}

	public long insertItemProfessors(String nom, String email, String codi)
			throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_NOM_PROF, nom);
		initialValues.put(COL_EMAIL, email);
		initialValues.put(COL_CODI_ASSIG, codi);
		return mDb.insert(DATABASE_TABLE_PROFESSORS, null, initialValues);
	}

	public long insertItemObjectiu(String codi, String objectiu)
			throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_CODI_ASSIG, codi);
		initialValues.put(COL_OBJECTIUS, objectiu);
		return mDb.insert(DATABASE_TABLE_OBJECTIUS, null, initialValues);
	}

	public long insertItemAgenda(String titol, String descripcio, String data)
			throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_TITOL, titol);
		initialValues.put(COL_DESCRIP, descripcio);
		initialValues.put(COL_DATAPUB, data);
		return mDb.insert(DATABASE_TABLE_AGENDA, null, initialValues);
	}

	public long insertItemAssigRaco(int codi, String nomAssig, String idAssig)
			throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_CODI_ASSIG, codi);
		initialValues.put(COL_NOM_ASSIG, nomAssig);
		initialValues.put(COL_ID_ASSIG, idAssig);
		return mDb.insert(DATABASE_TABLE_ASSIG_RACO, null, initialValues);
	}

	public long insertItemAulaOcupacio(String nomAula, String places,
			String dataActualitzacio, String dataInici, String dataFi,
			String nomAssig, String hihaClasse) throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_NOM_AULA, nomAula);
		initialValues.put(COL_PLACES, places);
		initialValues.put(COL_DATA_ACTUALITZACIO, dataActualitzacio);
		initialValues.put(COL_DATA_INICI, dataInici);
		initialValues.put(COL_DATA_FI, dataFi);
		initialValues.put(COL_NOM_ASSIG, nomAssig);
		initialValues.put(COL_HI_HA_CLASSE, hihaClasse);
		return mDb.insert(DATABASE_TABLE_AULES_OCUPACIO, null, initialValues);
	}

	public long insertItemHorari(String horaInici, String horaFi,
			String nomAssig, String nomAula, int dia, int mes, int any)
			throws SQLException {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COL_HORA_INICI, horaInici);
		initialValues.put(COL_HORA_FI, horaFi);
		initialValues.put(COL_NOM_ASSIG, nomAssig);
		initialValues.put(COL_NOM_AULA, nomAula);
		initialValues.put(COL_DIA, dia);
		initialValues.put(COL_MES, mes);
		initialValues.put(COL_ANY, any);
		return mDb.insert(DATABASE_TABLE_HORARI, null, initialValues);
	}

	/** ELIMINAR ITEMS D'UNA TAULA X */
	public boolean deleteItemNoticia(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_NOTICIES, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemAvis(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_AVISOS, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemCorreu(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_CORREUS, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemAssigFib(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_ASSIG_FIB, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemProfessor(long rowId) throws SQLException {
		return mDb
				.delete(DATABASE_TABLE_PROFESSORS, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemObjectiu(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_OBJECTIUS, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemAgenda(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_AGENDA, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemAssigRaco(long rowId) throws SQLException {
		return mDb
				.delete(DATABASE_TABLE_ASSIG_RACO, COL_ID + "=" + rowId, null) > 0;
	}

	public boolean deleteItemAulaOcupacio(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_AULES_OCUPACIO, COL_ID + "=" + rowId,
				null) > 0;
	}

	public boolean deleteItemHorari(long rowId) throws SQLException {
		return mDb.delete(DATABASE_TABLE_HORARI, COL_ID + "=" + rowId, null) > 0;
	}

	public void deleteTableObjectiusAssig(String assig) throws SQLException {
		String[] args = new String[] { assig };
		mDb.rawQuery("DELETE FROM " + DATABASE_TABLE_OBJECTIUS + " WHERE "
				+ COL_CODI_ASSIG + " = ?", args);
	}

	public void deleteTableProfessorsAssig(String assig) throws SQLException {
		String[] args = new String[] { assig };

		mDb.rawQuery("DELETE FROM " + DATABASE_TABLE_PROFESSORS + " WHERE "
				+ COL_CODI_ASSIG + "=?", args);

	}

	/** OBTENIR TOTS ELS ITEMS D'UNA TAULA */

	public ArrayList<ItemGeneric> getAllNoticies() {
		try {
			mDb.execSQL(DATABASE_CREATE_TABLE_NOTICIES);
			Cursor mCursor = mDb.query(DATABASE_TABLE_NOTICIES, new String[] {
					COL_ID, COL_TITOL, COL_DESCRIP, COL_IMATGE, COL_DATAPUB,
					COL_TIPUS, COL_LINK }, null, null, null, null, null);

			ArrayList<ItemGeneric> list = new ArrayList<ItemGeneric>();
			if (mCursor != null && mCursor.moveToFirst()) {
				SimpleDateFormat formatter = AndroidUtils
						.crearSimpleDateFormat();
				do {
					Date date = AndroidUtils.dateXMLStringToDateControlador(
							formatter, mCursor.getString(4));
					Noticia n = new Noticia(mCursor.getString(1),
							mCursor.getString(2), mCursor.getString(3), date,
							mCursor.getInt(5), mCursor.getString(6));
					list.add(n);
				} while (mCursor.moveToNext());
			}
			mCursor.close();
			return list;
		} catch (SQLException e) {
			return null;
		}
	}

	public ArrayList<ItemGeneric> getAllAvisos() {
		try {
			mDb.execSQL(DATABASE_CREATE_TABLE_AVISOS);
			Cursor mCursor = mDb.query(DATABASE_TABLE_AVISOS, new String[] {
					COL_ID, COL_TITOL, COL_DESCRIP, COL_IMATGE, COL_DATAPUB,
					COL_TIPUS, COL_ID_ASSIG }, null, null, null, null, null);

			ArrayList<ItemGeneric> list = new ArrayList<ItemGeneric>();
			if (mCursor != null && mCursor.moveToFirst()) {
				SimpleDateFormat formatter = AndroidUtils
						.crearSimpleDateFormat();
				do {
					Date date = AndroidUtils.dateXMLStringToDateControlador(
							formatter, mCursor.getString(4));
					Avis a = new Avis(mCursor.getString(1),
							mCursor.getString(2), mCursor.getString(3), date,
							mCursor.getInt(5), mCursor.getString(6));
					list.add(a);
				} while (mCursor.moveToNext());
			}
			mCursor.close();
			return list;
		} catch (SQLException e) {
			return null;
		}
	}

	public ArrayList<Correu> getAllCorreus() {
		try {
			mDb.execSQL(DATABASE_CREATE_TABLE_CORREUS);
			Cursor mCursor = mDb.query(DATABASE_TABLE_CORREUS, new String[] {
					COL_ID, COL_TITOL, COL_DESCRIP, COL_IMATGE, COL_DATAPUB,
					COL_TIPUS, COL_LLEGITS, COL_NO_LLEGITS }, null, null, null,
					null, null);

			ArrayList<Correu> list = new ArrayList<Correu>();
			if (mCursor != null && mCursor.moveToFirst()) {
				SimpleDateFormat formatter = AndroidUtils
						.crearSimpleDateFormat();
				do {
					Date date = AndroidUtils.dateXMLStringToDateControlador(
							formatter, mCursor.getString(4));
					Correu correu = new Correu(mCursor.getString(1),
							mCursor.getString(2), mCursor.getString(3), date,
							mCursor.getInt(5), mCursor.getInt(6),
							mCursor.getInt(7));
					list.add(correu);
				} while (mCursor.moveToNext());
			}
			mCursor.close();
			return list;
		} catch (SQLException e) {
			return null;
		}
	}

	public ArrayList<Assignatura> getAllAssigFib() throws SQLException {
		Cursor mCursor = mDb.rawQuery("SELECT " + COL_ID + ", "
				+ COL_CODI_ASSIG + ", " + COL_NOM_ASSIG + ", " + COL_ID_ASSIG
				+ ", " + COL_CREDITS + " FROM " + DATABASE_TABLE_ASSIG_FIB
				+ " ORDER BY " + COL_ID_ASSIG, null);

		ArrayList<Assignatura> list = new ArrayList<Assignatura>();
		if (mCursor != null && mCursor.moveToFirst()) {
			do {
				Assignatura a = new Assignatura(mCursor.getInt(1),
						mCursor.getString(2), mCursor.getString(3),
						mCursor.getInt(4), null, null);
				list.add(a);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	/*public Cursor getAllObjectius() throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_TABLE_PROFESSORS, new String[] {
				COL_ID, COL_CODI_ASSIG, COL_OBJECTIUS }, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	public ArrayList<EventAgenda> getAllAgenda() throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_TABLE_AGENDA, new String[] {
				COL_ID, COL_TITOL, COL_DESCRIP, COL_DATAPUB }, null, null,
				null, null, null);

		ArrayList<EventAgenda> list = new ArrayList<EventAgenda>();
		if (mCursor != null && mCursor.moveToFirst()) {
			SimpleDateFormat formatter = AndroidUtils.crearSimpleDateFormat();
			do {
				EventAgenda e = null;
				Date dateFi = AndroidUtils.dateXMLStringToDateControlador(
						formatter, mCursor.getString(2));
				Date dateIni = AndroidUtils.dateXMLStringToDateControlador(
						formatter, mCursor.getString(3));
				e = new EventAgenda(mCursor.getString(1), dateFi, dateIni);
				list.add(e);
			} while (mCursor.moveToNext());
			mCursor.close();
		}
		return list;
	}

	public ArrayList<AssignaturesAvisos> getAllAssigRaco() throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_TABLE_ASSIG_RACO, new String[] {
				COL_ID, COL_CODI_ASSIG, COL_NOM_ASSIG, COL_ID_ASSIG }, null,
				null, null, null, null);

		ArrayList<AssignaturesAvisos> list = new ArrayList<AssignaturesAvisos>();
		if (mCursor != null && mCursor.moveToFirst()) {
			do {
				AssignaturesAvisos a = new AssignaturesAvisos(
						mCursor.getString(2), mCursor.getInt(1),
						mCursor.getString(3), null, null, null, null);
				list.add(a);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<Aula> getAllAulaOcupacio() throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_TABLE_AULES_OCUPACIO, new String[] {
				COL_ID, COL_NOM_AULA, COL_PLACES, COL_DATA_ACTUALITZACIO,
				COL_DATA_INICI, COL_DATA_FI, COL_NOM_ASSIG, COL_HI_HA_CLASSE },
				null, null, null, null, null);

		ArrayList<Aula> list = new ArrayList<Aula>();
		if (mCursor != null && mCursor.moveToFirst()) {
			SimpleDateFormat formatter = AndroidUtils.crearSimpleDateFormat();
			do {
				Date update = AndroidUtils.dateXMLStringToDateControlador(
						formatter, mCursor.getString(3));
				Date inici = AndroidUtils.dateXMLStringToDateControlador(
						formatter, mCursor.getString(4));
				Date fi = AndroidUtils.dateXMLStringToDateControlador(
						formatter, mCursor.getString(5));
				Aula aula = new Aula(mCursor.getString(1),
						mCursor.getString(2), update, inici, fi,
						mCursor.getString(6), mCursor.getString(7));
				list.add(aula);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	/*public Cursor getAllHorari() throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_TABLE_HORARI, new String[] {
				COL_ID, COL_HORA_INICI, COL_HORA_FI, COL_NOM_ASSIG,
				COL_NOM_AULA, COL_DIA, COL_MES, COL_ANY }, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/
	
	public int getAllHorariSize() throws SQLException {
		Cursor mCursor = mDb.query(DATABASE_TABLE_HORARI, new String[] {
				COL_ID, COL_HORA_INICI, COL_HORA_FI, COL_NOM_ASSIG,
				COL_NOM_AULA, COL_DIA, COL_MES, COL_ANY }, null, null, null,
				null, null);
		
		int res = 0;
		if (mCursor != null&& mCursor.moveToFirst()) {
			res = mCursor.getCount();
		}
		return res;
	}

	/** OBTENIR UN ITEM EN PARTICULAR */

	/*public Cursor getNoticia(long rowId) throws SQLException {
		Cursor mCursor = mDb
				.query(true, DATABASE_TABLE_NOTICIES, new String[] { COL_ID,
						COL_TITOL, COL_DESCRIP, COL_IMATGE, COL_DATAPUB,
						COL_TIPUS }, COL_ID + "=" + rowId, null, null, null,
						null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	/*public Cursor getAvis(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_AVISOS, new String[] {
				COL_ID, COL_TITOL, COL_DESCRIP, COL_IMATGE, COL_DATAPUB,
				COL_TIPUS, COL_ID_ASSIG }, COL_ID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	/*public Cursor getCorreu(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_CORREUS, new String[] {
				COL_ID, COL_TITOL, COL_DESCRIP, COL_IMATGE, COL_DATAPUB,
				COL_TIPUS, COL_LLEGITS, COL_NO_LLEGITS }, COL_ID + "=" + rowId,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	/*public Cursor getAssigsFib(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_ASSIG_FIB,
				new String[] { COL_ID, COL_CODI_ASSIG, COL_NOM_ASSIG,
						COL_ID_ASSIG }, COL_ID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	/*public Cursor getProfessor(long rowId) throws SQLException {
		Cursor mCursor = mDb
				.query(true, DATABASE_TABLE_ASSIG_FIB, new String[] { COL_ID,
						COL_NOM_PROF, COL_EMAIL, COL_CODI_ASSIG }, COL_ID + "="
						+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	/*public Cursor getAulaOcupacio(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_AULES_OCUPACIO,
				new String[] { COL_ID, COL_NOM_AULA, COL_PLACES,
						COL_DATA_ACTUALITZACIO, COL_DATA_INICI, COL_DATA_FI,
						COL_NOM_ASSIG, COL_HI_HA_CLASSE },
				COL_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	/*public Cursor getHorari(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE_HORARI, new String[] {
				COL_ID, COL_HORA_INICI, COL_HORA_FI, COL_NOM_ASSIG,
				COL_NOM_AULA, COL_DIA, COL_MES, COL_ANY },
				COL_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}*/

	/** Consultes Concretes */
	public ArrayList<Professors> getProfessorsAssig(String id)
			throws SQLException {
		String[] args = new String[] { id };
		Cursor mCursor = mDb.rawQuery("SELECT " + COL_NOM_PROF + ", "
				+ COL_EMAIL + " FROM " + DATABASE_TABLE_PROFESSORS + " WHERE "
				+ COL_CODI_ASSIG + "=?", args);
		ArrayList<Professors> list = new ArrayList<Professors>();
		if (mCursor != null && mCursor.moveToFirst()) {
			do {
				Professors p = new Professors(mCursor.getString(0), mCursor.getString(1), Integer.valueOf(id));
				list.add(p);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<String> getObjectiuAssig(String id) throws SQLException {
		String[] args = new String[] { id };
		Cursor mCursor = mDb.rawQuery("SELECT " + COL_OBJECTIUS + " FROM "
				+ DATABASE_TABLE_OBJECTIUS + " WHERE " + COL_CODI_ASSIG + "=?",
				args);
		ArrayList<String> list = new ArrayList<String>();
		if (mCursor != null && mCursor.moveToFirst()) {
			do {
				list.add(mCursor.getString(0));
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<Long> getProfessorPrimaryKey(String id)
			throws SQLException {
		String[] args = new String[] { id };
		Cursor mCursor = mDb.rawQuery(
				"SELECT " + COL_ID + " FROM " + DATABASE_TABLE_PROFESSORS
						+ " WHERE " + COL_CODI_ASSIG + "=?", args);

		ArrayList<Long> list = new ArrayList<Long>();
		if (mCursor != null && mCursor.moveToFirst()) {
			do {
				list.add(mCursor.getLong(0));
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<Long> getObjectiuPrimaryKey(String id) throws SQLException {
		String[] args = new String[] { id };
		Cursor mCursor = mDb.rawQuery("SELECT " + COL_ID + " FROM "
				+ DATABASE_TABLE_OBJECTIUS + " WHERE " + COL_CODI_ASSIG + "=?",
				args);

		ArrayList<Long> list = new ArrayList<Long>();
		if (mCursor != null && mCursor.moveToFirst()) {
			do {
				list.add(mCursor.getLong(0));
			} while (mCursor.moveToNext());
		}
		return list;
	}

	public int getCreditsFib(String id) throws SQLException {
		String[] args = new String[] { id };
		Cursor mCursor = mDb.rawQuery("SELECT " + COL_CREDITS + " FROM "
				+ DATABASE_TABLE_ASSIG_FIB + " WHERE " + COL_CODI_ASSIG + "=?",
				args);
		int credits = 0;
		if (mCursor != null) {
			mCursor.moveToFirst();
			credits = mCursor.getInt(0);
		}
		mCursor.close();
		return credits;
	}

	public void updateInfoAssig(String id, String credits) throws SQLException {
		String[] args = new String[] { credits, id };
		Cursor mCursor = mDb.rawQuery("UPDATE " + DATABASE_TABLE_ASSIG_FIB
				+ " SET " + COL_CREDITS + "=? WHERE " + COL_CODI_ASSIG + "=?",
				args);
		if (mCursor != null && mCursor.moveToFirst());
		mCursor.close();
	}

	public ArrayList<AssignaturesAvisos> getAvisAssig(String nom)
			throws SQLException {
		String[] args = new String[] { nom };
		Cursor mCursor = mDb.rawQuery("SELECT " + COL_TITOL + ", "
				+ COL_DESCRIP + ", " + COL_DATAPUB + " , " + COL_ID_ASSIG
				+ ", " + COL_IMATGE + " FROM " + DATABASE_TABLE_AVISOS
				+ " WHERE " + COL_ID_ASSIG + "=?", args);

		ArrayList<AssignaturesAvisos> list = new ArrayList<AssignaturesAvisos>();
		if (mCursor != null && mCursor.moveToFirst()) {
			SimpleDateFormat formatter = AndroidUtils.crearSimpleDateFormat();
			do {
				Date date = AndroidUtils.dateXMLStringToDateControlador(
						formatter, mCursor.getString(2));
				AssignaturesAvisos a = new AssignaturesAvisos(
						mCursor.getString(3), 0, mCursor.getString(3),
						mCursor.getString(0), mCursor.getString(1), date,
						mCursor.getString(4));
				list.add(a);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	public ArrayList<EventHorari> getDiaHorari(String dia, String mes,
			String any) throws SQLException {
		String[] args = new String[] { dia, mes, any };
		Cursor mCursor = mDb.rawQuery("SELECT " + COL_HORA_INICI + ", "
				+ COL_HORA_FI + ", " + COL_NOM_ASSIG + ", " + COL_NOM_AULA
				+ ", " + COL_DIA + ", " + COL_MES + ", " + COL_ANY + " FROM "
				+ DATABASE_TABLE_HORARI + " WHERE " + COL_DIA + "=? AND "
				+ COL_MES + "=? AND " + COL_ANY + "=?", args);
		ArrayList<EventHorari> list = new ArrayList<EventHorari>();
		if (mCursor != null&& mCursor.moveToFirst()) {
			do {
				EventHorari e = new EventHorari(mCursor.getString(0), mCursor.getString(1),
						mCursor.getString(2), mCursor.getString(3), mCursor.getInt(4),
						mCursor.getInt(5), mCursor.getInt(6));
				list.add(e);
			} while (mCursor.moveToNext());
		}
		mCursor.close();
		return list;
	}

	/** ELIMINEM TOTA LA TAULA */

	public void deleteTableNoticies() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_NOTICIES);
		mDb.execSQL(DATABASE_CREATE_TABLE_NOTICIES);
	}

	public void deleteTableAvisos() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_AVISOS);
		mDb.execSQL(DATABASE_CREATE_TABLE_AVISOS);
	}

	public void deleteTableCorreus() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_CORREUS);
		mDb.execSQL(DATABASE_CREATE_TABLE_CORREUS);
	}

	public void deleteTableAssigFib() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_ASSIG_FIB);
		mDb.execSQL(DATABASE_CREATE_TABLE_ASSIGNATURES_FIB);
	}

	public void deleteTableProfessors() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_PROFESSORS);
		mDb.execSQL(DATABASE_CREATE_TABLE_PROFESSORS);
	}

	public void deleteTableObjectius() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_OBJECTIUS);
		mDb.execSQL(DATABASE_CREATE_TABLE_OBJECTIUS);
	}

	public void deleteTableAgenda() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_AGENDA);
		mDb.execSQL(DATABASE_CREATE_TABLE_AGENDA);
	}

	public void deleteTableAssigRaco() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_ASSIG_RACO);
		mDb.execSQL(DATABASE_CREATE_TABLE_ASSIGNATURES_RACO);
	}

	public void deleteTableAulesOcupacio() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_AULES_OCUPACIO);
		mDb.execSQL(DATABASE_CREATE_TABLE_AULES_OCUPACIO);
	}

	public void deleteTableHorari() throws SQLException {
		mDb.execSQL("DROP TABLE IF exists " + DATABASE_TABLE_HORARI);
		mDb.execSQL(DATABASE_CREATE_TABLE_HORARI);
	}

	public void deleteTablesLogout() throws SQLException {
		deleteTableAgenda();
		deleteTableCorreus();
		deleteTableAvisos();
		deleteTableAssigRaco();
		deleteTableAulesOcupacio();
		deleteTableHorari();
	}

	/** ELIMINEM TOTA LA BD */

	public void deleteDB() throws SQLException {
		mDBHelper.deleteDatabase(mDb);
	}

	/** Crear i actualitzar BD */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static Context mContext;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			DatabaseHelper.mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_TABLE_NOTICIES);
			db.execSQL(DATABASE_CREATE_TABLE_AVISOS);
			db.execSQL(DATABASE_CREATE_TABLE_CORREUS);
			db.execSQL(DATABASE_CREATE_TABLE_ASSIGNATURES_FIB);
			db.execSQL(DATABASE_CREATE_TABLE_PROFESSORS);
			db.execSQL(DATABASE_CREATE_TABLE_OBJECTIUS);
			db.execSQL(DATABASE_CREATE_TABLE_AGENDA);
			db.execSQL(DATABASE_CREATE_TABLE_ASSIGNATURES_RACO);
			db.execSQL(DATABASE_CREATE_TABLE_AULES_OCUPACIO);
			db.execSQL(DATABASE_CREATE_TABLE_HORARI);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_NOTICIES);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_CORREUS);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_AVISOS);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_ASSIG_FIB);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_PROFESSORS);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_OBJECTIUS);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_AGENDA);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_ASSIG_RACO);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_AULES_OCUPACIO);
			db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE_HORARI);
			onCreate(db);
		}

		public void deleteDatabase(SQLiteDatabase db) {
			this.close();
			db.close();
		}
	}

}
