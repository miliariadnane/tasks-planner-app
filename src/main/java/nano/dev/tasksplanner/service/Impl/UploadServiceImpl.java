package nano.dev.tasksplanner.service.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.dev.tasksplanner.aws.AWSBucket;
import nano.dev.tasksplanner.aws.AWSFileStore;
import nano.dev.tasksplanner.entity.User;
import nano.dev.tasksplanner.exception.domain.*;
import nano.dev.tasksplanner.repository.UserRepository;
import nano.dev.tasksplanner.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
@AllArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService {

    private AWSFileStore awsFileStore;
    private UserRepository userRepository;

    @Transactional
    public void saveUserImageProfile(String userId, MultipartFile file)
            throws UserNotFoundException, IOException, NotAnImageFileException, EmptyFileException  {

        // check file is not empty
        isFileEmpty(file);

        // The user exists in our DB
        User user = getUserOrThrow(userId);

        // Check File
        if (file != null){

            //Check the ContentType of file is image or not
            log.info("Checking if the contentType is image");
            isImage(file);

            //upload image to AWS S3
            Map<String,String> metadata = new HashMap<>();
            metadata.put("Content-Type",file.getContentType());
            metadata.put("Content-Length", String.valueOf(file.getSize()));
            String path = String.format("%s/user/userId-%d", AWSBucket.USER_IMAGE.getBucketName(),user.getId());
            String filename = String.format("%s-%s",file.getOriginalFilename(), UUID.randomUUID());

            try {
                awsFileStore.upload(path,filename,file.getInputStream(), Optional.of(metadata));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            //Set imageURL
            user.setProfileImage(filename);
            log.info("save profile image of the user successfully");
        }

    }

    @Override
    public void updateProfileImage(String userId, MultipartFile profileImage)
            throws UserNotFoundException, IOException, NotAnImageFileException, EmptyFileException {
        saveUserImageProfile(userId, profileImage);
        log.info("update profile image successfully");
    }

    public byte[] downloadImage(String userId) throws UserNotFoundException {
        User user = getUserOrThrow(userId);
        String path = String.format("%s/user/userId-%d", AWSBucket.USER_IMAGE.getBucketName(),user.getId());
        byte[] image = awsFileStore.download(path,user.getProfileImage());

        return image;
    }

    private void isImage(MultipartFile file) throws NotAnImageFileException {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new NotAnImageFileException("File must be an image [" + file.getContentType() + "]");
        }
    }

    private void isFileEmpty(MultipartFile file) throws EmptyFileException {
        if (file.isEmpty()) {
            throw new EmptyFileException("Cannot upload empty file [ " + file.getSize() + "]");
        }
    }

    private User getUserOrThrow(String userId) throws UserNotFoundException {
        log.info("Checking if user is already exists");
        return userRepository
                .findAll()
                .stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("User %s not found", userId)));
    }
}
