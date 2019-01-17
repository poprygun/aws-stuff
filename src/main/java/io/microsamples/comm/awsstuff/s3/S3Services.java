package io.microsamples.comm.awsstuff.s3;

import java.io.File;

public interface S3Services {
	void downloadFile(String keyName);
	void uploadFile(String keyName, File fileToUpload);
}
