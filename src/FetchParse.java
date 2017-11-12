import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class FetchParse extends Thread {

	public boolean success = false;

	String url = "";
	String destination = "";
	String op = "";
	boolean includeHeader = true;
	String date;
	String etad;
	boolean includeIndices = false;

	FetchParse(String URL, String dest, String Opt, String dt, boolean iH, boolean iI, String td) {
		url = URL;
		destination = dest;
		op = Opt;
		includeHeader = iH;
		date = dt;
		includeIndices = iI;
		etad = td;
	}

	private static void downloadUsingNIO(String urlStr, String file) throws IOException {
		URL url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}

	static class DateLabelFormatter extends AbstractFormatter {

		private static final long serialVersionUID = -4778153447202992730L;
		private String datePattern = "yyyy-MM-dd";
		private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

		@Override
		public Object stringToValue(String text) throws ParseException {
			return dateFormatter.parseObject(text);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value != null) {
				Calendar cal = (Calendar) value;
				return dateFormatter.format(cal.getTime());
			}

			return "";
		}

	}

	private static String unzip(String zipFilePath, String destDir) {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			String fileName = ze.getName();
			while (ze != null) {
				// System.out.println(fileName);
				File newFile = new File(destDir + File.separator + "temp.txt");
				// System.out.println("Unzipping to " +
				// newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destDir;

	}

	@Override
	public void run() {
		try {
			// String url =
			// "https://www.nseindia.com/content/historical/EQUITIES/2017/NOV/cm08NOV2017bhav.csv.zip";

			// Indices url =
			// https://www.nseindia.com/content/indices/ind_close_all_09112017.csv

			String res = "";
			for (int i = 0; i < destination.length(); i++) {
				if (destination.charAt(i) == '\\') {
					res += "\\\\";
				} else {
					res += destination.charAt(i);
				}
			}

			String destDir = res;
			// System.out.print(s);
			res += "\\\\Temp.csv.zip";

			downloadUsingNIO(url, res);

			String zipFilePath = res;

			unzip(zipFilePath, destDir);

			String line = "";
			BufferedReader br = new BufferedReader(new FileReader(destDir + "\\temp.txt"));

			BufferedWriter log = new BufferedWriter(new FileWriter(destDir + "\\" + date + op + ".txt"));

			if (!includeHeader) {
				br.readLine();
			}
			String dateFormatted = "TIMESTAMP";
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] country = line.split(",");

				if (country.length < 10) {
					continue;
				} else {

					if (!country[10].equals("TIMESTAMP")) {

						//String dt = country[10];

						StringTokenizer st = new StringTokenizer(country[10], "-");

						dateFormatted = "";
						dateFormatted = st.nextToken() + dateFormatted;
						// System.out.println(dateFormatted);
						String mm = "0";
						String month = st.nextToken();
						if (month.equals("JAN")) {
							mm = "01";
						} else if (month.equals("FEB")) {
							mm = "02";
						} else if (month.equals("MAR")) {
							mm = "03";
						} else if (month.equals("APR")) {
							mm = "04";
						} else if (month.equals("MAY")) {
							mm = "05";
						} else if (month.equals("JUN")) {
							mm = "06";
						} else if (month.equals("JUL")) {
							mm = "07";
						} else if (month.equals("AUG")) {
							mm = "08";
						} else if (month.equals("SEP")) {
							mm = "09";
						} else if (month.equals("OCT")) {
							mm = "10";
						} else if (month.equals("NOV")) {
							mm = "11";
						} else if (month.equals("DEC")) {
							mm = "12";
						}

						dateFormatted = mm + dateFormatted;
						dateFormatted = st.nextToken() + dateFormatted;
					}
					log.write(country[0] + "," + dateFormatted + "," + country[2] + "," + country[3] + "," + country[4]
							+ "," + country[5] + "," + country[8]);
					log.newLine();
				}

			}

			br.close();

			if (includeIndices) {

				url = "https://www.nseindia.com/content/indices/ind_close_all_" + etad + ".csv";
				System.out.println(url);
				downloadUsingNIO(url, destDir + "\\NSEIndex.txt");
			} else {
				br.close();
				log.close();

				File f1 = new File(zipFilePath);
				File f2 = new File(destDir + "\\temp.txt");
				f1.delete();
				f2.delete();
			}

			br = new BufferedReader(new FileReader(destDir + "\\NSEIndex.txt"));

			br.readLine();

			while ((line = br.readLine()) != null) {
				String[] iBits = line.split(",");
				//System.out.println(iBits[2]);
				if (iBits[2].equals("-") || iBits[8].equals("-")) {
					//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!");
					continue;
				} else {
					log.write(iBits[0] + "," + dateFormatted + "," + iBits[2] + "," + iBits[3] + "," + iBits[4] + "," + iBits[5] + "," + iBits[8]);
					log.newLine();
				}
			}

			log.close();

			File f1 = new File(zipFilePath);
			File f2 = new File(destDir + "\\temp.txt");
			f1.delete();
			f2.delete();

		} catch (Exception e) {

		}

		success = true;

	}

	public boolean checkDone() {
		return success;
	}
}
