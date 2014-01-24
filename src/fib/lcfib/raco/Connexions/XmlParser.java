package fib.lcfib.raco.Connexions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fib.lcfib.raco.AndroidUtils;
import fib.lcfib.raco.Model.Avis;
import fib.lcfib.raco.Model.ItemGeneric;
import fib.lcfib.raco.Model.Noticia;

public class XmlParser {

	private final String mTAG = "XMLParser";

	private static XmlParser sInstancia = null;
	// creador sincronizado para protegerse de posibles problemas multi-hilo
	// otra prueba para evitar instanciación múltiple
	private synchronized static void crearInstancia() {
		if (sInstancia == null) {
			sInstancia = new XmlParser();
		}
	}
	
	public static XmlParser getInstance() {
		if (sInstancia == null)
			crearInstancia();
		return sInstancia;
	}
	
	public LlistesItems parserData(int tipus, URL url){
		LlistesItems lli = new LlistesItems();
		switch (tipus) {
		case AndroidUtils.TIPUS_NOTICIA:/** Noticies */
			lli = parserNoticies(url);
			break;
		case AndroidUtils.TIPUS_AVISOS:
			lli = parserAvisos(url);
			break;
		default:
			break;
		}
		return lli;
	}

	protected LlistesItems parserAvisos(URL url) {
		LlistesItems lli = new LlistesItems();
		AndroidUtils au = AndroidUtils.getInstance();
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000);
			InputStream is = con.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document document = db.parse(is);
			Element element = document.getDocumentElement();

			NodeList nodeListGeneral = element.getElementsByTagName("item");
			String urlImatge = au.URL_AVISOS_IMATGE;
			String[] nomAssig;
			if (nodeListGeneral.getLength() > 0) {
				for (int i = 0; i < nodeListGeneral.getLength(); i++) {
					Element entry = (Element) nodeListGeneral.item(i);
					Element titleE = (Element) entry.getElementsByTagName(
							"title").item(0);
					Element descriptionE = (Element) entry
							.getElementsByTagName("description").item(0);
					Element linkE = (Element) entry
					.getElementsByTagName("link").item(0);

					String title = titleE.getFirstChild().getNodeValue();
					String description = "";
					if(descriptionE.getFirstChild() != null){
						description = descriptionE.getFirstChild().getNodeValue();	
					}
					
					String link = linkE.getFirstChild()
					.getNodeValue();
					Element pubDateE = (Element) entry.getElementsByTagName(
							"pubDate").item(0);
					
					Date pubDate = AndroidUtils.dateXMLStringToDateParser(pubDateE.getFirstChild()
							.getNodeValue());

					nomAssig = title.trim().split("-");
					lli.afegirItemGeneric((ItemGeneric) new Avis(title,
							description + link, urlImatge, pubDate,
							AndroidUtils.TIPUS_AVISOS, nomAssig[0]));
					lli.afegirImatge(urlImatge);

				}
				con.disconnect();
			} else {
				con.disconnect();
			}

		} catch (Exception e) {
			con.disconnect();
			e.printStackTrace();
			return null;
		}
		con.disconnect();
		return lli;
	}

	protected LlistesItems parserNoticies(URL url) {
		LlistesItems lli = new LlistesItems();
		AndroidUtils au = AndroidUtils.getInstance();
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000);
			InputStream is = con.getInputStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			Document document = null;
			db = dbf.newDocumentBuilder();
			document = db.parse(is);

			Element element = document.getDocumentElement();
			NodeList nodeListGeneral = element.getElementsByTagName("item");

			if (nodeListGeneral.getLength() > 0) {
				String _urlImatge;
				for (int i = 0; i < nodeListGeneral.getLength(); i++) {
					Element entry = (Element) nodeListGeneral.item(i);
					Element _titleE = (Element) entry.getElementsByTagName(
							"title").item(0);
					Element _descriptionE = (Element) entry
							.getElementsByTagName("description").item(0);
					Element _pubDateE = (Element) entry.getElementsByTagName(
							"pubDate").item(0);
					Element _urlImatgeE = (Element) entry.getElementsByTagName(
							"media:thumbnail").item(0);
					Element _linkE = (Element) entry.getElementsByTagName(
					"link").item(0);

					String _title = _titleE.getFirstChild().getNodeValue();
					String _description = _descriptionE.getFirstChild()
							.getNodeValue();
					String link = _linkE.getFirstChild().getNodeValue();
					Date _pubDate = AndroidUtils.dateXMLStringToDateParser(_pubDateE.getFirstChild()
								.getNodeValue());
					
					if (_urlImatgeE == null) {
						_urlImatge = au.URL_IMATGE_DEFAULT;
					} else {
						_urlImatge = _urlImatgeE.getAttribute("url");
					}
					/** Cas explicit per les notícies */
					lli.afegirItemGeneric((Noticia) new Noticia(_title,
							_description, _urlImatge, _pubDate, AndroidUtils.TIPUS_NOTICIA,
							link));
					lli.afegirImatge(_urlImatge);
				}
			} else {
				con.disconnect();
			}
		} catch (ParserConfigurationException e) {
			con.disconnect();
			return null;
		} catch (SAXException e) {
			con.disconnect();
			return null;
		} catch (IOException e) {
			con.disconnect();
			return null;
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con.disconnect();
		return lli;
	}
}
