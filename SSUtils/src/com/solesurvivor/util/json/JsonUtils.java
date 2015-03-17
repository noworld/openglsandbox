package com.solesurvivor.util.json;

import java.io.IOException;

import android.util.JsonReader;
import android.util.JsonToken;

import com.solesurvivor.util.logging.SSLog;

public class JsonUtils {
	
	private static final String TAG = JsonUtils.class.getSimpleName();
	
	private static boolean debug = false;
	
	public static void setDebug(boolean debugOn) {
		debug = debugOn;
	}

	public static boolean beginJson(JsonReader reader) throws IOException {
		JsonToken token = reader.peek();
		boolean success = false;
		
		if(debug){SSLog.d(TAG, "Begin JSON: %s", token.toString());}
		
		switch(token) {
		case BEGIN_ARRAY: reader.beginArray(); 			
			success = true;
			break;
		case BEGIN_OBJECT: reader.beginObject(); 
			success = true;
			break;
		default: success = false;
			break;
		}
		
		return success;
	}
	
	public static boolean endJson(JsonReader reader) throws IOException {
		JsonToken token = reader.peek();
		boolean success = false;
		
		if(debug){SSLog.d(TAG, "End JSON: %s", token.toString());}
		
		switch(token) {
		case END_ARRAY: reader.endArray();
			success = true;
			break;
		case END_OBJECT: reader.endObject(); 
			success = true;
			break;
		default: success = false;
			break;
		}
		
		return success;
		
	}
	
	public static boolean endJsonDocument(JsonReader reader) throws IOException {
		if(debug){SSLog.d(TAG, "End JSON Document: %s", reader.peek().toString());}
		return reader.peek().equals(JsonToken.END_DOCUMENT);
	}

	
}
