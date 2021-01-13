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
    static int FETCHING = 4;
    public XMPPChatClient(int id) {
        super(id);
    }
    @Override
    public void init() {
        setState(IDLE);
        addTransition(IDLE, new Message(Message.Types.RESOLVE_DOMAIN_NAME), "resolveDomain");
        addTransition(READY_TO_CONNECT, new Message(Message.Types.REGISTER), "registerOnServer");
        addTransition(CONNECTING, new Message(Message.Types.LOGIN_SUCCESSFULL), "connectionSuccessful");
        addTransition(CONNECTED, new Message(Message.Types.ROOM_LIST_REQUEST), "requestRoomList");
        addTransition(FETCHING, new Message(Message.Types.ROOM_LIST_RESPONSE), "roomListResponse");
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
        msg.setMessageId(Message.Types.REGISTER);
        System.out.println("Connecting...");
        sendMessage(msg);
        setState(CONNECTING);
    }
    public void connectionSuccessful(IMessage message){
        System.out.println("Login successful!");
        setState(CONNECTED);
    }
    public void requestRoomList(IMessage message){
        System.out.println("Fetching room list!");
        Message request = new Message(Message.Types.ROOM_LIST_REQUEST);
        request.setMessageId(Message.Types.ROOM_LIST_REQUEST);
        request.addParam(Message.Params.TOKEN, TOKEN);
        sendMessage(request);
        setState(FETCHING);
    }
    public void roomListResponse(IMessage message){
        Message msg = (Message) message;
        System.out.println("Room list:");
        for(Object o: (ArrayList)msg.getParam(Message.Params.ROOM_LIST, true))
            System.out.println("Room name: " + o.toString());
        setState(CONNECTED);
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

        tempMsg = new Message(Message.Types.REGISTER);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, username);
        tempMsg.addParam(Message.Params.PASSWORD, "password");
        tempMsg.addParam(Message.Params.NICKNAME, "medivh");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);
        tempMsg = new Message(Message.Types.ROOM_LIST_REQUEST);
        tempMsg.setToId(0);
        dis.addMessage(tempMsg);

        while(true){


        }

    }
}
