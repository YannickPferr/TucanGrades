package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import util.DataConnector;
import util.Mail;

public class TucanGrades {

	private final String GRADES_FILE = "grades.save";
	private final String CONFIG_FILE = "config.conf";
	private final String ERR_LOG = "err.log";
	private final String OUT_LOG = "out.log";
	
	private String jarDir;
	
	private Config config;
	private Mail mail;
	private DataConnector connector;

	public TucanGrades() {
		
		try {
			jarDir = new File(DataConnector.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParentFile().getAbsolutePath() + "/";
			
			try {
				PrintStream out = new PrintStream(jarDir + OUT_LOG);
				PrintStream err = new PrintStream(jarDir + ERR_LOG);
		        System.setOut(out);
		        System.setErr(err);
			} catch (FileNotFoundException e) {
				System.err.println("Logfile couldn't be written!");
				System.err.println(e);
			}
		 
			
			connector = new DataConnector(jarDir);
			config = connector.readConfig(CONFIG_FILE);
			if (config.getEmailActive()) {
				System.out.println("Starte mit Email Benachrichtigung...");
				mail = new Mail(config.getFromEmail(), config.getToEmail());
			}
			else
				System.out.println("Starte ohne Email Benachrichtigung...");
		} catch (URISyntaxException e) {
			System.err.println(e);
		}
	}

	public static void main(String[] args) {
		// Turn warnings off
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		TucanGrades tucan = new TucanGrades();
		
		while (true) {
			try {
				System.out.println(new Date() + ": Prüfe Noten...");
				tucan.checkGrades();
			} catch (Exception e) {
				System.err.println(e);
				continue;
			}
			try {
				System.out.println("Warte " + tucan.getCheckInterval() + " Minuten bis zur nächsten Ausführung...");
				Thread.sleep(tucan.getCheckInterval() * 60 * 1000);
			} catch (InterruptedException e) {
				System.err.println(e);
				continue;
			}
		}
	}

	// Login to tucan
	private HtmlPage tucanLogin(WebClient webClient, String user, String pass) throws Exception {
		HtmlPage currentPage = webClient.getPage("http://www.tucan.tu-darmstadt.de/");

		HtmlInput username = currentPage.getElementByName("usrname");
		username.setValueAttribute(user);

		HtmlInput password = currentPage.getElementByName("pass");
		password.setValueAttribute(pass);

		HtmlSubmitInput submitBtn = (HtmlSubmitInput) currentPage.getElementById("logIn_btn");
		currentPage = submitBtn.click();

		return currentPage;
	}

	// Extract grades from "Modulergebnisse"
	private void checkGrades() throws Exception {
		WebClient webClient = new WebClient();
		HtmlPage startPage = tucanLogin(webClient, config.getUsername(), config.getPassword());

		HtmlAnchor htmlAnchor = startPage.getAnchorByText("Modulergebnisse");
		HtmlPage modulergebnisse = htmlAnchor.click();

		LinkedList<ExamResult> results = new LinkedList<>();

		HtmlTableBody gradesTable = ((HtmlTable) modulergebnisse.getByXPath("//table[@class='nb list']").get(0))
				.getBodies().get(0);
		int rows = gradesTable.getRows().size();
		for (final HtmlTableRow row : gradesTable.getRows()) {
			ExamResult result = new ExamResult();
			for (final HtmlTableCell cell : row.getCells()) {
				switch (cell.getIndex()) {
				case 1:
					result.setName(cell.asText().trim());
					break;
				case 2:
					result.setGrade(cell.asText().trim());
					break;
				case 3:
					result.setCredits(cell.asText().trim());
					break;
				case 4:
					result.setStatus(cell.asText().trim());
					break;
				default:
					break;
				}
			}
			if (row.getIndex() != rows - 1)
				results.add(result);
		}

		LinkedList<ExamResult> newResults = newGrades(results);
		if (newResults.size() > 0) {
			if (config.getEmailActive())
				sendGrades(newResults);
			System.out.println("Neue Noten!");
			printGrades(newResults);

		} else
			System.out.println("Keine neuen Noten!");

		System.out.println("Alle Noten:");
		printGrades(results);

		connector.saveGradeStatus(GRADES_FILE, results);
	}

	private LinkedList<ExamResult> newGrades(LinkedList<ExamResult> results) {

		LinkedList<ExamResult> newResults = new LinkedList<>();

		LinkedList<ExamResult> oldResults = connector.readLastGradeStatus(GRADES_FILE);
		for (int i = 0; i < results.size(); i++) {
			ExamResult e = results.get(i);
			if (i < oldResults.size() && !e.equals(oldResults.get(i)))
				newResults.add(e);
		}

		return newResults;
	}

	public int getCheckInterval() {
		return config.getCheckInterval();
	}

	// Table formatted output
	private void sendGrades(LinkedList<ExamResult> results) {
		StringBuilder sb = new StringBuilder();

		for (ExamResult result : results) {
			sb.append(result.getName() + " " + result.getStatus() + " mit Note: " + result.getGrade());
			sb.append(System.lineSeparator());
		}
		mail.sendEmail(sb.toString());
	}

	// Table formatted output
	private void printGrades(LinkedList<ExamResult> results) {
		String leftAlignFormatHeader = "  %-40s   %-18s   %-7s   %-18s %n";
		String leftAlignFormat = "| %-40s | %-18s | %-7s | %-18s |%n";

		System.out.format(leftAlignFormatHeader, "Kurs", "Note", "Credits", "Status");
		System.out.format(
				"+------------------------------------------+--------------------+---------+--------------------+\n");
		for (int i = 0; i < results.size(); i++) {
			System.out.format(leftAlignFormat, results.get(i).getName(), results.get(i).getGrade(),
					results.get(i).getCredits(), results.get(i).getStatus());
		}
		System.out.format(
				"+------------------------------------------+--------------------+---------+--------------------+\n");
	}
}
