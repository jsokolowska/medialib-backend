package pik.repository.openstack;

import org.javaswift.joss.model.StoredObject;

public class MediaFile {
    private String userId;
    private String type;
    private String fileId;
    private String url;
    private String displayName;
    private final long size;

    public MediaFile(String userId, String  fileId, String type, String displayName, String url, long size) {
        this.userId = userId;
        this.type = type;
        this.fileId = fileId;
        this.url = url;
        this.displayName = displayName;
        this.size = size;
    }

    public MediaFile(String userId, String displayName, String  fileId) {
        this.userId = userId;
        this.fileId = fileId;
        this.displayName = displayName;

        this.type = "";
        this.size = 0;
        this.url = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**@return number of bytes used by this object*/
    public long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    protected void setUrl(String url){
        this.url = url;
    }

    @Override
    public String toString (){
        return "User:" + userId + "\n\tfileID:" + fileId + " displayName:" + displayName + " size:" + size +
                " type:" + type + "\n\turl:" + url;
    }

}
