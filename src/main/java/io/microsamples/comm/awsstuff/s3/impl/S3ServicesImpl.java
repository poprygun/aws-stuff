package io.microsamples.comm.awsstuff.s3.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import io.microsamples.comm.awsstuff.s3.S3Services;
import io.microsamples.comm.awsstuff.s3.util.Utility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Log4j2
public class S3ServicesImpl implements S3Services {
	
	
	@Autowired
	private AmazonS3 s3client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Override
	public void downloadFile(String keyName) {
		
		try {
			
            S3Object s3object = s3client.getObject(new GetObjectRequest(
            		bucketName, keyName));
            Utility.displayText(s3object.getObjectContent());
            log.info("Imported {}", keyName);
            
        } catch (AmazonServiceException ase) {
        	log.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
			log.info("Error Message:    " + ase.getMessage());
			log.info("HTTP Status Code: " + ase.getStatusCode());
			log.info("AWS Error Code:   " + ase.getErrorCode());
			log.info("Error Type:       " + ase.getErrorType());
			log.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
        	log.info("Caught an AmazonClientException: ");
            log.info("Error Message: " + ace.getMessage());
        } catch (IOException ioe) {
        	log.info("IOE Error Message: " + ioe.getMessage());
		}
	}

	@Override
	public void uploadFile(String keyName, File fileToUpload) {
		
		try {
	        s3client.putObject(new PutObjectRequest(bucketName, keyName, fileToUpload));
	        log.info("Uploaded {}", keyName);
	        
		} catch (AmazonServiceException ase) {
			log.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			log.info("Error Message:    " + ase.getMessage());
			log.info("HTTP Status Code: " + ase.getStatusCode());
			log.info("AWS Error Code:   " + ase.getErrorCode());
			log.info("Error Type:       " + ase.getErrorType());
			log.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            log.info("Caught an AmazonClientException: ");
            log.info("Error Message: " + ace.getMessage());
        }
	}

}
