package pik.repository.openstack;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pik.repository.util.MediaFile;
import pik.repository.util.MetadataChange;
import pik.repository.util.UploadEndpoint;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
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
    public boolean exists (String username, String fileId){
        Container container = account.getContainer(username);
        if(container.exists()){
            StoredObject object = container.getObject(fileId);
            if(object.exists()){
                return true;
            }
        }
        return false;
    }

    /** Deletes entry form database if such entry exists
     * @return true if file was deleted, false otherwise*/
    @Override
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
    public void uploadMediaFile(String user_id, String file_id){
        Container container = account.getContainer(user_id);
        if(!container.exists()){
            container.create();
            container.makePublic();
            set_cors(user_id);
        }
//        StoredObject object = container.getObject(file.getFileId());
//        object.uploadObject(resource);
//        object.setAndSaveMetadata(DISPLAY_NAME, file.getDisplayName());
    }
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

    private void set_cors(String user_id){
        ResourceBundle bundle = ResourceBundle.getBundle("swift");
        try {
            URL url = new URL(bundle.getString("authURL"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Storage-User", bundle.getString("user"));
            con.setRequestProperty("X-Storage-Pass", bundle.getString("pass"));
            int status = con.getResponseCode();
            if (status != 200) {
                return;
            }
            String token = con.getHeaderField("X-AUTH-TOKEN");
            String container_url = con.getHeaderField("X-Storage-Url") + "/" + user_id;
            con.disconnect();

            URL cUrl = new URL(container_url);
            HttpURLConnection connection = (HttpURLConnection) cUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-Auth-Token", token);
            connection.setRequestProperty("Origin", "http://localhost:3000");
            connection.setRequestProperty("X-Container-Meta-Access-Control-Allow-Origin", "http://localhost:3000");
            int st = connection.getResponseCode();
            connection.disconnect();

        } catch (IOException ignored) {}

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

    public List<MediaFile> getAllContaining (String userId, String name){
        List<MediaFile> mediaFiles = new ArrayList<>();

        Container container = account.getContainer(userId);
        if(!container.exists()) return null;

        for(StoredObject object : container.list()){
            String displayName = (String) object.getMetadata(DISPLAY_NAME);
            if(displayName != null && displayName.contains(name)){
                MediaFile media = storedObjectToMediaFile(object, userId);
                mediaFiles.add(media);
            }
        }
        return mediaFiles;
    }

}
