package com.agrizar.portal.enums;

public enum ETipoUsuarioNotificar {
	TECNICO    (1),
	USUARIO		(2);
	
	private int id;
	
	ETipoUsuarioNotificar(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
