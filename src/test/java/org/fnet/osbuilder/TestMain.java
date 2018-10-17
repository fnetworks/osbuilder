package org.fnet.osbuilder;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.fnet.osbuilder.os.OperatingSystem;
import org.fnet.osbuilder.os.targets.TargetRunner;
import org.fnet.osbuilder.os.targets.impl.ISOTarget;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TestMain {

	public static void main(String[] args) throws IOException {
		OperatingSystem os = new OperatingSystem(new File("test"));
		os.load();
		os.save();
//		FTPClient client = new FTPClient();
//		client.addProtocolCommandListener(new PrintCommandListener(System.out));
//
//		client.connect("ftp.gnu.org");
//
//		int reply = client.getReplyCode();
//		if (!FTPReply.isPositiveCompletion(reply)) {
//			client.disconnect();
//			throw new IOException("Could not connect to ftp server");
//		}
//
//		client.enterLocalPassiveMode();
//
//		client.login("anonymous", "");
//
//
//		FTPFile[] versions = client.listFiles("/gnu/binutils");
//		System.out.println(Arrays.toString(versions));
//		client.disconnect();
	}

}
