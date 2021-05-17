package pik.repository.openstack;

import java.io.File;
import java.util.List;

public interface MediaFileDAO {
    List<MediaFile> getAllUserFiles(String userId);

    MediaFile getMediaFile(String userId, String fileId);

    MediaFile getMediaFileByDisplayName(String userId, String displayName);

    void updateMediaFile(MediaFile file);

    void deleteMediaFile(MediaFile file);

    List<MediaFile> getAllUserImages(String userId);

    void uploadMediaFile(MediaFile file, File resource);
}
