package nano.dev.tasksplanner.service;

import nano.dev.tasksplanner.exception.domain.EmptyFileException;
import nano.dev.tasksplanner.exception.domain.NotAnImageFileException;
import nano.dev.tasksplanner.exception.domain.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {

    void saveUserImageProfile(String userId, MultipartFile file)
            throws UserNotFoundException, IOException, NotAnImageFileException, EmptyFileException;

    byte[] downloadImage(String userId) throws UserNotFoundException;

    void updateProfileImage(String userId, MultipartFile profileImage)
            throws UserNotFoundException, IOException, NotAnImageFileException, EmptyFileException;
}
