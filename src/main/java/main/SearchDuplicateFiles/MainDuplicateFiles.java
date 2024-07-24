package main.SearchDuplicateFiles;

import java.util.Scanner;

/**
 * Главная функция процедуры поиска одинаковых файлов (вызов функции)
 */
public class MainDuplicateFiles {
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите тип данных для поиска дубликатов:");
        System.out.println("1. Медиа файлы (png, jpeg, JPG, gif, mp4, mp3, waw, avi)");
        System.out.println("2. Документы (doc, docx, .xls, xlsx, pdf, ppt, pptx, txt)");
        System.out.println("3. Все поддерживаемые файлы");

        int choice = scanner.nextInt();
        scanner.nextLine();  // Избегаем проблем с переходом строки

        FileType fileType;
        switch (choice) {
            case 1:
                fileType = new MediaFileType();
                break;
            case 2:
                fileType = new DocumentFileType();
                break;
            case 3:
                fileType = new AnyFileType();
                break;
            default:
                System.out.println("Неверный выбор, по умолчанию выбраны все файлы.");
                fileType = new AnyFileType();
        }

        System.out.println("Введите путь к директории для сканирования:");
        String directory = scanner.nextLine();
        System.out.println(directory);

        DuplicateFiles duplicateFiles = new DuplicateFiles(fileType);
        duplicateFiles.processFiles(directory);
    }
}