package Model;

public class Provider {
	
    private String id;
    private String nama;
    private String email;
    private String password;
    private String role;
   
	public Provider(String id, String nama, String email, String password, String role) {
		super();
		this.id = id;
		this.nama = nama;
		this.email = email;
		this.password = password;
		this.role = role;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getNama() {
		return nama;
	}
	public void setNama(String nama) {
		this.nama = nama;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}  
    
	public String toString() {
        return id + "," + nama + "," + email + "," + password + "," + role;
    }
    
}