package pik.repository;

import javax.validation.constraints.NotEmpty;

public class MetadataChange {
    @NotEmpty
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public MetadataChange(String newDisplayName) {
        this.displayName = newDisplayName;
    }
}
