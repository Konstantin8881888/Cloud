package com.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHandler implements Runnable
{
    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    private static final String SERVER_DIR = "server_files";
    private static final String SEND_FILE_COMMAND = "file";
    private static final String SEND_PATH_COMMAND = "fileLs";
    private static final Integer BATCH_SIZE = 256;

    private byte[] batch;

    public FileHandler(Socket socket) throws IOException
    {
        this.socket = socket;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        batch = new byte[BATCH_SIZE];
        File file = new File(SERVER_DIR);
        if (!file.exists())
        {
            System.out.println(file);
            file.mkdir();
        }
        System.out.println("Client Accepted!");
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("Start Listening...");
            while (true)
            {
                String command = dis.readUTF();
                dos.writeUTF(SERVER_DIR);
                if (command.equals(SEND_FILE_COMMAND))
                {
                    String filename = dis.readUTF();
                    long size = dis.readLong();
                    try(FileOutputStream fos = new FileOutputStream(SERVER_DIR + "/" + filename))
                    {
                        for (int i = 0; i < (size + BATCH_SIZE - 1) / BATCH_SIZE; i++)
                        {
                            int read = dis.read(batch);
                            fos.write(batch,0, read);
                        }
                        System.out.println("File received on server: " + filename);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Error receiving file: " + e);
                    }
                }

//                Получение списка файлов сервера
                else if (command.equals(SEND_PATH_COMMAND))
                {
                    List<String> dir = getFiles(SERVER_DIR);
//                    int dirSize = dir.size();
//                    dos.write(dirSize);
                    for (int i = 0; i < dir.size(); i++) {
                        String dir1 = dir.get(i);
                        dos.writeUTF(dir1);
                    }

                }

//                Отправка файла с сервера на клиент
                else if (command.equals("receive"))
                {
                    String receivedFiles = dis.readUTF();
//                    System.out.println(receivedFiles);
                    String filepath = SERVER_DIR + "/" + receivedFiles;
                    File file = new File(filepath);
                    if (file.isFile())
                    {
                        try
                        {
                            dos.writeUTF(SEND_FILE_COMMAND);
                            dos.writeUTF(receivedFiles);
                            dos.writeLong(file.length());
                            try(FileInputStream fis = new FileInputStream(file))
                            {
                                byte[] bytes = fis.readAllBytes();
                                dos.write(bytes);
                                System.out.println("File sent from server: " + receivedFiles);
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
                else
                {
                    System.out.println("Unknown command received: " + command);
                }
            }
        }
        catch (Exception ignored)
        {
            System.out.println("Client dissconnect!");
        }
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
}
