package SPD.XMPP.Chat;

import FSM.IFSM;
import FSM.FSM;
import MessageTemplate.Message;
import FSM.TcpTransportServer;
import FSM.Dispatcher;
import FSM.IMessage;

import java.util.ArrayList;
import java.util.Scanner;
public class XMPPChatServer extends FSM implements IFSM {
    public XMPPChatServer(int id) {
        super(id);
    }
    ArrayList<Users> users = new ArrayList<Users>();
    static int READY = 0;
    static int READY1 = 1;
    static int READY2 = 2;
    @Override
    public void init() {
        addTransition(READY, new Message(Message.Types.REGISTER_TO_SERVER), "onServerRegister");
        addTransition(READY1, new Message(Message.Types.REGISTER), "onClientRegister");
        addTransition(READY2, new Message(Message.Types.LOGIN), "onClientLogin");
    }

    public void onServerRegister(IMessage message){
        Message msg = (Message) message;
        Message response = new Message(Message.Types.CONNECTED_SUCCESSFULL);
        response.setToAddress(msg.getFromAddress());
        sendMessage(response);
        setState(READY1);
    }
    public void onClientRegister(IMessage message){
        Message msg = (Message) message;
        Message response = new Message(Message.Types.REGISTRATION_SUCCESSFULL);
        response.setToAddress(msg.getFromAddress());
        Users user = new Users(Message.Params.USERNAME, Message.Params.EMAIL, Message.Params.PASSWORD, Message.Params.ROLE);
        users.add(user);
        System.out.println("Client " + user.getUSERNAME() + " connected!");
        sendMessage(response);
    }
    public void onClientLogin(IMessage message){
        Message msg = (Message) message;
        System.out.println(msg.getParam(Message.Params.USERNAME));
        Users user = new Users(Message.Params.USERNAME, Message.Params.EMAIL, Message.Params.PASSWORD, Message.Params.ROLE);
        for (Users u:users) {
            System.out.println(user);
            if(user.getUSERNAME().equals(u.getUSERNAME()) && user.getPASSWORD().equals(u.getPASSWORD())){
                Message response = new Message(Message.Types.LOGIN_SUCCESSFULL);
                response.setToAddress(msg.getFromAddress());
                sendMessage(response);
                System.out.println("1");
            } else {
                Message response = new Message(Message.Types.LOGIN_UNSUCCESSFULL);
                response.setToAddress(msg.getFromAddress());
                sendMessage(response);
                System.out.println("2");
            }
        }


    }

    static int SERVER_PORT = 9999;
    public static void main(String[] args) throws Exception{
	// write your code here
        XMPPChatServer XMPPChatServerFSM = new XMPPChatServer(0);
        TcpTransportServer tcpFSM = new TcpTransportServer(5);
        tcpFSM.setServerPort(SERVER_PORT);
        tcpFSM.setReceiver(XMPPChatServerFSM);

        Dispatcher dis = new Dispatcher(false);
        dis.addFSM(XMPPChatServerFSM);
        dis.addFSM(tcpFSM);
        dis.start();

        System.out.println("Server is running on port 9999!");
        while(true){
            Thread.sleep(1);
        }
    }
}
