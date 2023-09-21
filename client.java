import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class client {
    public static void main(String[] args) {
        final File[] fileToSend = new File[1];
        JFrame jFrame = new JFrame("Client");
        jFrame.setSize(450, 450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jlTitle = new JLabel("File Sender");
        jlTitle.setFont(new Font("Times New Roman", Font.BOLD, 80));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jlTitle1 = new JLabel("Choose a File to Send: ");
        jlTitle.setFont(new Font("Times New Roman", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(75, 0, 10, 0));

        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Times New Roman", Font.BOLD, 20));

        JButton jbChooseFile = new JButton("Select File");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Times New Roman", Font.BOLD, 20));

        JLabel jlFileName = new JLabel();
        jlFileName.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));

        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Select a file to send");

                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("The File you want to send is: " + fileToSend[0].getName());
                }
            }
        });

        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    jlFileName.setText("Choose a file first.");
                } else {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        Socket socket = new Socket("localhost", 1234);

                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        String fileName = fileToSend[0].getName();
                        byte[] fileNameBytes = fileName.getBytes();

                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        dataOutputStream.writeInt(fileNameBytes.length);
                        dataOutputStream.write(fileNameBytes);
                        dataOutputStream.write(fileContentBytes);

                        dataOutputStream.close();
                        fileInputStream.close();
                        socket.close();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        jFrame.add(jlTitle);
        jFrame.add(jlTitle1);
        jFrame.add(jpButton);
        jFrame.add(jlFileName);
        

        jFrame.setVisible(true);
    }
}
