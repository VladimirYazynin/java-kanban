package exception;

import com.sun.net.httpserver.HttpExchange;

public class ErrorHandler {

    public void handle(HttpExchange exchange, Exception e) {
        try {
            if (e instanceof ManagerSaveException) {
                //
                return;
            }

            if (e instanceof  NotFoundException) {
                //
                return;
            }
            e.printStackTrace();
            //
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }



}
