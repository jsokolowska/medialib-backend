package pik.repository.openstack;

import pik.repository.MetadataChange;

import java.io.File;
import java.util.List;

public interface MediaFileDAO {
    MediaFile getMediaFile(String userId, String fileId);
    MediaFile getMediaFileByDisplayName(String userId, String displayName);

    List<MediaFile> getAllByUserAndType (String userId, String type);
    List<MediaFile> getAllByUser (String userId);

    void uploadMediaFile(MediaFile file, File resource);
    boolean updateMediaFile(String username, String fileId, MetadataChange changes);
    boolean deleteMediaFile(String username, String fileId);

}
