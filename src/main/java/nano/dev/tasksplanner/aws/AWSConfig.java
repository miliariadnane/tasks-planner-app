package nano.dev.tasksplanner.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class AWSConfig {

    private final Environment environment;

    private static final String ACCESS_KEY_BUCKET = "ACCESS_KEY_BUCKET";
    private static final String SECRET_KEY_BUCKET = "SECRET_KEY_BUCKET";

    @Bean
    public AmazonS3 s3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
            environment.getRequiredProperty(ACCESS_KEY_BUCKET),
            environment.getRequiredProperty(SECRET_KEY_BUCKET)
        );
        return AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-1") // specify the region of yr bucket
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
