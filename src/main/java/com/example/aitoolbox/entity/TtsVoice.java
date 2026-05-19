package com.example.aitoolbox.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TtsVoice implements Serializable {

    private static final long serialVersionUID = 1L;

    private String voiceId;
    private String name;
    private String language;
    private String gender;
    private String description;

    public TtsVoice(String voiceId, String name, String language, String gender, String description) {
        this.voiceId = voiceId;
        this.name = name;
        this.language = language;
        this.gender = gender;
        this.description = description;
    }
}
