package com.agrizar.portal.enums;

public enum ETipoLog {
	NO_APLICA (0),
	REMISION_PUBLICADA (1),
	REMISION_NO_PUBLICADA (2);
	
	private int id;
	
	ETipoLog(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static ETipoLog getTipoProceso(int id) {
		for (ETipoLog tipo : ETipoLog.values()) {
			if (tipo.getId() == id) {
				return tipo;
			}
		}
		return null;
	}
}
