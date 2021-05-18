package pik.repository.openstack;

import java.io.File;
import java.util.List;

public interface MediaFileDAO {
    MediaFile getMediaFile(String userId, String fileId);
    MediaFile getMediaFileByDisplayName(String userId, String displayName);

    List<MediaFile> getAllByUserAndType (String userId, String type);
    List<MediaFile> getAllByUser (String userId);

    void uploadMediaFile(MediaFile file, File resource);
    void updateMediaFile(MediaFile file);
    boolean deleteMediaFile(String username, String fileId);

}
