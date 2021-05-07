package pik.repository.openstack;

import java.util.List;

public interface MediaFileDAO {
    List<MediaFile> getAllUserFiles(String userId);
    MediaFile getMediaFile(String userId, String filename);
    MediaFile getMediaFileByDisplayName (String userId, String displayName);
    void updateMediaFile(String userId, MediaFile file);
    void deleteMediaFile(String userId,MediaFile file);
}
