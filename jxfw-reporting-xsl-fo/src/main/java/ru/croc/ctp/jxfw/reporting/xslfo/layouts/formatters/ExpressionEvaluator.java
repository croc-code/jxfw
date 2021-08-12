package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Вспомогательный класс для выполнения TODO: C# кода.
 * Created by vsavenkov on 25.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings("rawtypes")
public class ExpressionEvaluator {

    /**
     * Регулярное выражение для замены {{@literal @}ParamName} на FormatterData.Params["ParamName"].
     */
    protected static Pattern m_Regex1;


    /**
     * Регулярное выражение для замены {#DbFieldName} на FormatterData.CurrentDataRow["DbFieldName"].
     */
    protected static Pattern m_Regex2;

    /**
     * Регулярное выражение для замены {%VarName} на FormatterData.Vars["VarName"].
     */
    protected static Pattern m_Regex3;


    /**
     * Кэш для вычислителей - содержит экземпляры IExpressionEvaluatorItem.
     */
    protected static Hashtable cachedClasses;

    /**
     * Вспомогательный интерфейс.
     */
    public interface IExpressionEvaluatorItem {

        /**
         * Метод выполнения соотв. кода.
         * @param formatterData     - Параметры
         */
        void evaluate(ReportFormatterData formatterData);
    }

    /**
     * Статический конструктор объекта.
     */
    static  {
        // Имя параметра может содержать буквы, цыфры, тире, точку, подчеркивание
        m_Regex1 = Pattern.compile("\\{\\@(?<name>[\\w\\-\\.]+)\\}", Pattern.MULTILINE);
        // Имя колонки может содержать буквы, цифры, пробел, точку, скобки [], тире, подчеркивание.
        m_Regex2 = Pattern.compile("\\{#(?<name>[\\w\\-\\.\\[\\]\\s]+)\\}", Pattern.MULTILINE);
        // Имя переменной отчета может содержать буквы, цыфры, тире, точку, подчеркивание
        m_Regex3 = Pattern.compile("\\{\\%(?<name>[\\w\\-\\.]+)\\}", Pattern.MULTILINE);
        cachedClasses = new Hashtable();
    }

    /**
     * Обработка строки.
     *      для замены {{@literal @}ParamName} на FormatterData.Params["ParamName"]
     *      для замены {#DbFieldName} на FormatterData.CurrentDataRow["DbFieldName"]
     * @param value     - Строка, подлежащая обработке
     * @return String   - Обработаная строка
     */
    protected static String prepareString(String value) {

        Matcher matcher1 = m_Regex1.matcher(value);
        Matcher matcher2 = m_Regex2.matcher(matcher1.replaceAll("FormatterData.Params[\"${name}\"]"));
        Matcher matcher3 = m_Regex3.matcher(matcher2.replaceAll("FormatterData.CurrentDataRow[\"${name}\"]"));

        return matcher3.replaceAll("FormatterData.Vars[\"${name}\"]");
    }

