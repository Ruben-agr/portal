package com.agrizar.portal.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DetecnoResponse400 {

	@JsonProperty("Success")
	private Boolean success;
	@JsonProperty("ResponseCode")
	private Integer responseCode;
	@JsonProperty("Message")
	private String message;
	@JsonProperty("Data")
	private String data;

}
