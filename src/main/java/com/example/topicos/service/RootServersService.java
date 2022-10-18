package com.example.topicos.service;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

@Service
public class RootServersService {
	
	@Value("${topicos.url}")
	private String atlasUrl;
	
	public String calculateInfos( String rootServer, Date date ) {
		
		Long dateInitial = date.getTime();
		Long dateFinal = date.getTime() + 300;
		
		ArrayList<String> list = retrieveRootServer(rootServer);
 
		JSONArray responseIpv4 = retrieveMeasurements( list.get(0), dateInitial, dateFinal );
		JSONArray responseIpv6 = retrieveMeasurements( list.get(1), dateInitial, dateFinal );

		return mountReport( responseIpv4, responseIpv6);
	}
	
	private String mountReport(JSONArray responseIpv4, JSONArray responseIpv6) {
		// TODO Auto-generated method stub
		return null;
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
				list.add("10310");
				list.add("11310");
				break;
			case "b":
				list.add("10310");
				list.add("11310");
				break;
			case "c":
				list.add("10310");
				list.add("11310");
				break;
			case "d":
				list.add("10310");
				list.add("11310");
				break;
			case "e":
				list.add("10310");
				list.add("11310");
				break;
			case "f":
				list.add("10310");
				list.add("11310");
				break;
			case "g":
				list.add("10310");
				list.add("11310");
				break;
			case "h":
				list.add("10310");
				list.add("11310");
				break;
			case "i":
				list.add("10310");
				list.add("11310");
				break;
			case "j":
				list.add("10310");
				list.add("11310");
				break;
			case "k":
				list.add("10310");
				list.add("11310");
				break;
			case "l":
				list.add("10310");
				list.add("11310");
				break;
			case "m":
				list.add("10310");
				list.add("11310");
				break;
		}
		return list;
	}
}
