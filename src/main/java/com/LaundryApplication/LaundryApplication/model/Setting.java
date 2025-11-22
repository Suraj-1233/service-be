package com.LaundryApplication.LaundryApplication.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "settings")
public class Setting {

    @Id
    private String id;

    private String key;
    private String value;

    public Setting() {}

    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getId() { return id; }
    public String getKey() { return key; }
    public String getValue() { return value; }

    public void setKey(String key) { this.key = key; }
    public void setValue(String value) { this.value = value; }
}
