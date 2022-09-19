package com.example.client;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CloudMainController implements Initializable {
    public ListView<String> clientView;

    public ListView<String> serverView;

    private String currentDirectory;

    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;

    private static final String SEND_FILE_COMMAND = "file";

    private static final Integer BATCH_SIZE = 256;

    private byte[] batch;

    private void initNetwork()
    {
        try
        {
            socket = new Socket("localhost", 8189);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception e)
        {
            System.out.println("Socket connection error: " + e);
        }

    }

    public void SendToServer(ActionEvent actionEvent)
    {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        String filepath = currentDirectory + "/" + fileName;
        File file = new File(filepath);
        if (file.isFile())
        {
            try
            {
                dos.writeUTF(SEND_FILE_COMMAND);
                dos.writeUTF(fileName);
                dos.writeLong(file.length());
                try(FileInputStream fis = new FileInputStream(file))
                {
                    byte[] bytes = fis.readAllBytes();
                    dos.write(bytes);
                    System.out.println("File sent from client: " + fileName);
                }
                catch (IOException e)
                {
                    System.out.println("File upload error: " + e);
                }

            }
            catch (Exception e)
            {
                System.err.println("File upload error: " + e.getMessage());
            }
        }
    }


    public void SendToClient(ActionEvent actionEvent) throws IOException
    {
//        Файл получается, с правильным названием и содержанием, однако программа-клиент зависает, подозреваю, что нужно создать отдельным потоком.
        String fileName = serverView.getSelectionModel().getSelectedItem();
        dos.writeUTF("receive");
        dos.writeUTF(fileName);
        String file_name = dis.readUTF();
        long size = dis.readLong();
        try(FileOutputStream fos = new FileOutputStream(currentDirectory + "/" + fileName))
        {
            for (int i = 0; i < (size + BATCH_SIZE - 1) / BATCH_SIZE; i++)
            {
                int read = dis.read(batch);
                fos.write(batch,0, read);
            }
            System.out.println("File received on client: " + fileName);
        }
        catch (Exception e)
        {
            System.out.println("Error receiving file: " + e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        initNetwork();
        setCurrentDirectory(System.getProperty("user.home"));
        fillView(clientView, getFiles(currentDirectory));
        clientView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2)
            {
                String selected = clientView.getSelectionModel().getSelectedItem();
                File selectedFile = new File(currentDirectory + "/" + selected);
                if (selectedFile.isDirectory())
                {
                    setCurrentDirectory(selectedFile.getAbsolutePath());
                }
            }
        });
//        Файл батч создаётся, но почему-то программа зависает с появлением на компе нужного файла, подозреваю, что нужно создать отдельным потоком.
        batch = new byte[BATCH_SIZE];
        try {
            takeFilesOfServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setCurrentDirectory(String directory)
    {
        currentDirectory = directory;
        fillView(clientView, getFiles(currentDirectory));
    }

    private void fillView(ListView<String> view, List<String> data)
    {
        view.getItems().clear();
        view.getItems().addAll(data);
    }

    private List<String> getFiles(String directory)
    {
        File dir = new File(directory);
        if (dir.isDirectory())
        {
            String[] list = dir.list();
            if (list != null)
            {
              List<String> files = new ArrayList<>(Arrays.asList(list));
              files.add(0,"..");
              return files;
            }
        }
        return List.of();
    }

    //        Получение списка файлов с сервера. Как получить длину массива с сервера, так и не придумал, оставил числом в итераторе.
    private void takeFilesOfServer() throws IOException {
        dos.writeUTF("fileLs");
        List<String> slist = new ArrayList<>();
//        int dirSize = dis.read();
        for (int i = 0; i < 6; i++) {
            String fileList = dis.readUTF();
            System.out.println(fileList);
            if (i != 0)
            {
                slist.add(fileList);
            }
        }
        fillView(serverView, slist);
    }
}
