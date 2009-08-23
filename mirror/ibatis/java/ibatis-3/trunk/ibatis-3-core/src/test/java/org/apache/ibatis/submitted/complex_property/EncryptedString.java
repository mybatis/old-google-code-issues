package org.apache.ibatis.submitted.complex_property;

public class EncryptedString {
	private String encrypted;
	
	public EncryptedString(){
		setEncrypted( null );
	}

	public EncryptedString(String message){
		this();
		
		//encrypt the message.
		setEncrypted( message );//TODO:encrypt
	}

	public String decrypt() { return encrypted; }//TODO:decrypt

	public String getEncrypted() { return encrypted; }
	public void setEncrypted(String arg) { this.encrypted = arg; }

}
