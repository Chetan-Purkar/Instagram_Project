package com.instagramclone.dto;


public class AuthRequest {
 
	private String username;
    private String email;
    private String password;

    public AuthRequest(String username, String email) {
    	
        this.username = username;
        this.email = email;
    }
    
   

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

	public String getPassword() {
		return password;
	}
    
}
