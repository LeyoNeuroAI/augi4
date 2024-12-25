package tech.intellibio.augi4.country;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CountryDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @CountryCodeUnique
    private String code;

    @NotNull
    @Size(max = 255)
    @CountryNameUnique
    private String name;

    @Size(max = 255)
    private String province;

    @NotNull
    private Boolean status;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(final String province) {
        this.province = province;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(final Boolean status) {
        this.status = status;
    }

}