    /**
     * Получение интерфейса для выражения на языке TODO: C#.
     * @param code              - Исходное выражение
     * @param processString     - Признак необходимости обработки строки методом {@link #prepareString(String)}/>
     * @return IExpressionEvaluatorItem - интерфейса для выполнения выражения на языке TODO: C#
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static IExpressionEvaluatorItem getEvaluator(String code, boolean processString) {

        String namespaceString;
        final String classnameString = "Foo";
        String keyString;
        IExpressionEvaluatorItem ev = null;
        keyString = String.valueOf(processString) + "?" + code;
        // Всю фигню кэшировать надо...
        // От греха подальше критическая секция
        synchronized (cachedClasses) {
            if (cachedClasses.containsKey(keyString)) {
                ev = (IExpressionEvaluatorItem) cachedClasses.get(keyString);
            } else {
                // Выполним неоходимые макроподстановки
                if (processString) {
                    code = prepareString(code);
                }
                /* TODO: надо понять, как конструировать код для Java`ы и выполнять
                // Сконструируем код
                namespaceString = "Croc.XmlFramework.ReportService.Utility.Temp.uuid" + UUID.randomUUID().toString()
                        .replaceAll("-", StringUtils.EMPTY);
                StringBuilder codeBody = new StringBuilder("namespace ");
                codeBody.append(namespaceString);
                codeBody.append("{ public class ");
                codeBody.append(classnameString);
                codeBody.append(":");
                codeBody.append(typeof(IExpressionEvaluatorItem).FullName.Replace('+', '.'));
                codeBody.append("{ void ");
                codeBody.append(typeof(IExpressionEvaluatorItem).FullName.Replace('+', '.'));
                codeBody.append(".Evaluate(");
                codeBody.append(typeof(ReportFormatterData).FullName);
                codeBody.append(" FormatterData){ ");
                codeBody.append(Code);
                codeBody.append("}}}"); // Закрыли функцию, класс и Namespace

                // Настроим компилятор
                CSharpCodeProvider provider = new CSharpCodeProvider();
                CompilerParameters cp = new CompilerParameters();
                cp.ReferencedAssemblies.Add("System.dll");
                cp.ReferencedAssemblies.Add("System.Data.dll");
                cp.ReferencedAssemblies.Add("System.Xml.dll");
                cp.ReferencedAssemblies.Add("System.Web.dll");
                cp.ReferencedAssemblies.Add(typeof(ExpressionEvaluator).Assembly.CodeBase.Replace("file:///", ""));
                cp.GenerateExecutable = false;
                cp.GenerateInMemory = true;

                // Попытаемся откомпилировать
                CompilerResults cr = provider.CompileAssemblyFromSource(cp, codeBody.ToString());

                // Проверим на ошибки
                if (cr.Errors.HasErrors)
                {
                    StringBuilder error = new StringBuilder();
                    error.Append("Error Compiling Expression: ");
                    foreach (CompilerError err in cr.Errors)
                    {
                        error.AppendFormat("{0}\n", err.ErrorText);
                    }
                    throw new ReportException("Error Compiling Expression: " + error.ToString() + "\n"
                        + codeBody.ToString());
                }

                // Создадим экземпляр новосозданного класса
                Assembly a = cr.CompiledAssembly;
                ev = a.CreateInstance(namespaceString + "." + classnameString) as IExpressionEvaluatorItem;
*/
                // Добавим результат в кэш
                cachedClasses.put(keyString, ev);
            }
        }
        return ev;
    }

    /**
     * Вычисление выражения на TODO: C#.
     * Внимание! Поля объекта FormatterData м.б. модифицированы
     * @param code              - Исходное выражение на языке TODO: C#
     * @param formatterData     - параметры
     * @param processString     - Признак необходимости обработки строки методом {@link #prepareString(String)}/>
     * @return Object   - результат
     */
    public static Object evaluate(String code, ReportFormatterData formatterData, boolean processString) {
        execute("FormatterData.CurrentValue = (" + code + "); return;", formatterData, processString);
        return formatterData.getCurrentValue();
    }

    /**
     * Вычисление выражения на TODO: C#.
     * Перед выполнением строки производится ее дополнительная обработка методом {@link #prepareString(String)}/>
     * Внимание! Поля объекта FormatterData м.б. модифицированы
     * @param code              - Исходное выражение на языке TODO: C#
     * @param formatterData     - параметры
     * @return Object   - результат
     */
    public static Object evaluate(String code, ReportFormatterData formatterData) {
        return evaluate(code, formatterData, true);
    }

    /**
     * Выполнение последовательности операторов на TODO: C#.
     * Внимание! Поля объекта FormatterData м.б. модифицированы
     * @param code              - Исходное выражение на языке TODO: C#
     * @param formatterData     - параметры
     * @param processString     - Признак необходимости обработки строки методом {@link #prepareString(String)}" />
     */
    public static void execute(String code, ReportFormatterData formatterData, boolean processString) {
        try {
            getEvaluator(code, processString).evaluate(formatterData);
        } catch (Exception ex) {
            throw new ReportException("Ошибка вычисления выражения \"" + code + "\"", ex);
        }
    }
}
