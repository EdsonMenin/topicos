package com.example.topicos.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.topicos.model.RegexServer;

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

		return mountReport( responseIpv4, responseIpv6, rootServer);
	}
	
	private String mountReport(JSONArray responseIpv4, JSONArray responseIpv6, String rootServer) {
		
		JSONObject ret =				new JSONObject();
		JSONObject infos = 				new JSONObject();
		
		List<Long> probIPV4 =			new ArrayList<Long>();
		List<Long> probIPV6 =			new ArrayList<Long>();
		List<String> servers = 			new ArrayList<String>();
		List<String> distinctServers = 	new ArrayList<String>();
		
		Long quantResultsIPV4 = 0l;
		Long quantResultsIPV6 = 0l;
		Long failResultsIPV4 = 0l;
		Long failResultsIPV6 = 0l;

	    for (int i = 0 ; i < responseIpv4.length(); i++) {
	    	
	        JSONObject obj = responseIpv4.getJSONObject(i);
	        
	        //ignora prob duplicada
	        Long prb_id = obj.getLong("prb_id");
	        
	        if( probIPV4.contains(prb_id) ) continue;
	        
	        probIPV4.add(prb_id);
	        
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
	        
	        serverName = adjustmentServerName( rootServer, serverName );
	        
	        if ( serverName == null ){
	        	failResultsIPV4++;
	        	continue;
	        }

	        servers.add(serverName);
	        
	        quantResultsIPV4++;
	        
	    }
	    
	    for (int i = 0 ; i < responseIpv6.length(); i++) {
	    	
	    	JSONObject obj = responseIpv4.getJSONObject(i);
	        JSONObject result = obj.has("result") ? obj.getJSONObject("result") : new JSONObject();
	        
	        //ignora prob duplicada
	        Long prb_id = obj.getLong("prb_id");
	        
	        if( probIPV6.contains(prb_id) ) continue;
	        
	        probIPV6.add(prb_id);
	        
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
	        
	        serverName = adjustmentServerName( rootServer, serverName );
	        
	        if ( serverName == null ){
	        	failResultsIPV6++;
	        	continue;
	        }
	        
	        servers.add(serverName);
	        
	        quantResultsIPV6++;
	    }
	    
	    distinctServers = servers.stream()
				                 .distinct()
				                 .collect(Collectors.toList());
	    
	    infos.put("quantResultsIPV4", 	quantResultsIPV4);
	    infos.put("quantResultsIPV6", 	quantResultsIPV6);
	    infos.put("quantResultsTotal", 	quantResultsIPV4 + quantResultsIPV6);
	    infos.put("failResultsIPV4", 	failResultsIPV4);
	    infos.put("failResultsIPV6", 	failResultsIPV6);
	    infos.put("quantFailsTotal", 	failResultsIPV4 + failResultsIPV6);
	    
	    ret.put("infos", 			infos);
	    ret.put("distinctServers", 	mountDistinctServers(servers, distinctServers));
		
		return ret.toString();
	}

	private String adjustmentServerName(String rootServer, String serverName) {
		
		String svName = null;
		
		switch (rootServer) {
			case "a":
			    if( matcherRegex( serverName, RegexServer.A.getRegexServer() ) ) 
			    	svName = serverName.substring(5, 8);
				break;
			case "b":
				if( matcherRegex( serverName, RegexServer.B.getRegexServer() ) ) 
			    	svName = serverName.substring(3, 6);
				break;
			case "c":
				if( matcherRegex( serverName, RegexServer.C.getRegexServer() ) ) 
			    	svName = serverName.substring(0, 3);
				break;
			case "d":
				if( matcherRegex( serverName, RegexServer.D.getRegexServer() ) ) 
			    	svName = serverName.substring(0, 4);
				break;
			case "e":
				if( matcherRegex( serverName, RegexServer.E.getRegexServer() ) ) 
			    	svName = serverName.substring(4, 7);
				break;
			case "f":
				if( matcherRegex( serverName, RegexServer.F.getRegexServer() ) ) 
			    	svName = serverName.substring(0, 3);
				break;
			case "g":
				if( matcherRegex( serverName, RegexServer.G.getRegexServer() ) ) 
			    	svName = serverName.substring(6, 9);
				break;
			case "h":
				if( matcherRegex( serverName, RegexServer.H.getRegexServer() ) ) 
			    	svName = serverName.substring(4, 7);
				break;
			case "i":
				if( matcherRegex( serverName, RegexServer.I.getRegexServer() ) ) 
			    	svName = serverName.substring(3, 6);
				break;
			case "j":
				if( matcherRegex( serverName, RegexServer.J.getRegexServer() ) ) 
			    	svName = serverName.substring(5, 8);
				break;
			case "k":
				if( matcherRegex( serverName, RegexServer.K.getRegexServer() ) ) 
			    	svName = serverName.substring(7, 10);
				break;
			case "l":
				if( matcherRegex( serverName, RegexServer.L.getRegexServer() ) ) 
			    	svName = serverName.substring(3, 6);
				break;
			case "m":
				if( matcherRegex( serverName, RegexServer.M.getRegexServer() ) ) 
			    	svName = serverName.substring(2, 5);
				break;
		}
		
		return svName;
	}
	
	private boolean matcherRegex(String serverName, String regexMatch ) {
		
		Pattern pattern = Pattern.compile( regexMatch, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher( serverName );
	    return matcher.find();
	}

	private JSONArray mountDistinctServers( List<String> servers, List<String> distinctServers ) {
		
		JSONArray arr = new JSONArray();
		
		for( String server : distinctServers ) {
			
			JSONObject obj = new JSONObject();
			Long count = 0l;
			
			for( String sv : servers ) if( server.equals(sv) ) count++;
			
			obj.put("server", server);
			obj.put("count", count);
			
			arr.put(obj);
		}
		
		return arr;
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
