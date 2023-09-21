import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server1 {

    static ArrayList<MyFile> myFiles = new ArrayList<>();
    static String downloadFolder = "C:\\Users\\lahiru\\Desktop\\downloadFolder"; // Change this to your desired download folder

    public static void main(String[] args) {
        int fileId = 0;

        JFrame jFrame = new JFrame("Server");
        jFrame.setSize(400, 400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jITitle = new JLabel("File Server");
        jITitle.setFont(new Font("Arial", Font.BOLD, 25));
        jITitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jITitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jITitle);
        jFrame.add(jScrollPane);

        jFrame.setVisible(true);

        try {
            ServerSocket serverSocket = new ServerSocket(1234);

            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();

                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                        MyFile file = new MyFile(fileId, fileName, fileContentBytes);
                        myFiles.add(file);
                        fileId++;

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                        JLabel jIFileName = new JLabel(fileName);
                        jIFileName.setFont(new Font("Arial", Font.BOLD, 25));
                        jIFileName.setBorder(new EmptyBorder(20, 0, 10, 0));

                        jpFileRow.setName(String.valueOf(file.getId()));
                        jpFileRow.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                JPanel panel = (JPanel) e.getSource();
                                int fileId = Integer.parseInt(panel.getName());

                                for (MyFile file : myFiles) {
                                    if (file.getId() == fileId) {
                                        JFrame jfPreview = createFrame(file.getFileName(), file.getFileData(), getFileExtension(file.getFileName()));

                                        jfPreview.setVisible(true);
                                    }
                                }
                            }
                        });

                        jpFileRow.add(jIFileName);
                        jPanel.add(jpFileRow);
                        jFrame.validate();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
        JFrame jFrame = new JFrame("File Downloader");
        jFrame.setSize(400, 400);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JLabel jITitle = new JLabel("File Downloader");
        jITitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jITitle.setFont(new Font("Arial", Font.BOLD, 25));
        jITitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        JLabel jIPrompt = new JLabel("Are you sure you want to download?");
        jIPrompt.setFont(new Font("Arial", Font.BOLD, 25));
        jIPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
        jIPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 20));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150, 20));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel jIFileContent = new JLabel();
        jIFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButtons = new JPanel();
        jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        if (fileExtension.equalsIgnoreCase("txt")) {
            jIFileContent.setText("<html>" + new String(fileData) + "</html>");
        } else {
            ImageIcon imageIcon = new ImageIcon(fileData);
            jIFileContent.setIcon(imageIcon);
        }

        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File downloadDir = new File(downloadFolder);
                if (!downloadDir.exists()) {
                    downloadDir.mkdir();
                }
                File fileToDownload = new File(downloadDir, fileName);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    fileOutputStream.write(fileData);
                    fileOutputStream.close();
                    jFrame.dispose();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        });

        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        jPanel.add(jITitle);
        jPanel.add(jIPrompt);
        jPanel.add(jpButtons);
        jPanel.add(jIFileContent);

        jFrame.add(jPanel);
        return jFrame;
    }

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        }
        return "No extension found";
    }

    static class MyFile {
        private int id;
        private String fileName;
        private byte[] fileData;

        public MyFile(int id, String fileName, byte[] fileData) {
            this.id = id;
            this.fileName = fileName;
            this.fileData = fileData;
        }

        public int getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getFileData() {
            return fileData;
        }
    }
}
