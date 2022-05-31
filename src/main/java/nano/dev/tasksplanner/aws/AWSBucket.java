package nano.dev.tasksplanner.aws;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AWSBucket {

    USER_IMAGE("aws-upload-image-tasksplanner");
    private final String bucketName;
}
