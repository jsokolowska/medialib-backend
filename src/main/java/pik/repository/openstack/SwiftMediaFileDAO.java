package pik.repository.openstack;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;

import java.util.ArrayList;
import java.util.List;

public class SwiftMediaFileDAO implements MediaFileDAO {
    private final String DISPLAY_NAME = "display-name";
    private final String DBusername = "test:tester";
    private final String DBPassword = "";
    private final String authURL = "http://127.0.0.1:8083/auth/v1.0";
    private final Account account;

    public SwiftMediaFileDAO(){
        AccountConfig config = new AccountConfig();
        config.setUsername(DBusername);
        config.setPassword(DBPassword);
        config.setAuthUrl(authURL);
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
            files.add(new MediaFile(obj, userId));
        }
        return files;
    }

    /** @return file or null if does not exist */
    @Override
    public MediaFile getMediaFile(String userId, String fileId) {
        Container container = account.getContainer(userId);
        if(!container.exists()) return null;

        StoredObject object = container.getObject(fileId);
        if (object.exists()) return null;
        return new MediaFile(object, userId);
    }

    @Override
    public MediaFile getMediaFileByDisplayName(String userId, String displayName) {
        Container container = account.getContainer(userId);
        if(!container.exists()) return null;

        for (StoredObject obj: container.list()){
            if (obj.getMetadata(DISPLAY_NAME).equals(displayName)){
                return new MediaFile(obj, userId);
            }
        }
        return null;
    }

    /** Currently allows only for displayName change*/
    @Override
    public void updateMediaFile(String userId, MediaFile file) {
        Container container = account.getContainer(userId);
        if(!container.exists()){
            container.create();
            container.makePublic();
        }
        StoredObject object = container.getObject(file.getFileId());
        if (! file.getFileId().equals(file.getDisplayName())){
            object.setAndSaveMetadata("displayName", file.getDisplayName());
        }
    }

    @Override
    public void deleteMediaFile(String userId, MediaFile file) {
        Container container = account.getContainer(userId);
        if(container.exists()){
            StoredObject object = container.getObject(file.getFileId());
            if (object.exists()){
                object.delete();
            }
        }
    }
}
