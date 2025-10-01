package com.agrizar.portal.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.agrizar.portal.models.request.RecepcionRequest;
import com.agrizar.portal.models.response.DetecnoFacturaResponse;
import com.agrizar.portal.models.response.DetecnoOrdenCompraResponse;
import com.agrizar.portal.models.response.DetecnoRecepcionResponse;
import com.agrizar.portal.models.response.DetecnoResponse;

@FeignClient(value = "detecno-api", url = "${detecno.api.url}")
public interface DetecnoApiClient {

	@PostMapping("/Auth/Token/{rfc}")
	public ResponseEntity<DetecnoResponse> getToken(@PathVariable String rfc, @RequestHeader("x-api-key") String apiKey);
	
	@GetMapping("/OrdenCompra")
	public ResponseEntity<DetecnoOrdenCompraResponse> getOrdenCompra(@RequestParam("NoOC") String oc, @RequestHeader("x-api-key") String apiKey, @RequestHeader("Authorization") String token);
	
	@GetMapping("/RecepcionMercancias")
	public ResponseEntity<DetecnoRecepcionResponse> getRecepciones(@RequestParam("NoOC") String oc, @RequestHeader("x-api-key") String apiKey, @RequestHeader("Authorization") String token);

	@GetMapping("/OrdenCompra/Consolidacion")
	public ResponseEntity<DetecnoFacturaResponse> getFacturas(@RequestParam("NoOC") String oc, @RequestHeader("x-api-key") String apiKey, @RequestHeader("Authorization") String token);

	@PostMapping("/RecepcionMercancias")
	public ResponseEntity<DetecnoResponse> publicarRemision(@RequestBody RecepcionRequest recepcion, @RequestHeader("x-api-key") String apiKey, @RequestHeader("Authorization") String token);
}
