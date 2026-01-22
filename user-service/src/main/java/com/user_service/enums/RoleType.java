package com.user_service.enums;

public enum RoleType {
	MANAGER,
	ADMIN,
	DEVELOPER,
	VIEWER;
	
	public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
