import java.io.File;

import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import com.jcraft.jsch.*;

public class TestJSch {
    public static void main(String args[]) {
        JSch jsch = new JSch();
        
        String hostName = "commentpool-baggio000.rhcloud.com";
        String username = "529ddb964382eca06f0004b0";
        String password = "1026wu";
        
        Session session = null;
        try {
            session = jsch.getSession(username, hostName, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get("remotefile.txt", "localfile.txt");
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();  
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
}