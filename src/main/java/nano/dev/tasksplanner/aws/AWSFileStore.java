package nano.dev.tasksplanner.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AWSFileStore {

    private final AmazonS3 s3;

    public void upload(String path,
                       String filename,
                       InputStream inputStream,
                       Optional<Map<String,String>> optionalMetadata
    ) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(inputStream.available());
        optionalMetadata.ifPresent(map -> {
            if(!map.isEmpty()){
                //map.forEach((key,value) -> metadata.addUserMetadata(key,value));
                map.forEach(metadata::addUserMetadata);
            }
        });
        try {
            s3.putObject(path,filename,inputStream,metadata);
        }catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to store file to AWS S3",e);
        }
    }

    public byte[] download(String path, String key) {
        try{
            S3Object objects = s3.getObject(path,key);
            return IOUtils.toByteArray(objects.getObjectContent());
        }catch (AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to download from s3",e);
        }
    }

    public void delete(String bucket, String key) {
        try {
            ObjectListing objectListing = s3.listObjects(bucket,key); //ObjectListing is list of key
            for (S3ObjectSummary objectSummary: objectListing.getObjectSummaries()) {
                s3.deleteObject(bucket,objectSummary.getKey());
            }
        }catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to delete file on AWS S3",e);
        }
    }
}
