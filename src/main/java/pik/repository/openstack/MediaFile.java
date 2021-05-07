package pik.repository.openstack;

import org.javaswift.joss.model.StoredObject;

public class MediaFile {
    private String userId;
    private String type;
    private String fileId;
    private String url;
    private String displayName;
    private long size;

    public MediaFile(String userId, String type, String displayName, String url, String  fileId, long size) {
        this.userId = userId;
        this.type = type;
        this.fileId = fileId;
        this.url = url;
        this.displayName = displayName;
        this.size = size;
    }

    public MediaFile(String userId, String type, String displayName, String url, String  fileId, long size, String dirPath) {
        this.userId = userId;
        this.type = type;
        this.fileId = fileId;
        this.displayName = displayName;
        this.size = size;
        this.url = url;
    }

    protected MediaFile (StoredObject obj, String userId){
        this.userId = userId;
        type = obj.getContentType();

        fileId = obj.getName();

        url = obj.getPublicURL();
        displayName = obj.getName(); //should be either object metadata or namehash?
        size = obj.getContentLength();
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

    /**@return number of bytes used by this object*/
    public long getSize() {
        return size;
    }


    public String getUrl() {
        return url;
    }

}
