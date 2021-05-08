package pik.repository.openstack;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SwiftMediaFileDAO implements MediaFileDAO {
    private final String DISPLAY_NAME = "display-name";
    private final Account account;

    public SwiftMediaFileDAO(){
        ResourceBundle bundle = ResourceBundle.getBundle("swift");
        AccountConfig config = new AccountConfig();
        config.setUsername(bundle.getString("user"));
        config.setPassword(bundle.getString("pass"));
        config.setAuthUrl(bundle.getString("authURL"));
        config.setAuthenticationMethod(AuthenticationMethod.BASIC);
        account = new AccountFactory(config).createAccount();
    }

    /** @return all user files or null if no files exist*/
    @Override
    public List<MediaFile> getAllUserFiles(String userId) {
        Container container = account.getContainer(userId);
        if(!container.exists()) return null;

        List<MediaFile> files = new ArrayList<>();
        for(StoredObject obj : container.list()){
            files.add( storedObjectToMediaFile(obj, userId));
        }
        return files;
    }

    /** This method does not create entry in the database.
     * To create entry use uploadMediaFile function.
     * @return file or null if does not exist */
    @Override
    public MediaFile getMediaFile(String userId, String fileId) {
        Container container = account.getContainer(userId);
        if(!container.exists()) return null;

        StoredObject object = container.getObject(fileId);
        if (!object.exists()) return null;
        return storedObjectToMediaFile(object, userId);
    }

    /** @return first file with specified displayName or null if such file does not exist*/
    @Override
    public MediaFile getMediaFileByDisplayName(String userId, String displayName) {
        Container container = account.getContainer(userId);
        if(!container.exists()) return null;

        for (StoredObject obj: container.list()){
            String objName = (String)obj.getMetadata(DISPLAY_NAME);
            if (objName.equals(displayName)){
                return storedObjectToMediaFile(obj, userId);
            }
        }
        return null;
    }

    /** Changes display name property. If specified file does not exits this function does nothing*/
    @Override
    public void updateMediaFile(MediaFile file) {
        Container container = account.getContainer(file.getUserId());
        if(container.exists()){
            StoredObject object = container.getObject(file.getFileId());
            object.setAndSaveMetadata(DISPLAY_NAME, file.getDisplayName());
        }
    }

    /** Deletes entry form database if such entry exists */
    @Override
    public void deleteMediaFile( MediaFile file) {
        Container container = account.getContainer(file.getUserId());
        if(container.exists()){
            StoredObject object = container.getObject(file.getFileId());
            if (object.exists()){
                object.delete();
            }
        }
    }

    /** Creates new entry in Swift database */
    public void uploadMediaFile(MediaFile file, File resource){
        Container container = account.getContainer(file.getUserId());
        if(!container.exists()){
            container.create();
            container.makePublic();
        }
        StoredObject object = container.getObject(file.getFileId());
        object.uploadObject(resource);
        object.setAndSaveMetadata(DISPLAY_NAME, file.getDisplayName());
    }

    private MediaFile storedObjectToMediaFile (StoredObject obj, String userId){
        String displayName = (String) obj.getMetadata(DISPLAY_NAME);
        String type = obj.getContentType();
        long size = obj.getContentLength();
        String url = obj.getPublicURL();
        String fileId = obj.getName();

        return new MediaFile(userId, fileId, type, displayName, url, size);
    }

}
