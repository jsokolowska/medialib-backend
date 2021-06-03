package pik.repository.util;

import javax.validation.constraints.NotEmpty;

public class MetadataChange {
    @NotEmpty
    private String displayName;

    private String description;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MetadataChange(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    public MetadataChange(String displayName) {
        this.displayName = displayName;
        this.description = "";
    }
}
