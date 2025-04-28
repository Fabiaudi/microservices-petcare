package com.ms.cadastro.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetApiDto {
    private String id;
    private String name;
    private String reference_image_id;
    private String url;
    private Image image;
    private String description;
    private String temperament;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
