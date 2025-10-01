package com.agrizar.portal.services.impl;

import org.springframework.stereotype.Service;

import com.agrizar.portal.client.DetecnoApiClient;
import com.agrizar.portal.services.TokenService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

	private final DetecnoApiClient detecnoApiClient;

	public String getToken(String rfc, String apiKey) {
		
		try {
			return (String) detecnoApiClient.getToken(rfc, apiKey)
					.getBody().getData().get("acc_tkn");

		} catch (Exception e) {
		}
		
		return "";
	}

}
