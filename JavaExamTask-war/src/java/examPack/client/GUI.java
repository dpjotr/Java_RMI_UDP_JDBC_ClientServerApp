package examPack.client;
import examPack.EmployeeException;
import examPack.IHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

class GUI extends JFrame {

    JFrame jf;
    Container contents;

    JButton getHistoryButton;
    JButton loginButton;
    JButton logoutButton;

    JPanel bottomPanel;
    JPanel loginPanel;

    JTextArea outputScreenTextArea;
    JTextArea loginNameTextArea;
    JTextArea loginPasswordTextArea;
    JTextArea employeeIdTextArea;

    RMI_client creator;

    GUI(RMI_client cl){
        creator=cl;

        jf=new JFrame("Client");
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.setSize(750, 450);

        contents=jf.getContentPane();

        outputScreenTextArea =new JTextArea("Login to proceed");
        loginNameTextArea =new JTextArea();
        loginNameTextArea.setColumns(10);
        loginPasswordTextArea =new JTextArea();
        loginPasswordTextArea.setColumns(10);
        logoutButton =new JButton("Logout");
        loginButton =new JButton("Login");
        getHistoryButton =new JButton("Get history");
        employeeIdTextArea=new JTextArea();
        employeeIdTextArea.setColumns(15);

        bottomPanel = new JPanel();
        loginPanel = new JPanel();
        contents.add(bottomPanel, BorderLayout.SOUTH);
        contents.add(loginPanel, BorderLayout.NORTH);

        loginPanel.add(new JLabel("login:"));
        loginPanel.add(loginNameTextArea);
        loginPanel.add(new JLabel("password:"));
        loginPanel.add(loginPasswordTextArea);
        loginPanel.add(loginButton);

        contents.add(outputScreenTextArea, BorderLayout.CENTER);
        bottomPanel.add(new JLabel("Enter id or name"));
        bottomPanel.add(employeeIdTextArea);
        bottomPanel.add(getHistoryButton, BorderLayout.EAST);
        bottomPanel.add(logoutButton, BorderLayout.WEST);

        jf.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            creator.login=loginNameTextArea.getText();
            creator.password=loginPasswordTextArea.getText();

            if(creator.login.trim().length()>0&&creator.password.trim().length()>0)
            {

                try {
                    creator.session=(IHistory) creator.sessionCreator
                                .testLogin(creator.login, creator.password);

                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
                finally {
                    outputScreenTextArea.setText(creator.session==null?
                            "Unable to login":"You have logged in, session was created");
                    loginNameTextArea.setText("");
                    loginPasswordTextArea.setText("");
                    
                }
            }
            else
            outputScreenTextArea.setText("Please enter login and password");
            }
        });

        getHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (creator.session!=null){                    
                    String employee=employeeIdTextArea.getText();
                    String history="";
                    try {
                        try {
                            int employeeId = Integer.parseInt(employee);
                            for (String x : creator.session.getHistory(employeeId)) history+=x+"\n";
                        } catch (NumberFormatException nfe) {                            
                            for (String x : creator.session.getHistory(employee)) history+=x+"\n";                           
                        }
                    }
                    catch (RemoteException re){

                    }
                    catch (EmployeeException ee){
                        
                        history=ee.getMessage();

                    }                    
                    outputScreenTextArea.setText(history);                    
                }
                
                else{
                    outputScreenTextArea.setText(
                            "Unable to show history, login in order to receive session");
                }
                
                
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (creator.session!=null&&creator.session.logout()){
                        creator.session=null;
                        outputScreenTextArea.setText("You have logout");
                    }
                    else outputScreenTextArea.setText("You have not login");

                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
    }
}
