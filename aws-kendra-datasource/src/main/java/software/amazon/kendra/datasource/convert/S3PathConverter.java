package software.amazon.kendra.datasource.convert;

import software.amazon.kendra.datasource.S3Path;

public class S3PathConverter {

    static software.amazon.awssdk.services.kendra.model.S3Path toSdk(S3Path model) {
        if (model == null) {
            return null;
        }
        return software.amazon.awssdk.services.kendra.model.S3Path
                .builder()
                .bucket(model.getBucket())
                .key(model.getKey())
                .build();
    }

    static S3Path toModel(software.amazon.awssdk.services.kendra.model.S3Path sdk) {
        if (sdk == null) {
            return null;
        }
        return S3Path
                .builder()
                .bucket(sdk.bucket())
                .key(sdk.key())
                .build();
    }
}
