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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class FetchParseBSE extends Thread {

	public boolean success = false;

	String url = "";
	String destination = "";
	String op = "";
	String date;
	boolean includeHeader = true;
	boolean includeIndices = false;
	
	FetchParseBSE(String URL, String dest, String Opt, String dt, boolean iH, boolean iI) {
		url = URL;
		destination = dest;
		op = Opt;
		date = dt;
		includeHeader = iH;
		includeIndices = iI;
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

			String fileName = unzip(zipFilePath, destDir);

			String line = "";
			BufferedReader br = new BufferedReader(new FileReader(destDir + "\\temp.txt"));

			BufferedWriter log = new BufferedWriter(new FileWriter(destDir + "\\" + date + op + ".txt"));

			if (!includeHeader) {
				br.readLine();
			}

			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] country = line.split(",");
				String dateFormatted = "";

				if (country.length < 10) {
					continue;
				} else {

					log.write(country[0] + "," + date + "," + country[4] + "," + country[5] + "," + country[6] + ","
							+ country[7] + "," + country[10]);
					log.newLine();
				}

			}

			br.close();
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
