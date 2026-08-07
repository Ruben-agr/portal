package com.agrizar.portal.enums;

public enum ETipoProceso {
	CONF_DB (10),
	DIAS_ATRAS (11),
	RANGO_FECHAS (12),
	ORDEN_COMPRA (13),
	NO_MOVIMIENTO (14),
	PUBLICAR_REM (20);
	
	private int id;
	
	ETipoProceso(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static ETipoProceso getTipoProceso(int id) {
		for (ETipoProceso tipo : ETipoProceso.values()) {
			if (tipo.getId() == id) {
				return tipo;
			}
		}
		return null;
	}
}
