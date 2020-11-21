package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Properties;
import java.util.regex.Pattern;

import main.Config;
import main.ExamResult;

public class DataConnector {

	String jarDir;

	public DataConnector(String jarDir) {
		this.jarDir = jarDir;
	}

	public Config readConfig(String configFile) {

		Config config = null;
		Properties properties = new Properties();
		FileInputStream stream;
		try {
			stream = new FileInputStream(jarDir + configFile);
			properties.load(stream);
			stream.close();

			if (properties.getProperty("emailActive", "false").equals("true")) {
				config = new Config(properties.getProperty("username"), properties.getProperty("password"),
						Integer.parseInt(properties.getProperty("checkInterval")), properties.getProperty("fromEmail"),
						properties.getProperty("toEmail"));
			} else {
				config = new Config(properties.getProperty("username"), properties.getProperty("password"),
						Integer.parseInt(properties.getProperty("checkInterval")));
			}
		} catch (NumberFormatException e) {
			System.err.println("Check Intervall muss eine Natürliche Zahl sein!");
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.err.println("Bitte erstelle eine Datei \"config.conf\" im gleichen Ordner wie die Jar Datei!");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return config;
	}

	public void saveGradeStatus(String gradeFile, LinkedList<ExamResult> results) {
		File grades = new File(jarDir + gradeFile);
		PrintWriter pw;
		try {
			pw = new PrintWriter(grades);
			for (ExamResult e : results)
				pw.println(e.toString());
			pw.close();
		} catch (FileNotFoundException e1) {
			System.err.println(e1);
		}
	}

	public LinkedList<ExamResult> readLastGradeStatus(String gradeFile) {
		LinkedList<ExamResult> results = new LinkedList<>();
		FileInputStream stream;
		try {
			stream = new FileInputStream(jarDir + gradeFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String s = null;
			while ((s = br.readLine()) != null) {
				if (s.startsWith("#"))
					continue;

				String[] arr = s.split(Pattern.quote("|"));
				results.add(new ExamResult(arr[0], arr[1], arr[2], arr[3]));
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}

		return results;
	}
}
