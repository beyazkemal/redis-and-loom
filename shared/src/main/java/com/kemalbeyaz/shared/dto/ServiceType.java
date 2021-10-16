package com.kemalbeyaz.shared.dto;

public enum ServiceType {
    REDIS("redis"),
    MYSQL("mysql"),
    DUMMY("dummy");

    private final String name;

    ServiceType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ServiceType fromName(final String name) {
        if (name.equals(REDIS.getName())) {
            return REDIS;
        } else if (name.equals(MYSQL.getName())) {
            return MYSQL;
        }

        return DUMMY;
    }
}
