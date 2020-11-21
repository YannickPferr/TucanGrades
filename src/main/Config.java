package main;

public class Config {

	private String username;
	private String password;
	private String fromEmail;
	private String toEmail;
	private boolean emailActive;
	private int checkInterval;
	
	public Config(String username, String password, int checkInterval) {
		this.username = username;
		this.password = password;
		this.checkInterval = checkInterval;
		emailActive = false;
	}
	
	public Config(String username, String password, int checkInterval, String fromEmail, String toEmail) {
		this.username = username;
		this.password = password;
		this.checkInterval = checkInterval;
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		emailActive = true;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getCheckInterval() {
		return checkInterval;
	}
	
	public String getFromEmail() {
		return fromEmail;
	}
	
	public String getToEmail() {
		return toEmail;
	}
	
	public boolean getEmailActive() {
		return emailActive;
	}
}
