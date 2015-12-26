package aas.project.tera.com.autoattendancesystem;

import java.net.MalformedURLException;

import io.socket.IOCallback;
import io.socket.SocketIO;

/**
 * Created by Administrator on 2015-03-27.
 */

/* 나중에 이 클래스를 싱글턴 클래스로 바꾸어야 한다 */
public class MySocketIO extends SocketIO {

    public MySocketIO(IOCallback ioCallback) throws MalformedURLException {
        super("http://192.168.43.131:1337", ioCallback);
    }
}
