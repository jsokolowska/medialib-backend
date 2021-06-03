package pik.repository.openstack;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import pik.repository.util.MediaFile;
import pik.repository.util.MetadataChange;

import java.io.File;
import java.util.*;

public class SwiftMediaFileDAO implements MediaFileDAO {
    private final String DISPLAY_NAME = "display-name";
    private final Account account;

    public SwiftMediaFileDAO(boolean mock){
        ResourceBundle bundle = ResourceBundle.getBundle("swift");
        AccountConfig config = new AccountConfig();
        config.setUsername(bundle.getString("user"));
        config.setPassword(bundle.getString("pass"));
        config.setAuthUrl(bundle.getString("authURL"));
        config.setAuthenticationMethod(AuthenticationMethod.BASIC);
        account = new AccountFactory(config).setMock(mock).createAccount();
    }
    public SwiftMediaFileDAO(){
        this(false);
    }

    public List<MediaFile> sth(){
        var result = new ArrayList<MediaFile>();
        Collection<Container> containers = account.list();
        for (Container currentContainer : containers) {
            Collection<StoredObject> lst = currentContainer.list();
            for(StoredObject obj : lst){
                result.add(storedObjectToMediaFile(obj, "-"));
            }
        }
        return result;
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

    /** Changes display name property.
     * @return  true if file exists, false if it doesnt*/
    @Override
    public boolean updateMediaFile(String username, String fileId, MetadataChange changes){
        Container container = account.getContainer(username);
        if(container.exists()){
            StoredObject object = container.getObject(fileId);
            if(object.exists()){
                object.setAndSaveMetadata(DISPLAY_NAME, changes.getDisplayName());
                return true;
            }
        }
        return false;
    }

    @Override
    /** Deletes entry form database if such entry exists
     * @return true if file was deleted, false otherwise*/
    public boolean deleteMediaFile(String userId, String fileId){
        Container container = account.getContainer(userId);
        if(container.exists()){
            StoredObject object = container.getObject(fileId);
            if (object.exists()){
                object.delete();
                return true;
            }
        }
        return false;
    }

    /** Creates new entry in Swift database */
    @Override
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

    @Override
    public List<MediaFile> getAllByUserAndType (String userId, String type){
        List<MediaFile> mediaFiles = new ArrayList<>();

        Container container = account.getContainer(userId);
        if(!container.exists()) return null;

        if(type.equals("image") || type.equals("video")){
            for(StoredObject object : container.list()){
                if(object.getContentType().startsWith(type)){
                    MediaFile media = storedObjectToMediaFile(object, userId);
                    mediaFiles.add(media);
                }
            }
            return mediaFiles;

        }else if(type.equals("any")){
            for(StoredObject object : container.list()){
                MediaFile media = storedObjectToMediaFile(object, userId);
                mediaFiles.add(media);
            }
            return mediaFiles;
        }
        return null;
    }

    @Override
    public List<MediaFile> getAllByUser (String userId){
        return getAllByUserAndType(userId, "any");
    }

    private MediaFile storedObjectToMediaFile (StoredObject obj, String userId){
        Object o = obj.getMetadata(DISPLAY_NAME);
        String displayName = (String) o;
        String type = obj.getContentType();
        long size = obj.getContentLength();
        String url = obj.getPublicURL();
        String fileId = obj.getName();

        return new MediaFile(userId, fileId, type, displayName, url, size);
    }

}
