package com.syc.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Base64;

public class Formatter {

    public String currency(float amount) {
        Format formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(new Float(amount));
    }

    public String pad(String string, int width) {
        if (string.length() >= width) {
            return string.substring(0, width);
        }
        StringBuffer output = new StringBuffer(string);
        for (int i = 0; i < (width - string.length()); i++) {
            output.append(' ');
        }
        return output.toString();
    }

    public String datetime(Timestamp dateTime) {
        if (null == dateTime)
            return "[Formatter]No se indico fecha";
        if (!(dateTime instanceof Timestamp))
            try {
                dateTime = (Timestamp) dateTime;
            } catch (Exception e) {
                return "[Formatter]Fecha no pertenece a \"java.sql.Timestamp\"";
            }
        Format formatter = DateFormat.getDateTimeInstance();
        try {
            return formatter.format(dateTime);
        } catch (Exception ex) {
            return "[Formatter]Fecha no parseable debido a: " + ex.getMessage();
        }
    }

    public String date(Date date) {
        if (null == date)
            return "[Formatter]No se indico fecha";
        if (!(date instanceof Date))
            try {
                date = (Date) date;
            } catch (Exception e) {
                return "[Formatter]Fecha no pertenece a \"java.sql.Timestamp\"";
            }
        Format formatter = DateFormat.getDateInstance();
        try {
            return formatter.format(date);
        } catch (Exception ex) {
            return "[Formatter]Fecha no parseable debido a: " + ex.getMessage();
        }
    }

    public String time(Timestamp dateTime) {
        if (null == dateTime)
            return "[Formatter]No se indico fecha";
        if (!(dateTime instanceof Timestamp))
            try {
                dateTime = (Timestamp) dateTime;
            } catch (Exception e) {
                return "[Formatter]Fecha no pertenece a \"java.sql.Timestamp\"";
            }
        Format formatter = DateFormat.getTimeInstance();
        try {
            return formatter.format(dateTime);
        } catch (Exception ex) {
            return "[Formatter]Fecha no parseable debido a: " + ex.getMessage();
        }
    }

    public static String numberToTextTimes(Integer number) {
        if (number == null || number <= 0 || number > 19)
            return "";
        switch(number) {
            case 1:
                return "PRIMERA";
            case 2:
                return "SEGUNDA";
            case 3:
                return "TERCERA";
            case 4:
                return "CUARTA";
            case 5:
                return "QUINTA";
            case 6:
                return "SEXTA";
            case 7:
                return "SEPTIMA";
            case 8:
                return "OCTAVA";
            case 9:
                return "NOVENA";
            case 10:
                return "DECIMA";
            default:
                return "DECIMA " + numberToTextTimes(number - 10);
        }
    }

    public static String numberToText(Double value) {
        if (value == null)
            return "";
        String num2Text = Formatter.numberToTextInt(value);
        if (value % 1 > 0) {
            Integer decimales = new Double((value % 1) * 100).intValue();
            num2Text += " " + decimales + "/100";
        }
        return num2Text;
    }

    //The parameter has to be double because of the rang we want to convert, Integer is not enoght
    public static String numberToTextInt(Double value) {
        if (value == null)
            return "";
        Double number = value - (value % 1);
        String num2Text = "";
        if (number.equals(0))
            num2Text = "CERO";
        else if (number.equals(1.0))
            num2Text = "UNO";
        else if (number.equals(2.0))
            num2Text = "DOS";
        else if (number.equals(3.0))
            num2Text = "TRES";
        else if (number.equals(4.0))
            num2Text = "CUATRO";
        else if (number.equals(5.0))
            num2Text = "CINCO";
        else if (number.equals(6.0))
            num2Text = "SEIS";
        else if (number.equals(7.0))
            num2Text = "SIETE";
        else if (number.equals(8.0))
            num2Text = "OCHO";
        else if (number.equals(9.0))
            num2Text = "NUEVE";
        else if (number.equals(10.0))
            num2Text = "DIEZ";
        else if (number.equals(11.0))
            num2Text = "ONCE";
        else if (number.equals(12.0))
            num2Text = "DOCE";
        else if (number.equals(13.0))
            num2Text = "TRECE";
        else if (number.equals(14.0))
            num2Text = "CATORCE";
        else if (number.equals(15.0))
            num2Text = "QUINCE";
        else if (number.compareTo(20.0) < 0)
            num2Text = "DIECI" + numberToTextInt(number - 10);
        else if (number.equals(20.0))
            num2Text = "VEINTE";
        else if (number.compareTo(30.0) < 0)
            num2Text = "VEINTI" + numberToTextInt(number - 20);
        else if (number.equals(30.0))
            num2Text = "TREINTA";
        else if (number.equals(40.0))
            num2Text = "CUARENTA";
        else if (number.equals(50.0))
            num2Text = "CINCUENTA";
        else if (number.equals(60.0))
            num2Text = "SESENTA";
        else if (number.equals(70.0))
            num2Text = "SETENTA";
        else if (number.equals(80.0))
            num2Text = "OCHENTA";
        else if (number.equals(90.0))
            num2Text = "NOVENTA";
        else if (number.compareTo(100.0) < 0)
            num2Text = numberToTextInt(new Double(number / 10).intValue() * 10.0) + " Y " + numberToTextInt(number % 10);
        else if (number.equals(100.0))
            num2Text = "CIEN";
        else if (number.compareTo(200.0) < 0)
            num2Text = "CIENTO " + numberToTextInt(number - 100);
        else if (number.equals(200.0) || number.equals(300.0) || number.equals(400.0) || number.equals(600.0) || number.equals(800.0)) {
            num2Text = numberToTextInt(number / 100) + "CIENTOS";
        } else if (number.equals(500.0))
            num2Text = "QUINIENTOS";
        else if (number.equals(700.0))
            num2Text = "SETECIENTOS";
        else if (number.equals(900.0))
            num2Text = "NOVECIENTOS";
        else if (number.compareTo(1000.0) < 0) {
            num2Text = numberToTextInt(new Double(number / 100).intValue() * 100.0) + " " + numberToTextInt(number % 100);
        } else if (number.equals(1000.0))
            num2Text = "MIL";
        else if (number.compareTo(2000.0) < 0)
            num2Text = "MIL " + numberToTextInt(number % 1000);
        else if (number.compareTo(1000000.0) < 0) {
            num2Text = numberToTextInt(number / 1000) + " MIL";
            if (number % 1000 > 0)
                num2Text += " " + numberToTextInt(number % 1000);
        } else if (number.equals(1000000.0))
            num2Text = "UN MILLON";
        else if (number.compareTo(2000000.0) < 0)
            num2Text = "UN MILLON " + numberToTextInt(number % 1000000);
        else if (new Double(number).compareTo(1000000000000.0) < 0) {
            num2Text = numberToTextInt(number / 1000000) + " MILLONES";
            if (number - (new Double(number / 1000000).intValue() * 1000000.0) > 0)
                num2Text += " " + numberToTextInt(number - (new Double(number / 1000000).intValue() * 1000000.0));
        } else {
            num2Text = "Cantidad fuera de rango";
        }
        return num2Text;
    }
}
