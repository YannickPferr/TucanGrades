package main;

public class ExamResult {

	public String name;
	public String grade;
	public String status;
	public String credits;
	
	public ExamResult() {
		
	}
	
	public ExamResult(String name, String grade, String status, String credits) {
		this.name = name;
		this.grade = grade;
		this.status = status;
		this.credits = credits;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("|");
		sb.append(grade);
		sb.append("|");
		sb.append(status);
		sb.append("|");
		sb.append(credits);
		
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public String getGrade() {
		return grade;
	}

	public String getStatus() {
		return status;
	}

	public String getCredits() {
		return credits;
	}

	public void setName(String name) {
		if(name.length() > 40)
			this.name = name.substring(0, 40);
		else
			this.name = name;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public void setStatus(String status) {
		if(status.trim().equals(""))
			this.status = "noch nicht gesetzt";
		else
			this.status = status;
	}
	
	public void setCredits(String credits) {
		this.credits = credits;
	}
	
	@Override
	public boolean equals(Object e) {
	
		if(!(e instanceof ExamResult))
			return false;
		
		ExamResult result = (ExamResult) e;
		if(name.equals(result.getName()) && grade.equals(result.getGrade())
				&& status.equals(result.getStatus()) && credits.equals(result.getCredits()))
			return true;
		
		return false;
	}
}
