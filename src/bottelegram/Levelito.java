package bottelegram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Levelito extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        //
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            //Recojo el mensaje de texto y sus caracteristicas
            String mensajeRecibido = update.getMessage().getText();//texto
            String usuario = update.getMessage().getFrom().getUserName();//user
            long chatID = update.getMessage().getChatId();//id del chat

            //Creo un mensaje para enviar
            SendMessage mensaje = new SendMessage();
            mensaje.setChatId(chatID);//le aseigno el char

            if (update.getMessage().isCommand()) {//Control de comandos

                mensaje = responderComando(mensajeRecibido, chatID);

            } else if (mensajeRecibido.indexOf("Crear quedada") == 0) {//Creación de quedadas

                mensaje = generarQuedada(mensajeRecibido, usuario);

            } else if (mensajeRecibido.indexOf("Crear encuesta") == 0) {//Creación de encuestas

                mensaje = generarEncuesta(mensajeRecibido, usuario);

            } else if (mensajeRecibido.contains("Buenos días @LevelitoBot") || mensajeRecibido.contains("Hola @LevelitoBot")) {

                mensaje.setReplyToMessageId(update.getMessage().getMessageId());
                mensaje.setText("Buenos días @" + usuario + ", tenga usted un buen dia.");

                //El puto amo
            } else if (mensajeRecibido.contains("@LevelitoBot") && mensajeRecibido.contains("el puto amo")) {
                
                mensaje.setReplyToMessageId(update.getMessage().getMessageId());
                mensaje.setText("Muchas gracias @" + usuario + ", gracias a decirme cosas tan bonitas hacen que merezca la pena ser vuestro esclavo.");

            } else if (isGay(mensajeRecibido)) {//Jakub gay
                
                mensaje.setText("@" + usuario + ", ¿estás seguro? No apostaría por ello.");
                
            }else if (mensajeRecibido.equals("Dios")) {//Es un dios
                
                mensaje.setText("@" + usuario + ", ¿me estás llamando? Aquí estoy");
                
            }
            try {
                execute(mensaje);//envia el mensaje
            } catch (TelegramApiException ex) {
                System.out.println(ex);
            }

            
        } else if (update.hasCallbackQuery()) {//Edita los mensajes con botones al recibir algo de uno de ellos

            //Preparo variables varias que utilizaré en cualquier caso
            String user = update.getCallbackQuery().getFrom().getUserName();
            long chatID = update.getCallbackQuery().getMessage().getChatId();
            int mensajeID = update.getCallbackQuery().getMessage().getMessageId();

            EditMessageText mensajeEditado = new EditMessageText();
            mensajeEditado.setChatId(chatID);
            mensajeEditado.setMessageId(mensajeID);

            //ir a las quedadas
            if (update.getCallbackQuery().getData().contains("yendo")) {
                if (update.getCallbackQuery().getMessage().getText().contains(user)) {
                    String mensajeNuevo = update.getCallbackQuery().getMessage().getText();
                    mensajeNuevo = mensajeNuevo.replaceAll("\n⭕ @" + user, "");
                    mensajeEditado.setText(mensajeNuevo);
                } else {
                    mensajeEditado.setText(update.getCallbackQuery().getMessage().getText() + "\r\n⭕ @" + user);

                }
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
                List<List<InlineKeyboardButton>> filas = new ArrayList<>();
                List<InlineKeyboardButton> linea = new ArrayList<>();

                linea.add(new InlineKeyboardButton().setText("Allé voy").setCallbackData("yendo @" + user));

                filas.add(linea);
                markup.setKeyboard(filas);
                mensajeEditado.setReplyMarkup(markup);

                //La actu de las votaciones
            } else if (update.getCallbackQuery().getData().contains("voto")) {
                String texto = update.getCallbackQuery().getMessage().getText().split("-")[0];

                if (!texto.contains(user)) {
                    texto = texto + "⭕ @" + user;
                    System.out.println(texto);
                    String[] opciones = update.getCallbackQuery().getMessage().getText().split("-");
                    ArrayList<Integer> votos = new ArrayList<>();

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
                    List<List<InlineKeyboardButton>> filas = new ArrayList<>();
                    List<InlineKeyboardButton> linea = new ArrayList<>();

                    for (int i = 1; i < opciones.length; i++) {
                        String opcionDeTurno = opciones[i].split(": ")[0];
                        System.out.println(opcionDeTurno);
                        Integer votoSacado = Integer.parseInt(String.valueOf(opciones[i].split(": ")[1].charAt(0)));
                        System.out.println(votoSacado);

                        if (update.getCallbackQuery().getData().split(",")[1].equals(opcionDeTurno)) {
                            votoSacado++;
                            //texto=texto+": "+opcionDeTurno;
                        }
                        votos.add(votoSacado);
                        texto = texto + "\r\n-" + opcionDeTurno + ": " + votos.get(i - 1);
                        linea.add(new InlineKeyboardButton().setText(opcionDeTurno).setCallbackData("voto @" + user + "," + opcionDeTurno));
                    }

                    mensajeEditado.setText(texto);
                    filas.add(linea);
                    markup.setKeyboard(filas);
                    mensajeEditado.setReplyMarkup(markup);
                    //PinChatMessage pin = new PinChatMessage(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());

                }
            }
            try {
                execute(mensajeEditado);
            } catch (TelegramApiException ex) {
                System.out.println(ex);
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "LevelitoBot";
    }

    public boolean isGay(String texto) {
        String textoMin = texto.toLowerCase();

        return textoMin.contains("sorry") && textoMin.contains("no") && textoMin.contains("gay")
                || (textoMin.contains("lo siento") && textoMin.contains("no") && textoMin.contains("homosexual"));
    }

    //El generador de respuestas de comandos
    public SendMessage responderComando(String comando, long chatID) {

        SendMessage mensaje = new SendMessage();

        if (comando.contains("ayudaquedadas")) {
            mensaje.setText("Crear quedada: en SITIO, para DIA a las");

        } else if (comando.contains("/ayudaencuestas")) {
            mensaje.setText("Crear encuesta pregunta: PREGUNTA. Respuestas:  DIA1, DIA2, DIA3...|| Crear encuesta pregunta: PREGUNTA. Respuestas: HORA1, HORA2, HORA3...");

        } else if (comando.contains("/outofcontext")) {
            Random rand = new Random();
            int opcion = rand.nextInt(10);
            try {
                File archivo = new File("C:\\" + chatID + ".txt");
                FileReader fr;
                fr = new FileReader(archivo);
                BufferedReader br = new BufferedReader(fr);

                String linea;
                int cont = 0;
                while ((linea = br.readLine()) != null) {
                    cont++;
                    if (cont == opcion) {
                        break;
                    }
                }
                mensaje.setText(linea);
            } catch (FileNotFoundException ex) {
                System.out.println(ex);
            } catch (IOException ex) {
                System.out.println(ex);
            }

        }
        return mensaje;
    }

    public SendMessage generarQuedada(String mensajeRecibido, String usuario) {

        SendMessage mensaje = new SendMessage();

        String datos = mensajeRecibido.split(": en")[1];
        String[] datosSeparados = datos.split(", para");
        System.out.println(datosSeparados[1]);
        mensaje.setText("¡Gente! Quedamos en el " + datosSeparados[0] + " el día" + datosSeparados[1]);

        //Hago las opciones del mensaje
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
        List<List<InlineKeyboardButton>> filas = new ArrayList<>();
        List<InlineKeyboardButton> linea = new ArrayList<>();

        linea.add(new InlineKeyboardButton().setText("Allé voy").setCallbackData("yendo @" + usuario));

        filas.add(linea);
        markup.setKeyboard(filas);

        mensaje.setReplyMarkup(markup);

        return mensaje;

    }

    public SendMessage generarEncuesta(String mensajeRecibido, String usuario) {

        SendMessage mensaje = new SendMessage();

        String pregunta = mensajeRecibido.split("pregunta: ")[1].split("Respuesta: ")[0];
        String[] opciones = mensajeRecibido.split("Respuestas: ")[1].split(", ");
        System.out.println(pregunta);

        //Hago las opciones del mensaje
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();//creo que es la barra de opciones
        List<List<InlineKeyboardButton>> filas = new ArrayList<>();
        List<InlineKeyboardButton> linea = new ArrayList<>();

        String textoMensaje = "¡Encuesta va! Elegid sabiamente. " + pregunta + " Ya han votado:";
        for (String s : opciones) {
            linea.add(new InlineKeyboardButton().setText(s).setCallbackData("voto @" + usuario + "," + s));
            textoMensaje = textoMensaje + "\r\n-" + s + ": 0";
        }
        mensaje.setText(textoMensaje);
        filas.add(linea);
        markup.setKeyboard(filas);
        mensaje.setReplyMarkup(markup);

        return mensaje;
    }
}
