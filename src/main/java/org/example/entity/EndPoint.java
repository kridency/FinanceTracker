package org.example.entity;

public enum EndPoint {
    CREATE("/create"),
    UPDATE("/update"),
    DELETE("/delete"),
    LIST("/list");

    private final String text;

    EndPoint(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static EndPoint fromString(String text) {
        for (EndPoint e : EndPoint.values()) {
            if (e.text.equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
