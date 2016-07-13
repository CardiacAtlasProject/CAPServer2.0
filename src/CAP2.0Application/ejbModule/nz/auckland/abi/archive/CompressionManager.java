package nz.auckland.abi.archive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.xeustechnologies.jtar.Octal;
import org.xeustechnologies.jtar.TarConstants;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarHeader;
import org.xeustechnologies.jtar.TarInputStream;
import org.xeustechnologies.jtar.TarOutputStream;

public class CompressionManager {

	private HashMap<String, List<File>> archiveStructure;
	private HashMap<String, byte[]> archiveData;
	private byte[] metaData;
	private String user;

	public CompressionManager(HashMap<String, List<File>> list) {
		archiveStructure = list;
		archiveData = new HashMap<String, byte[]>();
		user = "CAP";
	}

	public CompressionManager() {
		archiveStructure = new HashMap<String, List<File>>();
		archiveData = new HashMap<String, byte[]>();
		user = "CAP";
	}

	public void addFile(String subdir, File file) {
		if (!archiveStructure.containsKey(subdir)) {
			archiveStructure.put(subdir, new ArrayList<File>());
		}
		archiveStructure.get(subdir).add(file);
	}

	public void addData(String filename, byte[] data) {
		archiveData.put(filename, data);
	}

	public void addDirectory(String subdir, File dir) {
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(dir.listFiles()));
		addFileList(subdir, files);
	}

	public void addFileList(String subdir, ArrayList<File> ifiles) {
		ArrayList<File> files = new ArrayList<File>();
		for (File file : ifiles) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				addDirectory(subdir + "/" + file.getName(), file);
			}
		}
		if (!archiveStructure.containsKey(subdir)) {
			archiveStructure.put(subdir, new ArrayList<File>());
		}
		archiveStructure.get(subdir).addAll(files);
	}

	public void compressTo(File target) throws Exception {
		FileOutputStream dest = new FileOutputStream(target);
		GZIPOutputStream gzip = new GZIPOutputStream(dest);
		int count;
		// Create a TarOutputStream
		TarOutputStream out = new TarOutputStream(new BufferedOutputStream(gzip));

		for (String name : archiveStructure.keySet()) {
			List<File> filesToTar = archiveStructure.get(name);
			for (File f : filesToTar) {
				out.putNextEntry(new TarEntry(f, name + "/" + f.getName()));
				BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f));

				byte data[] = new byte[4096];
				while ((count = origin.read(data)) != -1) {
					out.write(data, 0, count);
				}

				out.flush();
				origin.close();
			}
		}
		for (String name : archiveData.keySet()) {
			out.putNextEntry(new TarEntry(new File(name), name));
			out.write(archiveData.get(name));
			out.flush();
		}

		out.close();
	}

	public byte[] compressToBytes() throws Exception {
		ByteArrayOutputStream dest = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(dest);
		int count;
		// Create a TarOutputStream
		TarOutputStream out = new TarOutputStream(new BufferedOutputStream(gzip));

		for (String name : archiveStructure.keySet()) {
			List<File> filesToTar = archiveStructure.get(name);
			for (File f : filesToTar) {
				out.putNextEntry(new TarEntry(f, name + "/" + f.getName()));
				BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f));

				byte data[] = new byte[4096];
				while ((count = origin.read(data)) != -1) {
					out.write(data, 0, count);
				}

				out.flush();
				origin.close();
			}
		}
		for (String name : archiveData.keySet()) {
			byte[] aData = archiveData.get(name);
			out.putNextEntry(createTarEntry(name, aData));
			out.write(aData);
			out.flush();
		}
		out.close();
		return dest.toByteArray();
	}

	public void setMetaData(byte[] meta) {
		metaData = meta;
	}

	public static void uncompressArchive(byte[] data, File target) throws Exception {
		TarInputStream tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))));
		TarEntry entry;
		while ((entry = tis.getNextEntry()) != null) {
			int count;
			byte buffer[] = new byte[4096];
			ByteArrayOutputStream fos = new ByteArrayOutputStream();
			BufferedOutputStream dest = new BufferedOutputStream(fos);
			while ((count = tis.read(buffer)) != -1) {
				dest.write(buffer, 0, count);
			}
			dest.flush();
			dest.close();
			String fname = entry.getName();
			File file = new File(target,fname);
			file.mkdirs(); //to ensure that directory structure of the entry is maintained
			Files.copy(new ByteArrayInputStream(fos.toByteArray()), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		tis.close();
	}

	public byte[] recompressToBytes() throws Exception {
		GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(metaData));
		TarInputStream tis = new TarInputStream(input);
		ByteArrayOutputStream dest = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(dest);
		int count;
		// Create a TarOutputStream
		TarOutputStream out = new TarOutputStream(new BufferedOutputStream(gzip));
		TarEntry entry;
		while ((entry = tis.getNextEntry()) != null) {
			byte data[] = new byte[4096];
			out.putNextEntry(entry);
			while ((count = tis.read(data)) != -1) {
				out.write(data, 0, count);
			}
			out.flush();
		}
		for (String name : archiveStructure.keySet()) {
			List<File> filesToTar = archiveStructure.get(name);
			for (File f : filesToTar) {
				out.putNextEntry(new TarEntry(f, name + "/" + f.getName()));
				BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f));

				byte data[] = new byte[4096];
				while ((count = origin.read(data)) != -1) {
					out.write(data, 0, count);
				}

				out.flush();
				origin.close();
			}
		}
		tis.close();

		out.close();
		return dest.toByteArray();
	}

	public void setFileUser(String name) {
		user = name;
	}

	private TarEntry createTarEntry(String filename, byte[] aData) {

		TarHeader header = new TarHeader();
		header.linkName = new StringBuffer("");

		String user = this.user;

		if (user.length() > 31)
			user = user.substring(0, 31);

		header.userId = 0;
		header.groupId = 0;
		header.userName = new StringBuffer(user);
		header.groupName = new StringBuffer("");

		header.name = new StringBuffer(filename);
		header.mode = 0100644;
		header.linkFlag = TarHeader.LF_NORMAL;

		header.size = aData.length;
		header.modTime = System.currentTimeMillis() / 1000;
		header.checkSum = 0;
		header.devMajor = 0;
		header.devMinor = 0;

		int offset = 0;
		byte[] outbuf = new byte[TarConstants.HEADER_BLOCK];
		offset = TarHeader.getNameBytes(header.name, outbuf, offset, TarHeader.NAMELEN);
		offset = Octal.getOctalBytes(header.mode, outbuf, offset, TarHeader.MODELEN);
		offset = Octal.getOctalBytes(header.userId, outbuf, offset, TarHeader.UIDLEN);
		offset = Octal.getOctalBytes(header.groupId, outbuf, offset, TarHeader.GIDLEN);

		long size = header.size;

		offset = Octal.getLongOctalBytes(size, outbuf, offset, TarHeader.SIZELEN);
		offset = Octal.getLongOctalBytes(header.modTime, outbuf, offset, TarHeader.MODTIMELEN);

		int csOffset = offset;
		for (int c = 0; c < TarHeader.CHKSUMLEN; ++c)
			outbuf[offset++] = (byte) ' ';

		outbuf[offset++] = header.linkFlag;

		offset = TarHeader.getNameBytes(header.linkName, outbuf, offset, TarHeader.NAMELEN);
		offset = TarHeader.getNameBytes(header.magic, outbuf, offset, TarHeader.MAGICLEN);
		offset = TarHeader.getNameBytes(header.userName, outbuf, offset, TarHeader.UNAMELEN);
		offset = TarHeader.getNameBytes(header.groupName, outbuf, offset, TarHeader.GNAMELEN);
		offset = Octal.getOctalBytes(header.devMajor, outbuf, offset, TarHeader.DEVLEN);
		offset = Octal.getOctalBytes(header.devMinor, outbuf, offset, TarHeader.DEVLEN);

		for (; offset < outbuf.length;)
			outbuf[offset++] = 0;

		long checkSum = 0;

		for (int i = 0; i < outbuf.length; ++i) {
			checkSum += 255 & outbuf[i];
		}

		Octal.getCheckSumOctalBytes(checkSum, outbuf, csOffset, TarHeader.CHKSUMLEN);

		return new TarEntry(outbuf);
	}

	public static void main(String args[]) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		File tarzf = new File("/home/jagir/ICMASCRATCH/test.tar.gz");
		Files.copy(tarzf.toPath(), bos);
		CompressionManager man = new CompressionManager();
		man.setMetaData(bos.toByteArray());
		man.addFile("adf", new File("/home/jagir/Desktop/test.xml"));
		FileOutputStream fos = new FileOutputStream(new File("/home/jagir/Desktop/test.tar.gz"));
		fos.write(man.recompressToBytes());
		fos.close();
	}
}
