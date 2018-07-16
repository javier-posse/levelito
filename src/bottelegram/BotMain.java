package bottelegram;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class BotMain {

    public static void main(String[] args) {
        //Se inicializa el contecto del API
        ApiContextInitializer.init();
        
        //Instanciar la API de bots de Telegram
        TelegramBotsApi botsApi=new TelegramBotsApi();
        
        try {
            //Se registra el bot
            botsApi.registerBot(new Levelito());
        } catch (TelegramApiException ex) {
            System.out.println(ex);
        }
    }
    
}
