package gmedia.net.id.kartikaelektrik.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import gmedia.net.id.kartikaelektrik.LoginScreen;

import java.util.HashMap;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "GmediaUser";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String TAG_UID = "uid";
	public static final String TAG_NIK = "nik";
	public static final String TAG_NIK_ASLI = "nik_asli";
	public static final String TAG_NAMA = "nama";
	public static final String TAG_PASSWORD = "password";
	public static final String TAG_TOKEN = "token";
	public static final String TAG_EXP = "expired_at";
	public static final String TAG_LEVEL = "level";
	public static final String TAG_LABA = "laba";
	public static final String TAG_NAMA_FULL = "nama_lengkap";
	public static final String TAG_NAMA_ASLI = "nama_asli";
	public static final String TAG_ID_JABATAN = "id_jabatan";
	public static final String TAG_JABATAN = "jabatan";
	public static final String TAG_LEVEL_JABATAN = "level_jabatan";
	public static final String TAG_USERNAME = "username";

	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String uid,String nik, String nama, String password, String token, String exp, String level, String laba, String fullName){

		editor.putBoolean(IS_LOGIN, true);
		
		editor.putString(TAG_UID, uid);
		
		editor.putString(TAG_NIK, nik);

		editor.putString(TAG_NAMA, nama);

		editor.putString(TAG_PASSWORD, password);

		editor.putString(TAG_TOKEN, token);

		editor.putString(TAG_EXP, exp);

		editor.putString(TAG_LEVEL, level);

		editor.putString(TAG_LABA, laba);

		editor.putString(TAG_NAMA_FULL, fullName);
		// commit changes
		editor.commit();
	}	
	
	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkLogin(){
		// Check login status
		//if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
		//	Intent i = new Intent(_context, Login.class);
			// Closing all the Activities
		//	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
		//	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
		//	_context.startActivity(i);
		//}
		
	}
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user uid
		user.put(TAG_UID, pref.getString(TAG_UID, ""));
		
		// user nik
		user.put(TAG_NIK, pref.getString(TAG_NIK, ""));

		user.put(TAG_NAMA, pref.getString(TAG_NAMA, ""));

		user.put(TAG_PASSWORD, pref.getString(TAG_PASSWORD, ""));

		user.put(TAG_TOKEN, pref.getString(TAG_TOKEN, ""));

		user.put(TAG_EXP, pref.getString(TAG_EXP, ""));

		user.put(TAG_LEVEL, pref.getString(TAG_LEVEL, ""));

		user.put(TAG_LABA, pref.getString(TAG_LABA, ""));

		user.put(TAG_NIK_ASLI, pref.getString(TAG_NIK_ASLI, ""));
		// return user
		return user;
	}

	public void saveNikAsli(String nikAsli){

		editor.putString(TAG_NIK_ASLI, nikAsli);

		editor.commit();
	}

	public void saveNamaAsli(String namaAsli){

		editor.putString(TAG_NAMA_ASLI, namaAsli);

		editor.commit();
	}

	public void saveJabatan(String jabatan){

		editor.putString(TAG_JABATAN, jabatan);

		editor.commit();
	}

	public void saveLevelJabatan(String levelJabatan){

		editor.putString(TAG_LEVEL_JABATAN, levelJabatan);

		editor.commit();
	}

	public void saveIdJabatan(String idJabatan){

		editor.putString(TAG_ID_JABATAN, idJabatan);

		editor.commit();
	}

	public void saveUsername(String username){

		editor.putString(TAG_USERNAME, username);

		editor.commit();
	}

	public String getUser(){
		return pref.getString(TAG_NAMA, "");
	}

	public String getNik(){
		return pref.getString(TAG_NIK, "");
	}

	public String getLevel(){
		return pref.getString(TAG_LEVEL, "");
	}

	public String getLaba(){
		return pref.getString(TAG_LABA, "");
	}

	public String getFullName(){
		return pref.getString(TAG_NAMA_FULL, "");
	}

	public String getNikAsli(){

		return pref.getString(TAG_NIK_ASLI, "");
	}

	public String getNamaAsli(){

		return pref.getString(TAG_NAMA_ASLI, "");
	}

	public String getIdJabatan(){

		return pref.getString(TAG_ID_JABATAN, "");
	}

	public String getJabatan(){

		return pref.getString(TAG_JABATAN, "");
	}

	public String getLevelJabatan(){

		return pref.getString(TAG_LEVEL_JABATAN, "");
	}

	public String getUsername(){

		return pref.getString(TAG_USERNAME, "");
	}

	public String getToken(){

		return pref.getString(TAG_TOKEN, "");
	}

	public String getUid(){

		return pref.getString(TAG_UID, "");
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(Activity activity){
		// Clearing all data from Shared Preferences

		try {
			editor.clear();
			editor.commit();
		}catch (Exception e){
			e.printStackTrace();
		}

		Intent intent = new Intent(activity, LoginScreen.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		activity.finish();
	}

	public void logout(){
		// Clearing all data from Shared Preferences

		try {
			editor.clear();
			editor.commit();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		if(!getUserDetails().get(TAG_NIK).isEmpty()){
			return true;
		}else{
			return false;
		}
		/*return pref.getBoolean(IS_LOGIN, false);*/
	}
}
