package io.avec.ced.data;

public enum Role {
    MANAGER("manager"),
    ADMIN("admin");

    private final String roleName;

    private Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
