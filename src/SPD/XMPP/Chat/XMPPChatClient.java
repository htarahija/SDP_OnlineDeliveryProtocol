package SPD.XMPP.Chat;

import FSM.IFSM;
import FSM.FSM;
import MessageTemplate.Message;
import FSM.TcpTransportClient;
import FSM.Dispatcher;
import FSM.IMessage;


import java.util.ArrayList;
import java.util.Scanner;

public class XMPPChatClient extends FSM implements IFSM {
    static String TOKEN = "123";
    static int IDLE = 0;
    static int READY_TO_CONNECT = 1;
    static int CONNECTING = 2;
    static int CONNECTED = 3;
    static int VERIFICATED_REG = 4;
    static int VERIFICATED_LOGIN = 5;
    static int ORDERED = 5;
    public XMPPChatClient(int id) {
        super(id);
    }
    @Override
    public void init() {
        setState(IDLE);
        addTransition(IDLE, new Message(Message.Types.RESOLVE_DOMAIN_NAME), "resolveDomain");
        addTransition(READY_TO_CONNECT, new Message(Message.Types.REGISTER_TO_SERVER), "registerOnServer");
        addTransition(CONNECTING, new Message(Message.Types.CONNECTED_SUCCESSFULL), "connectionSuccessful");
        addTransition(CONNECTED, new Message(Message.Types.REGISTER), "registerUser");
        addTransition(CONNECTED, new Message(Message.Types.LOGIN), "loginUser");
        addTransition(VERIFICATED_REG, new Message(Message.Types.REGISTRATION_SUCCESSFULL), "verificatedRegistredUser");
        addTransition(VERIFICATED_LOGIN, new Message(Message.Types.LOGIN_SUCCESSFULL), "verificatedLoginUser");
        addTransition(CONNECTED, new Message(Message.Types.LOGIN_UNSUCCESSFULL), "unlocatedLoginUser");
    }

    public void resolveDomain(IMessage message){
        Message msg = (Message)message;
        System.out.println("Resolving: " + msg.getParam(Message.Params.DOMAIN));
        //resolve domain, implement this please!
        Message tcpMSG = new Message(5555);
        tcpMSG.addParam(Message.Params.IP, "127.0.0.1");
        sendMessage(tcpMSG);
        System.out.println("Resolved!");
        setState(READY_TO_CONNECT);

    }
    public void registerOnServer(IMessage message){
        Message msg = (Message)message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.REGISTER_TO_SERVER);
        System.out.println("Connecting...");
        sendMessage(msg);
        setState(CONNECTING);
    }
    public void connectionSuccessful(IMessage message){
        System.out.println("Connected successful!");
        setState(CONNECTED);
    }
    public void registerUser(IMessage message){
        Message request = new Message(Message.Types.REGISTER);
        request.setMessageId(Message.Types.REGISTER);
        System.out.println("Registration...");
        sendMessage(request);
        setState(VERIFICATED_REG);
    }
    public void loginUser(IMessage message){
        Message request = new Message(Message.Types.LOGIN);
        request.setMessageId(Message.Types.LOGIN);
        System.out.println("Login...");
        sendMessage(request);
        setState(VERIFICATED_LOGIN);
    }
    public void unlocatedLoginUser(IMessage message){
        System.out.println("FULAA");
        setState(CONNECTED);
    }
    public void verificatedRegistredUser(IMessage message){
        System.out.println("Registrated user.");
        setState(CONNECTED);
    }
    public void verificatedLoginUser(IMessage message){
        System.out.println("Login.");
    }
    static int SERVER_PORT = 9999;
    static String SERVER_URL = "";
    static String SERVER_IP = "";
    public static void main(String[] args) throws Exception{
	// write your code here
        //client
        XMPPChatClient XMPPChatClientFSM = new XMPPChatClient(0);
        TcpTransportClient tcpFSM = new TcpTransportClient(5);
        tcpFSM.setServerPort(SERVER_PORT);
        tcpFSM.setReceiver(XMPPChatClientFSM);

        Dispatcher dis = new Dispatcher(false);
        dis.addFSM(XMPPChatClientFSM);
        dis.addFSM(tcpFSM);
        dis.start();

        Message as = new Message(Message.Types.REGISTER);


        Scanner input = new Scanner(System.in);

        String username = "";
        System.out.println("Enter URI of server in format username:port@host.ba");
        /*do{
            String temp = input.nextLine();
            if(temp.split(":").length != 2 || (temp.split(":")[1].split("@")).length != 2){
                System.out.println("Bad input, please try again!");
            }else {
                username = temp.split(":")[0];
                String port = temp.replace(username + ":","").split("@")[0];
                try{
                    SERVER_PORT = Integer.parseInt(port);
                    SERVER_URL = temp.replace(username + ":","").split("@")[1];
                    break;
                }catch(Exception e){
                    System.out.println("Bad port, please try again!");
                }
            }
        }while(true);*/

        SERVER_PORT = 9999;
        SERVER_URL = "www.klix.ba";
        username = "habib";
        System.out.println("User: " + username);
        System.out.println("Host: " + SERVER_URL + ":" + SERVER_PORT);
        tcpFSM.setServerPort(SERVER_PORT);


        Message tempMsg = new Message(Message.Types.RESOLVE_DOMAIN_NAME);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.DOMAIN, SERVER_URL);
        dis.addMessage(tempMsg);

        tempMsg = new Message(Message.Types.REGISTER_TO_SERVER);
        tempMsg.setToId(0);
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.REGISTER);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "username");
        tempMsg.addParam(Message.Params.PASSWORD, "password");
        tempMsg.addParam(Message.Params.EMAIL, "a@a.a");
        tempMsg.addParam(Message.Params.ROLE, "admin");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.LOGIN);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "reha");
        tempMsg.addParam(Message.Params.PASSWORD, "edo");
        tempMsg.addParam(Message.Params.EMAIL, "a@a.a");
        tempMsg.addParam(Message.Params.ROLE, "admin");
        dis.addMessage(tempMsg);

        while(true){


        }

    }
}
