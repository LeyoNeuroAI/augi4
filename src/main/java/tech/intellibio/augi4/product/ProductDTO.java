package tech.intellibio.augi4.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class ProductDTO {

    private Long id;

    @Size(max = 255)
    private String description;

    @NotNull
    @Size(max = 255)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
