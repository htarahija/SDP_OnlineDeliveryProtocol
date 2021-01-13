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

    static int READY = 0;
    @Override
    public void init() {
        addTransition(READY, new Message(Message.Types.REGISTER), "onClientRegister");
        addTransition(READY, new Message(Message.Types.ROOM_LIST_REQUEST), "returnRoomList");
    }

    public void onClientRegister(IMessage message){
        Message msg = (Message) message;
        Message response = new Message(Message.Types.LOGIN_SUCCESSFULL);
        response.setToAddress(msg.getFromAddress());
        sendMessage(response);
        System.out.println("Client " + msg.getParam(Message.Params.USERNAME) + " connected!");
    }
    public void returnRoomList(IMessage message){
        System.out.println("Room list requested!");
        Message msg = (Message) message;
        String token = ((Message) message).getParam(Message.Params.TOKEN);
        Message response = new Message(Message.Types.ROOM_LIST_RESPONSE);
        response.setToAddress(msg.getFromAddress());

        //check token for login
        if (token != null && true){
            ArrayList<String> room_list = new ArrayList<>();
            room_list.add("TKM1");
            room_list.add("TKM2");
            response.addParam(Message.Params.ROOM_LIST, room_list);
        }else{
            System.out.println("NOT AUTHENTICATED! METHOD NOT IMPLEMENTED!");
        }

        sendMessage(response);
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
