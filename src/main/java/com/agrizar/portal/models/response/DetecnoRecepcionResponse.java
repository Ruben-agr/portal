package com.agrizar.portal.models.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DetecnoRecepcionResponse {

	@JsonProperty("totalCount")
	private Integer totalCount;

	@JsonProperty("data")
	private List<DetecnoRM> data;
}
