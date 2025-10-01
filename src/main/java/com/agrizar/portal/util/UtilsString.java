package com.agrizar.portal.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;

public final class UtilsString {

	public static String emailMask (String mail) {
		String mask = "";
		int indexArroba = mail.indexOf("@");
		int posfinCorreo1Parte = 3;
		if (posfinCorreo1Parte > indexArroba) posfinCorreo1Parte = indexArroba;
		String uno = mail.substring(0, posfinCorreo1Parte);
		String dos = "";
		if (posfinCorreo1Parte < indexArroba) dos = mail.substring(posfinCorreo1Parte, indexArroba);
	    int posFinal = mail.length();
	    int indexPunto = mail.lastIndexOf(".");
	    int posfinDominio1Parte = indexArroba + 4; 
	    if (posfinDominio1Parte > indexPunto) posfinDominio1Parte = indexPunto;
	    String tres = mail.substring(indexArroba+1, posfinDominio1Parte);
	    String cuatro = "";
	    if (posfinDominio1Parte < indexPunto) cuatro = mail.substring(posfinDominio1Parte, indexPunto);
	    String finalMail= mail.substring(indexPunto, posFinal);
	    dos = repetirCaracter(dos, "•");
	    cuatro = repetirCaracter(cuatro, "•");
	    mask =uno + dos + "@" + tres + cuatro + finalMail;
		return mask;
	}

	public static String repetirCaracter (String cadena, String caracter) {
		String result = "";

		for (int i = 0; i < cadena.length(); i++) {
		    result += caracter;
		}
		return	result;
	}

	public static String formatearFecha (String fecha, String formato) {
		String result = "";
		
		String anio = fecha.substring(0, 4);
		String mes = fecha.substring(5, 7);
		String dia = fecha.substring(8, 10);
		
		if (formato.equals("ddmmaaaa")) {
			result = dia + mes + anio;
		}

		return	result;
	}
	
	public static String encodeText (String texto) {
		String result = "";
		
		try {
			result = MimeUtility.encodeText(texto,"UTF-8","B");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return	result;
	}
		
	public static String formatearDecimales (double numero, String format) {
    	NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
    	DecimalFormat df = (DecimalFormat)nf;
    	df.applyPattern(format);
    	String result = df.format(numero);
    	
		return result;
	}
	
	public static String convertirCaracteresXml(String texto) {
		String result = texto.replace("&amp;", "&").replace("&", "&amp;").replace("Ñ", "&#209;").replace("\"", "&quot;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;").replace("\u00c4", "&#196;").replace("\u00cb", "&#203;").replace("\u00cf", "&#207;").replace("\u00d6", "&#214;").replace("\u00dc", "&#220;");
		return result;	
	}
	
	public static boolean isNumeric(String texto) {

		Pattern pat = Pattern.compile("[0-9]*");
        Matcher mat = pat.matcher(texto);
        return mat.matches();
	}
	
	public static Integer convertirInteger(String texto) {
		Integer result = 0;
		
		try {
			result = Integer.parseInt(texto);
		} catch (Exception e) {
		}
		
		return result;
	} 

}
