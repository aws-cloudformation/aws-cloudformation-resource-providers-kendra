package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;

import software.amazon.kendra.datasource.S3Path;

import static org.assertj.core.api.Assertions.assertThat;

public class S3PathConverterTest {

    @Test
    void testToSdkOneDriveUsers() {
        String bucket = "bucket";
        String key = "key";
        S3Path modelS3Path = S3Path
                .builder()
                .bucket(bucket)
                .key(key)
                .build();

        software.amazon.awssdk.services.kendra.model.S3Path sdk =
                software.amazon.awssdk.services.kendra.model.S3Path
                                        .builder()
                                        .bucket(bucket)
                                        .key(key)
                                        .build();

        assertThat(S3PathConverter.toSdk(modelS3Path)).isEqualTo(sdk);
    }

    @Test
    void testToModelOneDriveUsers() {
        String bucket = "bucket";
        String key = "key";

        software.amazon.awssdk.services.kendra.model.S3Path sdk =
                software.amazon.awssdk.services.kendra.model.S3Path
                        .builder()
                        .bucket(bucket)
                        .key(key)
                        .build();

        S3Path modelS3Path = S3Path
                .builder()
                .bucket(bucket)
                .key(key)
                .build();

        assertThat(S3PathConverter.toModel(sdk)).isEqualTo(modelS3Path);
    }
}
