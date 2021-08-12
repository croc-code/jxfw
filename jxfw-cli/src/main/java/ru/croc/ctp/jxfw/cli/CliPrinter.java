package ru.croc.ctp.jxfw.cli;

import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.ResultHandler;
import org.springframework.stereotype.Component;

/**
 * Использовать компонент для вывода промежуточной информации, внимание!!! в то время когда cli ждет
 * ввода команды пользователем.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@Component
public class CliPrinter {

    private ResultHandler resultHandler;
    private LineReader lineReader;
    private Terminal terminal;

    /**
     * Конструктор.
     * @param resultHandler обработчик результата.
     * @param lineReader запрашивает ввод input от пользователя
     * @param terminal терминал
     */
    public CliPrinter(
            @Qualifier("main") ResultHandler resultHandler,
            Terminal terminal
    ) {
        this.resultHandler = resultHandler;
        this.terminal = terminal;
    }

    /**
     * Выводит сообщение (без вывода запроса по новой).
     * @param message сообщение
     */
    public void info(String message) {
        resultHandler.handleResult(message);
    }

    /**
     * Выводит сообщение, и повторяет запрос.
     * @param message сообщение
     */
    public void infoAndPrompt(String message) {
        resultHandler.handleResult(message);
        //переисуем последний запрос на ввод (PromptProvider)
        ((LineReaderImpl) lineReader).redrawLine();
        ((LineReaderImpl) lineReader).redisplay();

    }
    
    @Autowired
    public void setLineReader(LineReader lineReader) {
        this.lineReader = lineReader;
    }

}
