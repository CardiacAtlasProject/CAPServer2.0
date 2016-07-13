package nz.auckland.abi.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import nz.ac.auckland.abi.administration.FileResourcesManagerRemote;
import nz.ac.auckland.abi.administration.PACSCAPDatabaseSynchronizerRemote;

import org.json.simple.JSONObject;
 
@WebServlet(urlPatterns = {"/upload/*"})
@MultipartConfig
public class UploadService extends HttpServlet {
 	
 @EJB
 PACSCAPDatabaseSynchronizerRemote sync;
 
 @EJB
 FileResourcesManagerRemote packageManager;
 
 //https://forums.openshift.com/how-to-upload-and-serve-files-using-java-servlets-on-openshift
  private static final long serialVersionUID = 2857847752169838915L;
  int BUFFER_LENGTH = 4096;
 
  @SuppressWarnings("unchecked")
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	PrintWriter writer = response.getWriter();
	//Create a temporary directory
	String tempDir = sync.getTempDir();
	File wDir = new File(tempDir,"upload"+Math.random());
	while(wDir.exists()){
		wDir = new File(tempDir,"upload"+Math.random());
	}
	wDir.mkdirs();
	int failed = 0;
	Collection<Part> myparts = request.getParts();
	JSONObject progress = new JSONObject();
    for (Part part : myparts) {
        String fileName = getFileName(part);
        //System.out.println(fileName);

        try{
	        InputStream is = request.getPart(part.getName()).getInputStream();
	        //Create the file
	        File target = new File(wDir,fileName);
	        target.mkdirs();
	        Files.copy(is, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(Exception exx){
            JSONObject error = new JSONObject();
            error.put("error"+failed, "reading inputstream"+exx.getMessage());
            failed++;
            exx.printStackTrace();
        }
    }
    progress.put("failed", failed);
    progress.put("total",myparts.size());
    if(failed<myparts.size()){
    	try{
    		String rid = packageManager.createResource(wDir, 0);
    		progress.put("resourceid", rid);
    	}catch(Exception exx){
    		String fn = wDir.getAbsolutePath();
    		progress.put("resourceloc", fn.substring(tempDir.length()));
    	}
    }
   
    writer.write(progress.toJSONString());
 // flush the buffers to make sure the container sends the bytes
    writer.flush();
    
  }
 
  
  private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
          if (cd.trim().startsWith("filename")) {
            return cd.substring(cd.indexOf('=') + 1).trim()
                    .replace("\"", "");
          }
        }
        return null;
      }
}