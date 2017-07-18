package org.cardiacatlas.xpacs.web.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

@RestController
@RequestMapping("/upload")
public class UploadResource {
	private final Logger log = LoggerFactory.getLogger(PatientInfoResource.class);
	//Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "downloads/";
    private static String SCRIPTS_FOLDER = "scripts/";
	@PostMapping("/status")
    @Timed
  //Single file upload
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadfile,@RequestParam("UploadEntity") String UploadEntity) {

        log.debug("Single file upload!");
        log.debug("Uploading entity "+UploadEntity);
        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }
        
//        if(!(uploadfile.getOriginalFilename().split(".")[1].equals("csv"))){
//        	return new ResponseEntity("please upload a csv file only!", HttpStatus.BAD_REQUEST);
//        }

        try {

            saveUploadedFiles(Arrays.asList(uploadfile));

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        String s = null;
        try {
			Process p = Runtime.getRuntime().exec("python "+SCRIPTS_FOLDER+"/upload_"+UploadEntity+".py "+uploadfile.getOriginalFilename());
			p.waitFor();
			BufferedReader stdInput = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));

	            BufferedReader stdError = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));

	            // read the output from the command
	            log.debug("Here is the standard output of the command:\n");
	            while ((s = stdInput.readLine()) != null) {
	            	log.debug(s);
	            }
	            
	            // read any errors from the attempted command
	            log.debug("Here is the standard error of the command (if any):\n");
	            while ((s = stdError.readLine()) != null) {
	            	log.debug(s);
	            }
	            
		} catch (IOException e) {
			
			log.debug("error! "+e);
		}
        catch ( InterruptedException e){
        	log.debug("error! "+e);
        }

        return new ResponseEntity("Successfully uploaded - " +
                uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

    }
	
	//save file
    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            System.out.println("Path = "+path);
            Files.write(path, bytes);

        }

    }
}
