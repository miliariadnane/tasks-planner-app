package nano.dev.tasksplanner.controller;

import lombok.AllArgsConstructor;
import nano.dev.tasksplanner.exception.domain.*;
import nano.dev.tasksplanner.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping(path = "api/v1/image")
@AllArgsConstructor
public class UploadController {

    private UploadService uploadService;

    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity uploadProfileImage(
            @RequestParam("userId") String userId,
            @RequestParam(value = "profileImage",required = false) MultipartFile profileImage
    ) throws UserNotFoundException, EmptyFileException, IOException, NotAnImageFileException  {

        uploadService.saveUserImageProfile(userId, profileImage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity updateProfileImage(
            @RequestParam("userId") String userId,
            @RequestParam(value = "profileImage") MultipartFile profileImage
    ) throws UserNotFoundException, EmptyFileException, IOException, NotAnImageFileException {

        uploadService.updateProfileImage(userId, profileImage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/{userId}/download",
            produces = {IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
    public byte[] downloadImage(@PathVariable("userId") String userId) throws UserNotFoundException {
        return uploadService.downloadImage(userId);
    }
}
