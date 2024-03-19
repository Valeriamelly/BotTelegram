package bot;

import model.Reporte;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import util.ExcelLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyTelegramBot extends TelegramLongPollingBot {
    private List<Reporte> reporte;

    public MyTelegramBot() {
        ExcelLoader loader = new ExcelLoader();
        try {
            reporte = loader.cargarDatosDesdeExcel("D:\\PROGRAMACION\\Excel\\Seguimiento.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramBot());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotUsername() {
        return "EfiBot";
    }

    @Override
    public String getBotToken() {
        // Devuelve el token de tu bot aquí, para esto debe obtener su token proporcionado por BotFather de Telegram
        return "inserte su token";
    }


    private void enviarMensaje(String chatId, String texto) {
        SendMessage mensaje = new SendMessage(chatId, texto);
        mensaje.setParseMode("HTML"); // Configura el modo de parseo del mensaje a HTML

        try {
            execute(mensaje);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String buscarReportePorId(String id) {
        for (Reporte x : reporte) {
            if (x.getId().equals(id)) {

                String respuesta = "<b>Reporte Encontrado:</b>\n"
                        + "<b>ID:</b>  " + x.getId() + "\n"
                        + "<b>Fecha del Reporte:</b> " + x.getFechaReporte().toString() + "\n"
                        + "<b>Tipo de Reporte:</b> " + x.getTipoReporte() + "\n"
                        + "<b>Módulo:</b> " + x.getModulo() + "\n"
                        + "<b>Acción:</b> " + x.getAccion() + "\n"
                        + "<b>Problema/Observación:</b> " + x.getObservacion() + "\n"
                        + "<b>Solución:</b> " + x.getSolucion() + "\n"
                        + "<b>Prioridad:</b> " + x.getPrioridad() + "\n"
                        + "<b>ISO/IEC 25010:2011:</b> " + x.getNorma() + "\n";


                return respuesta;
            }
        }
        return "Reporte no encontrado";

    }


    private void enviarListaIdPaginada(String chatId, int pagina) {
        final int ITEMS_POR_PAGINA = 5; // Configura cuántos elementos deseas por mensaje
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        int start = pagina * ITEMS_POR_PAGINA;
        int end = Math.min(start + ITEMS_POR_PAGINA, reporte.size());
        int totalPaginas = (int) Math.ceil((double) reporte.size() / ITEMS_POR_PAGINA);

        for (int i = start; i < end; i++) {
            Reporte x = reporte.get(i);
            if (x.getId() != null && !x.getId().isEmpty()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(x.getId());
                button.setCallbackData("ID_" + x.getId());
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(button);
                rowsInline.add(rowInline);
            }
        }

        // Agregar botón "Retroceder" si no estamos en la primera página
        if (pagina > 0) {
            InlineKeyboardButton backButton = new InlineKeyboardButton();
            backButton.setText("Retroceder");
            backButton.setCallbackData("pag_" + (pagina - 1));
            List<InlineKeyboardButton> backRow = new ArrayList<>();
            backRow.add(backButton);
            rowsInline.add(backRow);
        }
        // Debug: Imprime los valores para verificar el cálculo
        System.out.println("Página: " + pagina + " de " + (totalPaginas - 1));

        // Agregar botón "Más..." solo si hay más reportes para mostrar en una nueva página
        if (pagina < totalPaginas - 1) {
            InlineKeyboardButton moreButton = new InlineKeyboardButton();
            moreButton.setText("Más...");
            moreButton.setCallbackData("pag_" + (pagina + 1));
            List<InlineKeyboardButton> moreRow = new ArrayList<>();
            moreRow.add(moreButton);
            rowsInline.add(moreRow);
        }
        System.out.println("No hay más páginas para mostrar.");

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Seleccione un ID de reporte:");
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String mensajeRecibido = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            switch (mensajeRecibido.toLowerCase()) {
                case "/start":
                    // Mensaje de bienvenida
                    enviarMensaje(chatId, "Bienvenido a EfiBot! Por favor, envíe el siguiente comando para listar los ID: /listar_reportes");
                    break;
                case "/listar_reportes":
                    enviarListaIdPaginada(chatId, 0); // Iniciar con la página 0
                    break;
                default:
                    enviarMensaje(chatId, "Comando no reconocido, intente con el siguiente comando: /start.");
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            int messageId = update.getCallbackQuery().getMessage().getMessageId();

            if (callbackData.startsWith("ID_")) {
                String idReporte = callbackData.substring(3);
                enviarMensaje(chatId, buscarReportePorId(idReporte));
            } else if (callbackData.startsWith("pag_")) {
                int nuevaPagina = Integer.parseInt(callbackData.substring(4));
                EditMessageReplyMarkup newMarkup = actualizarBotonesInline(chatId, messageId, nuevaPagina);
                try {
                    execute(newMarkup); // Esto editará el mensaje en lugar de enviar uno nuevo
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // Método auxiliar para crear un nuevo objeto EditMessageReplyMarkup
    private EditMessageReplyMarkup actualizarBotonesInline(String chatId, int messageId, int pagina) {
        final int ITEMS_POR_PAGINA = 5; // Cuántos elementos por mensaje
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        int start = pagina * ITEMS_POR_PAGINA;
        int end = Math.min(start + ITEMS_POR_PAGINA, reporte.size());
        int totalPaginas = (int) Math.ceil((double) reporte.size() / ITEMS_POR_PAGINA);

        for (int i = start; i < end; i++) {
            Reporte x = reporte.get(i);
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(x.getId());
            button.setCallbackData("ID_" + x.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        // Agregar botón "Retroceder" si no estamos en la primera página
        if (pagina > 0) {
            InlineKeyboardButton backButton = new InlineKeyboardButton();
            backButton.setText("Retroceder");
            backButton.setCallbackData("pag_" + (pagina - 1));
            List<InlineKeyboardButton> backRow = new ArrayList<>();
            backRow.add(backButton);
            rowsInline.add(backRow);
        }

        // Agregar botón "Más..." solo si hay más reportes para mostrar en una nueva página
        if (pagina < totalPaginas - 1) {
            InlineKeyboardButton moreButton = new InlineKeyboardButton();
            moreButton.setText("Más...");
            moreButton.setCallbackData("pag_" + (pagina + 1));
            List<InlineKeyboardButton> moreRow = new ArrayList<>();
            moreRow.add(moreButton);
            rowsInline.add(moreRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        return editMessageReplyMarkup;
    }


}