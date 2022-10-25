package com.example.topicos.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

@Service
public class RootServersService {
	
	@Value("${topicos.url}")
	private String atlasUrl;
	
	public String calculateInfos( String rootServer, String date ) {
		
		DateTimeFormatter formatter = 
		        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
		LocalDateTime dateFormated = LocalDateTime.parse(date, formatter);
		
		ZoneId zoneId = ZoneId.systemDefault();
		Long dateInitial = dateFormated.atZone(zoneId).toEpochSecond();
		Long dateFinal = dateFormated.atZone(zoneId).toEpochSecond() + 300;
		
		ArrayList<String> list = retrieveRootServer(rootServer);
 
		JSONArray responseIpv4 = retrieveMeasurements( list.get(0), dateInitial, dateFinal );
		JSONArray responseIpv6 = retrieveMeasurements( list.get(1), dateInitial, dateFinal );

		return mountReport( responseIpv4, responseIpv6);
	}
	
	private String mountReport(JSONArray responseIpv4, JSONArray responseIpv6) {
		
		JSONObject ret = new JSONObject();
		
		Long quantResultsIPV4 = 0l;
		Long quantResultsIPV6 = 0l;
		Long failResultsIPV4 = 0l;
		Long failResultsIPV6 = 0l;

	    for (int i = 0 ; i < responseIpv4.length(); i++) {
	    	
	        JSONObject obj = responseIpv4.getJSONObject(i);
	        JSONObject result = obj.has("result") ? obj.getJSONObject("result") : new JSONObject();
	        
	        if( result.isEmpty() ) {
	        	failResultsIPV4++;
	        	continue;
	        }
	        
	        JSONArray answers  = result.has("answers") ? result.getJSONArray("answers") : new JSONArray();
	        
	        if( answers.isEmpty() ) {
	        	failResultsIPV4++;
	        	continue;
	        }
	        
	        JSONObject arrAnsewrs = answers.getJSONObject(0);
	        
	        if( arrAnsewrs.isEmpty() ) {
	        	failResultsIPV4++;
	        	continue;
	        }
	        
	        String serverName  = arrAnsewrs.has("RDATA") ? arrAnsewrs.getString("RDATA") : "";
	        
	        ret.put("serverName", serverName);
	        
	        quantResultsIPV4++;
	        
	    }
	    
	    for (int i = 0 ; i < responseIpv6.length(); i++) {
	    	
	    	JSONObject obj = responseIpv4.getJSONObject(i);
	        JSONObject result = obj.has("result") ? obj.getJSONObject("result") : new JSONObject();
	        
	        if( result.isEmpty() ) {
	        	failResultsIPV6++;
	        	continue;
	        }
	        
	        JSONArray answers  = result.has("answers") ? result.getJSONArray("answers") : new JSONArray();
	        
	        if( answers.isEmpty() ) {
	        	failResultsIPV6++;
	        	continue;
	        }
	        
	        JSONObject arrAnsewrs = answers.getJSONObject(0);
	        
	        if( arrAnsewrs.isEmpty() ) {
	        	failResultsIPV6++;
	        	continue;
	        }
	        
	        String serverName  = arrAnsewrs.has("RDATA") ? arrAnsewrs.getString("RDATA") : "";
	        
	        ret.put("serverName", serverName);
	        
	        quantResultsIPV6++;
	    }
	    
	    ret.put("quantResultsIPV4", quantResultsIPV4);
	    ret.put("quantResultsIPV6", quantResultsIPV6);
	    ret.put("quantResultsTotal", quantResultsIPV4 + quantResultsIPV6);
	    ret.put("failResultsIPV4", failResultsIPV4);
	    ret.put("failResultsIPV6", failResultsIPV6);
	    ret.put("quantFailsTotal", failResultsIPV4 + quantResultsIPV6);
	    
		
		return ret.toString();
	}

	public JSONArray retrieveMeasurements( String rootServer, Long dateInitial, Long dateFinal ) {
		
		HttpResponse<String> response = Unirest.get( atlasUrl + "/api/v2/measurements/{rootServer}"
															  + "/results/?start={dateInitial}"
															  + "&stop={dateFinal}"
															  + "&format=json")
											.routeParam("rootServer", rootServer)
											.routeParam("dateInitial", String.valueOf( dateInitial ))
											.routeParam("dateFinal", String.valueOf( dateFinal ))
											.asString();
		
		return new JSONArray( response.getBody() );
	}
	
	public ArrayList<String> retrieveRootServer(String rootServer){
		
		ArrayList<String> list = new ArrayList<String>();
		
		switch (rootServer) {
			case "a":
				list.add("10309");
				list.add("11309");
				break;
			case "b":
				list.add("10310");
				list.add("11310");
				break;
			case "c":
				list.add("10311");
				list.add("11311");
				break;
			case "d":
				list.add("10312");
				list.add("11312");
				break;
			case "e":
				list.add("10313");
				list.add("11313");
				break;
			case "f":
				list.add("10304");
				list.add("11304");
				break;
			case "g":
				list.add("10314");
				list.add("11314");
				break;
			case "h":
				list.add("10315");
				list.add("11315");
				break;
			case "i":
				list.add("10305");
				list.add("11305");
				break;
			case "j":
				list.add("10316");
				list.add("11316");
				break;
			case "k":
				list.add("10301");
				list.add("11301");
				break;
			case "l":
				list.add("10308");
				list.add("11308");
				break;
			case "m":
				list.add("10306");
				list.add("11306");
				break;
		}
		return list;
	}
}
