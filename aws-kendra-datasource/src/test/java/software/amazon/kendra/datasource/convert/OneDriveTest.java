package software.amazon.kendra.datasource.convert;

import org.junit.jupiter.api.Test;
import software.amazon.kendra.datasource.OneDriveConfiguration;
import software.amazon.kendra.datasource.OneDriveUsers;
import software.amazon.kendra.datasource.S3Path;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OneDriveTest {

    @Test
    void testToSdkTopLevelFields() {
        String tenantDomain = "tenantDomain";
        String secretArn = "secretArn";
        List<String> includes = Arrays.asList("a");
        List<String> excludes = Arrays.asList("b");
        OneDriveConfiguration model = OneDriveConfiguration
                .builder()
                .tenantDomain(tenantDomain)
                .secretArn(secretArn)
                .inclusionPatterns(includes)
                .exclusionPatterns(excludes)
                .build();

        software.amazon.awssdk.services.kendra.model.OneDriveConfiguration sdk =
        software.amazon.awssdk.services.kendra.model.OneDriveConfiguration.builder()
                .secretArn(secretArn)
                .tenantDomain(tenantDomain)
                .inclusionPatterns(includes)
                .exclusionPatterns(excludes)
                .build();

        assertThat(OneDriveConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToSdkOneDriveUsers() {
        String bucket = "bucket";
        String key = "key";
        S3Path modelS3Path = S3Path
                .builder()
                .bucket(bucket)
                .key(key)
                .build();
        List<String> users = Arrays.asList("a");
        OneDriveUsers modelOneDriveUsers = OneDriveUsers
                .builder()
                .oneDriveUserList(users)
                .oneDriveUserS3Path(modelS3Path)
                .build();
        OneDriveConfiguration model = OneDriveConfiguration
                .builder()
                .oneDriveUsers(modelOneDriveUsers)
                .build();

        software.amazon.awssdk.services.kendra.model.OneDriveConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.OneDriveConfiguration.builder()
                        .oneDriveUsers(software.amazon.awssdk.services.kendra.model.OneDriveUsers
                                .builder()
                                .oneDriveUserS3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                        .builder()
                                        .bucket(bucket)
                                        .key(key)
                                        .build())
                                .oneDriveUserList(users)
                                .build())
                        .build();

        assertThat(OneDriveConverter.toSdk(model)).isEqualTo(sdk);
    }

    @Test
    void testToModelTopLevelFields() {
        String tenantDomain = "tenantDomain";
        String secretArn = "secretArn";
        List<String> includes = Arrays.asList("a");
        List<String> excludes = Arrays.asList("b");
        OneDriveConfiguration model = OneDriveConfiguration
                .builder()
                .tenantDomain(tenantDomain)
                .secretArn(secretArn)
                .inclusionPatterns(includes)
                .exclusionPatterns(excludes)
                .build();

        software.amazon.awssdk.services.kendra.model.OneDriveConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.OneDriveConfiguration.builder()
                        .secretArn(secretArn)
                        .tenantDomain(tenantDomain)
                        .inclusionPatterns(includes)
                        .exclusionPatterns(excludes)
                        .build();

        assertThat(OneDriveConverter.toModel(sdk)).isEqualTo(model);
    }

    @Test
    void testToModelOneDriveUsers() {
        String bucket = "bucket";
        String key = "key";
        S3Path modelS3Path = S3Path
                .builder()
                .bucket(bucket)
                .key(key)
                .build();
        List<String> users = Arrays.asList("a");
        OneDriveUsers modelOneDriveUsers = OneDriveUsers
                .builder()
                .oneDriveUserList(users)
                .oneDriveUserS3Path(modelS3Path)
                .build();
        OneDriveConfiguration model = OneDriveConfiguration
                .builder()
                .oneDriveUsers(modelOneDriveUsers)
                .build();

        software.amazon.awssdk.services.kendra.model.OneDriveConfiguration sdk =
                software.amazon.awssdk.services.kendra.model.OneDriveConfiguration.builder()
                        .oneDriveUsers(software.amazon.awssdk.services.kendra.model.OneDriveUsers
                                .builder()
                                .oneDriveUserS3Path(software.amazon.awssdk.services.kendra.model.S3Path
                                        .builder()
                                        .bucket(bucket)
                                        .key(key)
                                        .build())
                                .oneDriveUserList(users)
                                .build())
                        .build();

        assertThat(OneDriveConverter.toModel(sdk)).isEqualTo(model);
    }
}
