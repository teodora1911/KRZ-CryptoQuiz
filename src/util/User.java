package util;

public class User {
	
	private String username;
	private String password;
	private String certificatePath;
	
	public User(String username, String password, String certificatePath) {
		this.setUsername(username);
		this.setPassword(password);
		this.setCertificatePath(certificatePath);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCertificatePath() {
		return certificatePath;
	}

	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}
	
}
