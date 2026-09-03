package ifce.flamearrow.regor.remote;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.GetObjectArgs;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

public class Courier {
	private MinioClient minioClient;
	
	public Courier () {
		this.minioClient = MinioClient.builder()
				.endpoint(System.getenv("MINIO_ENDPOINT"))
				.credentials(System.getenv("MINIO_ACCESS_KEY"), System.getenv("MINIO_SECRET_KEY"))
				.build();
	}
	
	//TODO change printStackTrace to log4j 
	public InputStream fetchImage (String target) throws ServerException, IOException, RuntimeException {
		InputStream stream;
		try {
			stream = minioClient.getObject(
			GetObjectArgs.builder()
			.bucket("imgs")
			.object(target)
			.build());
		} catch (XmlParserException err) {
			err.printStackTrace();
			throw new RuntimeException("Weird error occurred");
		} catch (ServerException err2) {
			err2.printStackTrace();
			throw err2;
		} catch (NoSuchAlgorithmException err3 ) {
			err3.printStackTrace();
			throw new RuntimeException("Weird error occurred");
		} catch (IOException err4) {
			err4.printStackTrace();
			throw err4;
		} catch (InvalidResponseException err5) {
			err5.printStackTrace();
			throw new RuntimeException("Weird error occurred");
		} catch (InvalidKeyException err6) {
			err6.printStackTrace();
			throw new RuntimeException("Invalid key");
		} catch (InternalException err7) {
			err7.printStackTrace();
			throw new RuntimeException("Internal exception");
		} catch (InsufficientDataException err8) {
			err8.printStackTrace();
			throw new RuntimeException("Malformed query");
		} catch (ErrorResponseException err9) {
			err9.printStackTrace();
			throw new RuntimeException("Weird error occurred");
		}
		
		return stream;
	} 
	
	void uploadImage (byte[] rawImage) {
		//TODO
		;
	}
}
