import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
// прокси чеккер без потоков, с методом проверки и записью рабочих ip в файл
// группа 1313, 25.10.2021 , урок + дз (запись в файл)
// лабораторная работа №6
// файл с исходными данными для удобства кладем также в папку src, в ней же будет создан файл good_ip.txt с рабочими данными
public class Main {
	public static void main(String[] args) { // точка входа в программу
		try {
			// Иногда на каком-то ip программа застревает наглухо, тогда читаем данные из ip_no_all.txt, в нем удален проблемный адрес
			FileInputStream fis = new FileInputStream("src/ip_no_all.txt"); // создаем объект ввода данных из файла
			int i; // итератор
			String resultIp = ""; // результирующая строка
			while ((i = fis.read()) != -1) { // читаем из файла, пока он не закончится
				if(i == 13) continue; // Если встречаем символ возврата каретки - игнорируем
				else if(i == 10) { // Символ переноса строки
					String[] resultArray = resultIp.split(" "); // создаем массив из ip и порта
					String ip = resultArray[0]; // ip
					int port = Integer.parseInt(resultArray[1]); // port
					checkProxy(ip, port); // вызываем метод проверки
					resultIp = ""; // обнуляем строку
				}
				else if(i == 9) {resultIp += ":";} // Табуляцию заменяем на :
				else {resultIp += (char) i;} // цифры заменяем символами

			}
		} catch (Exception e) { // если файла с исходными данными для чтения нет
			e.printStackTrace();
		}
	}

	public static void checkProxy(String ip, int port) { // вынесли в отдельный метод
		try { // если соединение состоялось, значит ip и port рабочие, выполняем код для рабочих ip:port
			Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(ip, port)); // создаем объект прокси
			URL url =  new URL("https://vozhzhaev.ru/test.php"); // объект url // адрес назначения
			URLConnection connection = url.openConnection(proxy); // создаем соединение
			BufferedReader in = new BufferedReader( // объект буфера
					new InputStreamReader( // в нем входящий поток
							connection.getInputStream())); // у соединения вызываем поток ввода данных(?)

			System.out.println("ip: "+ip+":"+port+" рабочий"); // выводим рабочий ip
			try {
				FileWriter writer = new FileWriter("src/good_ip.txt",true); // дописываем в файл
				// если файла нет, то он будет создан в папке src
				String text ="ip: "+ip+":"+port+"\n"; // ip адрес и port
				writer.write(text); // текстовая запись в файл
				//writer.append("!"); //посимвольная запись в файл
				writer.close(); // закрываем запись
			} catch(Exception ex) {ex.printStackTrace();} // ловим исключения

			in.close(); // закрываем буферный поток ввода данных
		}
		catch (Exception e) { // соединение не удалось, значит ip:port не рабочие
			System.out.println("ip: "+ip+":"+port+" НЕ РАБОТАЕТ"); // выводим сообщение об этом
		}
	}

}
